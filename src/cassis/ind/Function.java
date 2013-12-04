/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Function.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.lang.reflect.Method;
import javax.vecmath.Vector3d;

import cassis.Thing;
import cassis.Element;
import cassis.visit.FunctionVisitor;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.struct.Parameter;
import cassis.struct.Vector;
import cassis.form.DiscreteForm;
import cassis.ind.function.MainFunction;
import cassis.ind.function.AuxFunction;

/**
 * A <b>function</b> is a functional entity that results in a numerical or
 * vector value and can be applied to all combinations of individuals from the
 * specified sorts. It computes this (numerical or vector) value by applying
 * its function definition to the specified (numerical or vector) methods of
 * the characteristic individuals of these sorts.
 * <p>
 * The <b>Function</b> class defines the characteristic individual for
 * functions. A function is represented by a {@link MainFunction}, an optional
 * {@link AuxFunction}, an array of target {@link PrimitiveSort}s, an array of
 * target methods of these sorts' characteristic individuals, and
 * a {@link FunctionVisitor}. The main-function is used to compute the value
 * of this function. If an aux-function is specified, this is used to compute
 * the input value to the main-function on each application of this
 * main-function. The target primitive sorts specify the respective individuals
 * the main-function or aux-function is applied to. The target methods,
 * corresponding to each target primitive sort, specify each individual's
 * method that is used in the computation. This method must return a double
 * value or an instance of the <tt>Double</tt>, <tt>Vector3d</tt> or
 * {@link Vector} class and may not take any arguments.<br>
 * This characteristic individual accepts no parameters.<br>
 * Forms of functions adhere to a discrete behavior.
 * @see DiscreteForm
 */
public class Function extends Individual {
    static {
        PrimitiveSort.register(Function.class, DiscreteForm.class, Parameter.NONE);
        new cassis.visit.vrml.Proto(Function.class, "icons/numeric.gif");
    }
    
    // representation
    private MainFunction mainfunction;
    private AuxFunction auxfunction;
    private PrimitiveSort[] targets;
    private Method[] methods;
    private FunctionVisitor visitor;
    
    // constructors
    
