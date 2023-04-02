package com.example.moneyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.moneyconverter.fragments.CalculatorFragment;
import com.example.moneyconverter.fragments.ErrorFragment;
import com.example.moneyconverter.fragments.ListOfCurrenciesFragment;
import com.example.moneyconverter.helpers.FileHelper;
import com.example.moneyconverter.models.Currency;
import com.example.moneyconverter.models.CurrencyRoot;
import com.example.moneyconverter.models.FilePaths;
import com.example.moneyconverter.models.UserTempData;
import com.example.moneyconverter.services.Private24Service;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final CalculatorFragment.CalculatorCallBack calculatorCallBack;
    private final ListOfCurrenciesFragment.CallBack listOfCurrenciesCallBack;
    private Retrofit retrofit = null;
    private Private24Service private24Service = null;
    private CurrencyRoot currencyRoot = null;
    private boolean isOldData = false;

    private Currency firstCurrency = null;
    private Currency secondCurrency = null;

    public MainActivity() {
        this.calculatorCallBack = new CalculatorFragment.CalculatorCallBack() {
            @Override
            public Currency getFirstCurrency() {
                return firstCurrency;
            }

            @Override
            public Currency getSecondCurrency() {
                return secondCurrency;
            }

            @Override
            public void swapCurrency() {
                var temp = firstCurrency;
                firstCurrency = secondCurrency;
                secondCurrency = temp;
            }

            @Override
            public boolean isOldData() {
                return isOldData;
            }

            @Override
            public void openListCurrencyFragment(boolean isFirstCurrency) {
                MainActivity.this.openListCurrencyFragment(isFirstCurrency);
            }
        };

        this.listOfCurrenciesCallBack = new ListOfCurrenciesFragment.CallBack() {
            @Override
            public void setFirstCurrency(Currency firstCurrency) {
                MainActivity.this.firstCurrency = firstCurrency;
            }

            @Override
            public void setSecondCurrency(Currency secondCurrency) {
                MainActivity.this.secondCurrency = secondCurrency;
            }

            @Override
            public void openCalculatorFragment() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new CalculatorFragment(calculatorCallBack), CalculatorFragment.class.getName())
                        .commit();
            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void loadDataFromAPI(){
        private24Service = retrofit.create(Private24Service.class);

        var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        var dateNow = dateFormat.format(Calendar.getInstance().getTime());

        private24Service.GetCurrencyRoot(dateNow).enqueue(new Callback<CurrencyRoot>() {
            @Override
            public void onResponse(Call<CurrencyRoot> call, Response<CurrencyRoot> response) {
                if(response.code() == 200 && response.body() != null && response.body().getExchangeRate() != null){
                    CurrencyRoot root = response.body();
                    UserTempData tempData = new UserTempData(root.getExchangeRate().get(0), root.getExchangeRate().get(1));
                    try {//Save currencyRoot to file
                        FileHelper.write(openFileOutput(FilePaths.API_DATA, MODE_PRIVATE), response.body());
                        if(isOldData){
                            var currency1 = root.getExchangeRate().stream().filter(x -> x.getCurrency().equals(firstCurrency.getCurrency()))
                                    .findFirst().orElse(root.getExchangeRate().get(0));
                            var currency2 = root.getExchangeRate().stream().filter(x -> x.getCurrency().equals(secondCurrency.getCurrency()))
                                    .findFirst().orElse(root.getExchangeRate().get(1));
                            tempData = new UserTempData(currency1, currency2);
                        }
                        isOldData = false;
                    } catch (FileNotFoundException ex) {
                        openMainFragment(root, tempData);
                    }finally {
                        openMainFragment(root, tempData);
                    }
                } else {
                    if(isOldData){
                        openMainFragment(currencyRoot,null);
                    }else {
                        openErrorFragment(response.code(), "Unexpected error!");
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrencyRoot> call, Throwable t) {
                if(isOldData){
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG);
                    openMainFragment(currencyRoot, null);
                }
                else {
                    currencyRoot = new CurrencyRoot();
                    openErrorFragment(404, "Please check your internet connection and restart the app.");
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

    private void openMainFragment(CurrencyRoot currencyRoot, UserTempData userTempData){
        this.currencyRoot = currencyRoot;
        if(userTempData != null){
            this.firstCurrency = userTempData.getLastFirstCurrency();
            this.secondCurrency = userTempData.getLastSecondCurrency();
        }else {
            if(this.firstCurrency == null && this.secondCurrency == null){
                this.firstCurrency = currencyRoot.getExchangeRate().get(0);
                this.secondCurrency = currencyRoot.getExchangeRate().get(1);
            }
        }
        //Open CalculatorFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new CalculatorFragment(calculatorCallBack), CalculatorFragment.class.getName())
                .commit();
    }

    private void openListCurrencyFragment(boolean isFirstCurrency){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        new ListOfCurrenciesFragment(isFirstCurrency, this.currencyRoot.getExchangeRate(), listOfCurrenciesCallBack))
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.privatbank.ua/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        try {
            currencyRoot = (CurrencyRoot) FileHelper.read(this.openFileInput(FilePaths.API_DATA));

            if(currencyRoot == null) loadDataFromAPI();

            var userTempData = (UserTempData)FileHelper.read(this.openFileInput(FilePaths.TEMP_DATA));
            this.firstCurrency = userTempData.getLastFirstCurrency();
            this.secondCurrency = userTempData.getLastSecondCurrency();
            var dateNow = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

            if(dateNow.equals(currencyRoot.getDate())){
                openMainFragment(currencyRoot, userTempData);
            }else {//try update data
                isOldData = true;
                loadDataFromAPI();
            }
        } catch (Exception e) {
            loadDataFromAPI();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.firstCurrency == null || this.secondCurrency == null){
            return;
        }
        try {
            FileHelper.write(openFileOutput(FilePaths.TEMP_DATA, MODE_PRIVATE), new UserTempData(this.firstCurrency, this.secondCurrency));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}