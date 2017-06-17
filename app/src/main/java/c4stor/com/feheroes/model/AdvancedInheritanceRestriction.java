package c4stor.com.feheroes.model;

import c4stor.com.feheroes.model.hero.MovementType;

import static c4stor.com.feheroes.model.skill.WeaponType.*;

/**
 * Created by eclogia on 16/06/17.
 */

public enum AdvancedInheritanceRestriction implements InheritanceRestriction {
    NOT_FLIER(new NotInheritanceRestrictionType(MovementType.FLIER)),
    NOT_STAFF(new NotInheritanceRestrictionType(STAFF)),
    DRAGON(new OrInheritanceRestrictionType(RBREATH, BBREATH, GBREATH)),
    PHYSICAL_MELEE(new OrInheritanceRestrictionType(SWORD, LANCE, AXE)),
    MELEE(new OrInheritanceRestrictionType(SWORD, LANCE, AXE, RBREATH, BBREATH, GBREATH)),
    NOT_RED(new OrInheritanceRestrictionType(LANCE, BTOME, BBREATH, AXE, GTOME, GBREATH, BOW, DAGGER, STAFF)),
    NOT_BLUE(new OrInheritanceRestrictionType(SWORD, RTOME, RBREATH, AXE, GTOME, GBREATH, BOW, DAGGER, STAFF)),
    NOT_GREEN(new OrInheritanceRestrictionType(SWORD, RTOME, RBREATH, LANCE, BTOME, BBREATH, BOW, DAGGER, STAFF)),
    NOT_COlORLESS(new OrInheritanceRestrictionType(SWORD, RTOME, RBREATH, LANCE, BTOME, BBREATH, AXE, GTOME, GBREATH));

    private InheritanceRestriction restriction;

    AdvancedInheritanceRestriction(InheritanceRestriction inheritanceRestrictionType) {
        this.restriction = inheritanceRestrictionType;
    }

    @Override
    public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
        return restriction.isCompatibleWith(inheritanceRestrictionType);
    }
}
