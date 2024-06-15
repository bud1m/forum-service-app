package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class SendNewCommentToTopic implements Serializable {

    @Serial
    private static final long serialVersionUID = -323456590123456789L;

    public SendNewCommentToTopic() {
    }

    private int topicId;
    private String comment;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
