package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;

import kz.regto.database.d_entry_set;

public class MainContainer extends ViewGroup {


    private LinkedList<ChipLogLimit> limitLogList=new LinkedList<ChipLogLimit>();
    private Main main;


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
            this.addView(v, i, v.getLayoutParams());
        }
        main = (Main)ct;
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
                    v.layout(33,33,v.getMeasuredWidth(), this.getMeasuredHeight());
                    break;
                case "RectView":
                    RectView rV = (RectView)v;
                    v.layout(rV.left_X, rV.top_Y, rV.right_X, rV.bottom_Y);
                    break;
            }
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - 33;
        int height = MeasureSpec.getSize(heightMeasureSpec) - 33;

        String className;
        int childCount = this.getChildCount();
        for(int i=0; i<childCount;i++) {
            View v = getChildAt(i);
            className = v.getClass().getName();
            className= className.substring(className.lastIndexOf(".") + 1);
            switch (className){
                case "BoardGrid":
                    int childWidthSpec = MeasureSpec.makeMeasureSpec(width,
                            MeasureSpec.EXACTLY);
                    int childHeightSpec = MeasureSpec.makeMeasureSpec(height,
                            MeasureSpec.EXACTLY);
                    v.measure(childWidthSpec, childHeightSpec);
                    break;
            }
        }

        setMeasuredDimension(width, height);
    }

    public int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        return resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
    }
    public void addCustomView(View Child){
        this.addView(Child);
        this.invalidate();
    }

    //Удаляем все элементы с доски
    public void ClearBoard(){
            boolean doBreak = false;
            while (!doBreak) {
                int childCount = this.getChildCount();
                int i;
                for(i=0; i<childCount; i++) {
                    View currentChild = this.getChildAt(i);
                    // Change ImageView with your disired type view
                    if (currentChild instanceof EntryAnimated) {
                        currentChild.setAnimation(null);
                        this.removeView(currentChild);
                        break;
                    }
                }
                if (i == childCount) {
                    doBreak = true;
                }
            }
        main.db.deleteEntrySetByGameID(main.dGame.getId());
       this.invalidate();
    }

    public void stepBack(){
        d_entry_set dEntrySet = main.db.getLastEntrySet();
        TextView tw = (TextView) this.getChild(dEntrySet.getEntry_id());
        if (tw!=null){
           if (main.db.getGameIdSum(dEntrySet.getGame_id(),dEntrySet.getEntry_id())==dEntrySet.getEntry_value()){
              Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
              rotate_animation.setStartOffset((int) (Math.random() * ((50) + 1)));
              tw.setAnimation(rotate_animation);
              this.removeView(tw);
           }
           else {
              int sum_chk = Integer.parseInt(tw.getText().toString())-1;
              if (sum_chk==0) tw.setText("");
              else tw.setText(""+sum_chk);
           }
           this.invalidate();
            TwoTextViews t2w =  (TwoTextViews)main.findViewById(R.id.CurrentEntry);
            int curB = Integer.parseInt(t2w.getField());
            curB = curB - dEntrySet.getEntry_value();
            t2w.setField(""+curB);
            TwoTextViews t2b =  (TwoTextViews)main.findViewById(R.id.balance);
            int balance = Integer.parseInt(t2b.getField()) + dEntrySet.getEntry_value();;
            t2b.setField("" + balance);
           main.db.deleteEntrySet(dEntrySet.getLog_id());
        }
    }

    public void ClearAllAlfa05() {
        boolean doBreak = false;
        while (!doBreak) {
            int childCount = this.getChildCount();
            int i;
            int ii=0;
            for(i=0; i<childCount; i++) {
                View currentChild = this.getChildAt(i);
                // Change ImageView with your disired type view
                if (currentChild instanceof EntryAnimated && currentChild.getAlpha()==0.7F) {
                    currentChild.setAnimation(null);
                    this.removeView(currentChild);
                    ii++;
                }
            }
            if (ii ==0) doBreak = true;
        }
    }

    public View getChild(int id){
        View rView=null;
        int childCount = this.getChildCount();
        for(int i=0; i<childCount;i++) {
            View vV = getChildAt(i);
            if (vV.getId()==id) {
                rView = vV;
                break;
            }
        }
        return rView;
    }
}
