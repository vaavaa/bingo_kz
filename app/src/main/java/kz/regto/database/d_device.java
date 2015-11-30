package kz.regto.database;

/**
 * Created by Старцев on 24.11.2015.
 */
public class d_device {
    private  int device_id;
    private String DeviceCode;
    private int status;
    private int balance;
    private String network_path;

    public String getNetwork_path() {
        return network_path;
    }

    public void setNetwork_path(String network_path) {
        this.network_path = network_path;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }
}
