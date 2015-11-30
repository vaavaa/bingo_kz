package kz.regto.bingo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class Lock extends FrameLayout {

    private float y1,y2;
    static final int MIN_DISTANCE = 150;

    public Lock(Context context){
        super(context);
        init(context);
    }

    public Lock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public Lock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context ct) {
        FrameLayout Relative_Lock = (FrameLayout)inflate(getContext(), R.layout.lockscreen, null);
        ImageButton ib = (ImageButton) Relative_Lock.findViewById(R.id.ib);
        this.addView(Relative_Lock);
    }
    @Override

    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getY();
                handled=true;
                break;
            case MotionEvent.ACTION_UP:
                y2 = event.getY();
                float deltaX = y2 - y1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                   Main prnt = (Main)getContext();
                    prnt.screen_lock_starting_procedure();
                }
                handled=true;
                break;
            default:
                // do nothing
                break;
        }
        return super.onTouchEvent(event) || handled;
    }
}
