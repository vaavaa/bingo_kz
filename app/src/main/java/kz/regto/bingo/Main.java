package kz.regto.bingo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kz.regto.database.DatabaseHelper;
import kz.regto.database.Utils;
import kz.regto.database.d_balance;
import kz.regto.database.d_device;
import kz.regto.database.d_entry_set;
import kz.regto.database.d_game;
import kz.regto.json.Balance;
import kz.regto.json.Network;
import kz.regto.json.JSONParser;
import kz.regto.json.PinCode;

public class Main extends AppCompatActivity implements TimerEvent, BoardGridEvents {

    View mRootView;
    MainContainer mc;
    TextView GameCode;
    int gWinNumber;
    BoardGrid board;
    TwoTextViews win;
    Lock lck;
    FrameLayout frameLayout;
    TimerRelative timerRelative;
    public int ilevelset=1;


    // Database Helper
    public DatabaseHelper db;
    public d_device BingoDevice;
    public d_game dGame;
    public d_entry_set dEntrySet;

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
            BingoDevice = new d_device();
            BingoDevice.setDeviceCode(new Utils().getUniquePsuedoID());
            db.createNewDevice(BingoDevice);
        }
        else {
            EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
            ET1.setText(BingoDevice.getNetwork_path());
        }

        //Ставка в 100
        findViewById(R.id.entry100).setSelected(true);
        mc=(MainContainer)findViewById(R.id.main_board);

        board=(BoardGrid)findViewById(R.id.board_grid);

        win =(TwoTextViews)findViewById(R.id.win);

        GameCode = (TextView)findViewById(R.id.GameCode);

        lck = (Lock)findViewById(R.id.r_lock);
        lck.bringToFront();
        setButtonsVisible(false);

    }

    public void setIlevelset(int ilevelset) {
        this.ilevelset = ilevelset;
    }

    public int getIlevelset() {
        return ilevelset;
    }


    public void setButtonsUnclickable(boolean bEnable){
        findViewById(R.id.card_step_back).setEnabled(!bEnable);
        findViewById(R.id.make_crd_null).setEnabled(!bEnable);
        findViewById(R.id.entry1000).setEnabled(!bEnable);
        findViewById(R.id.entry500).setEnabled(!bEnable);
        findViewById(R.id.entry200).setEnabled(!bEnable);
        findViewById(R.id.entry100).setEnabled(!bEnable);
        findViewById(R.id.x2).setEnabled(!bEnable);
        findViewById(R.id.auto).setEnabled(!bEnable);
        if (bEnable) {
            findViewById(R.id.card_step_back).setAlpha(.7f);
            findViewById(R.id.make_crd_null).setAlpha(.7f);
            findViewById(R.id.entry1000).setAlpha(.7f);
            findViewById(R.id.entry500).setAlpha(.7f);
            findViewById(R.id.entry200).setAlpha(.7f);
            findViewById(R.id.entry100).setAlpha(.7f);
            findViewById(R.id.x2).setAlpha(.7f);
            findViewById(R.id.auto).setAlpha(.7f);
            board.setBoard_blocked(true);
        }
        else {
            findViewById(R.id.card_step_back).setAlpha(1f);
            findViewById(R.id.make_crd_null).setAlpha(1f);
            findViewById(R.id.entry1000).setAlpha(1f);
            findViewById(R.id.entry500).setAlpha(1f);
            findViewById(R.id.entry200).setAlpha(1f);
            findViewById(R.id.entry100).setAlpha(1f);
            findViewById(R.id.x2).setAlpha(1f);
            findViewById(R.id.auto).setAlpha(1f);
            board.setBoard_blocked(false);
        }
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

    public void setLockerElement(FrameLayout FL){
        frameLayout = FL;
    }
    public void setTimerElement(TimerRelative tr){
        timerRelative = tr;
    }

    //0) Проверяем статус устройства, если активировано, то просто разблокируем, если нет
    //1) Просим линк из объекта устройства
    //2) Если линк есть, стучимся для проверки пин кода
    //3) Линк не работает, просим ввести линк, не работает сеть, просим дать нам сеть
    //4) Пин совпал, запросили баланс, если больше нуля, разблокировали и записали баланс в переменную устройства, запустили таймер.
    //5) Пин не совпал - сообщили что не совпал
    //6) Баланс равен 0 - сообщили
    public void screen_lock_starting_procedure () {
        //hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (BingoDevice.getStatus() != 1) {
            Network ntw = new Network();
            JSONParser Jprs = new JSONParser();
            if (ntw.isNetworkAvailable(this)) {
                EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
                if (ET1.getText().toString().length() > 0)
                        BingoDevice.setNetwork_path(ET1.getText().toString());

                if (BingoDevice.getNetwork_path().length()!= 0){
                    ET1.setText(BingoDevice.getNetwork_path());
                    final EditText ET = (EditText) frameLayout.findViewById(R.id.pin);
                    if (ET.getText().toString().length()>0) {
                        int iPinCode = Integer.parseInt(ET.getText().toString());
                        String url = BingoDevice.getNetwork_path().concat("/pincode.php");
                        PinCode pinCode = Jprs.tPinCode(url);
                        if (pinCode != null) {
                            if (iPinCode == pinCode.getPinCode()) {
                                db.updateDevice(BingoDevice);
                                timerRelative.HTTPRunTimer(BingoDevice.getNetwork_path().concat("/timer.php"));
                                screen_lock(false);
                                TimerStarted_sub();
                            } else {
                                Toast toast = Toast.makeText(this, "PIN is not correct", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(this, "Server is not reachable, path could be wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(this, "PIN is not correct", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    Toast toast = Toast.makeText(this, "Server is not reachable, path could be wrong", Toast.LENGTH_SHORT);
                    toast.show();
                    ET1.setFocusable(true);
                }
            } else {
                Toast toast = Toast.makeText(this, "Network is down, pls. check internet connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (BingoDevice.getBalance() >= 100) {
                screen_lock(false);
                timerRelative.HTTPRunTimer(BingoDevice.getNetwork_path().concat("/timer.php"));
                TimerStarted_sub();
            }
        }
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
        lck.invalidate();
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
    public void TimerOver(){
        //Когда таймер кончился, ждем выпавшего шарика
        setButtonsUnclickable(true);
    }

    @Override
    public void GameOver(){
        //ОБновили игру
        db.updateGame(dGame);

        WinBallContainer WBC = (WinBallContainer)this.findViewById(R.id.win_ball_container);
        WBC.UpdateNewOne(Integer.toString(dGame.getWin_ball()),db.getAllGameEntrySet(dGame.getId()));
        WBC.setAllSelected_false();

        //Считаем выйгрыш, если ничего нет, быдет 0
        int iWin = db.getGameSum(dGame.getId(),dGame.getWin_ball());

        //Текущий баланс увеличили на выйгрыш
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.balance);
        int cur_balance = Integer.parseInt(t2w.getField());
        cur_balance = cur_balance+iWin;

        TwoTextViews t2win =  (TwoTextViews)this.findViewById(R.id.win);
        t2win.setField(Integer.toString(iWin));

        //Обновляем баланс устройства
        BingoDevice.setBalance(cur_balance);
        db.updateDevice(BingoDevice);

        TwoTextViews t2E =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        int iEntry = Integer.parseInt(t2E.getField());

        //Создаем баланс
        d_balance dBalance = new d_balance();
        dBalance.setSum(iWin-iEntry);
        dBalance.setGame_id(dGame.getId());
        dBalance.setOperation(0);
        db.createNewBalance(dBalance);

        //Ни какой ставки в начеле новой игры нет
        t2E.setField("0");


        //Запускаем новую игру c задержкой в 2 секунды
        Handler temp_handler = new Handler();
        temp_handler.postDelayed(new Runnable() {
            public void run() {
                clearBoard();
                setButtonsUnclickable(false);
                TimerStarted_sub();
            }
        }, 2500);
    }

    private void TimerStarted_sub() {
        String url = BingoDevice.getNetwork_path().concat("/balance.php");
        JSONParser Jprs = new JSONParser();
        Balance balance = Jprs.tBalance(url);
        if (balance != null) {
            if (balance.getBalance() >= 100) {
                BingoDevice.setBalance(balance.getBalance());
                BingoDevice.setStatus(1);
                //Прописать в баланс текстовое поле
                db.updateDevice(BingoDevice);
                TwoTextViews Balance = (TwoTextViews) findViewById(R.id.balance);
                Balance.setField(Integer.toString(balance.getBalance()));

                //Получаем код игры на устройсте.
                String nCode = "";
                d_game l_gameCode = db.getLastGame();
                if (l_gameCode == null) nCode = "AA-0000";
                else nCode = l_gameCode.getGameCode();
                //Создаем новую игру
                nCode = timerRelative.GenerateNewGameCode(nCode);
                dGame = new d_game();
                dGame.setGameCode(nCode);
                dGame.setServer_game_id(timerRelative.getServerGameCode());
                if (dGame.getServer_game_id() == 0) {
                    //Если ошибка создания, то бдлокируем экран
                    Toast toast = Toast.makeText(this, "Ошибка создания игры. Не найден номер игры на сервере.", Toast.LENGTH_SHORT);
                    toast.show();
                    screen_lock(true);
                    return;
                }
                dGame.setWin_ball(-1);
                dGame.setDevice_id(db.getDevice().getDevice_id());
                dGame.setState(0);

                if (!db.createNewGame(dGame)) {
                    //Если ошибка создания, то бдлокируем экран
                    Toast toast = Toast.makeText(this, "Ошибка создания игры.", Toast.LENGTH_SHORT);
                    toast.show();
                    screen_lock(true);
                    return;
                }
                GameCode.setText(nCode);
                Animation rotate_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                rotate_animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        GameCode.setAlpha(1f);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        GameCode.setAlpha(0.4f);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                GameCode.setAnimation(rotate_animation);
                GameCode.animate();
                //Запустили таймер
                timerRelative.StartTimer();
            }
            else {
                 Toast toast = Toast.makeText(this, "Баланс на устройстве меньше 100, пополните баланс", Toast.LENGTH_SHORT);
                 toast.show();
                 BingoDevice.setStatus(0);
                 db.updateDevice(BingoDevice);
                 screen_lock(true);
            }
        }
        else {
            Toast toast = Toast.makeText(this, "Balance is not correct", Toast.LENGTH_SHORT);
            toast.show();
            screen_lock(true);
        }
    }

    @Override
    public void entrySet(boolean isBalanced){
        if (isBalanced) {
            int iEntry = getEntryfromLevel(this.getIlevelset());
            TwoTextViews t2w = (TwoTextViews) this.findViewById(R.id.CurrentEntry);
            TwoTextViews t2b = (TwoTextViews) this.findViewById(R.id.balance);
            int it2b = Integer.parseInt(t2b.getField());
            it2b = it2b - iEntry;
            t2b.setField("" + it2b);
            iEntry = iEntry + Integer.parseInt(t2w.getField());
            t2w.setField(Integer.toString(iEntry));
        }
    }
    private int getEntryfromLevel(int lvl){
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
    public int getLevelfromEntry(int Entry){
        int iEntry=0;
        switch (Entry) {
            case 100:
                iEntry=1;
                break;
            case 200:
                iEntry=2;
                break;
            case 500:
                iEntry=3;
                break;
            case 1000:
                iEntry=4;
                break;
        }
        return iEntry;
    }


    public void botsEntry(View view){
        board.set_random_entry();
    }

    public void x2_button(View view){
        List<d_entry_set> cl = db.getAllGameEntrySet(dGame.getId());
        BoardGrid bG = (BoardGrid)this.findViewById(R.id.board_grid);
        int currentLevel = this.getIlevelset();
        for (d_entry_set clp: cl){
            this.setIlevelset(getLevelfromEntry(clp.getEntry_value()));
            bG.EntryGetsPoint(clp.getX()-33,clp.getY()-33);
        }
        this.setIlevelset(currentLevel);
    }

    public void EntrySet(View view){
        findViewById(R.id.entry100).setSelected(false);
        findViewById(R.id.entry200).setSelected(false);
        findViewById(R.id.entry500).setSelected(false);
        findViewById(R.id.entry1000).setSelected(false);

        Button bPushed= (Button)view;

        bPushed.setSelected(true);
        String entryset = bPushed.getText().toString();
        if (entryset.equals("100"))  this.setIlevelset(1);
        if (entryset.equals("200"))  this.setIlevelset(2);
        if (entryset.equals("500"))  this.setIlevelset(3);
        if (entryset.equals("1000"))  this.setIlevelset(4);
    }

    public void ClearBoard(View view){
        clearBoard();
    }

    public void stepBack(View view){
        mc.stepBack();
    }

    private void clearBoard(){
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        t2w.setField("0");
        TwoTextViews t2b =  (TwoTextViews)this.findViewById(R.id.balance);
        int balance = Integer.parseInt(t2b.getField()) + db.getGameCurrentSum(dGame.getId());
        t2b.setField(""+balance);
        mc.ClearBoard();
    }

    public void ball_clicked(View v){
        board.showGame(v);
    }

    @Override
    protected void onDestroy(){
        setButtonsUnclickable(true);
        screen_lock(true);
        timerRelative.CloseAll();
        BingoDevice.setStatus(0);
        db.updateDevice(BingoDevice);
        db.close();
        super.onDestroy();
    }
}
