//package rs.raf.projekat.rpc.snapshot;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import rs.raf.projekat.rpc.ForumOutter.Comment;
//import rs.raf.projekat.rpc.ForumOutter.Topic;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//public class ForumSnapshotFile {
//
//    private static final Logger LOG = LoggerFactory.getLogger(ForumSnapshotFile.class);
//
//    private String path;
//
//    public ForumSnapshotFile(String path) {
//        super();
//        this.path = path;
//    }
//
//    public String getPath() {
//        return this.path;
//    }
//
//    *
//     * Save forum topics and comments to the snapshot file.
//    
//    public boolean save(Map<Integer, Topic> topics, Map<Integer, List<Comment>> comments) {
//        try {
//            StringBuilder snapshotData = new StringBuilder();
//            for (Map.Entry<Integer, Topic> entry : topics.entrySet()) {
//                Topic topic = entry.getValue();
//                snapshotData.append(topicToString(topic));
//                snapshotData.append("\n");
//                List<Comment> commentList = comments.getOrDefault(entry.getKey(), List.of());
//                for (Comment comment : commentList) {
//                    snapshotData.append(commentToString(comment));
//                    snapshotData.append("\n");
//                }
//            }
//            FileUtils.writeStringToFile(new File(path), snapshotData.toString());
//            return true;
//        } catch (IOException e) {
//            LOG.error("Fail to save snapshot", e);
//            return false;
//        }
//    }
//
//    *
//     * Load forum topics and comments from the snapshot file.
//    
//    public boolean load(Map<Integer, Topic> topics, Map<Integer, List<Comment>> comments) {
//        try {
//            File snapshotFile = new File(path);
//            if (!snapshotFile.exists()) {
//                LOG.warn("Snapshot file does not exist: {}", path);
//                return false;
//            }
//            List<String> lines = FileUtils.readLines(snapshotFile, "UTF-8");
//            Topic.Builder currentTopicBuilder = null;
//            for (String line : lines) {
//                if (line.startsWith("Topic:")) {
//                    if (currentTopicBuilder != null) {
//                        topics.put(currentTopicBuilder.getId(), currentTopicBuilder.build());
//                    }
//                    currentTopicBuilder = Topic.newBuilder();
//                    String[] parts = line.split(":");
//                    currentTopicBuilder.setId(Integer.parseInt(parts[1]));
//                    currentTopicBuilder.setContent(parts[2]);
//                } else if (line.startsWith("Comment:")) {
//                    if (currentTopicBuilder == null) {
//                        LOG.warn("Comment found before the topic");
//                        continue;
//                    }
//                    String[] parts = line.split(":");
//                    Comment.Builder commentBuilder = Comment.newBuilder();
//                    commentBuilder.setId(Integer.parseInt(parts[1]));
//                    commentBuilder.setTopicId(Integer.parseInt(parts[2]));
//                    commentBuilder.setContent(parts[3]);
//                    List<Comment> commentList = comments.getOrDefault(commentBuilder.getTopicId(), List.of());
//                    commentList.add(commentBuilder.build());
//                    comments.put(commentBuilder.getTopicId(), commentList);
//                }
//            }
//            if (currentTopicBuilder != null) {
//                topics.put(currentTopicBuilder.getId(), currentTopicBuilder.build());
//            }
//            return true;
//        } catch (IOException | NumberFormatException e) {
//            LOG.error("Fail to load snapshot", e);
//            return false;
//        }
//    }
//
//    private String topicToString(Topic topic) {
//        return "Topic:" + topic.getId() + ":" + topic.getContent();
//    }
//
//    private String commentToString(Comment comment) {
//        return "Comment:" + comment.getId() + ":" + comment.getTopicId() + ":" + comment.getContent();
//    }
//}
