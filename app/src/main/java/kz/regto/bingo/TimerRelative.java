package kz.regto.bingo;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TimerRelative extends RelativeLayout {


    private long secCounter = 30000;
    private boolean bTimer= false;
    private final String SER_CODE_LETTER="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int SER_CODE_INT=1;

    private final String URL_number = "http://192.168.1.3/json/index.php";
    private final String URL_timer = "http://192.168.1.3/json/timer.php";

    private long startTime = 0L;
    private TextView timerValue;
    private TextView WinNumber;
    private SupportTimer sp=new SupportTimer();
    private Handler customHandler = new Handler();


    private TimerRelative tr;

    private List<TimerEvent> listeners = new ArrayList<TimerEvent>();


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

        //Инициируем получение времени
        sp.execute(URL_timer, URL_number);

        View view = inflate(getContext(), R.layout.timer_relative, null);
        timerValue = (TextView) view.findViewById(R.id.timerValue);
        WinNumber = (TextView) view.findViewById(R.id.WinNumber);

        this.addListener((Main) context);
        tr=this;

        addView(view);
    }

    public void StartTimer(){
        if (!bTimer){
            startTime = sp.GetCurrentTime();
            secCounter =sp.GetFinalTime()-startTime;
            Log.v("1",Long.toString(secCounter));

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
        for (TimerEvent hl : listeners) hl.TimerOver();
        customHandler.removeCallbacks(updateTimerThread);
    }

    public String WinningNumber(){
        //Get win ball
        String sReturn;
        int wn =sp.GetWinBall();
        if (wn<9) sReturn = "0".concat(Integer.toString(wn));
        else sReturn = Integer.toString(wn);
        return sReturn;
    }


    public String GenerateNewGameCode(String serial){
        String ser = serial.substring(0,serial.indexOf("-"));
        String serCode = serial.substring(serial.indexOf("-")+1,serial.length());
        String ReturnValue="";
        serCode=serCode.replaceFirst("0*","");
        SER_CODE_INT = Integer.parseInt(serCode);
        int size_of_number;
        if (SER_CODE_INT>9999) {
            if (SER_CODE_LETTER.indexOf(ser.substring(1,1))==SER_CODE_LETTER.length()){
                ser = SER_CODE_LETTER.substring(SER_CODE_LETTER.indexOf(ser.substring(0,1)+1),1);
                ser=ser.concat(SER_CODE_LETTER.substring(1,1));
            }
            else {
                ser=ser.substring(0,1)
                        .concat(SER_CODE_LETTER.substring(SER_CODE_LETTER.indexOf(ser.substring(1, 1) + 1), 1));
            }
            SER_CODE_INT =1;
        }
        else SER_CODE_INT++;
        serCode=Integer.toString(SER_CODE_INT);
        size_of_number=4-serCode.length();
        //Создали цифры
        for (int i = size_of_number; i > 0; i--) ReturnValue = ReturnValue.concat("0");
        ReturnValue=ReturnValue.concat(serCode);
        ReturnValue = ser.concat("-").concat(ReturnValue);
        return ReturnValue;
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
            int secs = 0;
            int mins = 0;

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
}