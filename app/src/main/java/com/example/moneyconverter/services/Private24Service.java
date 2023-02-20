package com.example.moneyconverter.services;

import com.example.moneyconverter.models.CurrencyRoot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Private24Service {

    @GET("p24api/exchange_rates")
    Call<CurrencyRoot> GetCurrencyRoot(@Query("date") String date);

}
