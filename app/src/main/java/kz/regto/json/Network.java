package kz.regto.json;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

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

    protected String networkPath;
    Main main;

    public Network (Main context){
        main = context;
    }
    public Network (){
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
        if (this.getServerValue("web_service.php?comm=ping&par=0",1000)==null) return false;
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
                            sb.append(line);
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

    public Balance setBalance(){
        String url = networkPath+"/balance_income.php?device_server_id="+main.BingoDevice.getServerDeviceId()+
                "&balance="+main.db.getSettings("device_balance").getSettingsValue();
        Balance rMsg=null;
        String data = null;
        do {
            data = getJSON(url, 1000);
            rMsg = new Gson().fromJson(data, Balance.class);
        }while(rMsg==null);

        return rMsg;
    }

    public Balance setBalanceStatus(int status){
        String url = networkPath+"/balance_income.php?device_server_id="+main.BingoDevice.getServerDeviceId()+
                "&balance="+main.db.getSettings("device_balance").getSettingsValue()+"&status="+status;
        Balance rMsg=null;
        String data = null;
        data = getJSON(url, 1000);
        rMsg = new Gson().fromJson(data, Balance.class);
        return rMsg;
    }

    public Balance getBalance(String url) {
        Balance rMsg=null;
        String data = null;
        do {
            data = getJSON(url, 1000);
        }while(data==null);
        rMsg = new Gson().fromJson(data, Balance.class);
        return rMsg;
    }
    public Balance getBalance() {
        String url = networkPath+"/balance_outcome.php?device_server_id="+main.BingoDevice.getServerDeviceId();
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
        url = networkPath+url;
        String data=null;
        WebService rMsg=null;
        boolean exitflag = true;
        long curtime = SystemClock.uptimeMillis();
        long exittime =curtime+Milliseconds;
        do {
            data = getJSON(url,Milliseconds/2);
            try {Thread.sleep(100);}
            catch (InterruptedException Ex){}
            curtime = SystemClock.uptimeMillis();
            if (data!=null) exitflag = false;
            if (curtime > exittime) exitflag = false;
        }while (exitflag);
        if (data!=null) rMsg = new Gson().fromJson(data, WebService.class);
        return rMsg;
    }


    /**
     * Get single device from Server
     */
    public d_device getDeviceFromServer(d_device dDevice){
        String url = "device_service.php?comm=get_device_from_code&par="+dDevice.getDeviceCode();
        url = networkPath+url;
        String data=null;
        d_device rMsg=null;
        boolean exitflag = true;
        long curtime = SystemClock.uptimeMillis();
        long exittime =curtime+2000;
        do {
            data = getJSON(url,1000);
            try {Thread.sleep(100);}
            catch (InterruptedException Ex){}
            curtime = SystemClock.uptimeMillis();
            if (data!=null) exitflag = false;
            if (curtime > exittime) exitflag = false;
        }while (exitflag);
        if (data!=null) rMsg = new Gson().fromJson(data, d_device.class);
        return rMsg;
    }
    public d_device setDeviceOnServer(d_device dDevice){
        String url = "device_service.php?comm=set_status&stat="+dDevice.getStatus()+"&par="+dDevice.getServerDeviceId();
        url = networkPath+url;
        String data=null;
        d_device rMsg=null;
        boolean exitflag = true;
        long curtime = SystemClock.uptimeMillis();
        long exittime =curtime+2000;
        do {
            data = getJSON(url,1000);
            try {Thread.sleep(100);}
            catch (InterruptedException Ex){}
            curtime = SystemClock.uptimeMillis();
            if (data!=null) exitflag = false;
            if (curtime > exittime) exitflag = false;
        }while (exitflag);
        if (data!=null) rMsg = new Gson().fromJson(data, d_device.class);
        return rMsg;
    }
}
