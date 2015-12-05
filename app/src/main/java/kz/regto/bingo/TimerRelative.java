package kz.regto.bingo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import kz.regto.json.CurrentTime;
import kz.regto.json.JSONParser;

public class TimerRelative extends RelativeLayout {


    private long secCounter = 30000;
    private boolean bTimer= false;
    private long startTime = 0L;
    private TextView timerValue;
    private TextView WinNumber;
    private SupportTimer sp=new SupportTimer();
    private SupportGameResult sp_game=new SupportGameResult();

    private Handler customHandler = new Handler();
    private Handler GameResultHandler = new Handler();

    private List<TimerEvent> listeners = new ArrayList<>();

    Main prnt;

    public TimerRelative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public TimerRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TimerRelative(Context context) {
        super(context);
        initView(context);
    }

    public String getText(){
        return (String)timerValue.getText();
    }

    private void initView(Context context) {

        View view = inflate(getContext(), R.layout.timer_relative, null);
        timerValue = (TextView) view.findViewById(R.id.timerValue);
        WinNumber = (TextView) view.findViewById(R.id.WinNumber);
        addView(view);
        this.addListener((Main) context);
        prnt = (Main)context;
        prnt.setTimerElement(this);
    }
    public void HTTPRunTimer(String URL_timer){
        //Инициируем получение времени
        if (sp.getStatus()!= AsyncTask.Status.RUNNING) sp.execute(URL_timer, "");
    }

    public void StartTimer(){
        if (!bTimer){
            startTime = sp.GetCurrentTime();
            secCounter =sp.GetFinalTime()-startTime;
        //Сменили видимость выйгравшего номера и таймера
        TimerVisibility(true);

        customHandler.postDelayed(updateTimerThread, 0);
        //for (TimerEvent hl : listeners) hl.TimerStarted();
        bTimer=true;
        }
    }
    public void StopTimer(){
        bTimer=false;
        //Сменили видимость выйгравшего номера и таймера
        TimerVisibility(false);
        //Устанавливаем текст в шарик
        String sWinNumber = this.getWinningNumber();
        int gWinNumber = Integer.parseInt(sWinNumber);
        if (gWinNumber!=-1){
            GameOver();
            WinNumber.setText(sWinNumber);
        }
        else {
            WinNumber.setText("...");
            //Создали строку запроса об окончании игры
            String HttpGameResultRequest = prnt.BingoDevice.getNetwork_path().concat("timer.php?game_id="+prnt.dGame.getServer_game_id());
            //Запустили процесс опроса сервера
            sp_game.execute(HttpGameResultRequest,"");
            //И опросника сервера
            GameResultHandler.postDelayed(updateGameResult, 0);
        }
        //Сказали всем что таймер кончился
        for (TimerEvent hl : listeners) hl.TimerOver();
        //Остановили опросник времени
        customHandler.removeCallbacks(updateTimerThread);
    }
    public void GameOver(){
        GameResultHandler.removeCallbacks(updateGameResult);
        for (TimerEvent hl : listeners) hl.GameOver();
        this.invalidate();
    }

    public String getWinningNumber(){
        //Get win ball
        String sReturn;
        int wn =sp_game.GetWinBall();
        if (wn > -1) {
            if (wn<9){
                //Сохраняем в базу
                prnt.dGame.setWin_ball(wn);
                prnt.dGame.setState(1);
                sReturn = "0".concat(Integer.toString(wn));
            }
            else sReturn = Integer.toString(wn);
        }
        else sReturn = Integer.toString(wn);
        return sReturn;
    }


    public String getNewGameCode(){
        String game_code;
        game_code = sp.getGameCode();
        return game_code;
    }
    public int getServerGameCode(){
        int id_server_game;
        id_server_game = sp.getServer_game_id();
        return id_server_game;
    }

    //Принимаем подписчиков на события таймера
    public void addListener(TimerEvent toAdd) { listeners.add(toAdd);}

    private void TimerVisibility(boolean bTimer){
        if (bTimer) {
            WinNumber.setVisibility(View.GONE);
            timerValue.setVisibility(View.VISIBLE);
        }
        else{
            WinNumber.setVisibility(View.VISIBLE);
            timerValue.setVisibility(View.GONE);
        }

    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            long updatedTime = sp.GetCurrentTime() - startTime;
            int secs;
            int mins;

            if ((updatedTime) >= secCounter) StopTimer();
            else {
                secs = (int) ((secCounter - updatedTime) / 1000);
                mins = secs / 60;
                secs = secs % 60;
                timerValue.setText(String.format("%02d", mins).concat(":").concat(String.format("%02d", secs)));
                customHandler.postDelayed(this, 0);
            }


        }
    };
    private Runnable updateGameResult = new Runnable() {
        @Override
        public void run() {

           JSONParser jpr = new JSONParser();
            CurrentTime tProgress = jpr.getGameResult(prnt.BingoDevice.getNetwork_path().concat("timer.php?game_id="+prnt.dGame.getServer_game_id()));
            if (tProgress.getWinnumber() !=-1) {
            // /if (sp_game.GetWinBall()!=-1) {
                GameOver();
            //}
            }
            else
            GameResultHandler.postDelayed(this,0);
        }
    };
}