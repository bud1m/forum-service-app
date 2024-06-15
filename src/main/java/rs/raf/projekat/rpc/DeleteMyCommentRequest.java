package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class DeleteMyCommentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -930229469012345689L;

    public DeleteMyCommentRequest() {
    }

    private int commentId;
    private int topicId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
}
