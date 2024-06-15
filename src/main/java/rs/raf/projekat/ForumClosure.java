package rs.raf.projekat;

import com.alipay.sofa.jraft.Closure;
import rs.raf.projekat.rpc.ForumOutter.*;

public abstract class ForumClosure implements Closure {

    private ForumResponse response;
    private ForumOperation forumOperation;

    public void setForumOperation(ForumOperation forumOperation) {
        this.forumOperation = forumOperation;
    }

    public ForumOperation getForumOperation() {
        return forumOperation;
    }

    public ForumResponse getResponse() {
        return response;
    }

    public void setResponse(ForumResponse response) {
        this.response = response;
    }

    protected void failure(final String errorMsg, final String redirect) {
        final ForumResponse response = ForumResponse.newBuilder()
                .setSuccess(false)
                .setErrorMsg(errorMsg)
                .setRedirect(redirect)
                .build();
        setResponse(response);
    }

    protected void success(final ForumResponse response) {
        setResponse(response);
    }
}
