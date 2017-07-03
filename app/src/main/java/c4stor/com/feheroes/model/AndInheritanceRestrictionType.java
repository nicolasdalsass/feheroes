package c4stor.com.feheroes.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eclogia on 14/06/17.
 */

public class AndInheritanceRestrictionType implements InheritanceRestriction {
    public List<InheritanceRestriction> restrictions;

    public AndInheritanceRestrictionType(InheritanceRestriction... inheritanceRestrictionTypes) {
        restrictions = new ArrayList<>(inheritanceRestrictionTypes.length);
        restrictions.addAll(Arrays.asList(inheritanceRestrictionTypes));
    }

    @Override
    public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
        boolean result = restrictions.get(0).isCompatibleWith(inheritanceRestrictionType);
        for (int i = 1; i < restrictions.size(); i++) {
            result = result && restrictions.get(i).isCompatibleWith(inheritanceRestrictionType);
        }
        return result;
    }
}
