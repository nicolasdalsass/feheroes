package c4stor.com.feheroes.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eclogia on 14/06/17.
 */

public class OrInheritanceRestrictionType implements InheritanceRestrictionType {
    public List<InheritanceRestrictionType> restrictions;

    public OrInheritanceRestrictionType(InheritanceRestrictionType... inheritanceRestrictionTypes) {
        restrictions = new ArrayList<>(inheritanceRestrictionTypes.length);
        restrictions.addAll(Arrays.asList(inheritanceRestrictionTypes));
    }

    @Override
    public boolean isInheritanceCompatibleWith(InheritanceRestrictionType inheritanceRestrictionType) {
        boolean result = restrictions.get(0).isInheritanceCompatibleWith(inheritanceRestrictionType);
        for (int i = 1; i < restrictions.size(); i++) {
            result = result || restrictions.get(i).isInheritanceCompatibleWith(inheritanceRestrictionType);
        }
        return result;
    }
}
