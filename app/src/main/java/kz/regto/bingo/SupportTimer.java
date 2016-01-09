package kz.regto.bingo;

import android.os.AsyncTask;
import android.os.SystemClock;

import kz.regto.json.CurrentTime;
import kz.regto.json.Network;

/**
 * Created by Старцев on 25.11.2015.
 * Class helps to get time from server, in case of any network issues it generates by oneself;
 */
public class SupportTimer extends AsyncTask<String, CurrentTime, CurrentTime> {

    private long currentTime=0L;
    private int currentBall=-1;
    private long FinalTime=0L;
    private int server_game_id = 0;
    private String game_code ="";
    Network ntw = new Network();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        currentTime = SystemClock.uptimeMillis();
        FinalTime = SystemClock.uptimeMillis()+30000;
    }


    protected void onProgressUpdate(CurrentTime... progress) {

        currentTime = progress[0].getCurrenttime();
        currentBall = progress[0].getWinnumber();
        FinalTime = progress[0].getFinalCounter();
        server_game_id = progress[0].getGame_id();
        game_code = progress[0].getGame_code();

    }

    protected void onPostExecute(CurrentTime... result) {
        currentTime = result[0].getCurrenttime();
        currentBall = result[0].getWinnumber();
        FinalTime = result[0].getFinalCounter();
        server_game_id = result[0].getGame_id();
        game_code = result[0].getGame_code();
    }

    //Выполняем бесконечный цикл опроса сервера о текущем таймере
    //Если сервер не откликается возвращаем текущее время системы
    protected CurrentTime doInBackground(String... parameter) {
        String r_URL_timer = parameter[0];
        String r_URL_number = parameter[1];
        CurrentTime tProgress=new CurrentTime();
        int iProgress=-1;

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

    public long GetCurrentTime(){
          return currentTime;
    }
    public int GetWinBall(){
        return currentBall;
    }
    public long GetFinalTime(){
        return FinalTime;
    }
    public String getGameCode(){
        return game_code;
    }
    public int getServer_game_id(){
        return server_game_id;
    }


}
