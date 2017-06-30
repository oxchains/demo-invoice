package oxchains.invoice.rest.domain;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author aiet
 */
public class ChaincodeResp {

    private String txid;
    private String peer;
    private int success;
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean succeeded() {
        return 1 == success || isNotBlank(payload);
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
