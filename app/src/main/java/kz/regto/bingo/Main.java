package kz.regto.bingo;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

public class Main extends AppCompatActivity {

    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Убираем шторку status bar.
        mRootView = getWindow().getDecorView();
        //setOnSystemUiVisibilityChangeListener();
        showSystemUi();
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

    public void EntrySet(View view){
        findViewById(R.id.entry100).setSelected(false);
        findViewById(R.id.entry200).setSelected(false);
        findViewById(R.id.entry500).setSelected(false);
        findViewById(R.id.entry1000).setSelected(false);

        MainContainer mc = (MainContainer)findViewById(R.id.main_board);
        Button bPushed= (Button)view;

        bPushed.setSelected(true);
        String entryset = bPushed.getText().toString();
        if (entryset.equals("100"))  mc.setIlevelset(1);
        if (entryset.equals("200"))  mc.setIlevelset(2);
        if (entryset.equals("500"))  mc.setIlevelset(3);
        if (entryset.equals("1000"))  mc.setIlevelset(4);
    }
}
