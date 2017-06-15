package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.InheritanceRestriction;



public enum WeaponType implements InheritanceRestriction {
    SWORD, LANCE, AXE,
    RTOME, BTOME, GTOME,
    RBREATH, BBREATH, GBREATH,
    BOW, DAGGER, STAFF,
    IMPOSSIBLE, NO_WEAPON_RESTRICTION {
        @Override
        public boolean inheritedBy(InheritanceRestriction inheritanceRestriction) {
            return true;
        }
    };

    @Override
    public boolean inheritedBy(InheritanceRestriction inheritanceRestriction) {
        return inheritanceRestriction == this;
    }
}
