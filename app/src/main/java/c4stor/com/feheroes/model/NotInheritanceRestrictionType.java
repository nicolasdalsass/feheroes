package c4stor.com.feheroes.model;

/**
 * Created by eclogia on 14/06/17.
 */

public class NotInheritanceRestrictionType implements InheritanceRestriction {
    public InheritanceRestriction restriction;

    public NotInheritanceRestrictionType(InheritanceRestriction restriction) {
        this.restriction = restriction;
    }

    @Override
    public boolean isCompatibleWith(InheritanceRestriction inheritanceRestrictionType) {
        return restriction != inheritanceRestrictionType;
    }
}
