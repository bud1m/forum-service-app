package rs.raf.projekat.rpc.processor;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import rs.raf.projekat.ForumClosure;
import rs.raf.projekat.ForumService;
import rs.raf.projekat.rpc.ForumOutter.*;

public class DeleteMyCommentProcessor implements RpcProcessor<DeleteMyCommentRequest> {

    private final ForumService forumService;

    public DeleteMyCommentProcessor(ForumService forumService) {
        super();
        this.forumService = forumService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, DeleteMyCommentRequest request) {
        final ForumClosure closure = new ForumClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.forumService.deleteMyComment(request.getCommentId(),request.getTopicId(), closure);

    }

    @Override
    public String interest() {
        return NewTopicRequest.class.getName();
    }
}