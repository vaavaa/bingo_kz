package kz.regto.bingo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by spt on 12.10.2015.
 */
public class BoardGrid extends View {
    private Paint paint;
    private Paint bg_paint;
    private List<BoardGridEvents> listeners = new ArrayList<BoardGridEvents>();
    private LinkedList<ChipLog> mainLogList=new LinkedList<ChipLog>();
    private LinkedList<ChipLogLimit> limitLogList=new LinkedList<ChipLogLimit>();

    private boolean WasEntrySet=true;


    int left_X=0;
    int top_Y=0;
    int right_X=0;
    int bottom_Y=0;

    //Ни чего не нажато по X
    int x_pushed = 0;
    //Ни чего не нажато по Y;
    int y_pushed = 0;
    //какая ставка
    int ilevel=1;

    //Определеяем какое число нажато
    int x_pushed_number=-1;
    int y_pushed_number=-1;

    Rect[] mRect=new Rect[50];
    Rect[] mRectw = new Rect[50];
    Rect[] mRectC = new Rect[50];
    Rect[] mRectCw = new Rect[50];

    RectView mRecOperate;
    RectView mRecOperate1;
    Rect[] mRecOperateTemp=new Rect[2];

    boolean mRecOperateIsActive=false;

    boolean board_blocked=false;

    private int ii =0;
    private int iiw = 0;
    private int iic = 0;
    private int iicw = 0;

    private int column_light=0;
    private int row_light=0;
    private int correlation_light=0;



    private static final int GRID_STEP = 15;
    private static final int RADIUS_LIMIT=36;
    private static final int LINE_STEP=GRID_STEP*2;
    private static final int LINE_WIDTH=3;




    public BoardGrid(Context context) {
        super(context);
        init(context);
    }

    public BoardGrid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public BoardGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(final Context ct) {
        // Generate bitmap used for background
        paint= new Paint();
        bg_paint = new Paint();

        paint.setColor(Color.rgb(220, 220, 220));
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(60);


        for (int i = 0; i <50 ; i++) {
            mRect[i] = new Rect();
            mRectw[i] = new Rect();
            mRectC[i]= new Rect();
            mRectCw[i]= new Rect();
        }

        mRecOperateTemp[0]=new Rect();
        mRecOperateTemp[1]=new Rect();

        bg_paint.setColor(Color.rgb(220, 220, 200));

        this.listeners.add((Main) ct);

    }

    private void MakeTouchedRectangleArea(Canvas canvas){

        ii=1; iiw=1; iic=0; iicw=0;
        int correlate = (int)(14)/LINE_WIDTH;
        int columns;
        int rows;
        if (canvas.getHeight()<canvas.getWidth()) {
            columns = (int) canvas.getWidth() / 14;
            rows = (int) canvas.getHeight() / 3;
            column_light=columns;
            row_light=rows;
            correlation_light=correlate;

            //Рисуем вертикальные границы с некоторыми ограничениями всего столбцов 12,
            // последний 13ый столбец не нужен.
            for (int i = 1; i <= 12; i++) {
                if (i<4) mRect[ii].left = i * columns - GRID_STEP+correlate;
                else mRect[ii].left = i * columns - GRID_STEP;
                mRect[ii].top = GRID_STEP;
                if (i<4) mRect[ii].right = i * columns + GRID_STEP+correlate;
                else mRect[ii].right = i * columns + GRID_STEP;
                mRect[ii].bottom = 3 * rows - GRID_STEP;
                //canvas.drawRect(mRect[ii], paint);
                ii++;
            }
            //Рисуем стобцы ячеек
            for (int i = 0; i <= 13; i++) {
                if (i==0) mRectC[iic].left = i * columns+correlate;
                else mRectC[iic].left = (i * columns) +correlate;
                mRectC[iic].top = 0;
                if (i==13) mRectC[iic].right = i * columns + columns+correlate;
                else mRectC[iic].right = (i+1) * columns+correlate;
                if (i==0) mRectC[iic].bottom = 3 * rows+correlate;
                else mRectC[iic].bottom = 3 * rows + GRID_STEP;
                //canvas.drawRect(mRectC[iic], paint);
                iic++;
            }

            //Рисуем строки границ ячеек игрового поля
            for (int i = 1; i <= 3; i++) {
                if (i==3) mRectw[iiw].left = columns + GRID_STEP;
                else mRectw[iiw].left = columns-correlate;
                mRectw[iiw].top = i * rows - GRID_STEP;
                mRectw[iiw].right = 13*columns - GRID_STEP;
                mRectw[iiw].bottom = rows * i + GRID_STEP;
                //canvas.drawRect(mRectw[iiw], paint);
                iiw++;
            }

            //Рисуем строки игрового поля
            for (int i = 0; i <= 2; i++) {
                mRectCw[iicw].left = columns;
                mRectCw[iicw].top = i * rows+correlate;
                mRectCw[iicw].right = 14*columns;
                mRectCw[iicw].bottom = rows * i +rows-correlate;
                //canvas.drawRect(mRectCw[iicw], bg_paint);
                iicw++;
            }

        }
    }

