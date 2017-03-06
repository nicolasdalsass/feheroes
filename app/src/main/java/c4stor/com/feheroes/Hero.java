package c4stor.com.feheroes;

import java.io.Serializable;

/**
 * Created by Nicolas on 11/02/2017.
 */

/*

{"name":"Roy",""HP"":[19,20,21,41,44,47],"atk":[23,24,25,42,46,49],"def":[5,6,7,22,25,29],"speed":[8,9,10,27,31,34],"res":[3,4,5,25,28,31]}

 */

public class Hero implements Serializable {

    public String name;
    public int[] HP;
    public int[] atk;
    public int[] def;
    public int[] speed;
    public int[] res;


    public void mergeWith(Hero extraData) {
        mergeStat(this.HP, extraData.HP);
        mergeStat(this.atk, extraData.atk);
        mergeStat(this.def, extraData.def);
        mergeStat(this.speed, extraData.speed);
        mergeStat(this.res, extraData.res);
    }

    private void mergeStat(int[] thisStat, int[] otherStat) {
        for (int i = 0; i < thisStat.length; i++) {
            if (otherStat[i] != -1 && thisStat[i] != otherStat[i]) {
                thisStat[i] = otherStat[i];
            }
        }
    }

    @Override
    public String toString(){
        return name;
    }
}
