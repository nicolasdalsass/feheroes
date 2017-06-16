package c4stor.com.feheroes.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by eclogia on 16/06/17.
 */

public class AdvancedInheritanceRestrictionDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String restriction = json.getAsString().toUpperCase();
        for (AdvancedInheritanceRestriction advancedRestriction : AdvancedInheritanceRestriction.values()) {
            if (advancedRestriction.name().equals(restriction))
                return advancedRestriction;
        }
        return null;
    }
}
