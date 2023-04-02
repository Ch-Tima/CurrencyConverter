package com.example.moneyconverter.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHelper {

    public static <T> void write(FileOutputStream stream, T data){
        ObjectOutputStream obj = null;
        try {
            obj = new ObjectOutputStream(stream);
            obj.writeObject(data);
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

    public static <T> T read(FileInputStream stream) {
        T data = null;
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(stream);
            data = (T) obj.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                obj.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        }
    }
}
