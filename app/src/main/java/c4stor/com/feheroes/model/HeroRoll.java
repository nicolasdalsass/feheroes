package c4stor.com.feheroes.model;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import c4stor.com.feheroes.R;

/**
 * Created by Nicolas on 15/02/2017.
 */

public class HeroRoll implements Serializable {

    public Hero hero;
    public String nickname;
    public int stars;
    public List<String> boons;
    public List<String> banes;
    public String comment;
    public int equippedWeapon;
    public int equippedAssist;
    public int equippedSpecial;
    public int equippedA;
    public int equippedB;
    public int equippedC;

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

    public int getBST(Context c) {
        int hp= getHP(c);
        int a = getAtk(c);
        int d = getDef(c);
        int s = getSpeed(c);
        int r = getRes(c);
        if(a<0 || hp <0 || d <0 || s <0 || r <0){
            return -1;
        } else {
            return hp+a+d+s+r;
        }
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
        appendBoonsOrBanes(sb, boons, '+');
        sb.append(",");
        appendBoonsOrBanes(sb, banes, '-');
        sb.append(",");
        if (comment != null) {
            sb.append(comment);
        }
        return sb.toString();
    }

    private void appendBoonsOrBanes(StringBuilder sb, List<String> list, char plusMinus) {
        for (int i = 0; i < list.size() -1; i++) {
            sb.append(plusMinus).append(list.get(i)).append(" ");
        }
        if (list.size() > 0) {
            sb.append(plusMinus).append(list.get(list.size() - 1));
        }
    }
}
