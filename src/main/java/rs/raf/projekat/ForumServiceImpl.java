package rs.raf.projekat;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.StoreEngine;
import com.alipay.sofa.jraft.rhea.StoreEngineHelper;
import com.alipay.sofa.jraft.rhea.options.StoreEngineOptions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class ForumServiceImpl implements ForumService {
    private static final Logger LOG = LoggerFactory.getLogger(ForumServiceImpl.class);

    private final ForumServer forumServer;
    private final Executor readIndexExecutor;


    public ForumServiceImpl(ForumServer counterServer) {
        this.forumServer = counterServer;
        this.readIndexExecutor = createReadIndexExecutor();
    }

    private Executor createReadIndexExecutor() {
        final StoreEngineOptions opts = new StoreEngineOptions();
        return StoreEngineHelper.createReadIndexExecutor(opts.getReadIndexCoreThreads());
    }


    private boolean isLeader() {
        return this.forumServer.getFsm().isLeader();
    }

    private long getValue() {
        return this.forumServer.getFsm().getValue();
    }

    private String getRedirect() {
        return this.forumServer.redirect().getRedirect();
    }


    private void applyOperation(final ForumOperation op, final ForumClosure closure) {
        if (!isLeader()) {
            handlerNotLeaderError(closure);
            return;
        }

        try {
            closure.setForumOperation(op);
            final Task task = new Task();
            task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(op)));
            task.setDone(closure);
            this.forumServer.getNode().apply(task);
        } catch (CodecException e) {
            String errorMsg = "Fail to encode ForumOperation";
            LOG.error(errorMsg, e);
            closure.failure(errorMsg, StringUtils.EMPTY);
            closure.run(new Status(RaftError.EINTERNAL, errorMsg));
        }
    }

    private void handlerNotLeaderError(final ForumClosure closure) {
        closure.failure("Not leader.", getRedirect());
        closure.run(new Status(RaftError.EPERM, "Not leader"));
    }

    @Override
    public void sendNewTopic(String content, ForumClosure closure) {
        ForumOperation op = ForumOperation.createNewTopic(content);
        applyOperation(op, closure);
    }

    @Override
    public void sendNewCommentToTopic(int topicId, String comment, ForumClosure closure) {
        ForumOperation op = ForumOperation.createAddComment(topicId, comment);
        applyOperation(op, closure);
    }

    @Override
    public void replyToComment(int commentId, String reply,int topicId, ForumClosure closure) {
        ForumOperation op = ForumOperation.createReplyComment(commentId, reply,topicId);
        applyOperation(op, closure);
    }

    @Override
    public void updateMyComment(int commentId, String newContent, int topicId, ForumClosure closure) {
        ForumOperation op = ForumOperation.createUpdateComment(commentId, newContent,topicId);
        applyOperation(op, closure);
    }

    @Override
    public void deleteMyComment(int commentId, int topicId, ForumClosure closure) {
        ForumOperation op = ForumOperation.createDeleteComment(commentId, topicId);
        applyOperation(op, closure);
    }

    @Override
    public void getTopicsList(ForumClosure closure) {
        ForumOperation op = ForumOperation.createGetTopicsList();
        applyOperation(op, closure);
    }

    @Override
    public void getTopicComments(int topicId, ForumClosure closure) {
        ForumOperation op = ForumOperation.createGetComments(topicId);
        applyOperation(op, closure);
    }
}
