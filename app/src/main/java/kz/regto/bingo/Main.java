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

import java.util.ArrayList;
import java.util.List;

import kz.regto.database.DatabaseHelper;
import kz.regto.database.Utils;
import kz.regto.database.d_balance;
import kz.regto.database.d_device;
import kz.regto.database.d_entry_set;
import kz.regto.database.d_game;
import kz.regto.json.Balance;
import kz.regto.json.Network;
import kz.regto.json.WebService;

public class Main extends AppCompatActivity implements BalanceEvent, TimerEvent, BoardGridEvents {

    View mRootView;
    MainContainer mc;
    TextView GameCode;
    BoardGrid board;
    Lock lck;
    FrameLayout frameLayout;
    TimerRelative timerRelative;
    BalanceEngine BalanceRelative;
    public int ilevelset=1;

    //Network Helper
    public Network ntw;

    // Database Helper
    public DatabaseHelper db;
    public d_device BingoDevice;
    public d_game dGame;
    public d_entry_set dEntrySet;
    public List<d_entry_set> botEntrySet=null;


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
        db = new DatabaseHelper(this);
        //Открыли одну переменую для всех иснтрукций
        db.openDB();

        BingoDevice = db.getDevice();
        if (BingoDevice==null){
            //Создаем устройство
            BingoDevice = new d_device();
            BingoDevice.setDeviceCode(new Utils().getUniquePsuedoID());
            db.createNewDevice(BingoDevice);
        }
        //Network initialisation
        ntw = new Network(this);
        String NetworkPath="";
        if (db.getSettings("network_path")!=null) NetworkPath = db.getSettings("network_path").getSettingsValue();
        if (NetworkPath.length()> 0) {
            ntw.setNetworkPath(NetworkPath);
            EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
            ET1.setText(NetworkPath);
        }
        if (ntw.isNetworkAvailable(this))
            if (ntw.ConnectionExist()){
                BingoDevice = ntw.getDeviceFromServer(BingoDevice);
                db.updateDevice(BingoDevice);
                BalanceRelative.RunBalanсeListening(
                        ntw.getNetworkPath().concat("balance_outcome.php?device_server_id="+BingoDevice.getServerDeviceId()));
            }


        mc=(MainContainer)findViewById(R.id.main_board);

        board=(BoardGrid)findViewById(R.id.board_grid);
        GameCode = (TextView)findViewById(R.id.GameCode);

