package kz.regto.database;

public class d_settings {
    private int Id;
    private String SettingsName;
    private String SettingsValue;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSettingsName() {
        return SettingsName;
    }

    public void setSettingsName(String settingsName) {
        SettingsName = settingsName;
    }

    public String getSettingsValue() {
        return SettingsValue;
    }

    public void setSettingsValue(String settingsValue) {
        SettingsValue = settingsValue;
    }
}
