package kz.regto.json;

/**
 * Created by Старцев on 25.11.2015.
 */
public class CurrentTime {
    private long currenttime;
    private int winnumber;
    private long finalCounter;

    public long getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(long currenttime) {
        this.currenttime = currenttime;
    }

    public int getWinnumber() {
        return winnumber;
    }

    public void setWinnumber(int winnumber) {
        this.winnumber = winnumber;
    }

    public long getFinalCounter() {
        return finalCounter;
    }
    public void setFinalCounter(long finalCounter) {
        this.finalCounter = finalCounter;
    }
}
