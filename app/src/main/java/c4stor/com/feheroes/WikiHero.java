package c4stor.com.feheroes;

import com.google.gson.Gson;

/**
 * Created by Nicolas on 21/02/2017.
 */

public class WikiHero {

    private static Gson gson = new Gson();

    String name;
    HeroLevel[] data;



    public class HeroLevel{

        int[] hp;
        int[] def;
        int[] res;
        int[] spd;
        int[] atk;

    }

}
