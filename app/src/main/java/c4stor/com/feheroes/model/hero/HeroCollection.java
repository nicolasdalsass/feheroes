package c4stor.com.feheroes.model.hero;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import c4stor.com.feheroes.model.InheritanceRestriction;
import c4stor.com.feheroes.model.InheritanceRestrictionDeserializer;
import c4stor.com.feheroes.model.skill.HeroSkill;

/**
 * Created by Nicolas on 15/02/2017.
 */

public class HeroCollection extends ArrayList<HeroRoll> implements Serializable {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(InheritanceRestriction.class, new InheritanceRestrictionDeserializer()).create();

    public HeroCollection() {
        super();
    }

    public void save(Context c) {
        File directory = c.getFilesDir();
        File dataSave = new File(directory, "fehivchecker.collection");
        try {
            FileOutputStream fos = new FileOutputStream(dataSave);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
            writer.beginArray();
            gson.toJson(this, HeroCollection.class, writer);
            writer.endArray();
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }

    public static HeroCollection loadFromStorage(Context c) {
        File directory = c.getFilesDir();
        File dataSave = new File(directory, "fehivchecker.collection");
        if (!dataSave.exists()) {
            return new HeroCollection();
        } else {
            try {
                FileInputStream fis = new FileInputStream(dataSave);
                JsonReader reader = new JsonReader(new InputStreamReader(fis));
                reader.beginArray();
                HeroCollection hc = gson.fromJson(reader, HeroCollection.class);
                reader.endArray();
                reader.close();
                synchronizeSkills(hc);
                return hc;
            } catch (IOException e) {
                return new HeroCollection();
            }
        }
    }

    private static void synchronizeSkills(HeroCollection collection) {
        for (HeroRoll roll : collection) {
            List<HeroSkill> copyList = new ArrayList<>(6);
            for (HeroSkill equippedSkill : roll.equippedSkills) {
                for (HeroSkill skill : roll.skills) {
                    if (skill.id == equippedSkill.id) {
                        copyList.add(skill);
                        break;
                    }
                }
            }
            roll.equippedSkills.clear();
            roll.equippedSkills.addAll(copyList);
        }
    }
}
