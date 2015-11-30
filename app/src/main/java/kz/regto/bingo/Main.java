package kz.regto.bingo;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;

import kz.regto.database.DatabaseHelper;
import kz.regto.database.Utils;
import kz.regto.database.d_device;
import kz.regto.database.d_game;
import kz.regto.json.Network;
import kz.regto.json.JSONParser;
import kz.regto.json.PinCode;

public class Main extends AppCompatActivity implements TimerEvent, BoardGridEvents {

    View mRootView;
    MainContainer mc;
    TextView GameCode;
    String sGameCode="AA-0001";
    TimerRelative tR;
    TextView WN;
    int gWinNumber;
    BoardGrid board;
    TwoTextViews win;
    Lock lck;
    public d_device BingoDevice;



    // Database Helper
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Убираем шторку status bar.
        mRootView = getWindow().getDecorView();
        //setOnSystemUiVisibilityChangeListener();
        showSystemUi();


        //database initialisation
        //Создали подключение
        db = new DatabaseHelper(getApplicationContext());
        //Открыли одну переменую для всех иснтрукций
        db.openDB();

        BingoDevice = db.getDevice();

        if (BingoDevice==null){
            //Создаем устройство
            d_device dDevice = new d_device();
            dDevice.setDeviceCode(new Utils().getUniquePsuedoID());
            db.createNewDevice(dDevice);
        }

        //Ставка в 100
        findViewById(R.id.entry100).setSelected(true);
        mc=(MainContainer)findViewById(R.id.main_board);

        board=(BoardGrid)findViewById(R.id.board_grid);

        win =(TwoTextViews)findViewById(R.id.win);

        GameCode = (TextView)findViewById(R.id.GameCode);
        GameCode.setText(sGameCode);

