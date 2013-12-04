/*
 * FunctionalSort.java
 *
 * Created on October 29, 2003, 10:16 AM
 */

package blue.sort;

/**
 *
 * @author  cumming
 */
public class FunctionalSort extends Sort {
    protected String formula; //some kind of description of a formula? eg. getCount()
    
    /** Creates a new instance of FunctionSort */
    public FunctionalSort(Sorts context, String name, String formula) {
        super(context, name);
        this.formula = formula;
    }
    
    Sort combine(Sort other) {
        return null;
    }
    
    Sort duplicate(String name) {
        return null;
    }
    
    public blue.form.Form newForm() {
        return null;
    }
    
    blue.form.Form newForm(Sort parent) {
        return null;
    }
    
    Match relate(RecursiveSort other) {
        return null;
    }
    
    Match relate(PrimitiveSort other) {
        return null;
    }
    
    Match relate(AttributeSort other) {
        return null;
    }
    
    Match relate(DisjunctiveSort other) {
        return null;
    }
    
    Match relate(Aspect other) {
        return null;
    }
    
    public void visualize(blue.io.GraphicsContext gc) {
    }
    
}
