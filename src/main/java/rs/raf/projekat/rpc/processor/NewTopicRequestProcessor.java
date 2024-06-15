package rs.raf.projekat.rpc.processor;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import rs.raf.projekat.ForumClosure;
import rs.raf.projekat.ForumService;
import rs.raf.projekat.rpc.ForumOutter.*;


public class NewTopicRequestProcessor implements RpcProcessor<NewTopicRequest> {

    private final ForumService forumService;

    public NewTopicRequestProcessor(ForumService forumService) {
        super();
        this.forumService = forumService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, NewTopicRequest request) {
        final ForumClosure closure = new ForumClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.forumService.sendNewTopic(request.getContent(), closure);

    }

    @Override
    public String interest() {
        return NewTopicRequest.class.getName();
    }
}
