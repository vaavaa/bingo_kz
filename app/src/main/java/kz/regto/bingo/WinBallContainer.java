package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
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

import kz.regto.database.d_entry_set;

/**
 * Created by spt on 03.11.2015.
 */
public class WinBallContainer extends RelativeLayout {

    private int iVisible=1;
    private Handler h;
    private TextView[] tViews = new TextView[11];
    private String lastWinNumber;
    private String lastNumber;
    private int rInt = 1;
    private List<d_entry_set> pdl;

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
    public void setAllSelected_false(){
        for (int i=1; i<=10;i++){
            TextView view = (TextView)this.findViewById(
                        getResourceByID("id", "f".concat(Integer.toString(i))));
            view.setSelected(false);
            view.setBackground(ContextCompat.getDrawable(getContext(), getResourceByID("drawable", "round_shape")));

        }
    }

    public void UpdateNewOne(String winNumber,List<d_entry_set> dl, Main mn ){
        lastWinNumber = winNumber;
        pdl = dl;
        if  (iVisible<=10){
            TextView view = (TextView)this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(11-iVisible))));
            lastWinNumber = winNumber;
            tViews[iVisible] = view;
            tViews[iVisible].setTag(pdl);
            h.post(showInfo);
            iVisible++;
        }
        else{
            mn.ClearGameCach();
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
                Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                tViews[rInt].setAnimation(rotate_animation);
                lastNumber = (String)tViews[rInt].getText();
                if (lastNumber.length()==0) lastNumber = lastWinNumber;
                tViews[rInt].setText(lastWinNumber);
                tViews[rInt].setTag(pdl);
            }
            else {
                List <d_entry_set> t_pdl=(List <d_entry_set>)tViews[rInt].getTag();
                String lastNumber1 = (String)tViews[rInt].getText();
                tViews[rInt].setText(lastNumber);
                tViews[rInt].setTag(pdl);
                lastNumber = lastNumber1;
                pdl = t_pdl;
            }

            if (rInt ==(iVisible-1)){
                rInt=1;
                h.removeCallbacks(showInfo);
                tViews[iVisible-1].setVisibility(View.VISIBLE);
            }
            else {
                rInt++;
                h.postDelayed(showInfo,100);
            }
        }
    };
}