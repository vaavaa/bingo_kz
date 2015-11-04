package kz.regto.bingo;

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
    private ProgressBar progressBar;
    private long startTime = 0L;


    private long secCounter = 50000;
    private long secCounterPlus = 2000;
    private long secCounterPlus2 = 2000;
    private boolean bTimer= true;

    private Handler customHandler = new Handler();
    private Animation anim = new AlphaAnimation(0.0f, 1.0f);


    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private List<TimerEvent> listeners = new ArrayList<TimerEvent>();

    static Responder rr = new Responder();

    public TimerRelative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView();
    }

    public TimerRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView();
    }

    public TimerRelative(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.timer_relative, null);
        timerValue = (TextView) view.findViewById(R.id.timerValue);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);

        anim.setDuration(350); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        progressBar.setMax((int) secCounter / 100);

        startTime = SystemClock.uptimeMillis();
        this.addListener(rr);
        for (TimerEvent hl : listeners) hl.TimerStarted();
        customHandler.postDelayed(updateTimerThread, 0);


        addView(view);
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
        int i = 42;
        int ii = 55;

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            progressBar.setProgress((int) ((secCounter - updatedTime) / 100));

            if ((updatedTime) >= secCounter) {
                int secs = 0;
                int mins = 0;
                secs = 0;
                if (ii > 40) {
                    timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ii);
                    ii--;
                } else timerValue.setTextColor(Color.parseColor("#ffffff"));

                if ((updatedTime) >= (secCounter + secCounterPlus)) {
                    secs = (int) (secCounter) / 1000;
                    mins = secs / 60;
                    secs = secs % 60;
                    //Даем всем знать что таймер кончился
                    if (bTimer){
                        for (TimerEvent hl : listeners) hl.TimerOver();
                        bTimer=false;
                    }
                }

                if ((updatedTime) >= (secCounter + secCounterPlus + secCounterPlus2)) {
                    startTime = SystemClock.uptimeMillis();
                    timeSwapBuff = 0;
                    progressBar.setProgress(0);
                    i = 42;
                    ii = 55;
                    //Даем всем знать что таймер начался
                    for (TimerEvent hl : listeners) hl.TimerStarted();
                    bTimer=true;
                }

                timerValue.setText(Integer.toString(mins).concat(":").concat(String.format("%02d", secs)));

            } else {
                long persnt = (int) updatedTime * 100 / secCounter;

                if (persnt > 80) {
                    if (timerValue.getCurrentTextColor() == Color.parseColor("#ffffff")) {
                        timerValue.setTextColor(Color.parseColor("#A5C63B"));
                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55);
                        timerValue.setVisibility(View.VISIBLE);
                        timerValue.startAnimation(anim);
                    }
                }
                if ((persnt >= 50) && (persnt < 80)) {
                    if (i < 55) {
                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, i);
                        i++;
                    }
                }
                if (persnt < 50) {
                    timerValue.clearAnimation();
                    if (timerValue.getCurrentTextColor() != Color.parseColor("#ffffff")) {
                        timerValue.setTextColor(Color.parseColor("#ffffff"));
                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
                        timerValue.clearAnimation();
                        timerValue.setAlpha(1.0f);
                    }
                }

                int secs = (int) ((secCounter - updatedTime) / 1000);
                int mins = secs / 60;
                secs = secs % 60;

                timerValue.setText(Integer.toString(mins).concat(":").concat(String.format("%02d", secs)));
            }
            customHandler.postDelayed(this, 0);
        }
    };
}