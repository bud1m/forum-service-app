package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class ReplyToComment implements Serializable {

    @Serial
    private static final long serialVersionUID = -9102294190123456789L;

    public ReplyToComment() {
    }

    private int commentId;
    private String reply;
    private int topicId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
}
