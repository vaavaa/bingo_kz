package kz.regto.bingo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import kz.regto.database.d_device;
import kz.regto.database.d_entry_set;
import kz.regto.database.d_game;
import kz.regto.database.d_settings;
import kz.regto.json.Network;

public class Main extends AppCompatActivity implements TimerEvent, BoardGridEvents {

    View mRootView;
    MainContainer mc;
    TextView GameCode;
    BoardGrid board;
    Lock lck;
    FrameLayout frameLayout;
    TimerRelative timerRelative;
    BalanceEngine BalanceRelative;
    public int ilevelset = 1;

    //Network Helper
    public Network ntw;
    public boolean fNetworkError = false;

    // Database Helper
    public DatabaseHelper db;
    public d_device BingoDevice;
    public d_game dGame;
    public List<d_entry_set> botEntrySet = null;


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
        if (BingoDevice == null) {
            //Создаем устройство
            BingoDevice = new d_device();
            BingoDevice.setDeviceCode(new Utils().getUniquePsuedoID());
            db.createNewDevice(BingoDevice);
        }
        final EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
        //Network initialisation
        ntw = new Network(this);
        String NetworkPath = "";
        if (db.getSettings("network_path") != null)
            NetworkPath = db.getSettings("network_path").getSettingsValue();
        if (NetworkPath.length() > 0) {
            ntw.setNetworkPath(NetworkPath);
            ET1.setText(NetworkPath);
            if (ntw.isNetworkAvailable(this))
                if (ntw.ConnectionExist()) {
                    BingoDevice = ntw.getDeviceFromServer(BingoDevice);
                    db.updateDevice(BingoDevice);
                }
        }
        else {
            if (ET1.getText().toString().length()==0) ET1.setText(R.string.http);
        }
        mc = (MainContainer) findViewById(R.id.main_board);

        board = (BoardGrid) findViewById(R.id.board_grid);
        GameCode = (TextView) findViewById(R.id.GameCode);


        //Ставка в 100
        this.findViewById(R.id.entry100).setSelected(true);

        lck = (Lock) findViewById(R.id.r_lock);
        lck.bringToFront();
        setButtonsVisible(false);

