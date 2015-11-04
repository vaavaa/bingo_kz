package kz.regto.bingo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by spt on 12.10.2015.
 */
public class BoardGrid extends View {
    private Paint paint;
    private Paint bg_paint;


    int left_X=0;
    int top_Y=0;
    int right_X=0;
    int bottom_Y=0;
    boolean clrCanvas=false;

    //Ни чего не нажато по X
    int x_pushed = 0;
    //Ни чего не нажато по Y;
    int y_pushed = 0;


    Rect[] mRect=new Rect[50];
    Rect[] mRectw = new Rect[50];
    Rect[] mRectC = new Rect[50];
    Rect[] mRectCw = new Rect[50];
    Rect[] mLastRow3 = new Rect[3];
    Rect[] mLastRow1 = new Rect[2];

    RectView mRecOperate;
    RectView mRecOperate1;
    Rect[] mRecOperateTemp=new Rect[2];
    boolean mRecOperateIsAcive=false;


    private int ii =0;
    private int iiw = 0;
    private int iic = 0;
    private int iicw = 0;
    private int ii3 = 0;
    private int ii1 = 0;

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
            if (i<2) mLastRow1[i]=new Rect();
            if (i<3) mLastRow3[i]=new Rect();
            mRect[i] = new Rect();
            mRectw[i] = new Rect();
            mRectC[i]= new Rect();
            mRectCw[i]= new Rect();
        }

        mRecOperateTemp[0]=new Rect();
        mRecOperateTemp[1]=new Rect();

        bg_paint.setColor(Color.rgb(220, 220, 200));

    }

    private void MakeTouchedRectangleArea(Canvas canvas){

        ii=1; iiw=1; iic=0; iicw=0; ii3=0; ii1= 0;
        int correlate = (int)(14)/LINE_WIDTH;
        int columns;
        int rows;
        if (canvas.getHeight()<canvas.getWidth()) {
            columns = (int) canvas.getWidth() / 14;
            rows = (int) canvas.getHeight() / 5;
            column_light=columns;
            row_light=rows;
            correlation_light=correlate;

            //Заполняем нижнюю строку, там всего два прямоуголника
            mLastRow1[0].left =columns+correlate;
            mLastRow1[0].top = rows*4+correlate;
            mLastRow1[0].right = 7*columns;
            mLastRow1[0].bottom  = rows * 4 + rows-correlate;

            mLastRow1[1].left =7*columns+ 2* correlate;
            mLastRow1[1].top = rows*4+correlate;
            mLastRow1[1].right = 13*columns+correlate;
            mLastRow1[1].bottom  = rows * 4 + rows-correlate;

            //Заполняем предпоследнюю строку, там 3  прямоуголника
            for (int i = 0; i <= 2; i++) {
                mLastRow3[ii3].left = columns+ 4*columns*i+2*correlate;
                mLastRow3[ii3].top = rows*3+correlate;
                mLastRow3[ii3].right = columns + 4*columns*i + 4*columns;
                mLastRow3[ii3].bottom  = rows * 3 + rows-correlate;
                //canvas.drawRect(mLastRow3[ii3], bg_paint);
                ii3++;
            }
            //Рисуем вертикальные границы с некоторыми ограничениями всего столбцов 12,
            // последний 13ый столбец не нужен.
            for (int i = 1; i <= 12; i++) {
                mRect[ii].left = i * columns - GRID_STEP+correlate;
                mRect[ii].top = GRID_STEP;
                mRect[ii].right = i * columns + GRID_STEP+correlate;
                if ((i==1)||(i==13)) mRect[ii].bottom = 3 * rows - GRID_STEP;
                else mRect[ii].bottom = 3 * rows + GRID_STEP;
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
        //Object now has name for an identification
        int idV;
        idV = Integer.parseInt(Integer.toString(xTouch_new)+Integer.toString(yTouch_new));
        touchedView.setId(idV);
        main_container_parent = (MainContainer)BoardGrid.this.getParent();
        main_container_parent.setChildName(touchedView);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();
        int xTouch_new;
        int yTouch_new;

        // get touch event coordinates and make transparent from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                xTouch_new = getXCrossed(xTouch, yTouch);
                yTouch_new = getYCrossed(xTouch, yTouch);

                if (xTouch_new>0 && yTouch_new>0){
                    EntryGetsPoint(xTouch_new,yTouch_new);
                    GetUserTouch(xTouch_new,yTouch_new);
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
                    EntryGetsPoint(xTouch_new,yTouch_new);
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
                if (mRecOperateIsAcive){
                    mRecOperate.setVisibility(View.GONE);
                    mRecOperate1.setVisibility(View.GONE);
                    mRecOperateIsAcive=false;
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

        //Those are two buttons
        for (int i = 0; i <= 1; i++)
            if (mLastRow1[i].contains(xTouch, yTouch)) {
                result[1] = mLastRow1[i].centerY();
                mRecOperateTemp[1]= mLastRow1[i];
                y_pushed = 5;
                break;
            }

        //This is three buttons
        if (result[1] == 0)
            for (int i = 0; i <= 2; i++)
                if (mLastRow3[i].contains(xTouch, yTouch)) {
                    result[1] = mLastRow3[i].centerY();
                    mRecOperateTemp[1]=mLastRow3[i];
                    y_pushed = 5;
                    break;
                }
        //Нажата zero
        if (result[1] == 0)
            if (mRectC[0].contains(xTouch, yTouch)){
                result[1] = mRectC[0].centerY();
                mRecOperateTemp[1]=mRectC[0];
                y_pushed = 5;
            }

        //Две кнопки
        for (int i = 0; i <= 1; i++)
            if (mLastRow1[i].contains(xTouch, yTouch)) {
                result[0] = mLastRow1[i].centerX();
                mRecOperateTemp[0]=mLastRow1[i];
                x_pushed=5;
                break;
            }
        //Three buttons
        if (result[0] == 0)
            for (int i = 0; i <= 2; i++)
                if (mLastRow3[i].contains(xTouch, yTouch)) {
                    result[0] = mLastRow3[i].centerX();
                    mRecOperateTemp[0]=mLastRow3[i];
                    x_pushed=5;
                    break;
                }
        //Нажата zero
        if (result[0] == 0)
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
                break;
            }
        //Rows
        if (result == 0)
            for (int i = 1; i <= iic; i++) {
                if (mRectC[i].contains(xTouch, yTouch)) {
                    result = mRectC[i].centerX();
                    mRecOperateTemp[0]=mRectC[i];
                    x_pushed=4;
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

        MainContainer main_container_parent;
        main_container_parent = (MainContainer)BoardGrid.this.getParent();
        mRecOperate =(RectView)main_container_parent.getChildAt(1);
        mRecOperate1=(RectView)main_container_parent.getChildAt(2);

        if ((y_pushed==5)&&(x_pushed==5)) {
            mRecOperate.RectArea(mRecOperateTemp[0].left,
                    0, mRecOperateTemp[0].right, row_light*3+correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsAcive=true;
        }
        //Ячейки
        if ((y_pushed==4)&&(x_pushed==4)){
            if (mRectC[13].contains(x,y))
                mRecOperate.RectArea(mRectC[0].right,
                        y-row_light/2+correlation_light,
                        mRectC[13].left,
                        y+row_light/2-correlation_light);
            else  mRecOperate.RectArea((int)(x-column_light/2)-correlation_light,
                    ((int)y-row_light/2)-correlation_light,
                    (x+column_light/2)+correlation_light,
                    ((int)y+row_light/2)+correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsAcive=true;
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
            mRecOperateIsAcive=true;
        }
        //нажато пересечение границы по y и строка
        if ((y_pushed==4)&&(x_pushed==1)) {
            //Если попадаем на первую границу
            if (mRect[1].contains(x, y)){
                mRecOperate.RectArea(x - correlation_light,
                        y - row_light / 2 - correlation_light,
                        (x + column_light) + correlation_light,
                        y + row_light / 2 + correlation_light);
                mRecOperate1.RectArea(mRectC[0].left,
                        mRectC[0].top,
                        mRectC[0].right,
                        mRectC[0].bottom);
                mRecOperate1.setVisibility(View.VISIBLE);
                }
            else  mRecOperate.RectArea((int)(x-column_light)-correlation_light,
                    ((int)y-row_light/2)-correlation_light,
                    (x+column_light)+correlation_light,
                    ((int)y+row_light/2)+correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsAcive=true;
        }

        if ((y_pushed==1)&&(x_pushed==4)){
            //Если попадаем на первую границу
            if (mRectw[iicw].contains(x, y))
                mRecOperate.RectArea((x-column_light/2)-correlation_light,
                        correlation_light,
                        (x+column_light/2)+correlation_light,
                        y+correlation_light);
            else mRecOperate.RectArea((int)(x-column_light/2)-correlation_light,
                    ((int)y-row_light)-correlation_light,
                    (x+column_light/2)+correlation_light,
                    ((int)y+row_light)+correlation_light);
            mRecOperate.setVisibility(View.VISIBLE);
            main_container_parent.invalidate();
            mRecOperateIsAcive=true;
        }

    }
}

