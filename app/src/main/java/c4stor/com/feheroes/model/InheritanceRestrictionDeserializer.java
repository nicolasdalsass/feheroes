package c4stor.com.feheroes.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import c4stor.com.feheroes.model.hero.MovementType;
import c4stor.com.feheroes.model.skill.WeaponType;

/**
 * Created by eclogia on 16/06/17.
 */

public class InheritanceRestrictionDeserializer implements JsonDeserializer<InheritanceRestriction> {
    @Override
    public InheritanceRestriction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonRestriction = json.getAsString();
        for (MovementType restriction : MovementType.values()) {
            if (restriction.name().equals(jsonRestriction))
                return restriction;
        }
        for (WeaponType restriction : WeaponType.values()) {
            if (restriction.name().equals(jsonRestriction))
                return restriction;
        }
        for (AdvancedInheritanceRestriction restriction : AdvancedInheritanceRestriction.values()) {
            if (restriction.name().equals(jsonRestriction))
                return restriction;
        }
        return null;
    }
}
