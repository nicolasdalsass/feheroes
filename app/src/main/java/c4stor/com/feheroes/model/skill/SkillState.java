package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.R;

/**
 * Created by eclogia on 14/06/17.
 */

public enum SkillState {
    EQUIPPED(0, R.string.skill_equipped),
    LEARNED(1, R.string.skill_learned),
    LEARNABLE(2, R.string.skill_learnable),
    TO_INHERIT(3, R.string.skill_to_inherit);

    public int stateNumber;
    public int stateStringId;

    SkillState(int stateNumber, int stateStringId) {
        this.stateNumber = stateNumber;
        this.stateStringId = stateStringId;
    }

    public static SkillState getStateFromIndex(int i) {
        for (SkillState state : SkillState.values()) {
            if (i == state.stateNumber)
                return state;
        }
        return TO_INHERIT;
    }
}