    public LinkedList<ChipLog> getMainLogList(){
            return mainLogList;
    }

    public void setMainLogList(LinkedList<ChipLog> smainLogList){
        mainLogList=smainLogList;
    }

    public LinkedList<ChipLogLimit> getLimitLogList(){
        return limitLogList;
    }
    public void setLimitLogList(LinkedList<ChipLogLimit> slimitLogList){
        limitLogList=slimitLogList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Создаем прямоугольники что бы знать куда и как было нажатие
        MakeTouchedRectangleArea(canvas);
    }


    //Получаем пересечение прямоугольников
    private void EntryGetsPoint(int xTouch_new, int yTouch_new){
        EntryAnimated touchedView;
        MainContainer main_container_parent;

        touchedView=new EntryAnimated(this.getContext());
        touchedView.RectArea(xTouch_new - (int) (RADIUS_LIMIT / 2), yTouch_new - (int) (RADIUS_LIMIT / 2), xTouch_new + (int) (RADIUS_LIMIT / 2), yTouch_new + (int) (RADIUS_LIMIT / 2));
        //Object now has name for identification
        int idV;
        idV = Integer.parseInt(Integer.toString(xTouch_new)+Integer.toString(yTouch_new));
        touchedView.setId(idV);
        main_container_parent = (MainContainer)BoardGrid.this.getParent();
        ilevel = main_container_parent.setChildName(touchedView, this);
        for (BoardGridEvents hl : listeners) hl.entrySet(ilevel);
    }

    private void copyToMainStore(LinkedList<ChipLog> nLogList){
        if (WasEntrySet)
            for (ChipLog cl: nLogList) mainLogList.add(cl);
    }

    public void setBoard_blocked(Boolean locktheboard){
        board_blocked = locktheboard;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;
        limitLogList.clear();

        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();
        int xTouch_new;
        int yTouch_new;

        if (!board_blocked){

            // get touch event coordinates and make transparent from it
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    xTouch = (int) event.getX(0);
                    yTouch = (int) event.getY(0);

                    xTouch_new = getXCrossed(xTouch, yTouch);
                    yTouch_new = getYCrossed(xTouch, yTouch);

                    if (xTouch_new>0 && yTouch_new>0){
                        int idV;
                        idV = Integer.parseInt(Integer.toString(xTouch_new)+Integer.toString(yTouch_new));

                        limitLogList.add(new ChipLogLimit(ilevel,idV));
                        EntryGetsPoint(xTouch_new, yTouch_new);

                        copyToMainStore(getPushedNumber(xTouch_new, yTouch_new));
                        GetUserTouch(xTouch_new, yTouch_new);

                        invalidate();
                    }
                    handled = true;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:

                    // It secondary pointers, so obtain their ids and check
                    pointerId = event.getPointerId(actionIndex);
                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);
                    xTouch_new = getXCrossed(xTouch, yTouch);
                    yTouch_new = getYCrossed(xTouch, yTouch);
                    if (xTouch_new>0 && yTouch_new>0){
                        int idV;
                        idV = Integer.parseInt(Integer.toString(xTouch_new) + Integer.toString(yTouch_new));


                        limitLogList.add(new ChipLogLimit(ilevel,idV));
                        EntryGetsPoint(xTouch_new,yTouch_new);
                        copyToMainStore(getPushedNumber(xTouch_new,yTouch_new));

                        invalidate();
                    }
                    handled = true;
                    break;

                case MotionEvent.ACTION_MOVE:
    //                final int pointerCount = event.getPointerCount();
    //                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
    //                    // Some pointer has moved, search it by pointer id
    //
    //                    xTouch = (int) event.getX(actionIndex);
    //                    yTouch = (int) event.getY(actionIndex);
    //
    //                    xTouch_new = getXCrossed(xTouch, yTouch);
    //                    yTouch_new = getYCrossed(xTouch, yTouch);
    //
    //                    if (xTouch_new!=moveXpoint[actionIndex]&&yTouch_new!=moveYpoint[actionIndex]){
    //                        moveXpoint[actionIndex]=xTouch_new;
    //                        moveYpoint[actionIndex]=yTouch_new;
    //                        touchedView=new AnimatedPoint(this.getContext());
    //                        touchedView.RectArea(xTouch_new - (int) (RADIUS_LIMIT / 2), yTouch_new - (int) (RADIUS_LIMIT / 2), xTouch_new + (int) (RADIUS_LIMIT / 2), yTouch_new + (int) (RADIUS_LIMIT / 2));
    //                        //Object now has name for an identification
    //                        touchedView.name =Integer.toString(xTouch_new - (int) (RADIUS_LIMIT / 2)) + Integer.toString(yTouch_new - (int) (RADIUS_LIMIT / 2))+Integer.toString(xTouch_new + (int) (RADIUS_LIMIT / 2))+Integer.toString(yTouch_new + (int) (RADIUS_LIMIT / 2));
    //                        main_container_parent = (main_container)grid.this.getParent();
    //                        main_container_parent.setName(touchedView);
    //                    }
    //                }
    //                invalidate();
                    handled = true;
                    break;

                case MotionEvent.ACTION_UP:
                    if (mRecOperateIsActive){
                        mRecOperate.setVisibility(View.GONE);
                        mRecOperate1.setVisibility(View.GONE);
                        mRecOperateIsActive=false;
                        invalidate();
                    }
                    handled = true;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // not general pointer was up

                    pointerId = event.getPointerId(actionIndex);
                    invalidate();
                    handled = true;
                    break;

                case MotionEvent.ACTION_CANCEL:
                    handled = true;
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        return super.onTouchEvent(event) || handled;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.left_X=0;
        this.top_Y=0;
        this.right_X=widthMeasureSpec;
        this.bottom_Y=heightMeasureSpec;
    }