        lck = (Lock)findViewById(R.id.r_lock);
        lck.bringToFront();
        setButtonsVisible(false);

    }
    public void setButtonsVisible(boolean bVisible){
        if (!bVisible) {
            findViewById(R.id.card_step_back).setVisibility(View.INVISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry500).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry200).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry100).setVisibility(View.INVISIBLE);
            findViewById(R.id.x2).setVisibility(View.INVISIBLE);
            findViewById(R.id.auto).setVisibility(View.INVISIBLE);
        }
        else {
            findViewById(R.id.card_step_back).setVisibility(View.VISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.VISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.VISIBLE);
            findViewById(R.id.entry500).setVisibility(View.VISIBLE);
            findViewById(R.id.entry200).setVisibility(View.VISIBLE);
            findViewById(R.id.entry100).setVisibility(View.VISIBLE);
            findViewById(R.id.x2).setVisibility(View.VISIBLE);
            findViewById(R.id.auto).setVisibility(View.VISIBLE);
        }
    }
    public void screen_lock_starting_procedure (){
        //1) Просим линк из объекта
        //2) Если линк есть, стучимся для проверки пин кода
        //3) Линк не работает, просим ввести линк, не работает сеть, просим дать нам сеть
        //4) Пин совпал, запросили баланс, если больше нуля, разблокировали
        //5) Пин не совпал - сообщили что не совпал
        //6) Баланс равен 0 - сообщили

        screen_lock(true);
        screen_lock(false);

    }
    public void screen_lock(boolean bSkrn){
        Runnable mRunnable;
        Handler mHandler=new Handler();

        //Если команда - разблокировать и состояние утсройсва активно, то разблокировать
        if (!bSkrn ){
            Animation push_up = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
            lck.startAnimation(push_up);
            lck.setVisibility(View.GONE);
            mRunnable = new Runnable() {
                @Override
                public void run() {setButtonsVisible(true);}
            };
            mHandler.postDelayed(mRunnable, 300);
        }
        else {
            setButtonsVisible(false);
            Animation push_up_in = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            lck.startAnimation(push_up_in);
            lck.invalidate();
            lck.setVisibility(View.VISIBLE);
        }
    }

    public void unlocked(View v){
        screen_lock_starting_procedure();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        showSystemUi();
    }

    public void showSystemUi() {
        int flag= (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mRootView.setSystemUiVisibility(flag);
        ActionBar bar = getSupportActionBar();
        if (bar!=null && bar.isShowing()) bar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume(){
        super.onResume();
        showSystemUi();
    }

    @Override
    public void TimerOver(TimerRelative tR){
        board.setBoard_blocked(true);
        gWinNumber = Integer.parseInt(tR.WinningNumber());
        win.setField(Integer.toString(Integer.parseInt(win.getField()) + GameResultCalculation()));
        clearBoard();
    }

    @Override
    public void TimerStarted(TimerRelative tR){
        if (WN!=null) WN.setVisibility(View.GONE);
        if (board!=null) board.setBoard_blocked(false);
        if (win!=null) win.setField("0");
        String nCode;

        nCode=tR.GenerateNewGameCode(sGameCode);
        //newGameInDatatabase(nCode);

        if (GameCode!=null){

            GameCode.setText(nCode);
            sGameCode=nCode;
            Animation rotate_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            rotate_animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    GameCode.setAlpha(1f);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    GameCode.setAlpha(0.1f);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            GameCode.setAnimation(rotate_animation);
            GameCode.animate();
        }
        else {
            sGameCode=nCode;
        }

    }
    @Override
    public void entrySet(int entryType){
        int iEntry = getEntryfromLevel(entryType);
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        iEntry = iEntry + Integer.parseInt(t2w.getField());
        t2w.setField(Integer.toString(iEntry));
    }
    public void botsEntry(View view){
        board.set_random_entry();
    }

    public void x2_button(View view){
        int iEntry;
        int ibalance;
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        TwoTextViews balance =  (TwoTextViews)this.findViewById(R.id.balance);
        ibalance = Integer.parseInt(balance.getField());
        iEntry = Integer.parseInt(t2w.getField());
        if (2*iEntry<=ibalance){
            LinkedList<ChipLog> cl = board.getMainLogList();
            LinkedList<ChipLogLimit> cll = board.getLimitLogList();
            for (ChipLog clp: cl){
                mc.setIlevelset(clp.getEntry());
                board.EntryGetsPoint(clp.getX(),clp.getY());
            }
        }
    }

    public void StartNextTimer(View v){
        tR.StartTimer();
    }

    public int getEntryfromLevel(int lvl){
        int iEntry=0;
         switch (lvl) {
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
    public void EntrySet(View view){
        findViewById(R.id.entry100).setSelected(false);
        findViewById(R.id.entry200).setSelected(false);
        findViewById(R.id.entry500).setSelected(false);
        findViewById(R.id.entry1000).setSelected(false);

        Button bPushed= (Button)view;

        bPushed.setSelected(true);
        String entryset = bPushed.getText().toString();
        if (entryset.equals("100"))  mc.setIlevelset(1);
        if (entryset.equals("200"))  mc.setIlevelset(2);
        if (entryset.equals("500"))  mc.setIlevelset(3);
        if (entryset.equals("1000"))  mc.setIlevelset(4);
    }

    public void ClearBoard(View view){
        clearBoard();
    }
    public void stepBack(View view){
        mc.stepBack();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private int GameResultCalculation(){
        int total_sum=0;
        LinkedList<ChipLog> FR= board.getMainLogList();
        for (ChipLog cl:FR){
            if (cl.NumberChip==gWinNumber){
                total_sum=total_sum+(((int)36/cl.divideBy)*getEntryfromLevel(cl.getEntry()));
            }
        }
        return total_sum;
    }

    private void clearBoard(){
        mc.ClearBoard();
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        t2w.setField("0");
        board.setMainLogList(new LinkedList<ChipLog>());
    }

    public void ball_clicked(View v){
        Animation rotate_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        v.setAnimation(rotate_animation);
        v.animate();
    }

    @Override
    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

}
