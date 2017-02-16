package c4stor.com.feheroes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nicolas on 15/02/2017.
 */

public class HeroRoll implements Serializable {

    public Hero hero;
    public int stars;
    public List<String> boons;
    public List<String> banes;

    public HeroRoll(Hero hero, int stars, List<String> boons, List<String> banes) {
        this.hero = hero;
        this.stars = stars;
        this.boons = boons;
        this.banes = banes;
    }
}
