package kz.regto.json;

import android.content.Context;
import android.net.ConnectivityManager;

public class Network {

    private String networkPath;

    public String getNetworkPath() {
        return networkPath;
    }
    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
