package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.Inheritance;


public enum WeaponType implements Inheritance {
    SWORD, LANCE, AXE,
    RTOME, BTOME, GTOME,
    RBREATH, BBREATH, GBREATH,
    BOW, DAGGER, STAFF,
    NONE, ALL;

    public boolean inheritedBy(Inheritance inheritance){
        return this == inheritance;
    }
}
