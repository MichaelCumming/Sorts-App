/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AspectsSort.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 16.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import java.util.Vector;
import java.util.HashSet;
import blue.struct.Parameter;
import blue.io.*;
import sortsApp.*;

/**
 * An <b>Aspects-Sort</b> defines a linking of two {@link Sort}s.
 * It is similar to a {@link PrimitiveSort} in that it depends on the
 * specification of a characteristic individual (i.e., {@link blue.ind.Relation})
 * and a template specifying the algebraic behavior of its forms
 * (i.e., {@link blue.form.RelationalForm}). However, an <i>aspects-sort</i> is not
 * a sort that can be used as such in compositions with other sorts. Instead,
 * an aspects-sort defines two {@link blue.sort.Aspect}s, corresponding to
 * both sorts that it links, that each represent a separate, unidirectional view
 * of this linking. These <i>aspects</i> specify simple sorts, and are defined
 * automatically as the result of the definition of an aspects-sort. Individual
 * aspects can be used within the definition of other sorts. Each aspect is identified by a unique name. <p>
 * The <b>AspectsSort</b> class extends on the {@link PrimitiveSort} class.
 * It represents an aspects sort additionally by the array of aspect names.
 * The definition of an aspects-sort is similar to the definition of a primitive
 * sort, where the argument list specifies both sorts to be linked, and the name
 * that is assigned to the sort is replaced by a list of aspect names separated
 * by comma's and enclosed within parentheses, such that the order of the aspect
 * names corresponds to the order of the argument sorts (see also {@link PrimitiveSort}).<br>
 * <tt>definition := '(' names ')' ':' primitive '(' names ')' <br>names := name ',' name
 * <br>primitive := '[' charind ']'</tt>
 */
public class AspectsSort extends PrimitiveSort {
    // representation
    private Aspect aspects[];
    // constructor
    
    /**
     * Creates an <b>aspects-sort</b> according to the specified definition,
     * and within the specified context. This definition includes the assignment.
     * @param context a {@link Sorts} object
     * @param expression a {@link blue.io.ParseReader} object that presents the sort's definition
     * @throws ParseException if the reader does not present a proper definition
     */
    AspectsSort(Sorts context, ParseReader expr) throws ParseException {
        super(context);
        if (expr.token() != '(')
            throw new ParseException(expr, "'(' expected");
        Vector tuple = new Vector();
        while (true) {
            if (expr.newToken() != IDENTIFIER)
                throw new ParseException(expr, "Expected an identifier");
            tuple.addElement(expr.tokenString());
            if (expr.newToken() != ',') break;
        }
        if (expr.token() != ')')
            throw new ParseException(expr, "')' expected");
        if (expr.newToken() != ':')
            throw new ParseException(expr, "':' expected");
        expr.newToken();
        Parameter parameter = characteristic(expr);
        int aspectcount = parameter.cardinality();
        if (aspectcount == 0)
            throw new ParseException(expr, "This characteristic Individual accepts no aspects");
        if (aspectcount < tuple.size())
            throw new ParseException(expr, "Too few aspects specified");
        if (aspectcount > tuple.size())
            throw new ParseException(expr, "Too many aspects specified");
        expr.newToken();
        this.arguments(context, parameter, expr);
        String[] names = new String[tuple.size()];
        tuple.copyInto(names);
        this.aspects = new Aspect[names.length];
        this.assign(names);
        for (int n = 0; n < names.length; n++) {
            this.aspects[n] = new Aspect(this.context(), this);
            this.aspects[n].assign(names[n]);
            this.aspects[n].canonical = names[n];
            this.aspects[n].definition = this.definition;
        }
    }
    // access methods
    
    /**
     * Returns an array of all <b>aspects</b> belonging to this aspects-sort.
     * @return an {@link Aspect} array
     */
    Aspect[] aspects() { return this.aspects; }
    
    /**
     * Returns the <b>argument</b> sort of this aspects-sort that corresponds to the specified aspect.
     * @return a {@link Sort} object
     */
    Sort argument(Aspect aspect) {
        int n = 0;
        for ( ; (aspect != this.aspects[n]) && (n < this.aspects.length); n++);
        if (n >= this.aspects.length)
            throw new IllegalArgumentException("Invalid Aspect specified");
        return ((Sort[]) this.arguments().value()) [n];
    }
    // methods
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be duplicated.
     * @throws IllegalArgumentException always
     */
    Sort duplicate(String name) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be combined with any other sort.
     * @throws IllegalArgumentException always
     */
    Sort combine(Sort other) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be related to any other sort except another aspects-sort.
     * @throws IllegalArgumentException always
     */
    Match relate(Aspect other) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be related to any other sort except another aspects-sort.
     * @throws IllegalArgumentException always
     */
    Match relate(PrimitiveSort other) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be related to any other sort except another aspects-sort.
     * @throws IllegalArgumentException always
     */
    Match relate(AttributeSort other) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be related to any other sort except another aspects-sort.
     * @throws IllegalArgumentException always
     */
    Match relate(DisjunctiveSort other) {
        throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    
    /**
     * <b>Relates</b> this aspects-sort to another aspects-sort and returns the
     * resulting match. If these are not identical, but their definitions are
     * identical (except for the assignment operation), then these are denoted
     * equivalent. In all other cases, no match exists.
     * @param other an <tt>AspectsSort</tt> object
     * @return a {@link Match} object
     */
    Match relate(AspectsSort other) {
        if (this == other)
            return Match.identical(this);
        if (this.canonical == other.canonical)
            return Match.equivalent(this, other);
        return Match.none;
    }
    
    //28.Sug.2003 Michael Cumming
    //return a list of string defs for each sort
    public Vector getSortDefProps() { //set of Strings returned. always in pairs?? Assume yes: 28.08.2003
        Vector sortDefs = new Vector();
        Aspect[] a = this.aspects();
        //if (a.length != 2)
        System.out.println("175.Aspect array has " + String.valueOf(a.length) + " members");
        for (int i = 0; i < a.length; i++) {
            Sort s = a[i].argument();
            if (s != null) {
                sortDefs.add(s.toString()); //send sorts or strings?
            }
        }
        return sortDefs;
    }
    
    /** Getter for property aspects.
     * @return Value of property aspects.
     *
     */
    public blue.sort.Aspect[] getAspects() {
        return this.aspects;
    }
    
    //29.Aug.2003 MC
    //normally two Aspects
    public blue.sort.Aspect getFirstAspect() {
        if (this.aspects.length==2)
            return this.aspects[0];
        else System.out.println("Aspect list not equal to two");
        return null;
    }
    
    //29.Aug.2003 MC
    //test method
    public blue.sort.Aspect getSecondAspect() {
        if (this.aspects.length==2)
            return this.aspects[1];
        else System.out.println("Aspect list not equal to two");
        return null;
    }
    
    /** Setter for property aspects.
     * @param aspects New value of property aspects.
     *
     */
    public void setAspects(blue.sort.Aspect[] aspects) {
        this.aspects = aspects;
    }
    
}
