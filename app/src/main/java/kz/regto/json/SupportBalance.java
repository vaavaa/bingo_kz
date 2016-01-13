package kz.regto.json;

import android.os.AsyncTask;
import android.os.SystemClock;

import kz.regto.database.d_balance;

/**
 * Created by Старцев on 25.11.2015.
 * Class helps to get time from server, in case of any network issues it generates time by oneself;
 */
public class SupportBalance extends AsyncTask<String, Balance, Balance> {

    Network ntw = new Network();
    int currentBalance=0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    protected void onProgressUpdate(Balance... progress) {
        currentBalance = progress[0].getBalance();
    }

    protected void onPostExecute(Balance... result) {
    }

    //Выполняем бесконечный цикл опроса сервера о текущем балансе
    protected Balance doInBackground(String... parameter) {
        Balance tProgress;
        int iProgress=-1;

           do {
               tProgress = ntw.getBalance();
                   if (tProgress!=null){
                       publishProgress(tProgress);
                       try {Thread.sleep(250);}
                       catch (InterruptedException Ex){}
                   }
           }while (!this.isCancelled());
        return tProgress;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }
}
