package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class GetTopicCommentsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -950224690124356789L;

    public GetTopicCommentsRequest() {
    }

    private int topicId;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
}
