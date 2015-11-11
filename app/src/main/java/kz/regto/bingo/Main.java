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
import android.widget.Button;
import android.widget.ImageView;

public class Main extends AppCompatActivity implements TimerEvent {

    View mRootView;
    MainContainer mc;


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
        mc.ClearBoard();
    }

    @Override
    public void TimerStarted(){
//        TextView GameCode;
//        String nCode;
//
////        TimerRelative tR = (TimerRelative)findViewById(R.id.gameTimer);
////        nCode=tR.GenerateNewGameCode();
//        GameCode = (TextView)findViewById(R.id.GameCode);
//        //GameCode.setText(nCode);
//
//        Animation rotate_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//        GameCode.setAnimation(rotate_animation);
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
        mc.ClearBoard();
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

}
