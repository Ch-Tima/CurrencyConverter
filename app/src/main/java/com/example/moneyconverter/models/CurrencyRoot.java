package com.example.moneyconverter.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CurrencyRoot implements Serializable {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("baseCurrency")
    @Expose
    private Integer baseCurrency;
    @SerializedName("baseCurrencyLit")
    @Expose
    private String baseCurrencyLit;
    @SerializedName("exchangeRate")
    @Expose
    private List<Currency> exchangeRate;

    public CurrencyRoot() {
        this.date = null;
        this.bank = null;
        this.baseCurrency = -1;
        this.baseCurrencyLit = null;
        this.exchangeRate = null;
    }

    public CurrencyRoot(String date, String bank, Integer baseCurrency, String baseCurrencyLit, List<Currency> exchangeRate) {
        this.date = date;
        this.bank = bank;
        this.baseCurrency = baseCurrency;
        this.baseCurrencyLit = baseCurrencyLit;
        this.exchangeRate = exchangeRate;
    }

    public String getDate() {
        return date;
    }

    public String getBank() {
        return bank;
    }

    public Integer getBaseCurrency() {
        return baseCurrency;
    }

    public String getBaseCurrencyLit() {
        return baseCurrencyLit;
    }

    public List<Currency> getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String toString() {
        return "CurrencyRoot{" +
                "date='" + date + '\'' +
                ", bank='" + bank + '\'' +
                ", baseCurrency=" + baseCurrency +
                ", baseCurrencyLit='" + baseCurrencyLit + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
