package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by spt on 26.10.2015.
 */
public class RectView extends TextView
{
    public int left_X=0;
    public int top_Y=0;
    public int right_X=0;
    public int bottom_Y=0;

    int rectColor=0;

    public RectView(Context context){
        super(context);
        init(context);
    }

    public RectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public RectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context ct) {
        this.setBackgroundResource(R.drawable.rect_view);
        //this.invalidate();
//        if(rectColor==0) rectColor=this.getResourceByID("color", "colorHL");
//        //Собственный метод
//        this.setBackgroundColor(ContextCompat.getColor(ct,rectColor));
//        this.getBackground().setAlpha(45);
    }

    public void RectArea(int X, int Y, int rX, int rY) {
        this.left_X = X;
        this.top_Y = Y;
        this.right_X = rX;
        this.bottom_Y = rY;
        if (rectColor!=0){
            this.setBackgroundColor(ContextCompat.getColor(this.getContext(),rectColor));
            this.getBackground().setAlpha(35);
            this.invalidate();
        }
    }

    public int getRectColor() {
        return rectColor;
    }

    public void setRectColor(int rctColor){
        rectColor=rctColor;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension(124, 124);
    }

    public int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        final int resourceId = resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
        return resourceId;
    }
}
