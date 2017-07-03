package c4stor.com.feheroes.model.hero;

/**
 * Created by eclogia on 29/06/17.
 */

public class HeroGrowth {
    public final static int[][] growthRates = new int[][]
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
     *
     * @param rarity the HeroRoll's rarity (value from 3 to 5)
     * @param growthPoint the Hero's growth value for a particular stat (value from 1 to 10)
     * @param baseStat the hero's neutral level 1 stat
     * @return the level 40 stat if the hero has a bane in this stat
     */
    public static int getLvl40Bane(int rarity, int growthPoint, int baseStat) {
        return baseStat - 1 + growthRates[growthPoint -1][rarity -3];
    }

    public static int getLvl40Neutral(int rarity, int growthPoint, int baseStat) {
        return baseStat + growthRates[growthPoint][rarity -3];
    }

    public static int getLvl40Boon(int rarity, int growthPoint, int baseStat) {
        return baseStat + 1 + growthRates[growthPoint +1][rarity -3];
    }
}
