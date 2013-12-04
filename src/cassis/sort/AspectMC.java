/*
 * AspectMC.java
 *
 * Created on 30 maart 2005, 11:57
 */

package cassis.sort;

/**
 *
 * @author cumming
 */
public class AspectMC extends Aspect {
    
    /** Creates a new instance of AspectMC */
    public AspectMC(Sorts context, AspectsSort instance) {
        super(context, instance);
    }
    
    //only for an Aspect sort
    //29.Aug.2003 MC
    public String getFirstPartDef() {
        String def = this.definition(); //e.g. "[Property] (*src1*, dest1)"
        return def.substring(def.indexOf("(") + 1, def.indexOf(","));
    }
    
    //29.Aug.2003 MC
    public String getSecondPartDef() {
        String def = this.definition(); //e.g. "[Property] (src1, *dest1*)"
        return def.substring(def.indexOf(",") + 2, def.indexOf(")"));
    }
    
}
