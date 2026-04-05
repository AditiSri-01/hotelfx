package com.hotel.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    // Generic Method to save ANY list of Serializable objects
    public static <T extends Serializable> void saveList(List<T> list, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new ArrayList<>(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic Method to load ANY list of Serializable objects
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> loadList(String filename) {
        File file = new File(filename);
        if (!file.exists())
            return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
