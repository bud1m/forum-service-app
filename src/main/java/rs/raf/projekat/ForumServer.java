package rs.raf.projekat;

import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import org.apache.commons.io.FileUtils;
import rs.raf.projekat.rpc.ForumGrpcHelper;
import rs.raf.projekat.rpc.ForumOutter.*;
import rs.raf.projekat.rpc.processor.*;

import java.io.File;
import java.io.IOException;

public class ForumServer {

    private final RaftGroupService raftGroupService;
    private final Node node;
    private final ForumStateMachine fsm;

    public ForumServer(final String dataPath, final String groupId, final PeerId serverId,
                       final NodeOptions nodeOptions) throws IOException {

        File f = new File(dataPath);
        System.out.println("[Created File] Path: " + f.getAbsolutePath());
        FileUtils.forceMkdir(f);

        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());


        ForumGrpcHelper.initGRpc();
        ForumGrpcHelper.setRpcServer(rpcServer);

        ForumService forumService = new ForumServiceImpl(this);
        ReplyToCommentProcessor newTopic = new ReplyToCommentProcessor(forumService);
        rpcServer.registerProcessor(newTopic);
        rpcServer.registerProcessor(new UpdateMyCommentProcessor(forumService));
        rpcServer.registerProcessor(new SendNewCommentToTopicProcessor(forumService));
        rpcServer.registerProcessor(new DeleteMyCommentProcessor(forumService));
        rpcServer.registerProcessor(new GetTopicCommentsProcessor(forumService));

        this.fsm = new ForumStateMachine();

        // Node options setup
        nodeOptions.setFsm(this.fsm);
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");

        // Init and start
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        this.node = this.raftGroupService.start();
    }

    public ForumStateMachine getFsm() {
        return this.fsm;
    }

    public Node getNode() {
        return this.node;
    }

    public RaftGroupService RaftGroupService() {
        return this.raftGroupService;
    }

    public ForumResponse redirect() {
        final ForumResponse.Builder builder = ForumResponse.newBuilder().setSuccess(false);
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
                builder.setRedirect(leader.toString());
            }
        }
        return builder.build();
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.out.println("Usage : java com.alipay.sofa.jraft.example.counter.CounterServer {dataPath} {groupId} {serverId} {initConf}");
            System.out.println("Example: java com.alipay.sofa.jraft.example.counter.CounterServer /tmp/server1 counter 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }

        final String dataPath = args[0];
        final String groupId = args[1];
        final String serverIdStr = args[2];
        final String initConfStr = args[3];

        final NodeOptions nodeOptions = new NodeOptions();

        nodeOptions.setElectionTimeoutMs(1000);
        nodeOptions.setDisableCli(false);
        nodeOptions.setSnapshotIntervalSecs(30);

        final PeerId serverId = new PeerId();
        if (!serverId.parse(serverIdStr)) {
            throw new IllegalArgumentException("Fail to parse serverId:" + serverIdStr);
        }

        final Configuration initConf = new Configuration();
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initConfStr);
        }

        nodeOptions.setInitialConf(initConf);

        final ForumServer forumServer = new ForumServer(dataPath, groupId, serverId, nodeOptions);
        System.out.println("Started counter server at port:" + forumServer.getNode().getNodeId().getPeerId().getPort());

        // GrpcServer need block to prevent process exit
        ForumGrpcHelper.blockUntilShutdown();
    }
}
