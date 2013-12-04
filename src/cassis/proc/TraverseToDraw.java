/*
 * PrintOut.java
 *
 * Created on 11 februari 2005, 14:48
 */

package cassis.proc;

import cassis.ind.*;

/**
 *
 * @author  cumming
 */
public class TraverseToDraw extends Traversal {
    
    
    /** Creates a new instance of PrintOut */
    public TraverseToDraw() {
        super();
    }
    
    public String toString() { //return String.valueOf(this.count);
        return null;
    }
    
    public void initialize() {
        
    }
    
    /**Draw elements as they are visited */
    public void visit(Individual current) {
        current.toString();
        //        if (current.getClass() == Weight.class)
        //            this.count += ((Weight)current).value();
    }
    
    public void leave(Individual current) {
        //        if (current.getClass() == Weight.class)
        //            this.weight = 0.0;
        //        else if (current.getClass() == Point.class)
        //this.position = null;
    }
    
}
