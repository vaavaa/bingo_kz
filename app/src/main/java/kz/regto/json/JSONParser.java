package kz.regto.json;

import android.os.StrictMode;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 String data = getJSON("http://localhost/authmanager.php");
 AuthMsg msg = new Gson().fromJson(data, AuthMsg.class);
 System.out.println(msg);
 */
public class JSONParser {
        // constructor
        public JSONParser() {}

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
                catch (ConnectException ex){
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
        String data = getJSON(url, 1000);
        if (data!=null){
            CurrentTime msg = new Gson().fromJson(data, CurrentTime.class);
            rMsg = msg;
        };
        return rMsg;
    }
    public PinCode getPinCode(String url) {
        PinCode rMsg=null;
        String data = getJSON(url, 1000);
        if (data!=null){
            PinCode msg = new Gson().fromJson(data, PinCode.class);
            rMsg = msg;
        };
        return rMsg;
    }


}
