package c4stor.com.feheroes;

/**
 * Created by Nicolas on 08/02/2017.
 */

public class FEHero {

    public String name;
    public int HP;
    public int Atk;
    public int Spd;
    public int Def;
    public int Res;
    public int HP40;
    public int Atk40;
    public int Spd40;
    public int Def40;
    public int Res40;

    public FEHero(String a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k) {
        this.name=a;
        this.HP = b;
        this.Atk =c;
        this.Spd=d;
        this.Def=e;
        this.Res=f;
        this.HP40=g;
        this.Atk40 =h;
        this.Spd40=i;
        this.Def40=j;
        this.Res40=k;
    }

    public String toString() {
        return name;
    }
}
