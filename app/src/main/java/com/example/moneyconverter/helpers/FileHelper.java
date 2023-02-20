package com.example.moneyconverter.helpers;

import com.example.moneyconverter.models.CurrencyRoot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHelper {

    public static void write(FileOutputStream stream, CurrencyRoot currencyRoot){
        ObjectOutputStream obj = null;
        try {
            obj = new ObjectOutputStream(stream);
            obj.writeObject(currencyRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                obj.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static CurrencyRoot read(FileInputStream stream){
        CurrencyRoot currencyRoot = new CurrencyRoot();
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(stream);
            currencyRoot = (CurrencyRoot) obj.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                obj.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return currencyRoot;
        }
    }

}
