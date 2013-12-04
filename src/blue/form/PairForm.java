/*
 * PairForm.java
 *
 * Created on October 31, 2003, 2:21 PM
 */

package blue.form;

import blue.Element;
import blue.ind.Individual;
import java.util.Vector;

/**
 *
 * @author  cumming
 */
public class PairForm {
    Vector pairs; //Vector of OnePairs
    
    //inner class for each pair
    class OnePair {
        Individual objInd;
        Individual attInd;
        
        private OnePair(Individual objInd, Individual attInd) {
            this.objInd = objInd;
            this.attInd = attInd;
        }
    }
    
    /** Creates a new instance of PairForm */
    public PairForm() {
        this.pairs = new Vector();
    }
    
    public void addOnePairIndividuals(Individual objInd, Individual attInd) {
        OnePair oneP = new OnePair(objInd, attInd);
        this.pairs.add(oneP);
    }
    
}
