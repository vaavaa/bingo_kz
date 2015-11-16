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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spt on 03.11.2015.
 */
public class WinBallContainer extends RelativeLayout {

    private int iVisible=1;

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
        //Выставили на показ
        addView(view);
    }

    public void UpdateNewOne(String winNumber){
        if  (iVisible<=9){
            TextView view = (TextView)this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(iVisible))));
            view.setText(winNumber);
            view.setVisibility(View.VISIBLE);
            iVisible++;
        }
        this.invalidate();
    }

    private int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        return resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
    }
}