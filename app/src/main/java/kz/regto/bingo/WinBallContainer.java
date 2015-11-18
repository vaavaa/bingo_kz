package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by spt on 03.11.2015.
 */
public class WinBallContainer extends RelativeLayout {

    private int iVisible=1;
    private Handler h;
    private TextView[] tViews = new TextView[10];
    private String lastWinNumber;
    private String lastNumber;
    private int rInt = 1;

    public WinBallContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public WinBallContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public WinBallContainer(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        //Надули
        View view = inflate(getContext(), R.layout.win_ball_text_view_container, null);
        view.setVisibility(View.VISIBLE);
        //Выставили на показ
        addView(view);
        h = new Handler();
    }


    public void UpdateNewOne(String winNumber){
        lastWinNumber = winNumber;
        if  (iVisible<=9){
            TextView view = (TextView)this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(10-iVisible))));
            lastWinNumber = winNumber;
            tViews[iVisible] = view;
            h.post(showInfo);
            iVisible++;
        }
        else{
            h.post(showInfo);
        }
        this.invalidate();
    }

    private int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        return resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
    }

    // Изображение перетекает
    Runnable showInfo = new Runnable() {
        public void run() {
            if (rInt==1){
                lastNumber = (String)tViews[rInt].getText();
                if (lastNumber.length()==0) lastNumber = lastWinNumber;
                tViews[rInt].setText(lastWinNumber);
            }
            else {
                String lastNumber1 = (String)tViews[rInt].getText();
                tViews[rInt].setText(lastNumber);
                lastNumber = lastNumber1;
            }

            if (rInt ==(iVisible-1)){
                rInt=1;
                h.removeCallbacks(showInfo);
                tViews[iVisible-1].setVisibility(View.VISIBLE);
                Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                tViews[rInt].setAnimation(rotate_animation);
            }
            else {
                rInt++;
                h.postDelayed(showInfo, 250);
            }
        }
    };
}