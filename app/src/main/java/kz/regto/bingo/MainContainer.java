package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import java.util.LinkedList;


/**
 * Created by spt on 23.10.2015.
 */
public class MainContainer extends ViewGroup {

    int ilevelset=1;
    private LinkedList<LogChanges> mLog = new LinkedList<>();

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

    public void setIlevelset(int ilevelset) {
        this.ilevelset = ilevelset;
    }

    public int getIlevelset() {
        return ilevelset;
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

    public int setChildName(View child){
        int iReturn = 0;
        int childCount = this.getChildCount();
        String className;
        for(int i=0; i<childCount;i++) {
            View vV = getChildAt(i);
            className = vV.getClass().getName();
            className= className.substring(className.lastIndexOf(".") + 1);
            switch (className){
                case "EntryAnimated":
                    TextView v = (TextView)getChildAt(i);
                    int idV=v.getId();
                    int animPoint = child.getId();
                    if (idV==animPoint) {
                        //Фишка уже стоит на поле мы повышаем ее статус до заданого или на 1
                        int level=v.getBackground().getLevel();
                        if (level ==ilevelset) {
                            v.setText("2");
                        }
                        else level =ilevelset;
                        if (level>4) level=4;

                        mLog.add(new LogChanges(v.getId(), v.getBackground().getLevel(),level));
                        v.setBackground(ContextCompat.getDrawable(getContext(), getResourceByID("drawable",EntryName(level))));
                        v.getBackground().setLevel(level);

                        iReturn=level;
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
                if (iReturn==0) {
                    child.setBackground(ContextCompat.getDrawable(getContext(), getResourceByID("drawable", EntryName(ilevelset))));
                    child.getBackground().setLevel(ilevelset);
                    child.bringToFront();
                    this.addView(child);
                    mLog.add(new LogChanges(child.getId(),0,ilevelset));
                    invalidate();
                    iReturn=ilevelset;
                }
                break;
        }

        return iReturn;
    }
    private String EntryName(int level){
        String nameDrawable  = "card_entry" + Integer.toString(level);
        return nameDrawable;
    }

    //блядскаяпроцедура которая все же удалила
    public void ClearBoard(){
            boolean doBreak = false;
            while (!doBreak) {
                int childCount = this.getChildCount();
                int i;
                for(i=0; i<childCount; i++) {
                    View currentChild = this.getChildAt(i);
                    // Change ImageView with your disired type view
                    if (currentChild instanceof EntryAnimated) {

                        Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                        currentChild.setAnimation(rotate_animation);

                        this.removeView(currentChild);
                        break;
                    }
                }

                if (i == childCount) {
                    doBreak = true;
                }
            }
       this.invalidate();
    }

    public void stepBack(){
        LogChanges operatedLog;
        if (mLog.size()>0){
            operatedLog=mLog.get(mLog.size()-1);
            if (operatedLog.getpLevel()==0)
                for(int i=0; i<this.getChildCount();i++){
                    if (this.getChildAt(i).getId()==operatedLog.getId_object()){

                        Animation rotate_animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                        rotate_animation.setStartOffset((int) (Math.random() * ((100) + 1)));
                        this.getChildAt(i).setAnimation(rotate_animation);

                        this.removeView(this.getChildAt(i));
                        break;
                    }
                }
            else {
                for(int i=0; i<this.getChildCount();i++)
                    if (this.getChildAt(i).getId()==operatedLog.getId_object()){
                        this.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(),
                                getResourceByID("drawable", EntryName(operatedLog.getpLevel()))));
                        this.getChildAt(i).getBackground().setLevel(operatedLog.getpLevel());
                            }
                    }
            mLog.remove(operatedLog);
            this.invalidate();
        }

    }

    private class LogChanges{
        int id_object =0;
        int pLevel =0;
        int cLevel=0;

        LogChanges(int id, int p, int c){
            id_object =id;
            pLevel =p;
            cLevel=c;
        }

        public int getId_object() {
            return id_object;
        }

        public void setId_object(int id_object) {
            this.id_object = id_object;
        }

        public int getcLevel() {
            return cLevel;
        }

        public int getpLevel() {
            return pLevel;
        }

        public void setcLevel(int cLevel) {
            this.cLevel = cLevel;
        }

        public void setpLevel(int pLevel) {
            this.pLevel = pLevel;
        }
    }
}
