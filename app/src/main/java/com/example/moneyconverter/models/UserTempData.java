package com.example.moneyconverter.models;

import java.io.Serializable;

public class UserTempData implements Serializable {

    private Currency lastFirstCurrency;
    private Currency lastSecondCurrency;

    private double lastFirstNumber;
    private double lastSecondNumber;

    public UserTempData(Currency lastFirstCurrency, Currency lastSecondCurrency) {
        this.lastFirstCurrency = lastFirstCurrency;
        this.lastSecondCurrency = lastSecondCurrency;
    }

    public Currency getLastFirstCurrency() {
        return lastFirstCurrency;
    }

    public void setLastFirstCurrency(Currency lastFirstCurrency) {
        this.lastFirstCurrency = lastFirstCurrency;
    }

    public Currency getLastSecondCurrency() {
        return lastSecondCurrency;
    }

    public void setLastSecondCurrency(Currency lastSecondCurrency) {
        this.lastSecondCurrency = lastSecondCurrency;
    }

    public double getLastFirstNumber() {
        return lastFirstNumber;
    }

    public void setLastFirstNumber(double lastFirstNumber) {
        this.lastFirstNumber = lastFirstNumber;
    }

    public double getLastSecondNumber() {
        return lastSecondNumber;
    }

    public void setLastSecondNumber(double lastSecondNumber) {
        this.lastSecondNumber = lastSecondNumber;
    }
}
