package kz.regto.json;

import android.os.AsyncTask;
import android.os.SystemClock;

/**
Class for set entries to it's place
 */
public class SupportBoard extends AsyncTask<String, CurrentTime, CurrentTime> {

    Network ntw = new Network();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    protected void onProgressUpdate(CurrentTime... progress) {


    }

    protected void onPostExecute(CurrentTime... result) {
    }

    protected CurrentTime doInBackground(String... parameter) {
        String r_URL_timer = parameter[0];
        CurrentTime tProgress;
           do {
               tProgress = ntw.getTimer(r_URL_timer);
               if (tProgress!=null){
                   publishProgress(tProgress);
                   try {Thread.sleep(250);}
                   catch (InterruptedException Ex){}
               }
           }while (!this.isCancelled());
        return tProgress;
    }

}
