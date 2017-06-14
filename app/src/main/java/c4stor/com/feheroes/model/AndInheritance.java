package c4stor.com.feheroes.model;

/**
 * Created by eclogia on 14/06/17.
 */

public class AndInheritance implements Inheritance {
    public Inheritance condition1;
    public Inheritance condition2;

    public AndInheritance(Inheritance condition1, Inheritance condition2) {
        this.condition1 = condition1;
        this.condition2 = condition2;
    }
}
