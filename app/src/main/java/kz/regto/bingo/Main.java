package kz.regto.bingo;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Main extends AppCompatActivity {

//    private TextView timerValue;
//    private ProgressBar progressBar;
//    private long startTime = 0L;
//
//
//    private long secCounter = 50000;
//    private long secCounterPlus = 2000;
//    private long secCounterPlus2 = 2000;
//
//
//    private Handler customHandler = new Handler();
//    private Animation anim = new AlphaAnimation(0.0f, 1.0f);
//
//
//    long timeInMilliseconds = 0L;
//    long timeSwapBuff = 0L;
//    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar.isShowing()) bar.hide();

        //Таймер
//        timerValue = (TextView) findViewById(R.id.timerValue);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
//
//        anim.setDuration(350); //You can manage the blinking time with this parameter
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        progressBar.setMax((int) secCounter / 100);
//
//        startTime = SystemClock.uptimeMillis();
//        customHandler.postDelayed(updateTimerThread, 0);
        //Конец блока запуска таймера

    }

//    public void card_click(View v){
//        Button button = (Button) findViewById(v.getId());
//        if (!button.isSelected()) {
//            button.setSelected(true);
//        }
//        else button.setSelected(false);
//
//    }
//
//    public void run_connection(View v) {
//        Button button = (Button) findViewById(v.getId());
//    }
//
//    public void auto_click(View v) {
//        Button button = (Button) findViewById(v.getId());
//    }
//
//    private Runnable updateTimerThread = new Runnable() {
//        int i = 42;
//        int ii = 55;
//        public void run() {
//
//            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
//            updatedTime = timeSwapBuff + timeInMilliseconds;
//            progressBar.setProgress((int) ((secCounter - updatedTime) / 100));
//
//            if ((updatedTime) >= secCounter ) {
//                int secs = 0;
//                int mins = 0;
//                secs = 0;
//                int milliseconds = 0;
//                if (ii > 40) {
//                    timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ii);
//                    ii --;
//                }
//                else timerValue.setTextColor(Color.parseColor("#ffffff"));
//
//                if ((updatedTime) >= (secCounter + secCounterPlus) ) {
//                    secs = (int)(secCounter)/1000;;
//                    mins = secs / 60;
//                    secs = secs % 60;
//                    milliseconds = (int)(secCounter) % 1000;
//                }
//
//                if ((updatedTime) >= (secCounter + secCounterPlus+secCounterPlus2) ) {
//                    startTime = SystemClock.uptimeMillis();
//                    timeSwapBuff =0;
//                    progressBar.setProgress(0);
//                    i = 42;
//                    ii = 55;
//                }
//
//                timerValue.setText("" + mins + ":"
//                        + String.format("%02d", secs) + ":"
//                        + String.format("%03d", milliseconds).substring(0, 2));
//
//            }
//            else{
//                long persnt = (int)updatedTime*100/secCounter;
//
//                if ( persnt >80) {
//                    if (timerValue.getCurrentTextColor()==Color.parseColor("#ffffff")){
//                        timerValue.setTextColor(Color.parseColor("#A5C63B"));
//                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55);
//                        timerValue.setVisibility(View.VISIBLE);
//                        timerValue.startAnimation(anim);
//                    }
//                }
//                if ((persnt >=50) && (persnt<80))  {
//                    if (i < 55) {
//                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, i);
//                        i ++;
//                    }
//                }
//                if (persnt < 50){
//                    timerValue.clearAnimation();
//                    if (timerValue.getCurrentTextColor()!=Color.parseColor("#ffffff")){
//                        timerValue.setTextColor(Color.parseColor("#ffffff"));
//                        timerValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
//                        timerValue.clearAnimation();
//                        timerValue.setAlpha(1.0f);
//                    }
//                }
//
//                int secs = (int) ((secCounter - updatedTime) / 1000);
//                int mins = secs / 60;
//                secs = secs % 60;
//                int milliseconds = ((int) ((secCounter - updatedTime)) % 1000);
//
//                timerValue.setText("" + mins + ":"
//                        + String.format("%02d", secs) + ":"
//                        + String.format("%03d", milliseconds).substring(0, 2));
//            }
//            customHandler.postDelayed(this, 0);
//        }
//
//    };


}
