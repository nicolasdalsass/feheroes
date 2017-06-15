package c4stor.com.feheroes.model;

/**
 * Created by eclogia on 14/06/17.
 */

public class NotInheritanceRestriction implements InheritanceRestriction {
    public InheritanceRestriction restriction;

    public NotInheritanceRestriction(InheritanceRestriction restriction) {
        this.restriction = restriction;
    }

    @Override
    public boolean inheritedBy(InheritanceRestriction inheritanceRestriction) {
        return restriction != inheritanceRestriction;
    }
}
