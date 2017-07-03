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
import c4stor.com.feheroes.model.hero.HeroInfo;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.skill.Skill;

/**
 * Created by eclogia on 19/06/17.
 */

public final class ModelSingleton {

    public Map<String, Hero> fiveStarsMap = null;
    public Map<String, Hero> fourStarsMap = null;
    public Map<String, Hero> threeStarsMap = null;
    public Map<String, HeroInfo> heroMap = null;
    public Map<Integer, Skill> skillsMap = null;
    public HeroCollection collection = new HeroCollection();

    public Gson gson = new GsonBuilder().registerTypeAdapter(InheritanceRestriction.class, new InheritanceRestrictionDeserializer()).create();

    private static volatile ModelSingleton instance;

    private ModelSingleton() {}

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
        if(skillsMap==null) {
            skillsMap = new HashMap<>();
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
    }

    private void initHeroes(Context context) throws IOException {
        if (heroMap == null || heroMap.get("Selena").availability.size() == 0) {
            heroMap = new HashMap<>();
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
        }
        if (threeStarsMap == null || threeStarsMap.get("Selena").availability.size() == 0) {//test for updating old files
            threeStarsMap=new TreeMap<>();
            fourStarsMap=new TreeMap<>();
            fiveStarsMap=new TreeMap<>();
            File dataFile = new File(context.getFilesDir(), "hero.data");
            if (dataFile.exists()) {
                try {
                    initHeroesFromInputStream(new FileInputStream(dataFile), context);
                } catch (Exception e) {
                    initHeroesFromCombo(context);
                }
            } else {
                initHeroesFromCombo(context);
            }
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
            heroMap.put(h.name, h);
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
            Hero jH = gson.fromJson(line, Hero.class);
            int rarity = Integer.valueOf(jH.name.substring(jH.name.length() - 1));
            jH.name = jH.name.substring(0, jH.name.length() - 1);
            cleanStat(jH.atk);
            cleanStat(jH.HP);
            cleanStat(jH.def);
            cleanStat(jH.res);
            cleanStat(jH.speed);
            jH.rarity = rarity;
            int nameIdentifier = context.getResources().getIdentifier(jH.name.toLowerCase(), "string", context.getPackageName());
            HeroInfo heroInfo = heroMap.get(jH.name);
            jH.movementType = heroInfo.movementType;
            jH.weaponType = heroInfo.weaponType;
            jH.hpGrowth = heroInfo.hpGrowth;
            jH.atkGrowth = heroInfo.atkGrowth;
            jH.spdGrowth = heroInfo.spdGrowth;
            jH.defGrowth = heroInfo.defGrowth;
            jH.resGrowth = heroInfo.resGrowth;
            jH.availability = heroInfo.availability;
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

    private void cleanStat(int[] stat) {
        if (stat[4] == -1 || stat[5] == -1 || stat[3] == -1) {
            stat[4] = -1;
            stat[5] = -1;
            stat[3] = -1;
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