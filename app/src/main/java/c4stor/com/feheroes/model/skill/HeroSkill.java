package c4stor.com.feheroes.model.skill;

/**
 * Created by eclogia on 07/07/17.
 */

public class HeroSkill extends Skill {
    public SkillState skillState;

    public HeroSkill() {
        skillState = SkillState.TO_INHERIT;
    }

    public HeroSkill(Skill skill, SkillState skillState) {
        this.id = skill.id;
        this.name = skill.name;
        this.mods = skill.mods;
        this.inheritance = skill.inheritance;
        this.skillType = skill.skillType;
        this.skillState = skillState;
    }
}
