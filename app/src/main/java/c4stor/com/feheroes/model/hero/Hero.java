package c4stor.com.feheroes.model.hero;

import com.google.gson.Gson;

import java.io.Serializable;

import c4stor.com.feheroes.model.skill.WeaponType;

/**
 * Created by Nicolas on 11/02/2017.
 */

public class Hero implements Serializable {

    public int rarity;
    public String name;
    public int[] HP;
    public int[] atk;
    public int[] def;
    public int[] speed;
    public int[] res;
    public int[] mods1;
    public int[] mods40;

    private final static int[][] growthRates = new int[][]
            {{7, 8, 8},
                    {9, 10, 10},
                    {11, 12, 13},
                    {13, 14, 15},
                    {15, 16, 17},
                    {17, 18, 19},
                    {19, 20, 22},
                    {21, 22, 24},
                    {23, 24, 26},
                    {25, 26, 28},
                    {27, 28, 30},
                    {29, 31, 33}};

    /**
     * @param rarity      the HeroRoll's rarity (value from 3 to 5)
     * @param growthPoint the Hero's growth value for a particular stat (value from 1 to 10)
     * @param baseStat    the hero's neutral level 1 stat
     * @return the level 40 stat if the hero has a bane in this stat
     */
    public static int getLvl40Bane(int rarity, int growthPoint, int baseStat) {
        if (baseStat > 0)
            return baseStat - 1 + growthRates[growthPoint - 1][rarity - 3];
        else
            return -1;
    }

    public static int getLvl40Neutral(int rarity, int growthPoint, int baseStat) {
        if (baseStat > 0)
            return baseStat + growthRates[growthPoint][rarity - 3];
        else
            return -1;
    }

    public static int getLvl40Boon(int rarity, int growthPoint, int baseStat) {
        if (baseStat > 0)
            return baseStat + 1 + growthRates[growthPoint + 1][rarity - 3];
        else
            return -1;
    }


    public Hero(SimpleHero simpleHero, HeroInfo heroInfo) {
        this.rarity = simpleHero.rarity;
        this.name = simpleHero.name;
        this.HP = new int[]{simpleHero.HP - 1,
                simpleHero.HP,
                simpleHero.HP > -1 ? simpleHero.HP + 1 : -1,
                getLvl40Bane(rarity, heroInfo.hpGrowth, simpleHero.HP),
                getLvl40Neutral(rarity, heroInfo.hpGrowth, simpleHero.HP),
                getLvl40Boon(rarity, heroInfo.hpGrowth, simpleHero.HP)};
        this.atk = new int[]{simpleHero.atk - 1,
                simpleHero.atk,
                simpleHero.atk > -1 ? simpleHero.atk + 1 : -1,
                getLvl40Bane(rarity, heroInfo.atkGrowth, simpleHero.atk),
                getLvl40Neutral(rarity, heroInfo.atkGrowth, simpleHero.atk),
                getLvl40Boon(rarity, heroInfo.atkGrowth, simpleHero.atk)};
        this.speed = new int[]{simpleHero.speed - 1,
                simpleHero.speed,
                simpleHero.speed > -1 ? simpleHero.speed + 1 : -1,
                getLvl40Bane(rarity, heroInfo.spdGrowth, simpleHero.speed),
                getLvl40Neutral(rarity, heroInfo.spdGrowth, simpleHero.speed),
                getLvl40Boon(rarity, heroInfo.spdGrowth, simpleHero.speed)};
        this.def = new int[]{simpleHero.def - 1,
                simpleHero.def,
                simpleHero.def > 0 ? simpleHero.def + 1 : -1,
                getLvl40Bane(rarity, heroInfo.defGrowth, simpleHero.def),
                getLvl40Neutral(rarity, heroInfo.defGrowth, simpleHero.def),
                getLvl40Boon(rarity, heroInfo.defGrowth, simpleHero.def)};
        this.res = new int[]{simpleHero.res - 1,
                simpleHero.res,
                simpleHero.res > 0 ? simpleHero.res + 1 : -1,
                getLvl40Bane(rarity, heroInfo.resGrowth, simpleHero.res),
                getLvl40Neutral(rarity, heroInfo.resGrowth, simpleHero.res),
                getLvl40Boon(rarity, heroInfo.resGrowth, simpleHero.res)};
    }

    @Override
    public String toString() {

        Gson g = new Gson();
       return g.toJson(this).toString();
    }
}
