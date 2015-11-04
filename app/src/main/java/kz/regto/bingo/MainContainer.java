package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by spt on 23.10.2015.
 */
public class MainContainer extends ViewGroup {
    public MainContainer(Context context) {
        super(context);
        init(context);
    }

    public MainContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public MainContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(final Context ct) {
        // Generate bitmap used for background
        this.setBackground(ContextCompat.getDrawable(ct, getResourceByID("drawable", "empty_board")));

        int childCount = this.getChildCount();
        for(int i=0; i<childCount;i++) {
            View v = getChildAt(i);
            this.addView(v,i,v.getLayoutParams());
            Log.v("1",Integer.toString(v.getId()));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
          String className;
        int childCount = this.getChildCount();
        for(int i=0; i<childCount;i++) {
            View v = getChildAt(i);
            className = v.getClass().getName();
            className= className.substring(className.lastIndexOf(".") + 1);
            switch (className){
                case "EntryAnimated":
                    EntryAnimated vV = (EntryAnimated)v;
                    v.layout(vV.left_X,vV.top_Y,vV.right_X,vV.bottom_Y);
                    break;
                case "BoardGrid":
                    v.layout(0,0,this.getWidth(),this.getHeight());
                    break;
                case "RectView":
                    RectView rV = (RectView)v;
                    v.layout(rV.left_X,rV.top_Y,rV.right_X,rV.bottom_Y);
                    break;
            }
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        final int resourceId = resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
        return resourceId;
    }

    public boolean setChildName(View child){
        boolean mReturn = false;
        int childCount = this.getChildCount();
        String className;
        for(int i=0; i<childCount;i++) {
            View vV = getChildAt(i);
            className = vV.getClass().getName();
            className= className.substring(className.lastIndexOf(".") + 1);
            switch (className){
                case "EntryAnimated":
                    View v = getChildAt(i);
                    int idV=v.getId();
                    int animPoint = child.getId();
                    if (idV==animPoint) {
                        mReturn=true;
                        int level=v.getBackground().getLevel()+1;
                        if (level>8) level=8;
                        String nameDrawable  = "card_entry" + Integer.toString(level);
                        v.setBackground(ContextCompat.getDrawable(getContext(), getResourceByID("drawable", nameDrawable)));
                        v.getBackground().setLevel(level);
                        v.bringToFront();
                        v.invalidate();
                        invalidate();
                        break;
                    }
                    break;
            }
        }
        className = child.getClass().getName();
        className= className.substring(className.lastIndexOf(".") + 1);
        switch (className){
            case "EntryAnimated":
                if (!mReturn) {
                    child.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.card_entry1));
                    child.getBackground().setLevel(1);
                    child.bringToFront();
                    this.addView(child);
                    invalidate();
                }
                break;
        }

        return mReturn;
    }
}
