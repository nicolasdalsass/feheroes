package c4stor.com.feheroes.model.skill;

import static c4stor.com.feheroes.model.skill.WeaponColor.BLUE;
import static c4stor.com.feheroes.model.skill.WeaponColor.COLORLESS;
import static c4stor.com.feheroes.model.skill.WeaponColor.GREEN;
import static c4stor.com.feheroes.model.skill.WeaponColor.RED;


public enum WeaponType {
    SWORD(RED), LANCE(BLUE), AXE(GREEN),
    RTOME(RED), BTOME(BLUE), GTOME(GREEN),
    RBREATH(RED), BBREATH(BLUE), GBREATH(GREEN),
    BOW(COLORLESS), DAGGER(COLORLESS), STAFF(COLORLESS);

    public WeaponColor color;

    WeaponType(WeaponColor color) {
        this.color = color;
    }
}
