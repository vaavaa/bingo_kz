package kz.regto.bingo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import kz.regto.json.CurrentTime;
import kz.regto.json.Network;
import kz.regto.json.SupportTimer;

public class TimerRelative extends RelativeLayout {


    private long secCounter = 30000;
    private long startTime = 0L;
    private TextView timerValue;
    private TextView WinNumber;
    private SupportTimer sp=new SupportTimer();

    private Handler customHandler = new Handler();
    private Handler GameResultHandler = new Handler();

    private List<TimerEvent> listeners = new ArrayList<>();
    private int wn_number =-1;

    private Main prnt;

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
        if (sp.getStatus()!= AsyncTask.Status.RUNNING) {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                sp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,URL_timer,"");
            else
                sp.execute(URL_timer,"");
        }
    }

    public void StartTimer(){
       startTime = sp.GetCurrentTime();
       secCounter =sp.GetFinalTime()-startTime;
        //Сменили видимость выйгравшего номера и таймера
        TimerVisibility(true);

        GameResultHandler.removeCallbacks(updateGameResult);
        customHandler.postDelayed(updateTimerThread, 0);
    }
    public void CloseAll() {
        sp.cancel(true);
        customHandler.removeCallbacks(updateTimerThread);
        GameResultHandler.removeCallbacks(updateGameResult);
    }

    public void StopTimer(boolean NeedResult){
        //Сменили видимость выйгравшего номера и таймера
        TimerVisibility(false);
        WinNumber.setText("...");


        //Запустили процесс опроса сервера
        //И опросника сервера
        if (NeedResult) GameResultHandler.postDelayed(updateGameResult, 0);
        else GameResultHandler.removeCallbacks(updateGameResult);

        //Сказали всем что таймер кончился
        for (TimerEvent hl : listeners) hl.TimerOver();
        //Остановили опросник времени
        customHandler.removeCallbacks(updateTimerThread);
    }
    public void GameOver(){
        WinNumber.setText(this.getWinningNumber());

        GameResultHandler.removeCallbacks(updateGameResult);
        this.invalidate();
        for (TimerEvent hl : listeners) hl.GameOver();
    }

    public String getWinningNumber(){
        //Get win ball
        String sReturn;
        int wn =wn_number;
        if (wn > -1) {
            if (wn<10){
                sReturn = "0".concat(Integer.toString(wn));
            }
            else sReturn = Integer.toString(wn);
            //Сохраняем в базу
            prnt.dGame.setWin_ball(wn);
            prnt.dGame.setState(1);
        }
        else sReturn = Integer.toString(wn);
        return sReturn;
    }

    public String GenerateNewGameCode(String serial, String deviceCode) {
        String SER_CODE_LETTER="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int SER_CODE_INT=1;
        if (serial.indexOf("/") > 0) serial = serial.substring(0,serial.indexOf("/"));
        String ser = serial.substring(0, serial.indexOf("-"));
        String serCode = serial.substring(serial.indexOf("-") + 1, serial.length());
        String ReturnValue = "";
        serCode = serCode.replaceFirst("0*", "");
        if (serCode.length() ==0) SER_CODE_INT = 1;
        else SER_CODE_INT = Integer.parseInt(serCode);
        int size_of_number;
        if (SER_CODE_INT == 9999) {
            if (SER_CODE_LETTER.indexOf(ser.substring(1, 2)) == SER_CODE_LETTER.length()-1) {
                String letter0 = "A";
                String letter = ser.substring(0,1);
                int p_letter = SER_CODE_LETTER.indexOf(letter)+1;
                ser = SER_CODE_LETTER.substring(p_letter,p_letter+1);
                ser = ser.concat(letter0);
            } else {
                String letter = ser.substring(1, 2);
                int p_letter = SER_CODE_LETTER.indexOf(letter)+1;
                ser = ser.substring(0, 1)
                        .concat(SER_CODE_LETTER.substring(p_letter, p_letter + 1));
            }
            SER_CODE_INT = 1;
        } else SER_CODE_INT++;
        serCode = Integer.toString(SER_CODE_INT);
        size_of_number = 4 - serCode.length();
        //Создали цифры
        for (int i = size_of_number; i > 0; i--) ReturnValue = ReturnValue.concat("0");
        ReturnValue = ReturnValue.concat(serCode);
        ReturnValue = ser.concat("-").concat(ReturnValue).concat("/").concat(deviceCode);
        return ReturnValue;
    }

    public String getServerGameSerial(){
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

            if ((updatedTime) >= secCounter) StopTimer(true);
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
            CurrentTime cTime;
            cTime = prnt.ntw.getTimer(prnt.ntw.getNetworkPath().concat("/timer1.php?game_id=").concat(""+prnt.dGame.getServer_game_id()));


            if (cTime.getWinnumber() >= 0) {
                wn_number =cTime.getWinnumber();
                GameOver();
            }
            else {GameResultHandler.postDelayed(this,0);}

        }
    };
}