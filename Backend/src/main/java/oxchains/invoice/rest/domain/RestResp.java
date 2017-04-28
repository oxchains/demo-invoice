package oxchains.invoice.rest.domain;

/**
 * @author aiet
 */
public class RestResp {

    public final int status;
    public final String message;
    public final Object data;

    private RestResp(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private RestResp(int status, String messsage) {
        this(status, messsage, null);
    }

    public static RestResp success(Object data) {
        return new RestResp(1, "success", data);
    }

    public static RestResp success(String message, Object data){
        return new RestResp(1, message, data);
    }

    public static RestResp fail(String message, Object data){
        return new RestResp(-1, message, data);
    }

    public static RestResp fail(String message){
        return new RestResp(-1, message);
    }

    public static RestResp fail(){
        return new RestResp(-1, "fail");
    }

}
