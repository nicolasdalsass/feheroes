package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.InheritanceRestriction;



public enum WeaponType implements InheritanceRestriction {
    SWORD, LANCE, AXE,
    RTOME, BTOME, GTOME,
    RBREATH, BBREATH, GBREATH,
    BOW, DAGGER, STAFF,
    NON_INHERITABLE, NO_RESTRICTIONS {
        @Override
        public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
            return true;
        }
    };

    @Override
    public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
        return inheritanceRestrictionType == this;
    }
}
