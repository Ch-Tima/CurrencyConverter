package com.example.moneyconverter.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.moneyconverter.R;
import com.example.moneyconverter.adapters.CurrencyAdapter;
import com.example.moneyconverter.models.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfCurrenciesFragment extends Fragment {

    private List<Currency> list;
    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private EditText search;
    private CallBack callBack;
    private boolean isFirstCurrency;


    public ListOfCurrenciesFragment(boolean isFirstCurrency, List<Currency> list, CallBack callBack) {
        this.callBack = callBack;
        this.list = list;
        this.isFirstCurrency = isFirstCurrency;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_list_of_currencies, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.list_currencies);
        adapter = new CurrencyAdapter(list, getContext(), currency -> setCurrency(currency));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        view.findViewById(R.id.btn_back).setOnClickListener(x ->  callBack.openCalculatorFragment());

        search = ((EditText)view.findViewById(R.id.search));
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().isBlank()){
                    adapter.setList(list);
                    return;
                }

                List<Currency> currencies = new ArrayList<>();
                for (Currency currency: list) {
                    var t = performKMPSearch(
                            currency.getCurrency().toLowerCase(),
                            s.toString().toLowerCase());
                    if(t.size() > 0){
                        currencies.add(currency);
                    }
                }
                adapter.setList(currencies);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public List<Integer> performKMPSearch(String text, String pattern) {
        int[] compliedPatternArray = compilePatternArray(pattern);

        int textIndex = 0;
        int patternIndex = 0;

        List<Integer> foundIndexes = new ArrayList<>();

        while (textIndex < text.length()) {
            if (pattern.charAt(patternIndex) == text.charAt(textIndex)) {
                patternIndex++;
                textIndex++;
            }
            if (patternIndex == pattern.length()) {
                foundIndexes.add(textIndex - patternIndex);
                patternIndex = compliedPatternArray[patternIndex - 1];
            }

            else if (textIndex < text.length() && pattern.charAt(patternIndex) != text.charAt(textIndex)) {
                if (patternIndex != 0)
                    patternIndex = compliedPatternArray[patternIndex - 1];
                else
                    textIndex = textIndex + 1;
            }
        }
        return foundIndexes;
    }
    public int[] compilePatternArray(String pattern) {
        int patternLength = pattern.length();
        int len = 0;
        int i = 1;
        int[] compliedPatternArray = new int[patternLength];
        compliedPatternArray[0] = 0;

        while (i < patternLength) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                compliedPatternArray[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = compliedPatternArray[len - 1];
                } else {
                    compliedPatternArray[i] = len;
                    i++;
                }
            }
        }
        System.out.println("Compiled Pattern Array " + Arrays.toString(compliedPatternArray));
        return compliedPatternArray;
    }

    private void setCurrency(Currency currency){
        if(isFirstCurrency){
            callBack.setFirstCurrency(currency);
        }
        else {
            callBack.setSecondCurrency(currency);
        }
        callBack.openCalculatorFragment();
    }


    public interface CallBack{
        void setFirstCurrency(Currency firstCurrency);
        void setSecondCurrency(Currency secondCurrency);
        void openCalculatorFragment();
    }

}