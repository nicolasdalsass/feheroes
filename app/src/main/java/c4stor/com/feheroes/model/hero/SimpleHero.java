package c4stor.com.feheroes.model.hero;

import java.io.Serializable;

/**
 * Created by Nicolas on 05/07/2017.
 */

public class SimpleHero implements Serializable    {

    public String name;
    public int rarity;
    public int HP;
    public int atk;
    public int def;
    public int speed;
    public int res;
    public int[] skills1;
    public int[] skills40;
}

