package c4stor.com.feheroes.model.hero;

import c4stor.com.feheroes.model.Inheritance;

/**
 * Created by eclogia on 14/06/17.
 */

public enum MovementType implements Inheritance {
    INFANTRY, ARMOR, CAVALRY, FLIER;

    public boolean inheritedBy(Inheritance inheritance){
        return this == inheritance;
    }
}
