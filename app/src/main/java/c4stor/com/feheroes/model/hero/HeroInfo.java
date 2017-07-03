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
<<<<<<< HEAD
    public List availability;
    public List weaponChain;
    public List assistChain;
    public List specialChain;
    public List aChain;
    public List bChain;
    public List cChain;

    public HeroInfo() {
        availability = new ArrayList(3);
        weaponChain = new ArrayList(4);
        assistChain = new ArrayList(3);
        specialChain = new ArrayList(3);
        aChain = new ArrayList(3);
        bChain = new ArrayList(3);
        cChain = new ArrayList(3);
=======
    public int[] availability;

    public HeroInfo() {
        availability = new int[3];
>>>>>>> 9e4b69c... Merge remote-tracking branch 'origin/master'
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
