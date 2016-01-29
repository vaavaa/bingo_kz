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

import kz.regto.database.d_balance;
import kz.regto.json.ServerResult;
import kz.regto.json.SupportBalance;

public class BalanceEngine extends RelativeLayout {

    private Main prnt;
    private SupportBalance sb =new SupportBalance();
    private Handler balanceHandlerIncome = new Handler();
    private List<BalanceEvent> listeners = new ArrayList<>();
    private String field_balance="";
    private String field_currentEntry="";
    private String field_win="";

    private int currBalance;
    private int currId;

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
        this.addListener((Main) context);
        prnt = (Main)context;
        prnt.setBalanceElement(this);
        addView(view);
    }

    public void CloseAll() {
        sb.cancel(true);
        balanceHandlerIncome.removeCallbacks(updateGameBalanceIncome);
    }

    public void RunBalanсeListening(String URL_Balance){
        //Инициируем получение баланса
        if (sb.getStatus()!= AsyncTask.Status.RUNNING)
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                sb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,URL_Balance,"");
            else
                sb.execute(URL_Balance,"");
        balanceHandlerIncome.postDelayed(updateGameBalanceIncome, 0);
    }

    public void StopBalanсeListening(){
        if (!sb.isCancelled()) sb.cancel(true);
    }

    public void RunBalanceSender(String URL_timer){
        //Инициируем отправку окончательного Баланса
        ServerResult sr = prnt.ntw.setBalance();
        int answer = sr.getAnswer();
        //здесь у нас должна быть более сложная логика, но пока оставляем
        if ((answer)!=0) {
            Toast toast = Toast.makeText(prnt, R.string.ServerAnswer, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Runnable updateGameBalanceIncome = new Runnable() {
        @Override
        public void run() {

            if (sb.getCurrentBalance()!=-1){

                currId = sb.getCurrentID();
                currBalance = sb.getCurrentBalance();

                d_balance dBalance = new d_balance();
                dBalance.setStatus(0);
                dBalance.setOperation(currId);
                dBalance.setSum(currBalance);
                dBalance = prnt.db.UpdateBalanceSmart(dBalance);

//                field_balance = Integer.toString(dBalance.getSum());
//                tfield_balance.setField(field_balance);
            }
            balanceHandlerIncome.postDelayed(this,0);

        }
    };

    public void setBalance(){


    }
    public int getBalance(){
        return currBalance;
    }

    //Принимаем подписчиков на события баланса
    public void addListener(BalanceEvent toAdd) { listeners.add(toAdd);}

    public String getField_balance() {
        return field_balance;
    }

    public String getField_currentEntry() {
        return field_currentEntry;
    }

    public void setField_win(String field_win) {
        this.field_win = field_win;
    }

    public String getField_win() {
        return field_win;
    }

    public void setField_balance(String field_balance) {
        this.field_balance = field_balance;
    }

    public void setField_currentEntry(String field_currentEntry) {
        this.field_currentEntry = field_currentEntry;
    }
}