package c4stor.com.feheroes.model.hero;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import c4stor.com.feheroes.model.skill.Skill;
import c4stor.com.feheroes.model.skill.WeaponType;

/**
 * Created by eclogia on 26/06/17.
 */

public class HeroInfo implements Serializable {

    public String name;
    public WeaponType weaponType;
    public MovementType movementType;
    public int hpGrowth;
    public int atkGrowth;
    public int spdGrowth;
    public int defGrowth;
    public int resGrowth;
    public int[] weaponChain;
    public int[] assistChain;
    public int[] specialChain;
    public int[] aChain;
    public int[] bChain;
    public int[] cChain;

    public HeroInfo() {
        weaponChain = new int[4];
        assistChain = new int[3];
        specialChain = new int[3];
        aChain = new int[3];
        bChain = new int[3];
        cChain = new int[3];
    }

    @Override
    public String toString(){
        return name;
    }

    public boolean canInherit(Skill skill) {
        //Seems a bit weak, but should work as long as there isn't a condition involving both weapon and movement type somewhere.
        return skill.inheritance.isCompatibleWith(weaponType) || skill.inheritance.isCompatibleWith(movementType);
    }

}
