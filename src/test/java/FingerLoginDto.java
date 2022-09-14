import java.io.Serializable;

public class FingerLoginDto implements Serializable {

    private String device;

    private String usertype;

    private String uid;

    private String cn;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public FingerLoginDto(String device, String usertype, String uid, String cn) {
        this.device = device;
        this.usertype = usertype;
        this.uid = uid;
        this.cn = cn;
    }

}
