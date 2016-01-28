package kz.regto.database;

public class d_balance {
    //operation - это ид баланса на сервере
    private int operation;
    private int sum;
    private long datetime;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
    public int getOperation() {
        return operation;
    }

    public int getSum() {
        return sum;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