    private int[] getYXCrossed_block(int xTouch,int yTouch){
        int[]result=new int[2];
        result[0]=0;
        result[1]=0;

//        //This is three buttons
//            for (int i = 0; i <= 2; i++)
//                if (mLastRow3[i].contains(xTouch, yTouch)) {
//                    result[1] = mLastRow3[i].centerY();
//                    mRecOperateTemp[1]=mLastRow3[i];
//                    y_pushed = 5;
//                    break;
//                }
        //Нажата zero
            if (mRectC[0].contains(xTouch, yTouch)){
                result[1] = mRectC[0].centerY();
                mRecOperateTemp[1]=mRectC[0];
                y_pushed = 5;
            }

//        //Three buttons
//        if (result[0] == 0)
//            for (int i = 0; i <= 2; i++)
//                if (mLastRow3[i].contains(xTouch, yTouch)) {
//                    result[0] = mLastRow3[i].centerX();
//                    mRecOperateTemp[0]=mLastRow3[i];
//                    x_pushed=5;
//                    break;
//                }
        //Нажата zero
            if (mRectC[0].contains(xTouch, yTouch)) {
                result[0] = mRectC[0].centerX();
                mRecOperateTemp[0]=mRectC[0];
                x_pushed=5;
            }

        return result;
    }

    private int getYCrossed(int xTouch,int yTouch){

        int result=0;
        int[]resultB=new int[2];

        resultB= getYXCrossed_block(xTouch,yTouch);

        if ((resultB[0]==0)&&(resultB[1]==0)) {
            //Those are column borders
            for (int i = 1; i <= iiw; i++) {
                if (mRectw[i].contains(xTouch, yTouch)) {
                    result = mRectw[i].centerY();
                    mRecOperateTemp[1] = mRectw[i];
                    y_pushed = 1;
                    y_pushed_number=i;
                    break;
                }
            }
            //Those are just column
            if (result == 0)
                for (int i = 0; i <= iicw; i++) {
                    if (mRectCw[i].contains(xTouch, yTouch)) {
                        result = mRectCw[i].centerY();
                        mRecOperateTemp[1] = mRectCw[i];
                        y_pushed = 4;
                        y_pushed_number=i+1;
                        break;
                    }
                }
        }
        else result = resultB[1];
        return result;
    }

