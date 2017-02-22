package c4stor.com.feheroes;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Nicolas on 21/02/2017.
 */

public class WikiParser {


    private final Context context;

    public WikiParser(Context c){
        this.context = c;
    }
    Map<String, Hero> fiveStarsMap = new TreeMap<String, Hero>();
    Map<String, Hero> fourStarsMap = new TreeMap<String, Hero>();


    private GsonBuilder builder = new GsonBuilder();

    public void enrichMap(Map<String, Hero> sourceMap, Map<String, Hero> extraData) {
        for (Map.Entry<String, Hero> heroEntry : sourceMap.entrySet()) {
            if (extraData.containsKey(heroEntry.getKey())) {
                heroEntry.getValue().mergeWith(extraData.get(heroEntry.getKey()));
            } else {
            Log.i("","Unmatched hero : "+heroEntry.getKey());
            }
        }
    }

    public void parse(File wikiFile) throws IOException {
        InputStream is = new FileInputStream(wikiFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        parse(sb.toString());
    }

    public void parse(String jsondata) {

        LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, List<Double>>>> o = (LinkedTreeMap) builder.create().fromJson(jsondata, Object.class);

        for (Map.Entry<String, LinkedTreeMap<String, LinkedTreeMap<String, List<Double>>>> entry : o.entrySet()) {

            if (entry.getValue().containsKey("4")) {

                String key = "4";
                List<Double> atkStuff = entry.getValue().get(key).get("atk");
                List<Double> hpStuff = entry.getValue().get(key).get("hp");
                List<Double> defStuff = entry.getValue().get(key).get("def");
                List<Double> resStuff = entry.getValue().get(key).get("res");
                List<Double> spdStuff = entry.getValue().get(key).get("spd");
                Hero hero = new Hero();
                hero.name = entry.getKey();
                hero.atk = makeStatFromStuff(atkStuff);
                hero.HP = makeStatFromStuff(hpStuff);
                hero.def = makeStatFromStuff(defStuff);
                hero.res = makeStatFromStuff(resStuff);
                hero.speed = makeStatFromStuff(spdStuff);
                int nameIdentifier = context.getResources().getIdentifier(hero.name.toLowerCase(), "string", context.getPackageName());
                if(nameIdentifier!=0){
                    fourStarsMap.put(capitalize(context.getResources().getString(nameIdentifier)), hero);
                }
            }

            if (entry.getValue().containsKey("5")) {

                String key = "5";
                List<Double> atkStuff = entry.getValue().get(key).get("atk");
                List<Double> hpStuff = entry.getValue().get(key).get("hp");
                List<Double> defStuff = entry.getValue().get(key).get("def");
                List<Double> resStuff = entry.getValue().get(key).get("res");
                List<Double> spdStuff = entry.getValue().get(key).get("spd");
                Hero hero = new Hero();
                hero.name = entry.getKey();
                hero.atk = makeStatFromStuff(atkStuff);
                hero.HP = makeStatFromStuff(hpStuff);
                hero.def = makeStatFromStuff(defStuff);
                hero.res = makeStatFromStuff(resStuff);
                hero.speed = makeStatFromStuff(spdStuff);
                int nameIdentifier = context.getResources().getIdentifier(hero.name.toLowerCase(), "string", context.getPackageName());
                if(nameIdentifier!=0){
                    fiveStarsMap.put(capitalize(context.getResources().getString(nameIdentifier)), hero);
                }
            }
        }

    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


    private int[] makeStatFromStuff(List<Double> stuff) {
        int[] result = new int[]{-1, -1, -1, -1, -1, -1};
        for (int i = 0; i < stuff.size(); i++) {
            try {
                int j = stuff.get(i).intValue();
                result[i] = j;

            } catch (ClassCastException e) {

            }
        }
        return result;
    }
}