    /**
     * Constructs a nondescript <b>Function</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * function. This function must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Function() {
        super();
        this.mainfunction = null;
        this.auxfunction = null;
        this.targets = null;
        this.methods = null;
        this.visitor = null;
    }
    private Function(Sort sort, MainFunction main_f, AuxFunction aux_f, PrimitiveSort[] targets, Method[] methods) {
        super(sort);
        this.mainfunction = main_f;
        this.auxfunction = aux_f;
        this.targets = targets;
        this.methods = methods;
        this.visitor = new FunctionVisitor(this);
    }
    
    /**
     * Constructs a <b>Function</b> from a main function name,
     * a single target primitive sort, and a single target method name of this
     * sort's characteristic individual, for the specified sort. This last sort
     * must allow for functions as individuals. The target method must return
     * either a double value or an instance of the <tt>Double</tt>,
     * <tt>Vector3d</tt> or {@link Vector} class, or an instance of the
     * <tt>Object</tt> class if the target sort's characteristic individual is
     * a <tt>Function</tt> and the target method is the <tt>value()</tt> method,
     * and may not take any arguments.
     * @param sort a {@link Sort} object
     * @param function  the name of a {@link MainFunction} object
     * @param target a {@link PrimitiveSort} object
     * @param method a <tt>String</tt> object
     * @throws IllegalArgumentException if the function definition name is not
     * recognized, the function's parameter count is not equal to one, or
     * the method name is not recognized or doesn't specify a valid method.
     */
    public Function(Sort sort, String function, PrimitiveSort target, String method) throws IllegalArgumentException {
        super(sort);
        this.mainfunction = (MainFunction) sort.context().profile().functions().getFunction(function);
        this.targets = new PrimitiveSort[] {target};
        this.methods = null;
        if (this.mainfunction == null)
            throw new IllegalArgumentException("Function name is not recognized");
        if (this.mainfunction.parameterCount() != 1)
            throw new IllegalArgumentException("Function's parameter count is greater than one");
        //if (target.equals(sort))
        //    throw new IllegalArgumentException("Target sort is identical to numeric function sort");
        try {
            this.methods = new Method[] {target.characteristic().getMethod(method, null)};
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Target sort's characteristic individual does not support the specified method");
        }
        if ((target.characteristic() != Function.class) || !method.equals("value")) {
            Class type = this.methods[0].getReturnType();
            if ((type != java.lang.Double.TYPE) &&
                    (type != java.lang.Double.class) &&
                    (type != Vector.class))
                throw new IllegalArgumentException("Target method does not return a double type or class or a vector class");
        }
    }
    /**
     * Constructs a <b>Function</b> from a main function name, an optional
     * auxiliary function name, an array of target primitive sorts,
     * and a corresponding array of target method names of the sort's
     * characteristic individuals, for the specified sort. This last sort must
     * allow for functions as individuals. Each target method must return
     * either a double(or <tt>Double</tt> object) or a {@link Vector}, unless
     * the target sort's characteristic individual is a <tt>Function</tt> and
     * the target method is the <tt>value()</tt> method, and may not take any
     * arguments.
     * @param sort a {@link Sort} object
     * @param main_f the name of a {@link MainFunction} object
     * @param aux_f  the name of a {@link AuxFunction} object
     * @param targets a {@link PrimitiveSort} array
     * @param methods a <tt>String</tt> array
     * @throws IllegalArgumentException if the function definition name is not
     * recognized, the function's parameter count is not equal to the number
     * of argument values, or a method name is not recognized or doesn't
     * specify a valid method.
     */
    public Function(Sort sort, String main_f, String aux_f, PrimitiveSort[] targets, String[] methods) throws IllegalArgumentException {
        super(sort);
        this.mainfunction = (MainFunction) sort.context().profile().functions().getFunction(main_f);
        this.auxfunction = (AuxFunction) sort.context().profile().functions().getFunction(aux_f);
        this.targets = targets;
        this.methods = null;
        if (this.mainfunction == null)
            throw new IllegalArgumentException("Function name is not recognized");
        if (this.targets.length != methods.length)
            throw new IllegalArgumentException("Number of methods does not correspond to number of target sorts");
        if (((this.auxfunction != null) && (this.auxfunction.parameterCount() != methods.length)) ||
                ((this.auxfunction == null) && (this.mainfunction.parameterCount() != methods.length)))
            throw new IllegalArgumentException("Function's parameter count does not correspond to the number of arguments");
        if ((this.auxfunction != null) && (this.mainfunction.parameterCount() != 1))
            throw new IllegalArgumentException("Incompatible function components: main function's parameter count is greater than one");
        this.methods = new Method[methods.length];
        for (int n = 0; n < targets.length; n++) {
            //if (this.targets[n].equals(sort))
            //    throw new IllegalArgumentException("Target sort " + this.targets[n] + " is identical to numeric function sort");
            try {
                this.methods[n] = this.targets[n].characteristic().getMethod(methods[n], null);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Target sort's characteristic individual does not support the specified method" + methods[n]);
            }
            if ((this.targets[n].characteristic() != Function.class) || !methods[n].equals("value")) {
                Class type = this.methods[n].getReturnType();
                if ((type != java.lang.Double.TYPE) &&
                        (type != java.lang.Double.class) &&
                        (type != Vector.class))
                    throw new IllegalArgumentException("Target method " + methods[n] + " does not return a double type or class or a vector class");
            }
        }
    }
    
    // access method
    
    /**
     * Return the name of the function's <b>main-function</b>.
     * @return a <tt>String</tt> object
     */
    public String mainFunction() {
        return this.mainfunction.name();
    }
    /**
     * Return the name of the function's <b>aux-function</b>.
     * @return a <tt>String</tt> object
     */
    public String auxFunction() {
        if (this.auxfunction == null) return null;
        return this.auxfunction.name();
    }
    
    /**
     * Return the function's array of <b>target</b> sorts.
     * @return a {@link PrimitiveSort} array
     */
    public PrimitiveSort[] targets() {
        return this.targets;
    }
    
    /**
     * Computes this function's (numerical or vector) <b>value</b>.
     * It first initializes its main-function, then visits this individual's
     * attribute form, and finally computes the function's result.
     * @return either a <tt>Double</tt> or <tt>Vector3d</tt> object
     * @see MainFunction#initialize()
     * @see cassis.Element#accept(cassis.visit.ElementVisitor, cassis.ind.Individual)
     * @see MainFunction#compute()
     */
    public Object value() {
        this.mainfunction.initialize();
        this.visitor.clear();
        if (this.attrDefined())
            this.attribute().accept(this.visitor, null);
        else if (this.associate() != null)
            this.associate().accept(this.visitor);
        return this.mainfunction.compute();
    }
    
