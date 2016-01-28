package kz.regto.json;

import android.os.AsyncTask;
import android.os.SystemClock;

import kz.regto.database.d_balance;

public class SupportBalance extends AsyncTask<String, Balance, Balance> {

    Network ntw = new Network();
    int currentBalance=0;
    int currentID = 0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    protected void onProgressUpdate(Balance... progress) {
        currentBalance = progress[0].getBalance();
        currentID = progress[0].getId_balance();
    }

    protected void onPostExecute(Balance... result) {
    }

    //Выполняем бесконечный цикл опроса сервера о текущем балансе
    protected Balance doInBackground(String... parameter) {
        String url = parameter[0];
        Balance tProgress;
        int iProgress=-1;

           do {
               tProgress = ntw.getBalance(url);
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

    public int getCurrentID() {
        return currentID;
    }
}
