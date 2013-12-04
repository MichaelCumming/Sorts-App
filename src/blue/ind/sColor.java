/*
 * Color.java
 *
 * Created on September 2, 2003, 10:29 AM
 */

package blue.ind;

import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.struct.Parameter;
import blue.form.DiscreteForm;

/**
 *
 * @author  cumming
 */
public class sColor extends Individual {
    private int r, g, b;
    private boolean nil = true;
    
    /** Creates a new instance of Color */
    public sColor(int redValue, int greenValue, int blueValue) {
        this.r = redValue;
        this.g = greenValue;
        this.b = blueValue;
        nil = false;
    }
    
    public int compare(blue.Thing other) {
        if(!(other instanceof sColor)) return FAILED;
        else {
            sColor otherC = (sColor)other;
            if ((otherC.r == this.r) && (otherC.g == this.g) && (otherC.b == this.b)) return EQUAL;
            else return NOTEQUAL;
        }
    }
    
    public blue.Element duplicate() {
        return new sColor(this.r, this.g, this.b);
    }
    
    public boolean nil() {
        return this.nil;
    }
    
    void parse(blue.io.ParseReader reader) throws blue.io.ParseException {
        return;
    }
    
    String valueToString() {
        if (this.nil) return NIL;
        else return
        (String.valueOf(this.r) + "." +
        String.valueOf(this.g) + "." +
        String.valueOf(this.b));
    }
    
    void visualizeValue(blue.io.GraphicsContext gc) {
        gc.label(this.valueToString());
    }
    
}
