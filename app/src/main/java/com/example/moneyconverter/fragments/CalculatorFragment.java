package com.example.moneyconverter.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneyconverter.MainActivity;
import com.example.moneyconverter.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class CalculatorFragment extends Fragment {
    private MainActivity mainActivity;


    private Double tempFirstNumber;
    private String selectedSymbol;
    private boolean isShowEquals;
    private DecimalFormat decimalFormat;


    private TextView firstNumber;
    private TextView firstCurrencyName;
    private TextView secondNumber;
    private TextView secondCurrencyName;
    private ImageButton swapButtonImage;

    public CalculatorFragment() {
        tempFirstNumber = 0.0;
        selectedSymbol = "";
        isShowEquals = false;
        decimalFormat = new DecimalFormat("#.###");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_calculator, container, false);

        if(mainActivity.isOldData()){
            ((TextView)view.findViewById(R.id.message)).setText("Data is outdated!");
        }

        //TableLayout with buttons
        var calculator = (TableLayout)view.findViewById(R.id.calculator_panel);
        //set on event "onClickNumber" on buttons
        for (int i = 0; i < calculator.getChildCount(); i++) {
            var tableRow = (TableRow)calculator.getChildAt(i);
            for (int j = 0; j < tableRow.getChildCount(); j++){
                if(tableRow.getChildAt(j).getTag() == null){
                    tableRow.getChildAt(j).setOnClickListener(x -> onClickNumber(x));
                }
                else if(tableRow.getChildAt(j).getTag().toString().equals("operation")){
                    tableRow.getChildAt(j).setOnClickListener(x -> onClickSymbol(x));
                }
                else;
            }
        }

        //set event to other buttons
        view.findViewById(R.id.btn_equals).setOnClickListener(x -> onClickEquals());
        view.findViewById(R.id.btn_clear).setOnClickListener(x -> clear());
        view.findViewById(R.id.btn_erase).setOnClickListener(x -> eraseLastNumber());

        //button swap currency
        swapButtonImage = view.findViewById(R.id.btn_swap);
        swapButtonImage.setOnClickListener(x -> swapCurrency());

        //first_currency_ui
        view.findViewById(R.id.first_currency).setOnClickListener(x -> openListCurrencyFragment("first"));
        firstCurrencyName = view.findViewById(R.id.first_currency_name);
        firstCurrencyName.setText(mainActivity.getFirstCurrency().getCurrency());
        firstNumber = view.findViewById(R.id.first_currency_number);

        //second_currency_ui
        view.findViewById(R.id.second_currency).setOnClickListener(x -> openListCurrencyFragment("second"));
        secondCurrencyName = view.findViewById(R.id.second_currency_name);
        secondCurrencyName.setText(mainActivity.getSecondCurrency().getCurrency());
        secondNumber = view.findViewById(R.id.second_currency_number);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = ((MainActivity)context);
    }

    private void openListCurrencyFragment(String str){
        var bundle  = new Bundle();
        bundle.putString("n", str);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, ListOfCurrenciesFragment.class, bundle)
                .commit();
    }
    private void onClickNumber(View view){
        var btn = (Button)view;

        var currentSrt = firstNumber.getText().toString();

        if(isShowEquals){
            tempFirstNumber = Double.valueOf(currentSrt);
            firstNumber.setText(btn.getText().toString());
            isShowEquals = false;
        }
        else {
            firstNumber.setText((currentSrt.equals("0") ? "" : currentSrt) + btn.getText().toString());
            calculationOfResult();
        }
    }
    private void onClickSymbol(View view){
        var symbol = ((Button)view).getText().toString();
        var currentNumber = Double.valueOf(this.firstNumber.getText().toString());

        if(symbol.equals(".") && firstNumber.getText().toString().indexOf(".") == -1){
            firstNumber.setText(firstNumber.getText().toString() + symbol);
            return;
        }

        if((!selectedSymbol.isBlank() && !symbol.equals(".") &&
                (currentNumber.equals(0.0) || currentNumber.equals(0))) || isShowEquals){
            selectedSymbol = symbol;
            return;
        }

        if(selectedSymbol.isBlank()){
            this.selectedSymbol = symbol;
            tempFirstNumber = currentNumber;
            this.firstNumber.setText("0");
        }
        else {
            currentNumber = calculation(tempFirstNumber, currentNumber, selectedSymbol);

            this.selectedSymbol = symbol;
            this.firstNumber.setText(currentNumber.toString());
            this.tempFirstNumber = 0.0;

            this.isShowEquals = true;

        }
        calculationOfResult();
    }
    private void onClickEquals() {
        var currentNumber = Double.valueOf(this.firstNumber.getText().toString());

        currentNumber = calculation(tempFirstNumber, currentNumber, selectedSymbol);
        this.firstNumber.setText(currentNumber.toString());
        selectedSymbol = "";
        tempFirstNumber = 0.0;
        isShowEquals = false;

        calculationOfResult();
    }

    private Double calculation(Double n1, Double n2, String symbol){
        double result = 0.0;
        switch (selectedSymbol){
            case "+":
                result = n1 + n2;
                break;
            case "-":
                if(n1 == 0){
                    result = n2;
                }
                else {
                    result = n1 - n2;
                }
                break;
            case "*":
                result = n1 * n2;
                break;
            case "/":
                if(n2 != 0){
                    result = n1 / n2;
                }
                else {
                    Toast.makeText(mainActivity.getApplicationContext(), "You can't divide by zero!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return Double.parseDouble(decimalFormat.format(result).replace(',', '.'));
    }

    private void calculationOfResult(){
        try {

            var count = Double.valueOf(firstNumber.getText().toString());
            var buyFirst = mainActivity.getFirstCurrency().getSaleRateNB();
            var buySecond = mainActivity.getSecondCurrency().getSaleRateNB();

            double result = 0.0;

            if(!mainActivity.getFirstCurrency().getCurrency().equals("UAH") &&
                    !mainActivity.getSecondCurrency().getCurrency().equals("UAH")){
                result = buyFirst / (buySecond) * count;
            }
            else if(mainActivity.getFirstCurrency().getCurrency().equals("UAH")){
                result = count / buySecond;
            }
            else{
                result = buyFirst * count;
            }
            secondNumber.setText(decimalFormat.format(result));

        }catch (Exception ex){
            firstNumber.setText("0");
            secondNumber.setText("0");
        }
    }

    private void clear(){
        firstNumber.setText("0");
        secondNumber.setText("0");
        selectedSymbol = "";
        tempFirstNumber = 0.0;
        isShowEquals = false;
    }
    private void eraseLastNumber(){
        var firstStr = firstNumber.getText().toString();
        if(firstStr.length() == 1){
            firstStr = "0";
        }else {
            firstStr = firstStr.substring(0, firstStr.length()-1);
        }
        firstNumber.setText(firstStr);
        calculationOfResult();
    }
    private void swapCurrency(){
        mainActivity.swapCurrency();

        RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        swapButtonImage.startAnimation(animation);

        firstCurrencyName.setText(mainActivity.getFirstCurrency().getCurrency());
        secondCurrencyName.setText(mainActivity.getSecondCurrency().getCurrency());
        calculationOfResult();

    }

}