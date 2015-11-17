package kz.regto.bingo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spt on 03.11.2015.
 */
public class TimerRelative extends RelativeLayout {
    private TextView timerValue;
    private long startTime = 0L;

    private long secCounter = 5000;
    private long secCounterPlus = 2000;
    private long secCounterPlus2 = 2000;
    private boolean bTimer= false;

    private String SER_CODE_LETTER="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int SER_CODE_INT=1;

    private Handler customHandler = new Handler();
    private Animation anim = new AlphaAnimation(0.0f, 1.0f);


    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;

    private TimerRelative tr;

    private List<TimerEvent> listeners = new ArrayList<TimerEvent>();

    public TimerRelative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView(context);
    }

    public TimerRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
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

        anim.setDuration(350); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        this.addListener((Main) context);
        startTime = SystemClock.uptimeMillis();
        tr=this;

        StartTimer();

        addView(view);
    }

    public void StartTimer(){
        if (!bTimer){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        for (TimerEvent hl : listeners) hl.TimerStarted(tr);
            bTimer=true;
        }
    }

    public int WinningNumber(){
        int wn = (int) (Math.random() * ((36) + 1));
        return wn;
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

    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimerRelative, 0, 0);

        try {
//            label = a.getString(R.styleable.TwoTextViews_label_text);
//            field = a.getString(R.styleable.TwoTextViews_field_text);
//            size_text = a.getDimensionPixelSize(R.styleable.TwoTextViews_size_text, 0);
//            color_text=a.getColor(R.styleable.TwoTextViews_color_text, 0);

        } finally {
            a.recycle();
        }
    }
    //Принимаем подписчиков на события таймера
    public void addListener(TimerEvent toAdd) { listeners.add(toAdd); }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = 0;
            int mins = 0;

            if ((updatedTime) >= secCounter) {
                secs = 0;
                mins = 0;
                for (TimerEvent hl : listeners) hl.TimerOver();
                bTimer=false;
                timerValue.setText(Integer.toString(mins).concat(":").concat(String.format("%02d", secs)));
                customHandler.removeCallbacks(updateTimerThread);

            } else {
                //long persnt = (int) updatedTime * 100 / secCounter;
                secs = (int) ((secCounter - updatedTime) / 1000);
                mins = secs / 60;
                secs = secs % 60;
                timerValue.setText(Integer.toString(mins).concat(":").concat(String.format("%02d", secs)));
                customHandler.postDelayed(this, 0);
            }


        }
    };
}