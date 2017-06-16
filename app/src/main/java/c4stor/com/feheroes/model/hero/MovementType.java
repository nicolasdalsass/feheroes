package c4stor.com.feheroes.model.hero;

import c4stor.com.feheroes.model.InheritanceRestrictionType;

/**
 * Created by eclogia on 14/06/17.
 */

public enum MovementType implements InheritanceRestrictionType {
    INFANTRY, ARMOR, CAVALRY, FLIER;

    @Override
    public boolean isInheritanceCompatibleWith(InheritanceRestrictionType inheritanceRestrictionType) {
        return inheritanceRestrictionType == this;
    }
}
