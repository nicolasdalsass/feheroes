package c4stor.com.feheroes.model.skill;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;

import c4stor.com.feheroes.model.InheritanceRestriction;

/**
 * Created by Nicolas on 17/03/2017.
 */

public class Skill implements Comparable<Skill> {
    public int id;
    public String name;
    public int[] mods;
    public InheritanceRestriction inheritance;
    public SkillType skillType;

    public Skill() {
        skillState = SkillState.LEARNABLE;
    }

    public void initSkillType(){
        if (isWeapon(id))
            skillType = SkillType.WEAPON;
        else if (isAssist(id))
            skillType = SkillType.ASSIST;
        else if (isSpecial(id))
            skillType = SkillType.SPECIAL;
        else if (isPassiveA(id))
            skillType = SkillType.PASSIVE_A;
        else if (isPassiveB(id))
            skillType = SkillType.PASSIVE_B;
        else
            skillType = SkillType.PASSIVE_C;
    }

    public static boolean isPassiveC(int i) {
        return i >= 50000;
    }

    public static boolean isPassiveB(int i) {
        return i >= 40000 && i < 50000;
    }

    public static boolean isPassiveA(int i) {
        return i >= 30000 && i < 40000;
    }

    public static boolean isSpecial(int i) {
        return i >= 20000 && i < 30000;
    }

    public static boolean isAssist(int i) {
        return i >= 10000 && i < 20000;
    }

    public static boolean isWeapon(int i) {
        return 0 < i && i < 10000;
    }

    public static boolean areSameSkillType(int id1, int id2) {
        //you could probably do a difference check on the ids instead
        return (isWeapon(id1) && isWeapon(id2)) || (isAssist(id1) && isAssist(id2)) || (isSpecial(id1) && isSpecial(id2))
                || (isPassiveA(id1) && isPassiveA(id2)) || (isPassiveB(id1) && isPassiveB(id2)) || (isPassiveC(id1) && isPassiveC(id2));
    }

    public boolean isSameSkillType(Skill otherSkill) {
        return this.skillType == otherSkill.skillType;
    }

    @Override
    public int compareTo(@NonNull Skill o) {
        int compareType = this.skillType.compareTo(o.skillType);
        if (compareType != 0)
            return compareType;
        else
            return this.name.compareTo(o.name);
    }
}
