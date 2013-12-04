/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Functions.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind.function;

import java.util.Hashtable;
import javax.vecmath.Vector3d;
import com.eteks.parser.CompilationException;

import cassis.IllegalOverwriteException;
import cassis.sort.User;
import cassis.parse.*;

/**
 * The <b>Functions</b> class provides functionality for storing and
 * maintaining function definitions. A <tt>Functions</tt> instance is
 * represented by the user profile it is defined for, and a facility for
 * storing function definitions by their name. A number of function definitions
 * are predefined. These function definitions may be overwritten as long
 * as these are not active, i.e., these have not yet been retrieved.
 * @see Function
 * @see #getFunction(String)
 */
public class Functions {
    
    // constants
    /**
     * The name of the predefined main-function '<b>count</b>':<br>
     * count(x) = c : {c(0) = 0, c(+1) = c+1}
     */
    static final String COUNT = "count";
    /**
     * The name of the predefined main-function '<b>sum</b>':<br>
     * sum(x) = s : {s(0) = 0, s(+1) = s+x}
     */
    static final String SUM = "sum";
    /**
     * The name of the predefined main-function '<b>avg</b>':<br>
     * avg(x) = s/c : {s(0) = 0, s(+1) = s+x, c(0) = 0, c(+1) = y+1}
     */
    static final String AVG = "avg";
    /**
     * The name of the predefined main-function '<b>max</b>':<br>
     * max(x) = m : {m(0) = -inf, m(+1) = if (x > m) then x else m}
     */
    static final String MAX = "max";
    /**
     * The name of the predefined main-function '<b>min</b>':<br>
     * min(x) = m : {m(0) = inf, m(+1) = if (x < m) then x else m}
     */
    static final String MIN = "min";
    /**
     * The name of the predefined main-function '<b>innerproduct</b>':<br>
     * innerproduct(x,y) = z : {z(0) = 0, z(+1) = z+x*y}
     */
    static final String INNER = "innerproduct";
    /**
     * The name of the predefined main-function '<b>midpoint</b>':<br>
     * midpoint(p,q) = m/c : {m(0) = (0,0,0), m(+1) = m+(p+q)/2, c(0) = 0, c(+1) = c+1}
     */
    static final String MID = "midpoint";
    /**
     * The name of the predefined aux-function '<b>product</b>':<br>
     * product(x,y) = x * y
     */
    static final String PROD = "product";
    /**
     * The name of the predefined aux-function '<b>distance</b>':<br>
     */
    static final String DIST = "distance";
    /**
     * The name of the predefined aux-function '<b>dist2line</b>':<br>
     */
    static final String PT2LN = "dist2line";
    /**
     * The name of the predefined aux-function '<b>dist2lnseg</b>':<br>
     */
    static final String PT2LS = "dist2lnseg";
    /**
     * The value <b>zero</b> as a <tt>Double</tt> object.
     */
    static final Double ZERO = new Double(0);
    /**
     * The value <b>negative infinity</b> as a <tt>Double</tt> object.
     */
    static final Double NEG_INF = new Double(Double.NEGATIVE_INFINITY);
    /**
     * The value <b>positive infinity</b> as a <tt>Double</tt> object.
     */
    static final Double POS_INF = new Double(Double.POSITIVE_INFINITY);
    /**
     * The <b>origin</b> vector as a <tt>Vector3d</tt> object.
     */
    static final Vector3d ORIGIN = new Vector3d(0,0,0);


    // representation
    private User profile;
    private Hashtable functions, predefined;

    // constructor

