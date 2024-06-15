package rs.raf.projekat.rpc;

import com.alipay.sofa.jraft.rpc.RpcServer;
import com.alipay.sofa.jraft.rpc.impl.MarshallerHelper;
import com.alipay.sofa.jraft.util.RpcFactoryHelper;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class ForumGrpcHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ForumGrpcHelper.class);

    public static RpcServer rpcServer;

    public static void initGRpc() {
        if ("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass().getName())) {
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.ForumResponse.class.getName(),
                    ForumOutter.ForumResponse.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.Topic.class.getName(),
                    ForumOutter.Topic.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.Comment.class.getName(),
                    ForumOutter.Comment.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.ReplyToComment.class.getName(),
                    ForumOutter.ReplyToComment.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.NewTopicRequest.class.getName(),
                    ForumOutter.NewTopicRequest.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.SendNewCommentToTopic.class.getName(),
                    ForumOutter.SendNewCommentToTopic.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.GetTopicCommentsRequest.class.getName(),
                    ForumOutter.GetTopicCommentsRequest.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.UpdateMyCommentRequest.class.getName(),
                    ForumOutter.UpdateMyCommentRequest.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ForumOutter.DeleteMyCommentRequest.class.getName(),
                    ForumOutter.DeleteMyCommentRequest.getDefaultInstance());


            MarshallerHelper.registerRespInstance(ForumOutter.ForumResponse.class.getName(),
                    ForumOutter.ForumResponse.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.Topic.class.getName(),
                    ForumOutter.Topic.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.Comment.class.getName(),
                    ForumOutter.Comment.getDefaultInstance());

            MarshallerHelper.registerRespInstance(ForumOutter.ReplyToComment.class.getName(),
                    ForumOutter.ReplyToComment.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.NewTopicRequest.class.getName(),
                    ForumOutter.NewTopicRequest.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.SendNewCommentToTopic.class.getName(),
                    ForumOutter.SendNewCommentToTopic.getDefaultInstance());

            MarshallerHelper.registerRespInstance(ForumOutter.GetTopicCommentsRequest.class.getName(),
                    ForumOutter.GetTopicCommentsRequest.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.UpdateMyCommentRequest.class.getName(),
                    ForumOutter.UpdateMyCommentRequest.getDefaultInstance());
            MarshallerHelper.registerRespInstance(ForumOutter.DeleteMyCommentRequest.class.getName(),
                    ForumOutter.DeleteMyCommentRequest.getDefaultInstance());
        }
    }

    public static void setRpcServer(RpcServer rpcServer) {
        ForumGrpcHelper.rpcServer = rpcServer;
    }

    public static void blockUntilShutdown() {
        if (rpcServer == null) {
            return;
        }
        if ("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass().getName())) {
            try {
                Method getServer = rpcServer.getClass().getMethod("getServer");
                Object grpcServer = getServer.invoke(rpcServer);

                Method shutdown = grpcServer.getClass().getMethod("shutdown");
                Method awaitTerminationLimit = grpcServer.getClass().getMethod("awaitTermination", long.class, TimeUnit.class);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        shutdown.invoke(grpcServer);
                        awaitTerminationLimit.invoke(grpcServer, 30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }));
                Method awaitTermination = grpcServer.getClass().getMethod("awaitTermination");
                awaitTermination.invoke(grpcServer);
            } catch (Exception e) {
                LOG.error("Failed to block grpc server", e);
            }
        }
    }

}
