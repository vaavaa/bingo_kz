package kz.regto.database;

/**
 * Created by Старцев on 24.11.2015.
 */
public class d_device {
    private int device_id;
    private String DeviceCode;
    private int status;
    private int ServerDeviceId;
    private int TypeId;
    private String Comment;
    private String type_name;
    private int game_limit;
    private String game_mask;

    public String getGame_mask() {
        return game_mask;
    }

    public void setGame_mask(String game_mask) {
        this.game_mask = game_mask;
    }

    public int getGame_limit() {
        return game_limit;
    }

    public void setGame_limit(int game_limit) {
        this.game_limit = game_limit;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int typeId) {
        TypeId = typeId;
    }

    public int getServerDeviceId() {
        return ServerDeviceId;
    }

    public void setServerDeviceId(int serverDeviceId) {
        ServerDeviceId = serverDeviceId;
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
