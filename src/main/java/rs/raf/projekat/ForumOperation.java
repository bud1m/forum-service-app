package rs.raf.projekat;

import java.io.Serializable;

public class ForumOperation implements Serializable {

    private static final long serialVersionUID = -6597003954824547294L;

    public static final byte CREATE_TOPIC = 0x01;
    public static final byte ADD_COMMENT = 0x02;
    public static final byte REPLY_COMMENT = 0x03;
    public static final byte UPDATE_COMMENT = 0x04;
    public static final byte DELETE_COMMENT = 0x05;
    public static final byte GET_TOPICS_LIST = 0x06;
    public static final byte GET_COMMENTS = 0x07;

    private byte op;
    private String content;
    private int commentId;
    private int topicId;
    private long timestamp;


    public static ForumOperation createNewTopic(final String content) {
        return new ForumOperation(CREATE_TOPIC, content);
    }

    public static ForumOperation createAddComment(final int topicId, final String content) {
        return new ForumOperation(ADD_COMMENT, topicId, content, System.currentTimeMillis());
    }

    public static ForumOperation createReplyComment(final int commentId, final String content, final int topicId) {
        return new ForumOperation(REPLY_COMMENT, commentId, content, topicId, System.currentTimeMillis());
    }

    public static ForumOperation createUpdateComment(final int commentId, final String content, final int topicId) {
        return new ForumOperation(UPDATE_COMMENT, commentId, content, topicId);
    }

    public static ForumOperation createDeleteComment(final int commentId, final int topicId) {
        return new ForumOperation(DELETE_COMMENT, commentId, topicId);
    }

    public static ForumOperation createGetTopicsList() {
        return new ForumOperation(GET_TOPICS_LIST);
    }

    public static ForumOperation createGetComments(final int topicId) {
        return new ForumOperation(GET_COMMENTS, topicId);
    }


    public ForumOperation(byte op, int commentId, String content, int topicId, long l) {
        this.op = op;
        this.commentId = commentId;
        this.topicId = topicId;
        this.content = content;
        this.timestamp = l;
    }

    public ForumOperation(byte op, int topicId, String content, long l) {
        this.op = op;
        this.topicId = topicId;
        this.content = content;
        this.timestamp = l;
    }

    public ForumOperation(byte op) {
        this(op, -1, null, -1, 0);
    }

    public ForumOperation(byte op, String content) {
        this(op, -1, content, -1, 0);
    }

    public ForumOperation(byte op, int topicId) {
        this.op = op;
        this.topicId = topicId;
    }

    public ForumOperation(byte op, int commentId, String content, int topicId) {
        this.op = op;
        this.commentId = commentId;
        this.content = content;
        this.topicId = topicId;
    }

    public ForumOperation(byte op, int commentId, int topicId) {
        this(op, commentId, null, topicId);
    }

    public byte getOp() {
        return op;
    }

    public String getContent() {
        return content;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getTopicId() {
        return topicId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isReadOp() {
        return GET_TOPICS_LIST == this.op || GET_COMMENTS == this.op;
    }
}