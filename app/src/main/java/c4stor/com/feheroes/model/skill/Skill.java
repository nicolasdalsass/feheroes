package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.Inheritance;

/**
 * Created by Nicolas on 17/03/2017.
 */

public class Skill {
    public int id;
    public String name;
    public int[] mods;
    public Inheritance inheritance;
    public SkillState state;
}
