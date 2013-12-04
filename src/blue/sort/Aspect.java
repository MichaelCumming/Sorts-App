/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Aspect.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import java.util.Vector;
import blue.struct.Argument;
import blue.struct.Parameter;
import blue.form.Form;
import blue.io.GraphicsContext;

/**
 * An <b>Aspect</b> is a simple {@link Sort} that defines a single, unidirectional view
 * of an {@link AspectsSort}, linking two sorts. Each aspects-sort's argument defines
 * exactly one aspect. An aspect always has a name assigned. <p>
 * The <b>Aspect</b> class represents an aspect additionally by the {@link AspectsSort} instance it belongs to.
 * An aspect is defined as part of the definition of its {@link AspectsSort}.
 */
public class Aspect extends Sort implements SimpleSort {
    // representation
    private AspectsSort instance;
    // constructor
    
    /**
     * Creates an <b>aspect</b> to the specified aspects-sort instance.
     * @param context a {@link Sorts} object
     * @param instance an {@link AspectsSort} object
     */
    Aspect(Sorts context, AspectsSort instance) {
        super(context);
        this.instance = instance;
    }
    // access methods
    
    /**
     * Returns the number of <b>named</b> components of this sort. This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#names
     */
    int names() { return this.instance.names(); }
    
    /**
     * Returns the number of <b>simple</b> components of this sort. This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#simples
     */
    int simples() { return this.instance.simples(); }
    
    /**
     * Returns the total number of <b>components</b> of this sort. This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#components
     */
    int components() { return this.instance.components(); }
    
    /**
     * Returns the class specifying the <b>characteristic</b> individual for this aspect.
     * This class is retrieved from the aspects-sort instance.
     * @return a <tt>Class</tt> object
     * @see AspectsSort#characteristic
     */
    public Class characteristic() { return this.instance.characteristic(); }
    
    /**
     * Returns the <b>arguments</b> of this aspect. These are retrieved from the aspects-sort instance.
     * @return an {@link blue.struct.Argument} object
     * @see AspectsSort#arguments
     */
    public Argument arguments() { return this.instance.arguments(); }
    
    /**
     * Returns an array of all <b>aspects</b> belonging to this aspect's aspects-sort instance.
     * @return an <tt>Aspect</tt> array
     * @see AspectsSort#aspects
     */
    public Aspect[] aspects() { return this.instance.aspects(); }
    
    /**
     * Returns the <b>argument</b> sort of this aspect's aspects-sort instance that corresponds to this aspect.
     * @return a {@link Sort} object
     * @see AspectsSort#argument
     */
    Sort argument() { return this.instance.argument(this); }
    // methods
    
    /**
     * Throws an illegal-argument-exception. An aspect cannot be duplicated.
     * @throws IllegalArgumentException always
     */
    Sort duplicate(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Aspect '" + this.canonical + "'");
    }
    
    /**
     * Creates a <b>new form</b> for this aspect as a form for its aspect-sort instance.
     * That is, the form is created according to the aspect-sort, but belongs to this aspect.
     * @return a {@link blue.form.Form} corresponding this sort
     * @see AspectsSort#newForm(Sort)
     */
    public Form newForm() {
        return this.instance.newForm(this);
    }
    
    /**
     * Creates a <b>new form</b> for this aspect's aspect-sort instance as a form for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * @param parent a <tt>Sort</tt> object
     * @return a {@link blue.form.Form} corresponding this sort
     * @see AspectsSort#newForm(Sort)
     */
    Form newForm(Sort parent) {
        return this.instance.newForm(parent);
    }
    
    /**
     * <b>Visualize</b> this aspect using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public void visualize(GraphicsContext gc) {
        gc.beginGroup(AttributeSort.class, 1);
        gc.label(this.toString());
        gc.endGroup();
    }
    
    /**
     * <b>Combines</b> this aspect with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if the other sort is an instance of {@link AspectsSort}
     */
    Sort combine(Sort other) throws IllegalArgumentException {
        if (other instanceof AspectsSort)
            throw new IllegalArgumentException("Aspects sort '" + other.canonical + "'");
        return new AttributeSort(this, other);
    }
    
    /**
     * <b>Relates</b> this aspect to another aspect and returns the resulting match.
     * If both aspects are not identical, their aspect-sort instances are related
     * instead. If these are equivalent, then, if the aspect's arguments are identical,
     * both aspects are also denoted equivalent. Otherwise, both argument sorts
     * are related and their match assigned to the aspects. The result is at most
     * strongly similar. In all other cases, no match exists.
     * @param other an <tt>Aspect</tt> object
     * @return a {@link Match} object
     * @see AspectsSort#relate(AspectsSort)
     */
    Match relate(Aspect other) {
        if (this == other)
            return Match.identical(this);
        Match relate = this.instance.relate(other.instance);
        if (relate.isEquivalent()) {
            if (this.argument() == other.argument())
                return Match.equivalent(this, other);
            Sort[] args = (Sort[]) this.arguments().value();
            relate = args[0].relate(args[1]);
            if (!relate.isIncongruous())
                relate = relate.assign(this, other).toStronglySimilar();
            return relate;
        }
        return Match.none;
    }
    
    /**
     * <b>Relates</b> this aspect to a primitive sort and returns
     * the resulting match. If no match has already been stored, the primitive sort
     * is instead related to the aspect. If a match is found for this, it is reversed then stored.
     * @param other a {@link PrimitiveSort} object
     * @return a {@link Match} object
     * @see PrimitiveSort#relate(Aspect)
     */
    Match relate(PrimitiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
    
    /**
     * <b>Relates</b> this aspect to an attribute sort and returns
     * the resulting match. If no match has already been stored, the attribute sort
     * is instead related to the aspect. If a match is found for this, it is reversed then stored.
     * @param other an {@link AttributeSort} object
     * @return a {@link Match} object
     * @see AttributeSort#relate(Aspect)
     */
    Match relate(AttributeSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
    
    /**
     * <b>Relates</b> this aspect to a disjunctive sort and returns
     * the resulting match. If no match has already been stored, the disjunctive sort
     * is instead related to the aspect. If a match is found for this, it is reversed then stored.
     * @param other an {@link DisjunctiveSort} object
     * @return a {@link Match} object
     * @see DisjunctiveSort#relate(Aspect)
     */
    Match relate(DisjunctiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
    
    /**
     * <b>Relates</b> this aspect to a recursive sort and returns
     * the resulting match. If no match has already been stored, the recursive sort
     * is instead related to the aspect. If a match is found for this, it is reversed then stored.
     * @param other an {@link RecursiveSort} object
     * @return a {@link Match} object
     * @see RecursiveSort#relate(Aspect)
     */
    Match relate(RecursiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
    
    /** Getter for property instance.
     * @return Value of property instance.
     *
     */
    public blue.sort.AspectsSort getInstance() {
        return instance;
    }
    
    /** Setter for property instance.
     * @param instance New value of property instance.
     *
     */
    public void setInstance(blue.sort.AspectsSort instance) {
        this.instance = instance;
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
