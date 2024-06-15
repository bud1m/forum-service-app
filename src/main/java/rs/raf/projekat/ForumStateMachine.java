package rs.raf.projekat;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.alipay.sofa.jraft.util.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.raf.projekat.rpc.ForumOutter.*;
//import rs.raf.projekat.rpc.snapshot.ForumSnapshotFile;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import static rs.raf.projekat.ForumOperation.*;

public class ForumStateMachine extends StateMachineAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ForumStateMachine.class);

    private static ThreadPoolExecutor executor = ThreadPoolUtil
            .newBuilder()
            .poolName("JRAFT_EXECUTOR")
            .enableMetric(true)
            .coreThreads(3)
            .maximumThreads(5)
            .keepAliveSeconds(60L)
            .workQueue(new SynchronousQueue<>())
            .threadFactory(
                    new NamedThreadFactory("JRaft-Executor-", true)).build();

    private final AtomicLong value = new AtomicLong(0);
    private final AtomicLong leaderTerm = new AtomicLong(-1);

    // ForumStore instance
    private final ForumStore forumStore = new ForumStore();

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    public long getValue() {
        return this.value.get();
    }


//    @Override
//    public void onSnapshotSave(final SnapshotWriter writer, final Closure done) {
//        final String snapshotPath = writer.getPath() + File.separator + "data";
//        executor.submit(() -> {
//            final ForumSnapshotFile snapshot = new ForumSnapshotFile(snapshotPath);
//            if (snapshot.save(forumStore.getTopics(),forumStore.getComments())) {
//                if (writer.addFile("data")) {
//                    done.run(Status.OK());
//                } else {
//                    done.run(new Status(RaftError.EIO, "Fail to add file to writer"));
//                }
//            } else {
//                done.run(new Status(RaftError.EIO, "Fail to save forum snapshot %s", snapshot.getPath()));
//            }
//        });
//    }




    @Override
    public void onApply(final Iterator iter) {
        while (iter.hasNext()) {
            ForumResponse returnValue = null;

            ForumOperation forumOperation = null;
            ForumClosure closure = null;
            if (iter.done() != null) {

                closure = (ForumClosure) iter.done();
                forumOperation = closure.getForumOperation();

            } else {
                final ByteBuffer data = iter.getData();
                try {
                    forumOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(
                            data.array(), ForumOperation.class.getName());

                } catch (final CodecException e) {
                    LOG.error("Fail to decode request", e);
                }
                if (forumOperation != null && forumOperation.isReadOp()) {
                    iter.next();
                    continue;
                }
            }

            if (forumOperation != null) {
                try {
                    switch (forumOperation.getOp()) {
                        case CREATE_TOPIC:
                            returnValue = forumStore.createTopic(forumOperation.getContent());
                            break;
                        case ADD_COMMENT:
                            returnValue =  forumStore.addComment(forumOperation.getTopicId(), forumOperation.getContent());
                            break;
                        case REPLY_COMMENT:
                            returnValue = forumStore.replyComment(forumOperation.getCommentId(), forumOperation.getContent(),forumOperation.getTopicId());
                            break;
                        case UPDATE_COMMENT:
                            returnValue =forumStore.updateComment(forumOperation.getCommentId(), forumOperation.getContent(),forumOperation.getTopicId());
                            break;
                        case DELETE_COMMENT:
                            returnValue =forumStore.deleteComment(forumOperation.getCommentId(),forumOperation.getTopicId());
                            break;
                        case GET_TOPICS_LIST:
                            returnValue =forumStore.getTopicsList();
                            break;
                        case GET_COMMENTS:
                            returnValue =forumStore.getComments(forumOperation.getTopicId());
                            break;
                        default:
                            LOG.warn("Unhandled forum operation: {}", forumOperation.getOp());
                            break;
                    }
                } catch (Exception e) {
                    LOG.error("Operation failed: " + e.getMessage());
                }
            }

            iter.next();

            if (closure != null) {
                closure.success(returnValue);
                closure.run(Status.OK());
            }
        }
    }
}
