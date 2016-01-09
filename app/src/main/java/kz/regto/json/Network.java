package kz.regto.json;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import kz.regto.bingo.Main;
import kz.regto.database.d_device;
import kz.regto.database.d_settings;

public class Network {

    private String networkPath;
    Main main;

    public void Network (Main context){
        main = context;
    }

    public String getNetworkPath() {
        return networkPath;
    }
    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
        d_settings settings = new d_settings();
        settings.setSettingsName("network_path");
        settings.setSettingsValue(networkPath);
        main.db.createNewSettings(settings);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean ConnectionExist(){
        if (this.getServerValue("web_service.php?comm=ping&par=0",1000)!=null) return false;
        else return true;
    }

    public String getJSON(String url, int timeout) {

        HttpURLConnection c = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            try {
                c.connect();
                int status = c.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }
            }
            catch (ConnectException ex){}
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public CurrentTime getTimer(String url) {
        CurrentTime rMsg=null;
        String data = null;
        do {
            data = getJSON(url, 1000);
        }while(data==null);
        rMsg = new Gson().fromJson(data, CurrentTime.class);
        return rMsg;
    }

    public Balance tBalance(String url) {
        Balance rMsg=null;
        String data = null;
        do {
            data = getJSON(url, 1000);
        }while(data==null);

        rMsg = new Gson().fromJson(data, Balance.class);
        return rMsg;
    }

    public CurrentTime getGameResult(String url) {
        CurrentTime rMsg=null;
        String data = null;
        do{
            data = getJSON(url, 1000);
        }while(data!=null);
            rMsg = new Gson().fromJson(data, CurrentTime.class);
        return rMsg;
    }

    public WebService getServerValue(String url, int Milliseconds) {
        String data=null;
        WebService rMsg=null;
        long curtime = SystemClock.uptimeMillis();
        long exittime =curtime+Milliseconds;
        do {
            data = getJSON(url, 1000);
            curtime = SystemClock.uptimeMillis();
        }while (data == null || curtime>exittime);
        rMsg = new Gson().fromJson(data, WebService.class);
        return rMsg;
    }


    /**
     * Get single device from Server
     */
    public d_device getDeviceFromServer(d_device dDevice){

        return dDevice;
    }
}
