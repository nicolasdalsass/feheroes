package c4stor.com.feheroes;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nicolas on 15/02/2017.
 */

public class HeroRoll implements Serializable {

    public Hero hero;
    public String nickname;
    public int stars;
    public List<String> boons;
    public List<String> banes;

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public HeroRoll(Hero hero, int stars, List<String> boons, List<String> banes) {
        this.hero = hero;
        this.stars = stars;
        this.boons = boons;
        this.banes = banes;
    }

    public int getStat(Context c, int statId, int[] stat) {
        String statName = c.getResources().getString(statId);
        if (boons != null && boons.contains(statName)) {
            return stat[5];
        } else if (banes != null && banes.contains(statName)) {
            return stat[3];
        } else {
            return stat[4];
        }
    }

    public int getHP(Context c) {
        return getStat(c, R.string.hp, hero.HP);
    }

    public int getAtk(Context c) {
        return getStat(c, R.string.atk, hero.atk);
    }

    public int getSpeed(Context c) {
        return getStat(c, R.string.spd, hero.speed);
    }

    public int getDef(Context c) {
        return getStat(c, R.string.def, hero.def);
    }

    public int getRes(Context c) {
        return getStat(c, R.string.res, hero.res);
    }


    public String getDisplayName(Context c) {
        if (nickname != null && !nickname.isEmpty())
            return nickname;
        else {
            int nameIdentifier = c.getResources().getIdentifier(hero.name.toLowerCase(), "string", c.getPackageName());
            return capitalize(c.getString(nameIdentifier));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(hero.name));
        sb.append(",");
        for (int i = 0; i < stars; i++) {
            sb.append("★");
        }
        sb.append(",");
        for (int i = 0; i < boons.size() - 1; i++) {
            sb.append("+" + boons.get(i) + " ");
        }
        if (boons.size() > 0) {
            sb.append("+" + boons.get(boons.size() - 1));
        }
        sb.append(",");
        for (int i = 0; i < banes.size() - 1; i++) {
            sb.append("+" + banes.get(i) + " ");
        }
        if (banes.size() > 0) {
            sb.append("-" + banes.get(banes.size() - 1));
        }
        return sb.toString();
    }
}
