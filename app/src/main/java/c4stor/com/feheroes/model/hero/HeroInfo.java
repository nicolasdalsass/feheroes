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
    public List<Integer> weaponChain;
    public List<Integer> assistChain;
    public List<Integer> specialChain;
    public List<Integer> aChain;
    public List<Integer> bChain;
    public List<Integer> cChain;
    public int hpGrowth;
    public int atkGrowth;
    public int spdGrowth;
    public int defGrowth;
    public int resGrowth;
    public List<Integer> weaponChain;
    public List<Integer> assistChain;
    public List<Integer> specialChain;
    public List<Integer> aChain;
    public List<Integer> bChain;
    public List<Integer> cChain;

    public HeroInfo() {
        weaponChain = new ArrayList<>(4);
        assistChain = new ArrayList<>(3);
        specialChain = new ArrayList<>(3);
        aChain = new ArrayList<>(3);
        bChain = new ArrayList<>(3);
        cChain = new ArrayList<>(3);
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
