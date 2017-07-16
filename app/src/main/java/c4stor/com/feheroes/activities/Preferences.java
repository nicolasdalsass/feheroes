package c4stor.com.feheroes.activities;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

/**
 * Created by Nicolas on 15/07/2017.
 */


public class Preferences {

    private String sorting = "name";
    private String locale = null;

    private static Gson gson = new Gson();

    public String getSorting() {
        return sorting;
    }

    public void setLocale(Locale locale, Context c) {
        this.locale = locale.toString();
        save(c);
    }

    public Locale getLocale() {
        if (locale == null) {
            return Locale.getDefault();
        } else {
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale l : locales) {
                if (l.toString().equalsIgnoreCase(locale)) {
                    return l;
                }
            }
            return Locale.getDefault();
        }
    }

    public void setSorting(String sorting, Context c) {
        this.sorting = sorting;
        save(c);
    }

    private Preferences() {

    }

    private static String fileName = "preferences";

    public void save(Context c) {
        File directory = c.getFilesDir();
        File dataSave = new File(directory, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(dataSave);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
            writer.beginArray();
            gson.toJson(this, Preferences.class, writer);
            writer.endArray();
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }

    public static Preferences loadFromStorage(Context c) {
        File directory = c.getFilesDir();
        File dataSave = new File(directory, fileName);
        if (!dataSave.exists()) {
            return new Preferences();
        } else {
            try {
                FileInputStream fis = new FileInputStream(dataSave);
                JsonReader reader = new JsonReader(new InputStreamReader(fis));
                reader.beginArray();
                Preferences hc = gson.fromJson(reader, Preferences.class);
                reader.endArray();
                reader.close();
                return hc;
            } catch (IOException e) {
                return new Preferences();
            }
        }
    }
}
