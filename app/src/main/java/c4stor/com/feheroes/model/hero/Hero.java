package c4stor.com.feheroes.model.hero;

import java.io.Serializable;

import c4stor.com.feheroes.model.skill.Skill;
import c4stor.com.feheroes.model.skill.WeaponType;

/**
 * Created by eclogia on 26/06/17.
 */

public class Hero implements Serializable {

    public String name;
    public WeaponType weaponType;
    public MovementType movementType;
    public int hpGrowth;
    public int atkGrowth;
    public int spdGrowth;
    public int defGrowth;
    public int resGrowth;

    @Override
    public String toString(){
        return name;
    }

    public boolean canInherit(Skill skill) {
        //Seems a bit weak, but should work as long as there isn't a condition involving both weapon and movement type somewhere.
        return skill.inheritance.isCompatibleWith(weaponType) || skill.inheritance.isCompatibleWith(movementType);
    }

}
