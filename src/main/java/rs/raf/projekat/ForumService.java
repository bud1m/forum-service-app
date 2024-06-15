package rs.raf.projekat;

public interface ForumService {

    void sendNewTopic(final String content, final ForumClosure closure);

    void sendNewCommentToTopic(final int topicId, final String comment, final ForumClosure closure);

    void replyToComment(final int commentId, final String reply,final int topicId, final ForumClosure closure);

    void updateMyComment(final int commentId, final String newContent, final int topicId, final ForumClosure closure);

    void deleteMyComment(final int commentId,final int topicId, final ForumClosure closure);

    void getTopicsList(final ForumClosure closure);

    void getTopicComments(final int topicId, final ForumClosure closure);
}
