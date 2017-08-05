package c4stor.com.feheroes.model.hero;

import java.io.Serializable;
import java.util.List;

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
        public List<Integer> skills1;
        public List<Integer> skills40;
    }

