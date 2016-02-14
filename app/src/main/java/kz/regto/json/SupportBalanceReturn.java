package kz.regto.json;

import android.os.AsyncTask;

public class SupportBalanceReturn extends AsyncTask<String, Integer, Integer> {

    Network ntw = new Network();
    int gameId=0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    protected void onProgressUpdate(Integer... progress) {
        gameId = progress[0];
    }

    protected void onPostExecute(Integer... result) {
    }

    //Выполняем бесконечный цикл опроса сервера о текущем балансе
    protected Integer doInBackground(String... parameter) {
        String url = parameter[0];
        Integer tProgress;
           do {
               tProgress = ntw.getMissedGame(url);
                   if (tProgress!=null){
                       publishProgress(tProgress);
                       try {Thread.sleep(500);}
                       catch (InterruptedException Ex){}
                   }
           }while (!this.isCancelled());
        return tProgress;
    }

    public int getGameId() {
        return gameId;
    }
}
