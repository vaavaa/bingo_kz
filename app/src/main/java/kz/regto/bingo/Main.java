package kz.regto.bingo;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends AppCompatActivity implements TimerEvent, BoardGridEvents {

    View mRootView;
    MainContainer mc;
    TextView GameCode;
    String sGameCode="AA-0001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Убираем шторку status bar.
        mRootView = getWindow().getDecorView();
        //setOnSystemUiVisibilityChangeListener();
        showSystemUi();
        //Ставка в 100
        findViewById(R.id.entry100).setSelected(true);
        mc=(MainContainer)findViewById(R.id.main_board);

        //Закругляем картинку
        ImageView image=(ImageView)findViewById(R.id.gold);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gold);
        image.setImageBitmap(getRoundedCornerBitmap(bitmap, 20));

        GameCode = (TextView)findViewById(R.id.GameCode);
        GameCode.setText(sGameCode);

    }

    private void showSystemUi() {
        ActionBar bar = getSupportActionBar();
        if (bar.isShowing()) bar.hide();

        int flag= View.SYSTEM_UI_FLAG_FULLSCREEN;
        mRootView.setSystemUiVisibility(flag);
    }

    @Override
    public void onResume(){
        super.onResume();
        showSystemUi();
    }

    @Override
    public void TimerOver(){
        clearBoard();
    }

    @Override
    public void TimerStarted(TimerRelative tR){
        String nCode;

        nCode=tR.GenerateNewGameCode(sGameCode);
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

    private void clearBoard(){
        mc.ClearBoard();
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        t2w.setField("0");
    }

}