        //Ставка в 100
        this.findViewById(R.id.entry100).setSelected(true);

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
        findViewById(R.id.pause).setEnabled(!bEnable);
        findViewById(R.id.quit).setEnabled(!bEnable);
        if (bEnable) {
            findViewById(R.id.card_step_back).setAlpha(.7f);
            findViewById(R.id.make_crd_null).setAlpha(.7f);
            findViewById(R.id.entry1000).setAlpha(.7f);
            findViewById(R.id.entry500).setAlpha(.7f);
            findViewById(R.id.entry200).setAlpha(.7f);
            findViewById(R.id.entry100).setAlpha(.7f);
            findViewById(R.id.x2).setAlpha(.7f);
            findViewById(R.id.auto).setAlpha(.7f);
            findViewById(R.id.pause).setAlpha(.7f);
            findViewById(R.id.quit).setAlpha(.7f);
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
            findViewById(R.id.pause).setAlpha(1f);
            findViewById(R.id.quit).setAlpha(1f);
            board.setBoard_blocked(false);
            board.MakeTouchedRectangleArea();
        }
    }

    public void setButtonsVisible(boolean bVisible){
        if (bVisible) {
            findViewById(R.id.card_step_back).setVisibility(View.VISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.VISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.VISIBLE);
            findViewById(R.id.entry500).setVisibility(View.VISIBLE);
            findViewById(R.id.entry200).setVisibility(View.VISIBLE);
            findViewById(R.id.entry100).setVisibility(View.VISIBLE);
            findViewById(R.id.x2).setVisibility(View.VISIBLE);
            findViewById(R.id.auto).setVisibility(View.VISIBLE);
            findViewById(R.id.pause).setVisibility(View.VISIBLE);
            findViewById(R.id.quit).setVisibility(View.VISIBLE);

        }
        else {
            findViewById(R.id.card_step_back).setVisibility(View.INVISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry500).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry200).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry100).setVisibility(View.INVISIBLE);
            findViewById(R.id.x2).setVisibility(View.INVISIBLE);
            findViewById(R.id.auto).setVisibility(View.INVISIBLE);
            findViewById(R.id.pause).setVisibility(View.INVISIBLE);
            findViewById(R.id.quit).setVisibility(View.INVISIBLE);
        }
    }

    public void setLockerElement(FrameLayout FL){
        frameLayout = FL;
    }
    public void setTimerElement(TimerRelative tr){
        timerRelative = tr;
    }
    public void setBalanceElement (BalanceEngine balanceElement){
        BalanceRelative = balanceElement;
    }

    //0) Проверяем включена ли связь устройства, если активировано, то просто разблокируем, если нет
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
        //Если связи нет, сообщение и выйти
        if (!ntw.isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this, R.string.NetworkIsDown, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //Забираем линк на сервер
        EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
        if (ET1.getText().toString().length() > 0) ntw.setNetworkPath(ET1.getText().toString());
        else {
            Toast toast = Toast.makeText(this,  R.string.ServerIsNotReachable, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //проверяем путь
        if (ntw.getNetworkPath().length()== 0
                || !ntw.ConnectionExist()){
            Toast toast = Toast.makeText(this,  R.string.ServerIsNotReachable, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        final EditText ET = (EditText) frameLayout.findViewById(R.id.pin);
        if (ET.getText().toString().length()==0) {
            Toast toast = Toast.makeText(this, R.string.PinIsEmpty, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        int iPinCode = Integer.parseInt(ET.getText().toString());
        int pinCode = ntw.getServerValue("web_service.php?comm=pincode&par=0",4000).getIntvalue();
        if (iPinCode != pinCode) {
            Toast toast = Toast.makeText(this,  R.string.PinIsNotCorrect, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        BingoDevice = ntw.getDeviceFromServer(BingoDevice);
        db.updateDevice(BingoDevice);

        db.updateDevice(BingoDevice);
        if (BingoDevice.getStatus() != 0) {
            Toast toast = Toast.makeText(this, R.string.StatusIsNotCorrect , Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        BalanceRelative.RunBalanсeListening(ntw.getNetworkPath().concat("/balance_outcome.php?device_server_id="+BingoDevice.getServerDeviceId()));
        timerRelative.HTTPRunTimer(ntw.getNetworkPath().concat("/timer.php"));
        screen_lock(false);
        TimerStarted_sub();
    }

    public void screen_lock(boolean bSkrn){

        //Если команда - разблокировать и состояние утсройсва активно, то разблокировать
        if (!bSkrn ){
            Animation push_up = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
            lck.startAnimation(push_up);
            lck.setVisibility(View.GONE);
            lck.invalidate();
            setButtonsVisible(true);
        }
        else {
            setButtonsVisible(false);
            Animation push_up_in = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            lck.startAnimation(push_up_in);
            lck.invalidate();
            lck.setVisibility(View.VISIBLE);
            lck.bringToFront();
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
    public void TimerOver(){
        final WinBallContainer  WBC = (WinBallContainer)this.findViewById(R.id.win_ball_container);
        WBC.setAll_lock(true);
        //Когда таймер кончился, ждем выпавшего шарика
        setButtonsUnclickable(true);
    }

    @Override
    public void GameOver(){
        //ОБновили игру
        db.updateGame(dGame);

        final WinBallContainer  WBC = (WinBallContainer)this.findViewById(R.id.win_ball_container);
        WBC.UpdateNewOne(Integer.toString(dGame.getWin_ball()), db.getAllGameEntrySet(dGame.getId()), this);
        WBC.setAllSelected_false();

        //Считаем выйгрыш, если ничего нет, будет 0
        int iWin = db.getGameSum(dGame.getId(),dGame.getWin_ball());

        //Текущий баланс увеличили на выйгрыш
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.balance);
        int cur_balance = Integer.parseInt(t2w.getField());
        cur_balance = cur_balance+iWin;



        final TwoTextViews t2win = (TwoTextViews)this.findViewById(R.id.win);
        t2win.setField(Integer.toString(iWin));

        //Обновляем баланс устройства
        //BingoDevice.setBalance(cur_balance);
        db.updateDevice(BingoDevice);

        TwoTextViews t2E =  (TwoTextViews)this.findViewById(R.id.CurrentEntry);
        int iEntry = Integer.parseInt(t2E.getField());

        //Создаем баланс
        d_balance dBalance = new d_balance();
        dBalance.setSum(iWin - iEntry);
        dBalance.setGame_id(dGame.getId());
        dBalance.setOperation(0);
        db.createNewBalance(dBalance);

        //Ни какой ставки в начеле новой игры нет
        t2E.setField("0");
        botEntrySet=null;

        String url = ntw.getNetworkPath().concat("/balance.php?device_id=")
                .concat(BingoDevice.getDeviceCode()).concat("&balance="+cur_balance);
        Balance balance = ntw.getBalance();
        if (balance != null) {
            //Запускаем новую игру c задержкой в 2 секунды
            Handler temp_handler = new Handler();
            temp_handler.postDelayed(new Runnable() {
                public void run() {
                    mc.ClearBoard(MainContainer.CLEAR_BOARD_ONLY);
                    setButtonsUnclickable(false);
                    WBC.setAll_lock(false);
                    t2win.setField("0");
                    if (db.getDBState()==DatabaseHelper.STATE_OPENED) TimerStarted_sub();
                }
            }, 2500);
        }
        else {
            Toast toast = Toast.makeText(this, "Не удалось сохранить результаты игры.", Toast.LENGTH_SHORT);
            toast.show();
            screen_lock(true);
        }
    }

    private void TimerStarted_sub() {
            int balance = BalanceRelative.getBalance();

            if (BingoDevice.getStatus()!=0){
                Toast toast = Toast.makeText(this, R.string.DeviceIsNotActive, Toast.LENGTH_SHORT);
                toast.show();
                screen_lock(true);
                return;
            }
            if (balance < 100)  {
                Toast toast = Toast.makeText(this, R.string.BalanceIsLow, Toast.LENGTH_SHORT);
                toast.show();
                screen_lock(true);
                return;
            }
            //Получаем код игры на устройсте.
            String nCode = "";
            d_game l_gameCode = db.getLastGame();
            if (l_gameCode == null) nCode = "AA-0000";
            else nCode = l_gameCode.getGameCode();
            //Создаем новую игру
            String url = ntw.getNetworkPath().concat("/web_service.php?par=").concat(BingoDevice.getDeviceCode()).concat("&comm=device_id");
            WebService deviceCode = ntw.getServerValue("device_id",BingoDevice.getServerDeviceId());
            nCode = timerRelative.GenerateNewGameCode(nCode, Integer.toString(deviceCode.getIntvalue()));

            dGame = new d_game();
            dGame.setGameCode(nCode);
            dGame.setServer_game_id(timerRelative.getServerGameCode());
            if (dGame.getServer_game_id() == 0) {
               //Если ошибка создания, то бдлокируем экран
               Toast toast = Toast.makeText(this, R.string.GameErrServerAnswer, Toast.LENGTH_SHORT);
               toast.show();
               screen_lock(true);
               return;
            }
            dGame.setWin_ball(-1);
            dGame.setDevice_id(db.getDevice().getDevice_id());
            dGame.setState(0);

            if (!db.createNewGame(dGame)) {
                //Если ошибка создания, то бдлокируем экран
                Toast toast = Toast.makeText(this, R.string.GameErrDataBase, Toast.LENGTH_SHORT);
                toast.show();
                screen_lock(true);
                return;
            }
            GameCode.setText(nCode);
            Animation rotate_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            rotate_animation.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {
                            GameCode.setAlpha(1f);
                        }
                @Override public void onAnimationEnd(Animation animation) {
                            GameCode.setAlpha(0.4f);
                        }
                @Override public void onAnimationRepeat(Animation animation) {}
            });

            GameCode.setAnimation(rotate_animation);
            GameCode.animate();
            //Запустили таймер
            timerRelative.StartTimer();
            //Запустили баланс

    }

    @Override
    public void   BalanceUpdated(){}
    @Override
    public void BonusUpdated() {}
    @Override
    public void EntryUpdated(){}

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
        if (botEntrySet==null) botEntrySet = new ArrayList<>();
        botEntrySet = board.set_random_entry(botEntrySet);
    }

    public void x2_button(View view){
        List<d_entry_set> cl = db.getAllGameEntrySet(dGame.getId());
        int balance = db.getGameCurrentSum(dGame.getId());
        TwoTextViews t2w =  (TwoTextViews)this.findViewById(R.id.balance);
        String myBalance = t2w.getField();
        int i_myBalance = Integer.parseInt(myBalance);
        if (balance > 0 && i_myBalance>=balance) {
            BoardGrid bG = (BoardGrid) this.findViewById(R.id.board_grid);
            int currentLevel = this.getIlevelset();
            for (d_entry_set clp : cl) {
                this.setIlevelset(getLevelfromEntry(clp.getEntry_value()));
                bG.EntryGetsPoint(clp.getX() - 33, clp.getY() - 33);
            }
            this.setIlevelset(currentLevel);
        }
        else {
            Toast toast = Toast.makeText(this, "Баланс на устройстве не достатрочен для удвоения, пополните баланс.", Toast.LENGTH_SHORT);
            toast.show();
        }
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
        mc.ClearBoard(MainContainer.CLEAR_ALL);
        final WinBallContainer  WBC = (WinBallContainer)this.findViewById(R.id.win_ball_container);
        WBC.setAll_lock(true);
        //Возвращем выбранные(если есть) в положение не выбран
        WBC.setAllSelected_false();
    }

    public void ball_clicked(View v){
        board.showGame(v);
    }

    public void pause(View v){
        //Остановили таймер
        timerRelative.StopTimer();
        screen_lock(true);
    }
    public void quit(View v){
        //Остановили таймер
        BingoDevice.setStatus(1);
        clearBoard();
        timerRelative.StopTimer();
        screen_lock(true);
        Toast toast = Toast.makeText(this, "Игра остановлена", Toast.LENGTH_SHORT);
        toast.show();
        final EditText ET = (EditText) frameLayout.findViewById(R.id.pin);
        ET.setText("");
    }
    public void ball_cancel_clicked(View v){
        v.setVisibility(View.INVISIBLE);
        WinBallContainer WBC = (WinBallContainer)findViewById(R.id.win_ball_container);
        mc.ClearAllAlfa05();
        WBC.setAllSelected_false();
    }

    @Override
    protected void onDestroy(){
        setButtonsUnclickable(true);
        screen_lock(true);
        BalanceRelative.CloseAll();
        timerRelative.CloseAll();
        BingoDevice.setStatus(0);
        db.updateDevice(BingoDevice);
        ClearGameCach();
        db.closeDB();
        super.onDestroy();
    }
    public void ClearGameCach(){
        db.deleteGameCache();
    }
}
