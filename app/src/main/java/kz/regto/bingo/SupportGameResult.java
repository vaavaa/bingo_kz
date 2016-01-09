package kz.regto.bingo;

import android.os.AsyncTask;
import android.util.Log;

import kz.regto.json.CurrentTime;

/**
 * Created by Старцев on 25.11.2015.
 * Class helps to get time from server, in case of any network issues it generates by oneself;
 */
public class SupportGameResult extends AsyncTask<String, CurrentTime, CurrentTime> {


    private int currentBall=-1;
    JSONParser jpr = new JSONParser();

    protected void onProgressUpdate(CurrentTime... progress) {
        currentBall = progress[0].getWinnumber();
        Log.v("1",Integer.toString(currentBall));
    }

    protected void onPostExecute(CurrentTime... result) {
        currentBall = result[0].getWinnumber();
    }

    //Выполняем бесконечный цикл опроса сервера о текущем таймере
    //Если сервер не откликается возвращаем текущее время системы
    protected CurrentTime doInBackground(String... parameter) {
        String r_URL_timer = parameter[0];
        CurrentTime tProgress=new CurrentTime();
        int iProgress=-1;
           do {
               tProgress = jpr.getGameResult(r_URL_timer);
                   if (tProgress==null){
                       tProgress.setWinnumber(-1);
                   };
               publishProgress(tProgress);
           }while (!this.isCancelled());
        return tProgress;
    }

    public int GetWinBall(){
        return currentBall;
    }



}