    private int getXCrossed(int xTouch,int yTouch) {
        int result = 0;
        int[]resultB;

        resultB= getYXCrossed_block(xTouch,yTouch);
        if ((resultB[0]==0)&&(resultB[1]==0)) {
        //borders
            for (int i = 1; i <= ii; i++)
                    if (mRect[i].contains(xTouch, yTouch)) {
                    result = mRect[i].centerX();
                    mRecOperateTemp[0]=mRect[i];
                    x_pushed=1;
                    x_pushed_number=i;
                break;
            }
        //Rows
        if (result == 0)
            for (int i = 1; i <= iic; i++) {
                if (mRectC[i].contains(xTouch, yTouch)) {
                    result = mRectC[i].centerX();
                    mRecOperateTemp[0]=mRectC[i];
                    x_pushed=4;
                    x_pushed_number=i;
                    break;
                }
            }

        }
        else result = resultB[0];
        return result;
    }

    private void GetUserTouch(int x,int y){
    // BORDERS = 1 - пересчение двух границ
    // BORDER_COLUMN=2 - пересечение границы и столбца
    // BORDER_ROW=3 - пересечение границы и строки
    // ROW_COLUMN=4 - пересечение столбца и строки
    // SPECIFIC_BUTTON=5 - нажата специфичная область
        int idV;
        idV = Integer.parseInt(Integer.toString(x)+Integer.toString(y));

        MainContainer main_container_parent;
        main_container_parent = (MainContainer)BoardGrid.this.getParent();
        mRecOperate =(RectView)main_container_parent.getChild(R.id.zRectView);
        mRecOperate1=(RectView)main_container_parent.getChild(R.id.xRectView);

        //pushed color
        mRecOperate.setRectColor(this.getResourceByID("color", "e".concat(Integer.toString(ilevel))));
        mRecOperate1.setRectColor(this.getResourceByID("color", "e".concat(Integer.toString(ilevel))));

        if ((y_pushed==5)&&(x_pushed==5)) {
            mRecOperate.RectArea(mRecOperateTemp[0].left,
                    0, mRecOperateTemp[0].right, row_light * 3 + correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsActive=true;
        }
        //Ячейки
        if ((y_pushed==4)&&(x_pushed==4)){
            if (mRectC[13].contains(x,y))
                mRecOperate.RectArea(mRectC[0].right,
                        y-row_light/2,
                        mRectC[13].left,
                        y+row_light/2);
            else  mRecOperate.RectArea((int)(x-column_light/2)-correlation_light,
                    ((int)y-row_light/2),
                    (x+column_light/2),
                    ((int)y+row_light/2));
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsActive=true;
        }
        //Пересечение границ
        if ((y_pushed==1)&&(x_pushed==1)){
            if (mRectw[iicw].contains(x, y))
                mRecOperate.RectArea(x-column_light-correlation_light,
                        y-row_light-correlation_light,
                        x+column_light+correlation_light,
                        y+correlation_light);
            else
            mRecOperate.RectArea((int)(x-column_light)-correlation_light,
                    ((int)y-row_light)-correlation_light,
                    (x+column_light)+correlation_light,
                    ((int)y+row_light)+correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsActive=true;
        }
        //нажато пересечение границы по y и строка
        if ((y_pushed==4)&&(x_pushed==1)) {
            //Если попадаем на первую границу
            if (mRect[1].contains(x, y)){
                mRecOperate.RectArea(x,
                        y - row_light / 2,
                        (x + column_light) ,
                        y + row_light / 2);
                mRecOperate1.RectArea(mRectC[0].left,
                        mRectC[0].top,
                        mRectC[0].right,
                        mRectC[0].bottom);
                mRecOperate1.setVisibility(View.VISIBLE);
                }
            else  mRecOperate.RectArea((x-column_light),
                    ((int)y-row_light/2),
                    (x+column_light),
                    ((int)y+row_light/2));
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsActive=true;
        }

        if ((y_pushed==1)&&(x_pushed==4)){
            //Если попадаем на первую границу
            if (mRectw[iicw].contains(x, y))
                mRecOperate.RectArea((x-column_light/2),
                        correlation_light,
                        (x+column_light/2),
                        y+correlation_light);
            else mRecOperate.RectArea((int)(x-column_light/2),
                    ((int)y-row_light),
                    (x+column_light/2),
                    ((int)y+row_light));
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsActive=true;
        }

    }
    public int getResourceByID(String ResType,String ResName) {
        Resources resources = getContext().getResources();
        return resources.getIdentifier(ResName, ResType,
                getContext().getPackageName());
    }

    public LinkedList<ChipLog> getPushedNumber(int x,int y){
        LinkedList<ChipLog> chLog = new LinkedList<>();
        int idV;
        idV = Integer.parseInt(Integer.toString(x)+Integer.toString(y));
        //Zero
        if ((y_pushed==5)&&(x_pushed==5)) {
            chLog.add(new ChipLog(0,ilevel,idV,1));
        }
        //Ячейки
        if ((y_pushed==4)&&(x_pushed==4)){
            switch (x_pushed_number){
                case 1:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(3,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(2,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(1,ilevel,idV,1));
                            break;
                    }
                    break;
                case 2:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(6,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(5,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(4,ilevel,idV,1));
                            break;
                    }
                    break;
                case 3:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(9,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(8,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(7,ilevel,idV,1));
                            break;
                    }
                    break;
                case 4:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(12,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(11,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(10,ilevel,idV,1));
                            break;
                    }
                    break;
                case 5:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(15,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(14,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(13,ilevel,idV,1));
                            break;
                    }
                    break;
                case 6:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(18,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(17,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(16,ilevel,idV,1));
                            break;
                    }
                    break;
                case 7:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(21,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(20,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(19,ilevel,idV,1));
                            break;
                    }
                    break;
                case 8:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(24,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(23,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(22,ilevel,idV,1));
                            break;
                    }
                    break;
                case 9:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(27,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(26,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(25,ilevel,idV,1));
                            break;
                    }
                    break;
                case 10:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(30,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(29,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(28,ilevel,idV,1));
                            break;
                    }
                    break;
                case 11:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(33,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(32,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(31,ilevel,idV,1));
                            break;
                    }
                    break;
                case 12:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(36,ilevel,idV,1));
                            break;
                        case 2:
                            chLog.add(new ChipLog(35,ilevel,idV,1));
                            break;
                        case 3:
                            chLog.add(new ChipLog(34,ilevel,idV,1));
                            break;
                    }
                    break;
            }
        }
        //Пересечение двух границ
        if ((y_pushed==1)&&(x_pushed==1)){
            switch(x_pushed_number){
                case 1:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(0,ilevel,idV,3));
                            chLog.add(new ChipLog(2,ilevel,idV,3));
                            chLog.add(new ChipLog(3,ilevel,idV,3));
                            break;
                        case 2:
                            chLog.add(new ChipLog(0,ilevel,idV,3));
                            chLog.add(new ChipLog(2,ilevel,idV,3));
                            chLog.add(new ChipLog(1,ilevel,idV,3));
                            break;
                    }
                    break;
                case 2:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(3,ilevel,idV,4));
                            chLog.add(new ChipLog(6,ilevel,idV,4));
                            chLog.add(new ChipLog(2,ilevel,idV,4));
                            chLog.add(new ChipLog(5,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(2,ilevel,idV,4));
                            chLog.add(new ChipLog(5,ilevel,idV,4));
                            chLog.add(new ChipLog(1,ilevel,idV,4));
                            chLog.add(new ChipLog(4,ilevel,idV,4));
                            break;
                    }
                    break;
                case 3:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(6,ilevel,idV,4));
                            chLog.add(new ChipLog(9,ilevel,idV,4));
                            chLog.add(new ChipLog(5,ilevel,idV,4));
                            chLog.add(new ChipLog(8,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(5,ilevel,idV,4));
                            chLog.add(new ChipLog(8,ilevel,idV,4));
                            chLog.add(new ChipLog(4,ilevel,idV,4));
                            chLog.add(new ChipLog(7,ilevel,idV,4));
                            break;
                    }
                    break;
                case 4:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(9,ilevel,idV,4));
                            chLog.add(new ChipLog(12,ilevel,idV,4));
                            chLog.add(new ChipLog(8,ilevel,idV,4));
                            chLog.add(new ChipLog(11,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(8,ilevel,idV,4));
                            chLog.add(new ChipLog(11,ilevel,idV,4));
                            chLog.add(new ChipLog(7,ilevel,idV,4));
                            chLog.add(new ChipLog(10,ilevel,idV,4));
                            break;
                    }
                    break;
                case 5:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(12,ilevel,idV,4));
                            chLog.add(new ChipLog(15,ilevel,idV,4));
                            chLog.add(new ChipLog(11,ilevel,idV,4));
                            chLog.add(new ChipLog(14,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(11,ilevel,idV,4));
                            chLog.add(new ChipLog(14,ilevel,idV,4));
                            chLog.add(new ChipLog(10,ilevel,idV,4));
                            chLog.add(new ChipLog(13,ilevel,idV,4));
                            break;
                    }
                    break;
                case 6:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(15,ilevel,idV,4));
                            chLog.add(new ChipLog(18,ilevel,idV,4));
                            chLog.add(new ChipLog(14,ilevel,idV,4));
                            chLog.add(new ChipLog(17,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(14,ilevel,idV,4));
                            chLog.add(new ChipLog(17,ilevel,idV,4));
                            chLog.add(new ChipLog(13,ilevel,idV,4));
                            chLog.add(new ChipLog(16,ilevel,idV,4));
                            break;
                    }
                    break;
                case 7:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(18,ilevel,idV,4));
                            chLog.add(new ChipLog(21,ilevel,idV,4));
                            chLog.add(new ChipLog(17,ilevel,idV,4));
                            chLog.add(new ChipLog(20,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(17,ilevel,idV,4));
                            chLog.add(new ChipLog(20,ilevel,idV,4));
                            chLog.add(new ChipLog(16,ilevel,idV,4));
                            chLog.add(new ChipLog(19,ilevel,idV,4));
                            break;
                    }
                    break;
                case 8:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(21,ilevel,idV,4));
                            chLog.add(new ChipLog(24,ilevel,idV,4));
                            chLog.add(new ChipLog(20,ilevel,idV,4));
                            chLog.add(new ChipLog(23,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(20,ilevel,idV,4));
                            chLog.add(new ChipLog(23,ilevel,idV,4));
                            chLog.add(new ChipLog(19,ilevel,idV,4));
                            chLog.add(new ChipLog(22,ilevel,idV,4));
                            break;
                    }
                    break;
                case 9:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(24,ilevel,idV,4));
                            chLog.add(new ChipLog(27,ilevel,idV,4));
                            chLog.add(new ChipLog(23,ilevel,idV,4));
                            chLog.add(new ChipLog(26,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(23,ilevel,idV,4));
                            chLog.add(new ChipLog(26,ilevel,idV,4));
                            chLog.add(new ChipLog(22,ilevel,idV,4));
                            chLog.add(new ChipLog(25,ilevel,idV,4));
                            break;
                    }
                    break;
                case 10:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(27,ilevel,idV,4));
                            chLog.add(new ChipLog(30,ilevel,idV,4));
                            chLog.add(new ChipLog(26,ilevel,idV,4));
                            chLog.add(new ChipLog(29,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(26,ilevel,idV,4));
                            chLog.add(new ChipLog(29,ilevel,idV,4));
                            chLog.add(new ChipLog(25,ilevel,idV,4));
                            chLog.add(new ChipLog(28,ilevel,idV,4));
                            break;
                    }
                    break;
                case 11:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(30,ilevel,idV,4));
                            chLog.add(new ChipLog(33,ilevel,idV,4));
                            chLog.add(new ChipLog(29,ilevel,idV,4));
                            chLog.add(new ChipLog(32,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(29,ilevel,idV,4));
                            chLog.add(new ChipLog(32,ilevel,idV,4));
                            chLog.add(new ChipLog(28,ilevel,idV,4));
                            chLog.add(new ChipLog(31,ilevel,idV,4));
                            break;
                    }
                    break;
                case 12:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(33,ilevel,idV,4));
                            chLog.add(new ChipLog(36,ilevel,idV,4));
                            chLog.add(new ChipLog(32,ilevel,idV,4));
                            chLog.add(new ChipLog(35,ilevel,idV,4));
                            break;
                        case 2:
                            chLog.add(new ChipLog(32,ilevel,idV,4));
                            chLog.add(new ChipLog(35,ilevel,idV,4));
                            chLog.add(new ChipLog(31,ilevel,idV,4));
                            chLog.add(new ChipLog(34,ilevel,idV,4));
                            break;
                    }
                    break;
            }
        }
        //Пересечение границы и столбца
        if((x_pushed ==4) && (y_pushed==1)){
            switch (x_pushed_number){
                case 1:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(3,ilevel,idV,2));
                            chLog.add(new ChipLog(2,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(2,ilevel,idV,2));
                            chLog.add(new ChipLog(1,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(3,ilevel,idV,3));
                            chLog.add(new ChipLog(2,ilevel,idV,3));
                            chLog.add(new ChipLog(1,ilevel,idV,3));
                            break;
                    }
                    break;
                case 2:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(6,ilevel,idV,2));
                            chLog.add(new ChipLog(5,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(5,ilevel,idV,2));
                            chLog.add(new ChipLog(4,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(4,ilevel,idV,3));
                            chLog.add(new ChipLog(5,ilevel,idV,3));
                            chLog.add(new ChipLog(6,ilevel,idV,3));
                            break;
                    }
                    break;
                case 3:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(9,ilevel,idV,2));
                            chLog.add(new ChipLog(8,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(8,ilevel,idV,2));
                            chLog.add(new ChipLog(7,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(7,ilevel,idV,3));
                            chLog.add(new ChipLog(8,ilevel,idV,3));
                            chLog.add(new ChipLog(9,ilevel,idV,3));
                            break;
                    }
                    break;
                case 4:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(12,ilevel,idV,2));
                            chLog.add(new ChipLog(11,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(11,ilevel,idV,2));
                            chLog.add(new ChipLog(10,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(10,ilevel,idV,3));
                            chLog.add(new ChipLog(11,ilevel,idV,3));
                            chLog.add(new ChipLog(12,ilevel,idV,3));
                            break;
                    }
                    break;
                case 5:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(15,ilevel,idV,2));
                            chLog.add(new ChipLog(14,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(14,ilevel,idV,2));
                            chLog.add(new ChipLog(13,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(13,ilevel,idV,3));
                            chLog.add(new ChipLog(14,ilevel,idV,3));
                            chLog.add(new ChipLog(15,ilevel,idV,3));
                            break;
                    }
                    break;
                case 6:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(18,ilevel,idV,2));
                            chLog.add(new ChipLog(17,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(17,ilevel,idV,2));
                            chLog.add(new ChipLog(16,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(16,ilevel,idV,3));
                            chLog.add(new ChipLog(17,ilevel,idV,3));
                            chLog.add(new ChipLog(18,ilevel,idV,3));
                            break;
                    }
                    break;
                case 7:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(21,ilevel,idV,2));
                            chLog.add(new ChipLog(20,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(20,ilevel,idV,2));
                            chLog.add(new ChipLog(19,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(21,ilevel,idV,3));
                            chLog.add(new ChipLog(19,ilevel,idV,3));
                            chLog.add(new ChipLog(20,ilevel,idV,3));
                            break;
                    }
                    break;
                case 8:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(24,ilevel,idV,2));
                            chLog.add(new ChipLog(23,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(23,ilevel,idV,2));
                            chLog.add(new ChipLog(22,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(22,ilevel,idV,3));
                            chLog.add(new ChipLog(23,ilevel,idV,3));
                            chLog.add(new ChipLog(24,ilevel,idV,3));
                            break;
                    }
                    break;
                case 9:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(27,ilevel,idV,2));
                            chLog.add(new ChipLog(26,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(26,ilevel,idV,2));
                            chLog.add(new ChipLog(25,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(25,ilevel,idV,3));
                            chLog.add(new ChipLog(26,ilevel,idV,3));
                            chLog.add(new ChipLog(27,ilevel,idV,3));
                            break;
                    }
                    break;
                case 10:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(30,ilevel,idV,2));
                            chLog.add(new ChipLog(29,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(29,ilevel,idV,2));
                            chLog.add(new ChipLog(28,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(28,ilevel,idV,3));
                            chLog.add(new ChipLog(29,ilevel,idV,3));
                            chLog.add(new ChipLog(30,ilevel,idV,3));
                            break;
                    }
                    break;
                case 11:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(33,ilevel,idV,2));
                            chLog.add(new ChipLog(32,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(32,ilevel,idV,2));
                            chLog.add(new ChipLog(31,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(31,ilevel,idV,3));
                            chLog.add(new ChipLog(33,ilevel,idV,3));
                            chLog.add(new ChipLog(32,ilevel,idV,3));
                            break;
                    }
                    break;
                case 12:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(36,ilevel,idV,2));
                            chLog.add(new ChipLog(35,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(35,ilevel,idV,2));
                            chLog.add(new ChipLog(34,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(34,ilevel,idV,3));
                            chLog.add(new ChipLog(35,ilevel,idV,3));
                            chLog.add(new ChipLog(36,ilevel,idV,3));
                            break;
                    }
                    break;
                }
            }
        //Пересечение границы и столбца
        if((x_pushed ==1) && (y_pushed==4)){
            switch (x_pushed_number){
                case 1:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(3,ilevel,idV,2));
                            chLog.add(new ChipLog(0,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(2,ilevel,idV,2));
                            chLog.add(new ChipLog(0,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(0,ilevel,idV,2));
                            chLog.add(new ChipLog(1,ilevel,idV,2));
                            break;
                    }
                    break;
                case 2:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(6,ilevel,idV,2));
                            chLog.add(new ChipLog(3,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(5,ilevel,idV,2));
                            chLog.add(new ChipLog(2,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(4,ilevel,idV,2));
                            chLog.add(new ChipLog(1,ilevel,idV,2));
                            break;
                    }
                    break;
                case 3:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(9,ilevel,idV,2));
                            chLog.add(new ChipLog(6,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(5,ilevel,idV,2));
                            chLog.add(new ChipLog(8,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(7,ilevel,idV,2));
                            chLog.add(new ChipLog(4,ilevel,idV,2));
                            break;
                    }
                    break;
                case 4:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(12,ilevel,idV,2));
                            chLog.add(new ChipLog(9,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(11,ilevel,idV,2));
                            chLog.add(new ChipLog(8,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(10,ilevel,idV,2));
                            chLog.add(new ChipLog(7,ilevel,idV,2));
                            break;
                    }
                    break;
                case 5:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(15,ilevel,idV,2));
                            chLog.add(new ChipLog(12,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(14,ilevel,idV,2));
                            chLog.add(new ChipLog(11,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(13,ilevel,idV,2));
                            chLog.add(new ChipLog(10,ilevel,idV,2));
                            break;
                    }
                    break;
                case 6:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(18,ilevel,idV,2));
                            chLog.add(new ChipLog(15,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(17,ilevel,idV,2));
                            chLog.add(new ChipLog(14,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(16,ilevel,idV,2));
                            chLog.add(new ChipLog(13,ilevel,idV,2));
                            break;
                    }
                    break;
                case 7:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(21,ilevel,idV,2));
                            chLog.add(new ChipLog(18,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(20,ilevel,idV,2));
                            chLog.add(new ChipLog(17,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(16,ilevel,idV,2));
                            chLog.add(new ChipLog(19,ilevel,idV,2));
                            break;
                    }
                    break;
                case 8:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(24,ilevel,idV,2));
                            chLog.add(new ChipLog(21,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(23,ilevel,idV,2));
                            chLog.add(new ChipLog(20,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(22,ilevel,idV,2));
                            chLog.add(new ChipLog(19,ilevel,idV,2));
                            break;
                    }
                    break;
                case 9:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(27,ilevel,idV,2));
                            chLog.add(new ChipLog(24,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(26,ilevel,idV,2));
                            chLog.add(new ChipLog(23,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(25,ilevel,idV,2));
                            chLog.add(new ChipLog(22,ilevel,idV,2));
                            break;
                    }
                    break;
                case 10:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(30,ilevel,idV,2));
                            chLog.add(new ChipLog(27,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(29,ilevel,idV,2));
                            chLog.add(new ChipLog(26,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(28,ilevel,idV,2));
                            chLog.add(new ChipLog(25,ilevel,idV,2));
                            break;
                    }
                    break;
                case 11:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(33,ilevel,idV,2));
                            chLog.add(new ChipLog(30,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(32,ilevel,idV,2));
                            chLog.add(new ChipLog(29,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(31,ilevel,idV,2));
                            chLog.add(new ChipLog(28,ilevel,idV,2));
                            break;
                    }
                    break;
                case 12:
                    switch( y_pushed_number){
                        case 1:
                            chLog.add(new ChipLog(36,ilevel,idV,2));
                            chLog.add(new ChipLog(33,ilevel,idV,2));
                            break;
                        case 2:
                            chLog.add(new ChipLog(35,ilevel,idV,2));
                            chLog.add(new ChipLog(32,ilevel,idV,2));
                            break;
                        case 3:
                            chLog.add(new ChipLog(34,ilevel,idV,2));
                            chLog.add(new ChipLog(31,ilevel,idV,2));
                            break;
                    }
                    break;
            }
        }
        return chLog;
    }
    public int SwitchILevel(int level){
        int iEntry=0;
        switch (level){
            case 1:
                iEntry=100;
                break;
            case 2:
                iEntry=200;
                break;
            case 3:
                iEntry=500;
                break;
            case 4:
                iEntry=1000;
                break;
        }
        return iEntry;
    }

    public boolean addNewChip(int objId,int level, LinkedList<ChipLogLimit> chLog){
        boolean bReturn;
        int iTotal=0;
        for (ChipLogLimit chipLogLimit : chLog) {
            if (chipLogLimit.getId()==objId)
                iTotal = iTotal+SwitchILevel(chipLogLimit.getEntry());
        }
        iTotal=iTotal+SwitchILevel(level);

        if (iTotal>1000) bReturn=false;
        else bReturn=true;

        WasEntrySet=bReturn;

        return bReturn;
    }
}

