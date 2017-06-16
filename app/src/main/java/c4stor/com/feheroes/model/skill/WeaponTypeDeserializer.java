package c4stor.com.feheroes.model.skill;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by eclogia on 16/06/17.
 */

public class WeaponTypeDeserializer implements JsonDeserializer<WeaponType> {
    @Override
    public WeaponType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String restriction = json.getAsString().toUpperCase();
        for (WeaponType weaponType : WeaponType.values()) {
            if (weaponType.name().equals(restriction))
                return weaponType;
        }
        return null;
    }
}
