package kz.regto.bingo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class EntryAnimated extends TextView{

    public int left_X=0;
    public int top_Y=0;
    public int right_X=0;
    public int bottom_Y=0;



    public EntryAnimated(Context context){
        super(context);
        init(context);
    }

    public EntryAnimated(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public EntryAnimated(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(final Context ct) {
        this.setGravity(Gravity.RIGHT);
        this.setTypeface(null, Typeface.BOLD);
    }

    public void animation(){
        Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotate_animation.setStartOffset((int) (Math.random() * ((100) + 1)));
        this.setAnimation(rotate_animation);
    }

    public void RectArea(int X, int Y, int rX, int rY) {
        this.left_X = X;
        this.top_Y = Y;
        this.right_X = rX;
        this.bottom_Y = rY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.animation();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension(124, 124);
    }
}
