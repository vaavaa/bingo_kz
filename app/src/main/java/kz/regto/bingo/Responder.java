package kz.regto.bingo;

import android.util.Log;

/**
 * Created by spt on 04.11.2015.
 */
public class Responder implements TimerEvent {
    @Override
    public void TimerOver(){
        Log.v("1", "Таймер завершился");
    }
    @Override
    public void TimerStarted(){
        Log.v("1", "Таймер начался");
    }
}