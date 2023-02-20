package com.example.moneyconverter.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyconverter.R;

public class ErrorFragment extends Fragment {

    public ErrorFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var code = this.getArguments().getString("code");
        var msg = this.getArguments().getString("message");

        var view = inflater.inflate(R.layout.fragment_error, container, false);
        ((TextView)view.findViewById(R.id.status_code)).setText(code);
        ((TextView)view.findViewById(R.id.message)).setText(msg);

        return view;
    }
}