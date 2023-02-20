package com.example.moneyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.moneyconverter.fragments.CalculatorFragment;
import com.example.moneyconverter.fragments.ErrorFragment;
import com.example.moneyconverter.helpers.FileHelper;
import com.example.moneyconverter.models.Currency;
import com.example.moneyconverter.models.CurrencyRoot;
import com.example.moneyconverter.models.FilePaths;
import com.example.moneyconverter.services.Private24Service;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit = null;
    private Private24Service private24Service = null;
    private CurrencyRoot currencyRoot = null;
    private boolean isOldData = false;

    private Currency firstCurrency;
    private Currency secondCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.privatbank.ua/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            currencyRoot = FileHelper.read(this.openFileInput(FilePaths.API_DATA));

            if(currencyRoot == null) loadDataFromAPI();

            var dateNow = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

            if(dateNow.equals(currencyRoot.getDate())){
                openMainFragment(currencyRoot);
            }else {//try update data
                isOldData = true;
                loadDataFromAPI();
            }
        } catch (Exception e) {
            loadDataFromAPI();
        }

    }

    private void loadDataFromAPI(){
        private24Service = retrofit.create(Private24Service.class);

        var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        var dateNow = dateFormat.format(Calendar.getInstance().getTime());

        private24Service.GetCurrencyRoot(dateNow).enqueue(new Callback<CurrencyRoot>() {
            @Override
            public void onResponse(Call<CurrencyRoot> call, Response<CurrencyRoot> response) {
                if(response.code() == 200 && response.body() != null && response.body().getExchangeRate() != null){
                    try {//Save currencyRoot to file
                        FileHelper.write(openFileOutput(FilePaths.API_DATA, MODE_PRIVATE), response.body());
                        isOldData = false;
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    openMainFragment(response.body());
                }
                else {
                    if(isOldData){
                        openMainFragment(currencyRoot);
                    }else {
                        openErrorFragment(response.code(), "Unexpected error!");
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrencyRoot> call, Throwable t) {
                if(isOldData){
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG);
                    openMainFragment(currencyRoot);
                }
                else {
                    currencyRoot = new CurrencyRoot();
                    openErrorFragment(404, "Please check your internet connection and restart the app.");
                    //Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openErrorFragment(Integer code, String message){
        var error = new Bundle();
        error.putString("code", code.toString());
        error.putString("message", message);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, ErrorFragment.class, error)
                .commit();
    }
    private void openMainFragment(CurrencyRoot currencyRoot){
        //Init
        this.currencyRoot = currencyRoot;
        firstCurrency = currencyRoot.getExchangeRate().get(0);
        secondCurrency = currencyRoot.getExchangeRate().get(1);

        //Open CalculatorFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, CalculatorFragment.class, null)
                .commit();
    }

    public List<Currency> getCurrencies() {
        return currencyRoot.getExchangeRate();
    }

    public Currency getFirstCurrency() {
        return firstCurrency;
    }

    public void setFirstCurrency(Currency firstCurrency) {
        this.firstCurrency = firstCurrency;
    }

    public Currency getSecondCurrency() {
        return secondCurrency;
    }

    public void setSecondCurrency(Currency secondCurrency) {
        this.secondCurrency = secondCurrency;
    }
    public void swapCurrency(){
        var temp = firstCurrency;
        firstCurrency = secondCurrency;
        secondCurrency = temp;
    }

    public boolean isOldData() {
        return isOldData;
    }
}