    /**
     * Tests whether this function equals <b>nil</b>.
     * This is the case if either the main-function, the array of target sorts,
     * or the array of methods is null.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt>
     * otherwise
     */
    public boolean nil() {
        return ((this.mainfunction == null) || (this.targets == null) || (this.methods == null));
    }
    
    // methods
    
    /**
     * <b>Applies</b> this function to an array of individuals.
     * It first invokes the respective methods to the individuals, then if
     * an aux-function is specified applies this aux- function to the resulting
     * values, and finally applies its main-function to the result.
     * @param inds an {@link Individual} array
     * @throws IllegalArgumentException if the number of individuals does not
     * equal the number of target sorts or if the individuals do not belong
     * to the respective target sorts
     * @see MainFunction#apply
     * @see AuxFunction#apply
     */
    public void apply(Individual[] inds) throws IllegalArgumentException {
        if (inds.length != this.methods.length)
            throw new IllegalArgumentException("Number of individuals does not correspond to number of methods");
        try {
            Object[] values = new Object[inds.length];
            for (int n = 0; n < inds.length; n++) {
                if (this.methods[n].getReturnType() == Vector.class)
                    values[n] = ((Vector) this.methods[n].invoke(inds[n], null)).vector3dValue();
                else values[n] = this.methods[n].invoke(inds[n], null);
            }
            if (this.auxfunction != null) {
                Object aux = this.auxfunction.apply(values);
                values = new Object[1];
                values[0] = aux;
            }
            this.mainfunction.apply(values);
        } catch (IllegalAccessException e) {
        } catch (java.lang.reflect.InvocationTargetException e) {
        }
    }
    
    
    /**
     * <b>Duplicates</b> this function. It returns a new individual with
     * the same specifications, defined for the base sort of this
     * function's sort.
     * @return an {@link Element} object
     * @see Sort#base()
     */
    public Element duplicate() {
        return new Function(this.ofSort().base(), this.mainfunction, this.auxfunction, this.targets, this.methods);
    }
    
    /**
     * Checks whether this function has <b>equal value</b> to another
     * individual. This condition applies if both functions have equal
     * main-functions and aux-functions, and target sorts and methods.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @throws ClassCastException if the argument is not a function
     */
    boolean equalValued(Individual other) {
        if (this.nil()) return (other.nil());
        if (!this.mainfunction.equals(((Function) other).mainfunction) ||
                this.targets.length != ((Function) other).targets.length)
            return false;
        if (this.auxfunction != null) {
            if (!this.auxfunction.equals(((Function) other).auxfunction)) return false;
        } else  if (((Function) other).mainfunction != null) return false;
        for (int n = 0; n < this.targets.length; n++)
            if (!this.targets[n].equals(((Function) other).targets[n]) ||
                !this.methods[n].equals(((Function) other).methods[n]))
                return false;
        return true;
    }
    
    /**
     * Compares this function to another thing.
     * The result equals {@link Thing#FAILED} if the argument is not a function.
     * Otherwise the result is defined by comparing the main-functions and
     * aux-functions, and the target sorts and methods of both functions.
     * @param other a {@link Thing} object
     * @return an integer value equal to one of {@link Thing#EQUAL},
     * {@link Thing#LESS}, {@link Thing#GREATER}, or {@link Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof Function)) return FAILED;
        int c = this.mainfunction.compare(((Function) other).mainfunction);
        if (c == 0) {
            if (this.auxfunction != null) {
                c = this.auxfunction.compare(((Function) other).auxfunction);
            } else if (((Function) other).mainfunction != null) c = -1;
        }
        if (c == 0) c = this.targets.length - ((Function) other).targets.length;
        if (c == 0)
            for (int n = 0; (c == 0) && (n < this.targets.length); n++) {
            c = this.targets[n].compare(((Function) other).targets[n]);
            if (c == 0) c = this.methods[n].getName().compareTo(((Function) other).methods[n].getName());
            }
        return (c < 0) ? LESS : ((c > 0) ? GREATER : EQUAL);
    }
    
    /**
     * Converts this function <b>to a string</b>. The result is
     * the main-function name, followed by the symbol "|" and the aux-function
     * name (if defined), then followed by a parenthesized list of expressions
     * of each target sort name and the method name concatenated with a '.',
     * and finally followed by the symbol '=' and this function's value.
     * This string can be included in an SDL description and subsequently
     * parsed to reveal the original function.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     * @see #value()
     */
    public String toString(Individual assoc) {
        if (this.nil()) return NIL;
        StringBuffer result = new StringBuffer(this.mainfunction.name());
        if (this.auxfunction != null) result.append("|").append(this.auxfunction.name());
        result.append("(").append(this.targets[0]).append(".").append(this.methods[0].getName());
        for (int n = 1; n < this.targets.length; n++)
            result.append(",").append(this.targets[n]).append(".").append(this.methods[n].getName());
        return result.append(") = ").append(this.value()).toString();
    }
    
