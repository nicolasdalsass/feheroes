package c4stor.com.feheroes.model.hero;

import java.io.Serializable;

import c4stor.com.feheroes.model.skill.Skill;
import c4stor.com.feheroes.model.skill.WeaponType;

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
    public int[] skills1;
    public int[] skills40;

    public WeaponType weaponType;
    public MovementType movementType;

    @Override
    public String toString(){
        return name;
    }

    public boolean canInherit(Skill skill) {
        //Seems a bit weak, but should work as long as there isn't a condition involving both weapon and movement type somewhere.
        return skill.inheritance.isCompatibleWith(weaponType) || skill.inheritance.isCompatibleWith(movementType);
    }
}
