package rs.raf.projekat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.raf.projekat.rpc.ForumOutter.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ForumStore {

    private static final Logger LOG = LoggerFactory.getLogger(ForumStore.class);

    public Map<Integer, List<Comment>> getComments() {
        return comments;
    }

    public Map<Integer, Topic> getTopics() {
        return topics;
    }

    private final Map<Integer,Topic> topics = new HashMap<>();
    private final Map<Integer, List<Comment>> comments = new HashMap<>();
    private final AtomicLong topicIdGenerator = new AtomicLong(0);
    private final AtomicLong commentIdGenerator = new AtomicLong(0);

    public ForumResponse createTopic(String content) {
        long topicId = topicIdGenerator.incrementAndGet();
        Topic topic = Topic.newBuilder()
                .setId((int) topicId)
                .setContent(content)
                .build();
        topics.put((int) topicId, topic);
        comments.put((int) topicId, new ArrayList<>());
        LOG.info("Created new topic with ID {} and content '{}'", topicId, content);
        return ForumResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Topic created successfully.")
                .build();
    }

    public ForumResponse addComment(int topicId, String content) throws Exception {
        List<Comment> topicComments = comments.get(topicId);
        if (topicComments != null) {
            long commentId = commentIdGenerator.incrementAndGet();
            Comment comment = Comment.newBuilder()
                    .setId((int) commentId)
                    .setTopicId(topicId)
                    .setContent(content)
                    .setTimestamp(System.currentTimeMillis())
                    .build();
            topicComments.add(comment);
            LOG.info("Added new comment '{}' to topic ID {}", content, topicId);
            return ForumResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Comment added successfully.")
                    .build();
        } else {
            LOG.warn("Topic ID {} does not exist", topicId);
            throw new Exception("Topic ID " + topicId + " does not exist");
        }
    }


    public ForumResponse replyComment(int parentId, String reply, int topicId) throws Exception {
        List<Comment> topicComments = comments.get(topicId);
        if (topicComments != null) {
            for (Comment comment : topicComments) {
                if (comment.getId() == parentId) {
                    long commentId = commentIdGenerator.incrementAndGet();
                    Comment replyComment = Comment.newBuilder()
                            .setId((int) commentId)
                            .setTopicId(topicId)
                            .setContent(reply)
                            .setParentId(parentId)
                            .setTimestamp(System.currentTimeMillis())
                            .build();
                    topicComments.add(replyComment);
                    LOG.info("Replied '{}' to comment ID {}", reply, parentId);
                    return ForumResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Replied to comment successfully.")
                            .build();
                }
            }
        }
        LOG.warn("Comment ID {} does not exist", parentId);
        throw new Exception("Comment ID " + parentId + " does not exist");
    }

    public ForumResponse updateComment(int commentId, String newContent, int topicId) throws Exception {
        List<Comment> topicComments = comments.get(topicId);
        if (topicComments != null) {
            for (Comment comment : topicComments) {
                if (comment.getId() == commentId) {
                    long currentTime = System.currentTimeMillis();
                    long commentTime = comment.getTimestamp();
                    long fiveMinutesInMillis = 5 * 60 * 1000; // 5 min

                    if (currentTime - commentTime > fiveMinutesInMillis) {
                        LOG.warn("Cannot update comment ID {}: more than 5 minutes have passed", commentId);
                        throw new Exception("Cannot update comment: more than 5 minutes have passed");
                    }

                    Comment updatedComment = comment.toBuilder()
                            .setContent(newContent)
                            .build();
                    topicComments.set(topicComments.indexOf(comment), updatedComment);
                    LOG.info("Updated comment ID {} with new content '{}'", commentId, newContent);
                    return ForumResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Comment updated successfully.")
                            .build();
                }
            }
        }
        LOG.warn("Comment ID {} does not exist", commentId);
        throw new Exception("Comment ID " + commentId + " does not exist");
    }


    public ForumResponse deleteComment(int commentId, int topicId) throws Exception {
        List<Comment> topicComments = comments.get(topicId);
        if (topicComments != null) {
            Comment targetComment = null;
            for (Comment comment : topicComments) {
                if (comment.getId() == commentId) {
                    targetComment = comment;
                    break;
                }
            }

            if (targetComment != null) {
                Set<Integer> commentsToDelete = new HashSet<>();
                commentsToDelete.add(commentId);

                if (targetComment.getParentId() == 0) {
                    for (Comment comment : topicComments) {
                        if (comment.getParentId() == commentId) {
                            commentsToDelete.add(comment.getId());
                        }
                    }
                }

                topicComments.removeIf(comment -> commentsToDelete.contains(comment.getId()));

                LOG.info("Deleted comment ID {} and its replies", commentId);
                return ForumResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Comment and its replies deleted successfully.")
                        .build();
            }
        }
        LOG.warn("Comment ID {} does not exist", commentId);
        throw new Exception("Comment ID " + commentId + " does not exist");
    }


    public ForumResponse getTopicsList() {
        List<Topic> topicList = new ArrayList<>(topics.values());
        LOG.info("Topics list: {}", topics);
        return ForumResponse.newBuilder()
                .setSuccess(true)
                .addAllTopics(topicList)
                .build();
    }

    public ForumResponse getComments(int topicId) throws Exception {
        List<Comment> topicComments = comments.get(topicId);
        if (topicComments != null) {
            LOG.info("Comments for topic ID {}: {}", topicId, topicComments);
            return ForumResponse.newBuilder()
                    .setSuccess(true)
                    .addAllComments(topicComments)
                    .build();
        } else {
            LOG.warn("Topic ID {} does not exist", topicId);
            throw new Exception("Topic ID " + topicId + " does not exist");
        }
    }
}
