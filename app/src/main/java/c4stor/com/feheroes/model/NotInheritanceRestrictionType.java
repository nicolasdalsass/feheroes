package c4stor.com.feheroes.model;

/**
 * Created by eclogia on 14/06/17.
 */

public class NotInheritanceRestrictionType implements InheritanceRestrictionType {
    public InheritanceRestrictionType restriction;

    public NotInheritanceRestrictionType(InheritanceRestrictionType restriction) {
        this.restriction = restriction;
    }

    @Override
    public boolean isInheritanceCompatibleWith(InheritanceRestrictionType inheritanceRestrictionType) {
        return restriction != inheritanceRestrictionType;
    }
}
