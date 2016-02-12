package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


import kz.regto.database.d_entry_set;

public class WinBallContainer extends RelativeLayout {

    private int state;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_UNSELECTED = 0;
    //Вот это делает все перемещения сама, - вещь!
    BlockingQueue queue = new ArrayBlockingQueue<>(10);


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
        state = STATE_UNSELECTED;
    }

    public void setAllSelected_false() {
        for (int i = 1; i <= 10; i++) {
            TextView view = (TextView) this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(i))));
            View v = this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(i)).concat("c")));
            v.setVisibility(INVISIBLE);
            view.setSelected(false);
            //view.setBackgroundResource(R.drawable.round_shape_filled);;
            state = STATE_UNSELECTED;
        }
    }

    public void setAll_lock(boolean iLock) {
        for (int i = 1; i <= 10; i++) {
            TextView view = (TextView) this.findViewById(
                    getResourceByID("id", "f".concat(Integer.toString(i))));
            view.setEnabled(!iLock);
        }
    }

    public void setAll_Visible(int[] iV) {
        if (queue.remainingCapacity() > 0) {
            for (int i = 1; i <= 10; i++) {
                TextView view = (TextView) this.findViewById(
                        getResourceByID("id", "f".concat(Integer.toString(i))));

                LineBalls lb = new LineBalls();
                lb.setBackground(R.drawable.round_shape);
                lb.setNumber("" + iV[i - 1]);
                lb.setTextColor(view.getCurrentTextColor());
                view.setBackgroundResource(R.drawable.round_shape);
                view.setVisibility(VISIBLE);
                view.setText("" + iV[i - 1]);
                queue.offer(lb);
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        state = newState;
    }

    //Нужно как то с логами прописать что бы найти ошибку с повторением шаров
    public void UpdateNewOne(String winNumber, List<d_entry_set> dl, Main mn) {
        try {
            queue.take();

            LineBalls lb = new LineBalls();
            lb.setBackground(R.drawable.round_shape_filled);
            lb.setNumber(winNumber);
            lb.setTextColor(Color.BLACK);

            queue.offer(lb);

            Iterator iterator = queue.iterator();
            for (int i = 1; i <= 10; i++) {
                TextView view = (TextView) this.findViewById(
                        getResourceByID("id", "f".concat(Integer.toString(i))));
                LineBalls lb_i = (LineBalls)iterator.next();
                view.setText(lb_i.getNumber());
                view.setBackgroundResource(lb_i.getBackground());
                view.setTextColor(lb_i.getTextColor());
            }
            this.invalidate();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getResourceByID(String ResType, String ResName) {
        Resources resources = getContext().getResources();
        return resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
    }

}