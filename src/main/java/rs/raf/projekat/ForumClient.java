package rs.raf.projekat;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.InvokeCallback;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import rs.raf.projekat.rpc.ForumOutter.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

public class ForumClient {

    public static void main(String[] args) throws InterruptedException, TimeoutException {
        if (args.length != 2) {
            System.out.println("Usage : java rs.raf.projekat.UserClient {groupId} {conf}");
            System.out
                    .println("Example: java rs.raf.projekat.UserClient user 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }

        final String groupId = args[0];
        final String confStr = args[1];

        final Configuration conf = new Configuration();
        if (!conf.parse(confStr)) {
            throw new IllegalArgumentException("Fail to parse conf:" + confStr);
        }

        RouteTable.getInstance().updateConfiguration(groupId, conf);

        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());

        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh leader failed");
        }

        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        System.out.println("Leader is " + leader);
        final int n = 10;
        final CountDownLatch latch = new CountDownLatch(n);
        final long start = System.currentTimeMillis();

        ForumClient userClient = new ForumClient();
        for (int i = 0; i < n; i++) {
            userClient.createTopic("Content " + i, cliClientService, leader, latch);
        }

        latch.await();
        System.out.println(n + " ops, cost : " + (System.currentTimeMillis() - start) + " ms.");
        System.exit(0);
    }


    public void createTopic(String content, CliClientServiceImpl cliClientService, PeerId leader, CountDownLatch latch) {
        NewTopicRequest newTopicRequest = NewTopicRequest.newBuilder()
                .setContent(content)
                .setReadOnlySafe(true)
                .build();

        try {
            cliClientService.getRpcClient().invokeAsync(leader.getEndpoint(), newTopicRequest, new InvokeCallback() {

                @Override
                public void complete(Object result, Throwable err) {
                    if (err == null) {
                        latch.countDown();
                        ForumResponse response = (ForumResponse) result;
                        System.out.println("createTopic response: " + response);
                    } else {
                        err.printStackTrace();
                        latch.countDown();
                    }
                }

                @Override
                public Executor executor() {
                    return null;
                }
            }, 5000);
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
            latch.countDown();
        }
    }
}