        ET1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ET1.getCurrentTextColor() == Color.parseColor("#00FFFFFF"))
                    ET1.setTextColor(Color.parseColor("#000000"));
                else ET1.setTextColor(Color.parseColor("#00FFFFFF"));
                return false;
            }
        });

    }

    public void setIlevelset(int ilevelset) {
        this.ilevelset = ilevelset;
    }

    public int getIlevelset() {
        return ilevelset;
    }


    public void setButtonsUnclickable(boolean bEnable) {
        findViewById(R.id.card_step_back).setEnabled(!bEnable);
        findViewById(R.id.make_crd_null).setEnabled(!bEnable);
        findViewById(R.id.entry1000).setEnabled(!bEnable);
        findViewById(R.id.entry500).setEnabled(!bEnable);
        findViewById(R.id.entry200).setEnabled(!bEnable);
        findViewById(R.id.entry100).setEnabled(!bEnable);
        //findViewById(R.id.x2).setEnabled(!bEnable);
        findViewById(R.id.auto).setEnabled(!bEnable);
        findViewById(R.id.quit).setEnabled(!bEnable);
        if (bEnable) {
            findViewById(R.id.card_step_back).setAlpha(.7f);
            findViewById(R.id.make_crd_null).setAlpha(.7f);
            findViewById(R.id.entry1000).setAlpha(.7f);
            findViewById(R.id.entry500).setAlpha(.7f);
            findViewById(R.id.entry200).setAlpha(.7f);
            findViewById(R.id.entry100).setAlpha(.7f);
            //findViewById(R.id.x2).setAlpha(.7f);
            findViewById(R.id.auto).setAlpha(.7f);
            findViewById(R.id.quit).setAlpha(.7f);
            board.setBoard_blocked(true);
        } else {
            findViewById(R.id.card_step_back).setAlpha(1f);
            findViewById(R.id.make_crd_null).setAlpha(1f);
            findViewById(R.id.entry1000).setAlpha(1f);
            findViewById(R.id.entry500).setAlpha(1f);
            findViewById(R.id.entry200).setAlpha(1f);
            findViewById(R.id.entry100).setAlpha(1f);
            //findViewById(R.id.x2).setAlpha(1f);
            findViewById(R.id.auto).setAlpha(1f);
            findViewById(R.id.quit).setAlpha(1f);
            board.setBoard_blocked(false);
            board.MakeTouchedRectangleArea();
        }
    }

    public void setButtonsVisible(boolean bVisible) {
        if (bVisible) {
            findViewById(R.id.card_step_back).setVisibility(View.VISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.VISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.VISIBLE);
            findViewById(R.id.entry500).setVisibility(View.VISIBLE);
            findViewById(R.id.entry200).setVisibility(View.VISIBLE);
            findViewById(R.id.entry100).setVisibility(View.VISIBLE);
            //findViewById(R.id.x2).setVisibility(View.VISIBLE);
            findViewById(R.id.auto).setVisibility(View.VISIBLE);
            findViewById(R.id.quit).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.card_step_back).setVisibility(View.INVISIBLE);
            findViewById(R.id.make_crd_null).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry1000).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry500).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry200).setVisibility(View.INVISIBLE);
            findViewById(R.id.entry100).setVisibility(View.INVISIBLE);
            //findViewById(R.id.x2).setVisibility(View.INVISIBLE);
            findViewById(R.id.auto).setVisibility(View.INVISIBLE);
            findViewById(R.id.quit).setVisibility(View.INVISIBLE);
        }
    }

    public void setLockerElement(FrameLayout FL) {
        frameLayout = FL;
    }

    public void setTimerElement(TimerRelative tr) {
        timerRelative = tr;
    }

    public void setBalanceElement(BalanceEngine balanceElement) {
        BalanceRelative = balanceElement;
    }

    public void screen_lock_starting_procedure() {
        //hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Если связи нет, сообщение и выйти
        if (!ntw.isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this, R.string.NetworkIsDown, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //Забираем линк на сервер и добавляем слеш в конце если его нет
        EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
        if (ET1.getText().toString().length() > 0) {
            String ntwPath = ET1.getText().toString();
            String slash = ntwPath.substring(ntwPath.length() - 1, ntwPath.length());
            if (!slash.equals("/")) ntwPath = ntwPath.concat("/");
            ET1.setText(ntwPath);
            ntw.setNetworkPath(ntwPath);
        } else {
            Toast toast = Toast.makeText(this, R.string.ServerIsNotReachable, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //проверяем путь
        if (ntw.getNetworkPath().length() == 0
                || !ntw.ConnectionExist()) {
            Toast toast = Toast.makeText(this, R.string.ServerIsNotReachable, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        final EditText ET = (EditText) frameLayout.findViewById(R.id.pin);
        if (ET.getText().toString().length() == 0) {
            if (db.getSettings("pin_code") != null) {
                ET.setText(db.getSettings("pin_code").getSettingsValue());
            } else {
                Toast toast = Toast.makeText(this, R.string.PinIsEmpty, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        int iPinCode = Integer.parseInt(ET.getText().toString());
        int pinCode = ntw.getServerValue("web_service.php?comm=pincode&par=0", 4000).getIntvalue();
        if (iPinCode != pinCode) {
            Toast toast = Toast.makeText(this, R.string.PinIsNotCorrect, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            d_settings settings = new d_settings();
            settings.setSettingsName("pin_code");
            settings.setSettingsValue(Integer.toString(pinCode));
            db.createNewSettings(settings);
        }

        if (db.getSettings("exit_code") == null) {
            d_settings settings = new d_settings();
            settings.setSettingsName("exit_code");
            settings.setSettingsValue("-1");
            db.createNewSettings(settings);
        }else{
            if (db.getSettings("exit_code").getSettingsValue().equals("-1")){
                //Изменили статус баланса на сервере
                if (ntw.setBalanceStatus(2)==null) {
                    Toast toast = Toast.makeText(this, R.string.err_set_status, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        }

        BingoDevice = ntw.getDeviceFromServer(BingoDevice);
        db.updateDevice(BingoDevice);

        if (BingoDevice.getStatus() != 0) {
            Toast toast = Toast.makeText(this, R.string.StatusIsNotCorrect, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (dGame!=null){
            //Считаем выйгрыш, если ничего нет, будет 0
            int iWin = db.getGameSum(dGame.getId(), dGame.getWin_ball());
            if (iWin > BingoDevice.getGame_limit()) {
                Toast toast = Toast.makeText(this, R.string.OutOfLimit, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        //Записали что за устройстово
        VerticalTextView vtv  = (VerticalTextView)findViewById(R.id.devCode);
        vtv.setText(BingoDevice.getComment());

        String game_id_code_path = getResources().getString(R.string.ntw_pth_server_gameid);
        BalanceRelative.RunBalanсeListening(ntw.getNetworkPath().concat("/balance_outcome.php?device_server_id=" + BingoDevice.getServerDeviceId()));
        timerRelative.HTTPRunTimer(ntw.getNetworkPath().concat(game_id_code_path));

        screen_lock(false);
        TimerStarted_sub();

        WinBallContainer WBC = (WinBallContainer) this.findViewById(R.id.win_ball_container);
        //get games log from server
        int[] games_log =  ntw.getGameLog();
        WBC.setAll_Visible(games_log);
    }

    public void screen_lock(boolean bSkrn) {

        //Если команда - разблокировать и состояние утсройсва активно, то разблокировать
        if (!bSkrn) {
            Animation push_up = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
            lck.startAnimation(push_up);
            lck.setVisibility(View.GONE);
            lck.invalidate();
            setButtonsVisible(true);
        } else {
            setButtonsVisible(false);
            Animation push_up_in = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            lck.startAnimation(push_up_in);
            lck.invalidate();
            lck.setVisibility(View.VISIBLE);
            lck.bringToFront();
        }

    }

    public void unlocked(View v) {
        screen_lock_starting_procedure();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        showSystemUi();
    }

    public void showSystemUi() {
        int flag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mRootView.setSystemUiVisibility(flag);
        ActionBar bar = getSupportActionBar();
        if (bar != null && bar.isShowing()) bar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        showSystemUi();
        //устанавливаем баланс полсе возвращения в приложение
        BalanceRelative.setBalance(BalanceRelative.getBalance());
    }

    @Override
    public void TimerOver() {
        final WinBallContainer WBC = (WinBallContainer) this.findViewById(R.id.win_ball_container);
        WBC.setAll_lock(true);
        //Когда таймер кончился, ждем выпавшего шарика
        setButtonsUnclickable(true);
    }

    @Override
    public void GameOver() {
        //ОБновили игру
        db.updateGame(dGame);

        final WinBallContainer WBC = (WinBallContainer) this.findViewById(R.id.win_ball_container);
        WBC.UpdateNewOne(Integer.toString(dGame.getWin_ball()), db.getAllGameEntrySet(dGame.getId()), this);
        WBC.setAllSelected_false();

        //Считаем выйгрыш, если ничего нет, будет 0
        int iWin = db.getGameSum(dGame.getId(), dGame.getWin_ball());

        //Текущий баланс увеличили на выйгрыш/ установли поля
        BalanceRelative.setWinSum(iWin);


        //Обновляем баланс устройства и ни какой ставки в начеле новой игры нет
        db.updateDevice(BingoDevice);
        //Обнулили ставки робота
        botEntrySet = null;

        //Проверяем, не вышел ли выйгрыш за лимит.
        // Такого не должно происходить, но тем ни менее

        if (BingoDevice.getGame_limit()>= iWin) {
            if (BalanceRelative.RunBalanceSender()) {
                //Запускаем новую игру c задержкой в 2 секунды
                Handler temp_handler = new Handler();
                temp_handler.postDelayed(new Runnable() {
                    public void run() {
                        mc.ClearBoard(MainContainer.CLEAR_BOARD_ONLY);
                        setButtonsUnclickable(false);
                        WBC.setAll_lock(false);
                        BalanceRelative.setWinZero();
                        if (db.getDBState() == DatabaseHelper.STATE_OPENED) TimerStarted_sub();
                    }
                }, getResources().getInteger(R.integer.NewGameResultDelay)); //4500 задержка перед новой игрой.
            } else {
                Toast toast = Toast.makeText(this, R.string.NoSaveSuccess, Toast.LENGTH_SHORT);
                toast.show();
                screen_lock(true);
            }
        }
        else {
            Toast toast = Toast.makeText(this, R.string.OutOfLimit, Toast.LENGTH_SHORT);
            toast.show();
            screen_lock(true);
        }
    }

    private void TimerStarted_sub() {
        int balance = BalanceRelative.getBalance();

        if (BingoDevice.getStatus() != 0) {
            Toast toast = Toast.makeText(this, R.string.DeviceIsNotActive, Toast.LENGTH_SHORT);
            toast.show();
            screen_lock(true);
            return;
        }
        if (balance < 100) {
            Toast toast = Toast.makeText(this, R.string.BalanceIsLow, Toast.LENGTH_SHORT);
            toast.show();
            screen_lock(true);
            return;
        }

        //Меняем цвет на невидимый
        EditText ET1 = (EditText) frameLayout.findViewById(R.id.n_path);
        ET1.setTextColor(Color.parseColor("#00FFFFFF"));
        //Pin code's field has no text
        EditText ET = (EditText) frameLayout.findViewById(R.id.pin);
        ET.setText("");

        dGame = new d_game();
        String game_id_code_path = getResources().getString(R.string.ntw_pth_server_gameid);
        if (timerRelative.getServerGameCode() == 0)
            dGame.setServer_game_id(ntw.getTimer(ntw.getNetworkPath().concat(game_id_code_path)).getGame_id());
        else dGame.setServer_game_id(timerRelative.getServerGameCode());
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

        //Получаем код игры на устройсте.
        String nCode = "";
//        d_game l_gameCode = db.getLastGame();
//        if (l_gameCode == null) nCode = "AA-0000";
//        else nCode = l_gameCode.getGameCode();
        //Создаем новую игру
        nCode = timerRelative.getServerGameSerial().concat("-"+BingoDevice.getServerDeviceId());   //timerRelative.GenerateNewGameCode(nCode, Integer.toString(BingoDevice.getServerDeviceId()));
        dGame.setGameCode(nCode);


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

        setButtonsUnclickable(false);
        //Запустили таймер
        timerRelative.StartTimer();
        //Запустили баланс
        fNetworkError = false;
        ShowWinText(false);
    }

    @Override
    public void entrySet(boolean isBalanced) {
        if (isBalanced) {
            int iEntry = getEntryfromLevel(this.getIlevelset());
            BalanceRelative.setEntry(iEntry);
        }
    }

    private int getEntryfromLevel(int lvl) {
        int iEntry = 0;
        switch (lvl) {
            case 1:
                iEntry = 100;
                break;
            case 2:
                iEntry = 200;
                break;
            case 3:
                iEntry = 500;
                break;
            case 4:
                iEntry = 1000;
                break;
        }
        return iEntry;
    }

    public int getLevelfromEntry(int Entry) {
        int iEntry = 0;
        switch (Entry) {
            case 100:
                iEntry = 1;
                break;
            case 200:
                iEntry = 2;
                break;
            case 500:
                iEntry = 3;
                break;
            case 1000:
                iEntry = 4;
                break;
        }
        return iEntry;
    }

    public void botsEntry(View view) {
        if (botEntrySet == null) botEntrySet = new ArrayList<>();
        botEntrySet = board.set_random_entry(botEntrySet);
    }

    public void x2_button(View view) {
        List<d_entry_set> cl = db.getAllGameEntrySet(dGame.getId());
        int balance = db.getGameCurrentSum(dGame.getId());
        int i_myBalance = BalanceRelative.getBalance();
        if (balance > 0 && i_myBalance >= balance) {
            BoardGrid bG = (BoardGrid) this.findViewById(R.id.board_grid);
            bG.X2_pack(cl);
        } else {
            Toast toast = Toast.makeText(this, R.string.x2Balance, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void EntrySet(View view) {
        findViewById(R.id.entry100).setSelected(false);
        findViewById(R.id.entry200).setSelected(false);
        findViewById(R.id.entry500).setSelected(false);
        findViewById(R.id.entry1000).setSelected(false);

        Button bPushed = (Button) view;

        bPushed.setSelected(true);
        String entryset = bPushed.getText().toString();
        if (entryset.equals("100")) this.setIlevelset(1);
        if (entryset.equals("200")) this.setIlevelset(2);
        if (entryset.equals("500")) this.setIlevelset(3);
        if (entryset.equals("1000")) this.setIlevelset(4);
    }

    public void ClearBoard(View view) {
        clearBoard();
    }

    public void stepBack(View view) {
        mc.stepBack();
        final WinBallContainer WBC = (WinBallContainer) this.findViewById(R.id.win_ball_container);
        WBC.setAll_lock(true);
        //Возвращем выбранные(если есть) в положение не выбран
        WBC.setAllSelected_false();
        WBC.setAll_lock(false);
    }

    private void clearBoard() {
        BalanceRelative.setEntry((-1) * BalanceRelative.getEntry());
        mc.ClearBoard(MainContainer.CLEAR_ALL);
        final WinBallContainer WBC = (WinBallContainer) this.findViewById(R.id.win_ball_container);
        WBC.setAll_lock(true);
        //Возвращем выбранные(если есть) в положение не выбран
        WBC.setAllSelected_false();
        WBC.setAll_lock(false);
    }

    public void ball_clicked(View v) {
        board.showGame(v);
    }

    public void NetworkError() {
        clearBoard();
        timerRelative.StopTimer(false);
        screen_lock(true);
        screen_lock_starting_procedure();
        fNetworkError = true;
    }

    public void quit(View v) {
        //Возвращем все в баланс
        clearBoard();
        //Изменили статус баланса на сервере
        if (ntw.setBalanceStatus(2)!=null) {
            ShowWinText(true);
            //Обнулили баланс
            BalanceRelative.setBalance(0);
            //Остановили таймер
            BingoDevice.setStatus(2);
            BingoDevice = ntw.setDeviceOnServer(BingoDevice);
            db.updateDevice(BingoDevice);
            timerRelative.StopTimer(false);
            screen_lock(true);
            Toast toast = Toast.makeText(this, R.string.GameStop, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(this, R.string.BalanceStatusError, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void ShowWinText(boolean HideShowAction) {
        TextView t1 = (TextView) findViewById(R.id.WinLabel);
        TextView t2 = (TextView) findViewById(R.id.WinFigure);
        if ((t1.getVisibility() == View.INVISIBLE) && !HideShowAction) return;
        else {
            //Если истина, показываем
            if (HideShowAction) {
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
                t2.setText("" + BalanceRelative.getBalance());
            }
            //Если нет то скрываем
            else {
                t1.setVisibility(View.INVISIBLE);
                t2.setVisibility(View.INVISIBLE);
                t2.setText("");
            }
        }
    }

    public void ball_cancel_clicked(View v) {
        v.setVisibility(View.INVISIBLE);
        WinBallContainer WBC = (WinBallContainer) findViewById(R.id.win_ball_container);
        mc.ClearAllAlfa05();
        WBC.setAllSelected_false();
    }

    @Override
    protected void onDestroy() {
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

    public void ClearGameCach() {
        db.deleteGameCache();
    }
}
