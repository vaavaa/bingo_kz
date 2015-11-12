package kz.regto.bingo;

/**
 * Created by spt on 04.11.2015.
 */
public interface TimerEvent {
    void TimerStarted(TimerRelative tr);
    void TimerOver();
}
