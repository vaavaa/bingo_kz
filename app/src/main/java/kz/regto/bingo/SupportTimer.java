package kz.regto.bingo;

import android.os.AsyncTask;
import android.os.SystemClock;

import kz.regto.json.CurrentTime;
import kz.regto.json.JSONParser;

/**
 * Created by Старцев on 25.11.2015.
 * Class helps to get time from server, in case of any network issues it generates by oneself;
 */
public class SupportTimer extends AsyncTask<String, CurrentTime, CurrentTime> {

    private Long currentTime=0L;
    private int currentBall=-1;
    JSONParser jpr = new JSONParser();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        currentTime = SystemClock.uptimeMillis();
    }


    protected void onProgressUpdate(CurrentTime... progress) {

        currentTime = progress[0].getCurrenttime();
        currentBall = progress[0].getWinnumber();

    }

    protected void onPostExecute(CurrentTime... result) {
        currentTime = result[0].getCurrenttime();
        currentBall = result[0].getWinnumber();
     }

    //Выполняем бесконечный цикл опроса сервера о текущем таймере
    //Если сервер не откликается возвращаем текущее время системы
    protected CurrentTime doInBackground(String... parameter) {
        String r_URL_timer = parameter[0];
        String r_URL_number = parameter[1];
        CurrentTime tProgress=new CurrentTime();
        int iProgress=-1;

           do {
               if (r_URL_timer.length()>0) {
                   tProgress = jpr.getTimer(r_URL_timer);
                   if (tProgress!=null){
                       if (tProgress.getCurrenttime() == 0L) tProgress.setCurrenttime(SystemClock.uptimeMillis());
                       if (tProgress.getWinnumber()==-1) tProgress.setWinnumber((int)(Math.random() * ((36) + 1)));
                   }
                   else {
                       tProgress=new CurrentTime();
                       tProgress.setCurrenttime(SystemClock.uptimeMillis());
                       tProgress.setWinnumber((int) (Math.random() * ((36) + 1)));
                   };

               }
               else {
                   tProgress.setCurrenttime(SystemClock.uptimeMillis());
                   tProgress.setWinnumber((int) (Math.random() * ((36) + 1)));
               };

               publishProgress(tProgress);
           }while (!this.isCancelled());
        return tProgress;
    }

    public long GetCurrentTime(){
          return currentTime;
    }
    public int GetWinBall(){
        return currentBall;
    }

}