    /**
     * Reads an SDL description of a function from a
     * {@link ParseReader} object and assigns the value to this numeric
     * function. This description must start with a function definition name
     * specifying an existing {@link MainFunction}. This may be followed by
     * a '|' and a second function definition name specifying an existing
     * {@link AuxFunction}. This must be followed by a parenthesized list of
     * expressions, each of a sort name specifying a {@link PrimitiveSort}
     * and a name of a method of this sort's characteristic individual
     * concatenated with a '.'. The method cannot take any arguments and
     * must return a double value or an instance of the <tt>Double</tt>,
     * <tt>Vector3d</tt> or {@link Vector} class. If this expression is
     * followed by a '=', a number or a parenthesized tuple of three numbers
     * must complete this description.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a
     * function
     * @see #toString
     */
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != IDENTIFIER)
            throw new ParseException(reader, "identifier expected");
        this.mainfunction = (MainFunction) this.ofSort().context().profile().functions().getFunction(reader.tokenString());
        if (this.mainfunction == null)
            throw new ParseException(reader, "function name not recognized");
        if (reader.newToken() == '|') {
            if (reader.newToken() != IDENTIFIER)
                throw new ParseException(reader, "identifier expected");
            this.auxfunction = (AuxFunction) this.ofSort().context().profile().functions().getFunction(reader.tokenString());
            if (this.auxfunction == null)
                throw new ParseException(reader, "function name not recognized");
            reader.newToken();
        }
        if (reader.token() != '(')
            throw new ParseException(reader, "'(' expected");
        boolean targets = true;
        int n = 0;
        java.util.Vector sorts = new java.util.Vector();
        java.util.Vector methods = new java.util.Vector();
        while (targets) {
            if (reader.newToken() != IDENTIFIER)
                throw new ParseException(reader, "identifier expected");
            Sort target = this.ofSort().context().sortOf(reader.tokenString());
            if (target == null)
                throw new ParseException(reader, "target sort name not recognized");
            if (!(target instanceof PrimitiveSort))
                throw new ParseException(reader, "target sort is not a primitive sort");
            //if (target.equals(this.ofSort()))
            //    throw new ParseException(reader, "target sort is identical to individual's sort");
            sorts.addElement((PrimitiveSort) target);
            if (reader.newToken() != '.')
                throw new ParseException(reader, "'.' expected");
            if (reader.newToken() != IDENTIFIER)
                throw new ParseException(reader, "identifier expected");
            try {
                methods.addElement(((PrimitiveSort) target).characteristic().getMethod(reader.tokenString(), null));
            } catch (NoSuchMethodException e) {
                throw new ParseException(reader, "method not supported by target sort's characteristic individual");
            }
            if (reader.newToken() == ',')
                n++;
            else targets = false;
        }
        this.targets = new PrimitiveSort[sorts.size()];
        sorts.copyInto(this.targets);
        this.methods = new Method[methods.size()];
        methods.copyInto(this.methods);
        if (reader.token() != ')')
            throw new ParseException(reader, "')' expected");
        if (reader.previewToken() == '=') {
            reader.newToken();
            if (reader.newToken() != NUMBER)
                throw new ParseException(reader, "number expected");
        }
        this.visitor = new FunctionVisitor(this);
    }
    
}
