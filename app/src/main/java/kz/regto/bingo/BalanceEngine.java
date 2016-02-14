package kz.regto.bingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kz.regto.database.DatabaseHelper;
import kz.regto.database.d_balance;
import kz.regto.database.d_settings;
import kz.regto.json.Balance;
import kz.regto.json.ServerResult;
import kz.regto.json.SupportBalance;

public class BalanceEngine extends RelativeLayout {

    private Main prnt;
    private SupportBalance sb =new SupportBalance();
    private Handler balanceHandlerIncome = new Handler();
    private Handler balanceHandlerDevice = new Handler();
    private Handler balanceHBack = new Handler();
    private String field_balance="";
    private String field_currentEntry="";
    private String field_win="";

    public d_settings BalanceSet = new d_settings();

    private TwoTextViews tfield_balance;
    private TwoTextViews tfield_currentEntry;
    private TwoTextViews tfield_win;

    public BalanceEngine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView(context);
    }

    public BalanceEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView(context);
    }

    public BalanceEngine(Context context) {
        super(context);
        initView(context);
    }
    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BalanceEngine, 0, 0);
        try {
            field_balance = a.getString(R.styleable.BalanceEngine_field_balance);
            field_currentEntry = a.getString(R.styleable.BalanceEngine_field_currentEntry);
            field_win = a.getString(R.styleable.BalanceEngine_field_win);
        } finally {
            a.recycle();
        }
    }

    private void initView(Context context) {
        View view = inflate(getContext(), R.layout.balance_elements, null);
        tfield_balance = (TwoTextViews) view.findViewById(R.id.balance);
        tfield_currentEntry = (TwoTextViews) view.findViewById(R.id.CurrentEntry);
        tfield_win = (TwoTextViews) view.findViewById(R.id.win);
        if (field_balance.length()>0) tfield_balance.setField(field_balance);
        if (field_currentEntry.length()>0) tfield_currentEntry.setField(field_currentEntry);
        if (field_win.length()>0) tfield_win.setField(field_win);
        prnt = (Main)context;
        prnt.setBalanceElement(this);
        addView(view);
    }

    public void CloseAll() {
        sb.cancel(true);
        balanceHandlerIncome.removeCallbacks(updateGameBalanceIncome);
        balanceHandlerDevice.removeCallbacks(updateDeviceBalance);
        balanceHBack.removeCallbacks(backBalance);
    }

    public void RunBalanсeListening(String URL_Balance){
        //Инициируем получение баланса
        if (sb.getStatus()!= AsyncTask.Status.RUNNING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                sb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_Balance, "");
            else
                sb.execute(URL_Balance, "");

            BalanceSet = prnt.db.getSettings("device_balance");
            if (BalanceSet !=null) {BalanceSet.setSettingsValue("0");}
            else {
                BalanceSet = new d_settings();
                BalanceSet.setSettingsName("device_balance");
                BalanceSet.setSettingsValue("0");
            }
            prnt.db.DeleteServerBalance();
        }


        //Устанавливаем баланс устройства - единая строка, которая и ведется
        prnt.db.createNewSettings(BalanceSet);
        //Запускаем все слушалки балансов
        balanceHandlerIncome.postDelayed(updateGameBalanceIncome, 0);
        balanceHandlerDevice.postDelayed(updateDeviceBalance, 500);
        balanceHBack.postDelayed(backBalance,800);


    }

    public void StopBalanсeListening(){
        if (!sb.isCancelled()) sb.cancel(true);
    }

    public boolean RunBalanceSender(){
        //Инициируем отправку окончательного Баланса
        Balance sr = prnt.ntw.setBalance();
        if (sr == null) return false;
        int answer = sr.getId_balance();
        //здесь у нас должна быть более сложная логика, но пока оставляем
        if ((answer)==0) return false;
        else {
            //Сбрасываем локальный баланс и ждем загрузки от сервера
            setBalance(0);
            return true;
        }
    }

    private Runnable backBalance = new Runnable() {
        @Override
        public void run() {

            balanceHBack.postDelayed(this, 800);
        }
    };


    private Runnable updateGameBalanceIncome = new Runnable() {
        @Override
        public void run() {

            int currId = sb.getCurrentID();
            int currBalance = sb.getCurrentBalance();
            if (prnt.ntw.isNetworkAvailable(prnt)) {
                if ((currBalance != -1) && (currId > 0)) {
                    d_balance dBalance = new d_balance();
                    dBalance.setStatus(0);
                    dBalance.setOperation(currId);
                    dBalance.setSum(currBalance);
                    prnt.db.UpdateBalanceFROMServer(dBalance);
                }
            }
            else {
                if (!prnt.fNetworkError) prnt.NetworkError();
            }
            balanceHandlerIncome.postDelayed(this, 250);
        }
    };
    private Runnable updateDeviceBalance = new Runnable() {
        @Override
        public void run() {
            int cbal =0;
            cbal = prnt.db.getCurrentServerBalance();
            if (cbal > 0 ) {
                BalanceSet = prnt.db.getSettings("device_balance");
                int CurUpdate = Integer.parseInt(BalanceSet.getSettingsValue()) + cbal;
                prnt.db.setCurrentServerBalanceFlag();
                setBalance(CurUpdate);
            }
            balanceHandlerDevice.postDelayed(this, 300);
        }
    };

    public void setBalance(int balanceUpdate ){
        BalanceSet = prnt.db.getSettings("device_balance");
        int newSum = balanceUpdate;
        BalanceSet.setSettingsValue("" + newSum);
        if (prnt.db.updateSettings(BalanceSet)) tfield_balance.setField(""+ newSum);
    }
    public int getBalance(){
        if (prnt.db.getDBState() == DatabaseHelper.STATE_OPENED) {
            BalanceSet = prnt.db.getSettings("device_balance");
            return Integer.parseInt(BalanceSet.getSettingsValue());
        }
        else return -1;
    }
    public void setBalancePlus(int balanceUpdate){
        BalanceSet = prnt.db.getSettings("device_balance");
        int newSum = Integer.parseInt(BalanceSet.getSettingsValue()) + balanceUpdate;
        setBalance(newSum);
    }
    public void setEntry(int newEntry){
        int fEntr =  Integer.parseInt(tfield_currentEntry.getField()) + newEntry;
        tfield_currentEntry.setField("" + fEntr);

        BalanceSet = prnt.db.getSettings("device_balance");
        int newSum = Integer.parseInt(BalanceSet.getSettingsValue()) - newEntry;
        setBalance(newSum);
    }
    public  int getEntry(){
        int curentr = Integer.parseInt(tfield_currentEntry.getField());
        return curentr;
    }


    public void setWinSum(int newSum){
        setBalancePlus(newSum);
        tfield_win.setField("" + newSum);
        tfield_currentEntry.setField("0");
    }
    public void setWinZero(){
        tfield_win.setField("0");
    }
}