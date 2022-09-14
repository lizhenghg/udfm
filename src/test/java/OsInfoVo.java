import java.io.Serializable;

public class OsInfoVo implements Serializable {

    private String historyinfoid;
    private String osType;
    private String osVersion;
    private String deviceId;
    private String manufacturer;
    private String deviceModel;

    public String getHistoryinfoid() {
        return historyinfoid;
    }

    public void setHistoryinfoid(String historyinfoid) {
        this.historyinfoid = historyinfoid;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public OsInfoVo(String historyinfoid, String osType, String osVersion,
                    String deviceId, String manufacturer, String deviceModel) {
        this.historyinfoid = historyinfoid;
        this.osType = osType;
        this.osVersion = osVersion;
        this.deviceId = deviceId;
        this.manufacturer = manufacturer;
        this.deviceModel = deviceModel;
    }

    @Override
    public String toString() {
        return String.format("OsInfoVo[historyinfoid=%s, osType=%s, osVersion=%s, deviceId=%s, manufacturer=%s, deviceModel=%s]",
                this.getHistoryinfoid(), this.getOsType(), this.getOsVersion(), this.getDeviceId(), this.getManufacturer(), this.getDeviceModel());
    }
}
