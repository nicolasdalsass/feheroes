package c4stor.com.feheroes.activities;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.model.InheritanceRestriction;
import c4stor.com.feheroes.model.InheritanceRestrictionDeserializer;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.hero.HeroInfo;
import c4stor.com.feheroes.model.hero.SimpleHero;
import c4stor.com.feheroes.model.skill.Skill;

/**
 * Created by eclogia on 19/06/17.
 */

public final class ModelSingleton {

    public Map<String, Hero> fiveStarsMap = new TreeMap<>();
    public Map<String, Hero> fourStarsMap = new TreeMap<>();
    public Map<String, Hero> threeStarsMap = new TreeMap<>();
    public Map<String, HeroInfo> basicsMap = new HashMap<>();
    public Map<Integer, Skill> skillsMap = new HashMap<>();
    public HeroCollection collection = new HeroCollection();


    public Gson gson = new GsonBuilder().registerTypeAdapter(InheritanceRestriction.class, new InheritanceRestrictionDeserializer()).create();

    private static volatile ModelSingleton instance;

    private ModelSingleton() {
    }

    public static ModelSingleton getInstance(Context context) throws IOException {
        if (instance == null) {
            synchronized (ModelSingleton.class) {
                if (instance == null) {
                    instance = new ModelSingleton();
                    instance.init(context);

                }
            }
        }
        return instance;
    }

    private void init(Context context) throws IOException {
        initHeroes(context);
        initSkillData(context);
        collection = HeroCollection.loadFromStorage(context);
    }

    private void initSkillData(Context context) throws IOException {
        File dataFile = new File(context.getFilesDir(), "skills.data");
        File localeFile = new File(context.getFilesDir(), "skills.locale");
        if (dataFile.exists()) {
            try {
                if (localeFile.exists()) {
                    initSkillsFromInputStream(new FileInputStream(dataFile), new FileInputStream(localeFile));
                } else {
                    initSkillsFromInputStream(new FileInputStream(dataFile));
                }
            } catch (Exception e) {
                initSkillsLocally(context);
            }
        } else {
            initSkillsLocally(context);
        }
    }

    private void initHeroes(Context context) throws IOException {
        File dataFile = new File(context.getFilesDir(), "hero.basics");
        if (dataFile.exists()) {
            try {
                initHeroesBasics(new FileInputStream(dataFile));
            } catch (Exception e) {
                initHeroesBasicsLocally(context);
            }
        } else {
            initHeroesBasicsLocally(context);
        }

        File heroDataFile = new File(context.getFilesDir(), "heroinfo.data");
        if (heroDataFile.exists()) {
            try {
                initHeroesFromInputStream(new FileInputStream(heroDataFile), context);
            } catch (Exception e) {
                initHeroesFromCombo(context);
            }
        } else {
            initHeroesFromCombo(context);
        }
    }

    private void initHeroesBasicsLocally(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.heroesjson);
        initHeroesBasics(inputStream);
    }

    private void initHeroesBasics(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            HeroInfo h = gson.fromJson(line, HeroInfo.class);
            basicsMap.put(h.name, h);
            line = reader.readLine();
        }
    }

    private void initHeroesFromCombo(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.combojson);
        initHeroesFromInputStream(inputStream, context);
    }

    private void initHeroesFromInputStream(InputStream inputStream, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            SimpleHero simpleHero = gson.fromJson(line, SimpleHero.class);
            int rarity = Integer.valueOf(simpleHero.name.substring(simpleHero.name.length() - 1));
            simpleHero.name = simpleHero.name.substring(0, simpleHero.name.length() - 1);
            simpleHero.rarity = rarity;
            int nameIdentifier = context.getResources().getIdentifier(simpleHero.name.toLowerCase(), "string", context.getPackageName());
            HeroInfo heroInfo = basicsMap.get(simpleHero.name);

            Hero jH = new Hero(simpleHero, heroInfo);
            String heroName = capitalize(context.getResources().getString(nameIdentifier));
            switch (rarity) {
                case 3:
                    threeStarsMap.put(heroName, jH);
                    break;
                case 4:
                    fourStarsMap.put(heroName, jH);
                    break;
                case 5:
                    fiveStarsMap.put(heroName, jH);
                    break;
            }
            line = reader.readLine();
        }
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private void initSkillsLocally(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.skillsjson);
        initSkillsFromInputStream(inputStream);
    }

    private void initSkillsFromInputStream(InputStream mainIS, InputStream localeIS) throws IOException {
        BufferedReader mainReader = new BufferedReader(new InputStreamReader(mainIS));
        BufferedReader localReader = new BufferedReader(new InputStreamReader(localeIS));
        //HEAVILY relies on both files having matching lines
        String mainLine = mainReader.readLine();
        String localeLine = localReader.readLine();
        while (mainLine != null && localeLine != null) {
            if (mainLine.length() > 0 && localeLine.length() > 0) {
                Skill skill = gson.fromJson(mainLine, Skill.class);
                Skill localeSkill = gson.fromJson(localeLine, Skill.class);
                if (skill.id == localeSkill.id)
                    skill.name = localeSkill.name;
                skill.initSkillType();
                skillsMap.put(skill.id, skill);
            }
            mainLine = mainReader.readLine();
            localeLine = localReader.readLine();
        }
    }

    private void initSkillsFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0) {
                Skill skill = gson.fromJson(line, Skill.class);
                skill.initSkillType();
                skillsMap.put(skill.id, skill);
            }
            line = reader.readLine();
        }
    }
}