package c4stor.com.feheroes.model.hero;

import c4stor.com.feheroes.model.InheritanceRestriction;

/**
 * Created by eclogia on 14/06/17.
 */

public enum MovementType implements InheritanceRestriction {
    INFANTRY, ARMOR, CAVALRY, FLIER;

    @Override
    public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
        return inheritanceRestrictionType == this;
    }
}
