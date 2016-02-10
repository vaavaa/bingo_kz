package kz.regto.bingo;

import android.graphics.Color;

public class LineBalls {
    private String number;
    private int background;
    private int textColor;

    public int getTextColor() {
        return textColor;
    }

    public int getBackground() {
        return background;
    }

    public String getNumber() {
        return number;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
