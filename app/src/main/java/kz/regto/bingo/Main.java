package kz.regto.bingo;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
}
