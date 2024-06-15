package rs.raf.projekat.rpc;

import java.io.Serial;
import java.io.Serializable;

public class NewTopicRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8107840697555088325L;

    public NewTopicRequest() {

    }

    private boolean readOnlySafe = true;
    private String content;

    public boolean isReadOnlySafe() {
        return readOnlySafe;
    }

    public void setReadOnlySafe(boolean readOnlySafe) {
        this.readOnlySafe = readOnlySafe;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
