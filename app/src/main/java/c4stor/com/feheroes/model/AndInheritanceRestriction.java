package c4stor.com.feheroes.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eclogia on 14/06/17.
 */

public class AndInheritanceRestriction implements InheritanceRestriction {
    public List<InheritanceRestriction> restrictions;

    public AndInheritanceRestriction(InheritanceRestriction... inheritanceRestrictions) {
        restrictions = new ArrayList<>(inheritanceRestrictions.length);
        restrictions.addAll(Arrays.asList(inheritanceRestrictions));
    }

    @Override
    public boolean inheritedBy(InheritanceRestriction inheritanceRestriction) {
        boolean result = restrictions.get(0).inheritedBy(inheritanceRestriction);
        for (int i = 1; i < restrictions.size(); i++) {
            result = result && restrictions.get(i).inheritedBy(inheritanceRestriction);
        }
        return result;
    }
}
