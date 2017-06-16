package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.InheritanceRestrictionType;



public enum WeaponType implements InheritanceRestrictionType {
    SWORD, LANCE, AXE,
    RTOME, BTOME, GTOME,
    RBREATH, BBREATH, GBREATH,
    BOW, DAGGER, STAFF,
    NON_INHERITABLE, NO_RESTRICTIONS {
        @Override
        public boolean isInheritanceCompatibleWith(InheritanceRestrictionType inheritanceRestrictionType) {
            return true;
        }
    };

    @Override
    public boolean isInheritanceCompatibleWith(InheritanceRestrictionType inheritanceRestrictionType) {
        return inheritanceRestrictionType == this;
    }
}