    /**
     * Constructs a <b>Functions</b> instance for the specified user profile,
     * and predefines the following functions: count, sum, avg, max, min,
     * innerproduct, midpoint, product, distance, dist2line and dist2lnseg.
     * @param profile a {@link cassis.sort.User} object
     * @see MainFunction#MainFunction(String, String[], String[], Object[], String[], String)
     * @see AuxFunction#AuxFunction(String, String[], String)
     */
    public Functions(User profile) {
	super();
	this.profile = profile;
	this.functions = new Hashtable();
        this.predefined = new Hashtable();
	try {
            Function fn = new MainFunction(Functions.COUNT, new String[] {"x"}, new String[] {"c"}, new Object[] {ZERO}, new String[] {"c + 1"}, "c");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.SUM, new String[] {"x"}, new String[] {"s"}, new Object[] {ZERO}, new String[] {"s + x"}, "s");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.AVG, new String[] {"x"}, new String[] {"s","c"}, new Object[] {ZERO, ZERO}, new String[] {"s + x","c + 1"}, "s/c");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.MAX, new String[] {"x"}, new String[] {"m"}, new Object[] {NEG_INF}, new String[] {"if (x > m) then x else m"}, "m");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.MIN, new String[] {"x"}, new String[] {"m"}, new Object[] {POS_INF}, new String[] {"if (x < m) then x else m"}, "m");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.INNER, new String[] {"x","y"}, new String[] {"z"}, new Object[] {ZERO}, new String[] {"z + x * y"}, "z");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new MainFunction(Functions.MID, new String[] {"p","q"}, new String[] {"m","c"}, new Object[] {ORIGIN, ZERO}, new String[] {"m + (p + q) / 2","c + 1"}, "m / c");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new AuxFunction(Functions.PROD, new String[] {"x","y"}, "x * y");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new AuxFunction(Functions.DIST, new String[] {"p","q"}, "mag(p - q)");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new AuxFunction(Functions.PT2LN, new String[] {"pt", "root", "dir"}, "mag(dir xprod (root - pt))");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
            fn = new AuxFunction(Functions.PT2LS, new String[] {"pt", "tail", "head"}, "if (- (head - tail) * (tail - pt) / sqr(head - tail)) < 0 then mag(tail - pt) else if (- (head - tail) * (tail - pt) / sqr(head - tail)) > 1 then mag(head - pt) else mag((head - tail) xprod (tail - pt)) / mag(head - tail)");
            this.functions.put(fn.name(), fn);
            this.predefined.put(fn.name(), Boolean.FALSE);
	} catch (CompilationException e) {
	    System.err.println("One or more function definitions are not predefined");
            System.err.println(e.getError() + ": " + e.getExtractedString());
	}
    }

    // access methods

    /**
     * Returns the user <b>profile</b> this <tt>Functions</tt> instance is
     * defined for.
     * @return a {@link cassis.sort.User} object
     */
    public User profile() { return this.profile; }

    /**
     * Returns the <tt>Functions</tt> instance defined for the specified
     * user profile.
     * @param name a string specifying the user profile
     * @return a <tt>Functions</tt> object
     * @see cassis.sort.User#find
     * @see cassis.sort.User#functions()
     */
    static Functions find(String name) {
	User profile = User.find(name);
	if (profile == null) return null;
	return profile.functions();
    }

    /**
     * <b>Puts a function definition</b> for retrieval by name in this
     * <tt>Functions</tt> instance.
     * @param fn a {@link Function} object
     * @throws IllegalOverwriteException if another (auxiliary) function
     * definition with the same name has already been put, that is not
     * predefined and inactive
     * @see #getFunction(String)
     */
    public void putFunction(Function fn) throws IllegalOverwriteException {
        if (fn == null) return;
        String name = fn.name();
        if (functions.get(name) != null) {
            if (predefined.get(name) != Boolean.FALSE)
                throw new IllegalArgumentException("Function name has already been assigned");
            predefined.remove(name);
        }
        this.functions.put(name, fn);
    }
    /**
     * <b>Gets a function definition</b> from this <tt>Functions</tt>
     * instance, given its name.
     * If this function definition is predefined, then it is activated.
     * @param name a function definition name
     * @return a {@link Function} object
     * @see #putFunction(Function)
     */
    public Function getFunction(String name) {
        if (name == null) return null;
        if (predefined.get(name) == Boolean.FALSE)
            predefined.put(name, Boolean.TRUE);
        return (Function) functions.get(name);
    }

    /**
     * Constructs a function definition from an SDL description of this
     * definition that is expressed by a {@link cassis.parse.ParseReader} object,
     * and stores this function definition in this <tt>Functions</tt>
     * instance.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a
     * function definition
     * @see MainFunction#parse
     * @see AuxFunction#parse
     */
    void parseFunction(ParseReader reader) throws ParseException {
        //
    }

    /**
     * <b>Cleans up</b> this <tt>Functions</tt> instance by removing all
     * functions.
     */
    public void cleanup() {
	this.functions.clear();
    }
}
