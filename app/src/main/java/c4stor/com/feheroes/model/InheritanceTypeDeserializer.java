package c4stor.com.feheroes.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import c4stor.com.feheroes.model.hero.MovementType;
import c4stor.com.feheroes.model.skill.WeaponType;

import static c4stor.com.feheroes.model.skill.WeaponType.*;

/**
 * Created by eclogia on 16/06/17.
 */

public class InheritanceTypeDeserializer implements JsonDeserializer<InheritanceRestrictionType> {
    @Override
    public InheritanceRestrictionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String restriction = json.getAsString().toUpperCase();
        for (WeaponType weaponType : WeaponType.values()) {
            if (weaponType.name().equals(restriction))
                return weaponType;
        }
        for (MovementType movType : MovementType.values()) {
            if (movType.name().equals(restriction))
                return movType;
        }
        for (AdvancedInheritanceRestriction advancedRestriction : AdvancedInheritanceRestriction.values()) {
            if (advancedRestriction.name().equals(restriction))
                return advancedRestriction;
        }
        return null;
    }
}
