package c4stor.com.feheroes.model.skill;

import c4stor.com.feheroes.model.Inheritance;

import static c4stor.com.feheroes.model.skill.WeaponColor.BLUE;
import static c4stor.com.feheroes.model.skill.WeaponColor.COLORLESS;
import static c4stor.com.feheroes.model.skill.WeaponColor.GREEN;
import static c4stor.com.feheroes.model.skill.WeaponColor.RED;


public enum WeaponType implements Inheritance {
    SWORD(RED), LANCE(BLUE), AXE(GREEN),
    RTOME(RED), BTOME(BLUE), GTOME(GREEN),
    RBREATH(RED), BBREATH(BLUE), GBREATH(GREEN),
    BOW(COLORLESS), DAGGER(COLORLESS), STAFF(COLORLESS),
    NONE(WeaponColor.NONE), ALL(WeaponColor.ALL);

    public WeaponColor color;

    WeaponType(WeaponColor color) {
        this.color = color;
    }
}
