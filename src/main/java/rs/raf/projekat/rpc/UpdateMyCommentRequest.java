package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class UpdateMyCommentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -9202294680123456789L;

    public UpdateMyCommentRequest() {
    }

    private int commentId;
    private String newContent;
    private int topicId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
}
