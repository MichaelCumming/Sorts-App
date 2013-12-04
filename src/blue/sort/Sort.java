/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sort.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.Thing;
import blue.IllegalOverwriteException;
import blue.io.GraphicsContext;
import blue.form.Form;

/**
 * A <b>Sort</b> is a data representation type that supports algebraic operations
 * on its <i>forms</i>. Conceptually, a sort is a set of similar models, i.e.,
 * {@link blue.ind.Individual}s, that can be unifiably described.
 * The working definition includes a specification of the algebraic behavior
 * of its {@link blue.form.Form}s, i.e., collections of <i>individuals</i>.
 * A sort may also be assigned a name; naming sorts serves for referencing
 * as well as to distinguish sorts that may otherwise be identical. <p> A <i>sort</i> can be either simple or composite.
 * A {@link PrimitiveSort} defines a primitive type, and is defined by
 * a characteristic individual and a template for the algebraic behavior of forms.
 * An {@link Aspect} defines a single, unidirectional view of a type
 * linking two sorts (and defined as an {@link AspectsSort}). Both primitive sorts and aspects are simple; these always have a
 * name assigned. All other sorts are composite; their naming is optional.
 * An {@link AttributeSort} specifies a composition of two sorts under the <i>attribute</i> operation.
 * A {@link DisjunctiveSort} specifies a disjunctive composition of sorts under the <i>sum</i> operation.
 * A {@link ConjunctiveSort} specifies a conjunctive composition of sorts under the <i>grid</i> operation.
 * A {@link RecursiveSort} specifies any sort that is recursively defined. A recursive sort necessarily has a name assigned.
 * <p> The <b>Sort</b> class is an abstract class specifying the common ground
 * for all sortal classes. This common representation includes the definition
 * of the sort, a canonical version of this definition, an optional name,
 * a timestamp of creation, and the following statistical information:
 * the number of named components, the number of simple components, and the number of all components.
 * Each sort is created within a specific context (see {@link Sorts});
 * the timestamp of creation corresponds to the sort's registration in this context.
 * The definition of a sort is dependent on the kind of sort; commonly, the assignment operation is denoted by ':'.
 */
public abstract class Sort implements Thing {
    // representation
    private Sorts context;
    private String name;
    private long stamp;
    private int named_comps, simple_comps, all_comps;
    // private boolean recursive;
    
    /** The <b>definition</b> of this sort, excluding the optional naming. */
    //added 'protected' 29.Aug.2003 MC
    protected String definition;
    
    /**
     * A <b>canonical</b> version of this sort's definition.
     * @see #definition
     */
    //added 'protected' 29.Aug.2003 MC
    protected String canonical;
    
    //added by MC 23.06.2003 to distinguish base part of Attribute sorts.
    /**See SortDrawerBuilder.drawAttributeSort()  line 278 */
    boolean attributeWeight = false;
    //
    // constructors
    /**
     * Creates a general <b>sort</b> within the specified context.
     * @param context a {@link Sorts} object
     */
    Sort(Sorts context) {
        super();
        this.context = context;
        this.name = this.definition = this.canonical = null;
        this.named_comps = this.simple_comps = this.all_comps = 0;
        this.stamp = 0;
        //isSecondAttribute = false;
        // this.recursive = false;
    }
    
    /**
     * Creates a general <b>sort</b> within the specified context, and assigns it the specified name.
     * @param context a {@link Sorts} object
     * @param name a <tt>String</tt> object
     */
    Sort(Sorts context, String name) {
        this(context);
        this.name = name;
        //isSecondAttribute = false;
    }
    // methods
    
    /**
     * <b>Assigns</b> a name to this sort. Also registers this sort in its context, and stores the resulting timestamp.
     * @param name a <tt>String</tt> object
     * @throws IllegalOverwriteException if this name has already been assigned to a sort
     * @see Sorts#register
     */
    void assign(String name) throws IllegalOverwriteException {
        if (this.context.sortOf(name) != null)
            throw new IllegalOverwriteException("'" + name + "' has already been assigned to a sort");
        this.name = name;
        this.named_comps++;
        this.stamp = this.context.register(name, this);
    }
    
    /**
     * <b>Assigns</b> an array of names to this (aspects)sort.
     * @param names a <tt>String[]</tt> object
     * @throws IllegalOverwriteException if any of these names has already been assigned to a sort
     * @see AspectsSort
     */
    void assign(String[] names) throws IllegalOverwriteException {
        for (int n = 0; n < names.length; n++)
            if (this.context.sortOf(names[n]) != null)
                throw new IllegalOverwriteException("'" + names[n] + "' has already been assigned a sort");
        StringBuffer buffer = new StringBuffer("(");
        if (names.length > 0) buffer.append(names[0]);
        for (int n = 1; n < names.length; n++)
            buffer.append(", ").append(names[n]);
        buffer.append(")");
        this.name = buffer.toString();
        this.context.addTimed(this);
    }
    // void recurse() {
    // this.recursive = true;
    // }
    // statistical methods
    
    /**
     * Returns the number of <b>named</b> components of this sort.
     * @return an integer value
     */
    int names() { return this.named_comps; }
    
    /**
     * Returns the number of <b>simple</b> components of this sort.
     * @return an integer value
     */
    int simples() { return this.simple_comps; }
    
    /**
     * Returns the total number of <b>components</b> of this sort.
     * @return an integer value
     */
    int components() { return this.all_comps; }
    
    /**
     * <b>Sets</b> the number of named, simple, and all components for this sort.
     * @param names the number of named components
     * @param simples the number of simple components
     * @param components the number of all components
     */
    void setStats(int names, int simples, int components) {
        this.named_comps = names;
        this.simple_comps = simples;
        this.all_comps = components;
    }
    
    /**
     * Augments the number of named, simple, and all components for this sort with the respective numbers for the other sort.
     * @param other a <tt>Sort</tt> object
     */
    void includeStats(Sort other) {
        this.named_comps += other.named_comps;
        this.simple_comps += other.simple_comps;
        this.all_comps += other.all_comps;
    }
    // sort interface methods
    
    /**
     * Returns the <b>context</b> of this sort.
     * @return a {@link Sorts} object
     */
    public Sorts context() { return this.context; }
    
    /**
     * Checks whether this sort has been assigned a name.
     * @return <tt>true</tt> if this sort has a name, <tt>false</tt> otherwise
     */
    public boolean isNamed() { return (this.name != null); }
    
    /**
     * Returns the <b>name</b> of this sort.
     * @return a <tt>String</tt> object
     */
    String name() { return this.name; }
    
    //added by MC:23.06.2003
    //    public void setSecondAttribute(boolean isSecond) {
    //		isSecondAttribute = isSecond;
    //    }
    //
    //    public boolean isSecondAttribute() {
    //		return isSecondAttribute;
    //    }
    
    /**
     * Returns a canonical description for this sort. This is the sort's name
     * if it has been assigned one, otherwise it is this sort's canonical definition.
     * @return a descriptive <tt>String</tt> for this sort
     * @see #canonical
     */
    public String toString() {
        if (this.name != null) return this.name;
        return this.canonical;
    }
    
    /**
     * Returns the <b>definition</b> of this sort.
     * @return a <tt>String</tt> object
     */
    public String definition() { return this.definition; }
    
    /**
     * Returns the <b>base</b> sort of this sort. A sort is identical to its base
     * unless it is an {@link blue.sort.AttributeSort} or a {@link blue.sort.RecursiveSort}; in the former case the base
     * denotes the left operand of the attribute operator; in the latter case the base
     * denotes the sort's instance or its base.
     * @return a <tt>Sort</tt> object
     * @see blue.sort.AttributeSort#base
     * @see blue.sort.RecursiveSort#instance
     */
    public Sort base() { return this; }
    
    /**
     * Creates a <b>new form</b> for this sort.
     * @return a {@link blue.form.Form} corresponding this sort
     */
    public abstract Form newForm();
    
    /**
     * Creates a <b>new form</b> for this sort as a form for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * @param parent a <tt>Sort</tt> object
     * @return a {@link blue.form.Form} corresponding this sort
     * @see blue.form.Form#setSort
     */
    abstract Form newForm(Sort parent);
    
    /**
     * <b>Visualize</b> this sort using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public abstract void visualize(GraphicsContext gc);
    
    /**
     * Checks if this sort <b>equals</b> another sort.
     * @param other an <tt>Object</tt> to compare this sort with
     * @return <tt>true</tt> if the argument is a sort, both sorts share
     * the same context, and these have identical descriptions; <tt>false</tt> otherwise
     * @see #toString
     */
    public boolean equals(Object other) {
        if (!(other instanceof Sort) || (this.context != ((Sort)other).context))
            return false;
        return this.toString().equals(((Sort)other).toString());
    }
    
    /**
     * <b>Compares</b> this sort with another sort for the purpose of sorting.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return {@link #FAILED} if the argument is not a sort or both sorts
     * do not share the same context; otherwise one of {@link #EQUAL},
     * {@link #LESS}, or {@link #GREATER}, dependent on the string comparison of their descriptions
     * @see #toString
     */
    public int compare(Thing other) {
        if (!(other instanceof Sort) || (this.context != ((Sort)other).context))
            return FAILED;
        int answer = this.toString().compareTo(((Sort)other).toString());
        if (answer < 0) return LESS;
        if (answer > 0) return GREATER;
        return EQUAL;
    }
    
    /**
     * Checks whether this sort's description is strictly <b>less than</b> the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }
    
    /**
     * Checks whether this sort's description is strictly <b>greater than</b> the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }
    
    /**
     * Checks whether this sort's description is <b>less than or equal</b> the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }
    
    /**
     * Checks whether this sort's description is <b>greater than or equal</b> the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }
    
    /**
     * Creates a <b>duplicate</b> of this sort and assigns it the specified name.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    abstract Sort duplicate(String name);
    
    /**
     * Checks whether this sort <b>contains</b> another sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     */
    public boolean contains(Sort other) {
        return false;
    }
    
    /**
     * Checks whether this sort is a <b>part of</b> another sort, that is, the other sort contains this sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort is a part of the other, <tt>false</tt> otherwise
     * @see blue.sort.Sort#contains
     */
    public boolean partOf(Sort other) {
        return other.contains(this);
    }
    
    /**
     * Combines this sort with another sort under the operation of <b>sum</b>.
     * Both sorts may be used to construct the resulting sort, depending on whether these are {@link DisjunctiveSort}s and
     * don't have a name. Otherwise, a new disjunctive sort is created and both operand sorts are inserted into this.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the operation of sum
     * @throws IllegalArgumentException if both sorts do not share the same context.
     * @see DisjunctiveSort#merge
     * @see DisjunctiveSort#insert
     */
    Sort sum(Sort other) throws IllegalArgumentException {
        DisjunctiveSort result;
        if (this.context != other.context)
            throw new IllegalArgumentException("Sort.sum: illegal arguments");
        if ((this instanceof DisjunctiveSort) && !this.isNamed()) {
            result = (DisjunctiveSort)this;
            if ((other instanceof DisjunctiveSort) && !other.isNamed())
                result.merge((DisjunctiveSort)other);
            else
                result.insert(other);
            return result;
        }
        if ((other instanceof DisjunctiveSort) && !other.isNamed())
            result = (DisjunctiveSort)other;
        else {
            result = new DisjunctiveSort(this.context);
            result.insert(other);
        }
        result.insert(this);
        return result;
    }
    
    /**
     * <b>Combines</b> this sort with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     */
    abstract Sort combine(Sort other);
    
    /**
     * <b>Relates</b> this sort to another sort and returns the resulting match.
     * @param other a <tt>Sort</tt> object
     * @return a {@link Match} object
     * @throws IllegalArgumentException if the sorts cannot be related, e.g., these do not share the same context
     */
    public Match relate(Sort other) throws IllegalArgumentException {
        if (this.context() != other.context())
            throw new IllegalArgumentException("Sort.relate: illegal arguments");
        if (other instanceof Aspect)
            return this.relate((Aspect)other);
        if (other instanceof AspectsSort)
            throw new IllegalArgumentException("Aspects sort '" + other.canonical + "'");
        if (other instanceof PrimitiveSort)
            return this.relate((PrimitiveSort)other);
        if (other instanceof AttributeSort)
            return this.relate((AttributeSort)other);
        if (other instanceof DisjunctiveSort)
            return this.relate((DisjunctiveSort)other);
        if (other instanceof RecursiveSort)
            return this.relate((RecursiveSort)other);
        return Match.none;
    }
    
    /**
     * <b>Relates</b> this sort to an aspect and returns the resulting match.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    abstract Match relate(Aspect other);
    
    /**
     * <b>Relates</b> this sort to a primitive sort and returns the resulting match.
     * @param other a {@link PrimitiveSort} object
     * @return a {@link Match} object
     */
    abstract Match relate(PrimitiveSort other);
    
    /**
     * <b>Relates</b> this sort to an attribute sort and returns the resulting match.
     * @param other an {@link AttributeSort} object
     * @return a {@link Match} object
     */
    abstract Match relate(AttributeSort other);
    
    /**
     * <b>Relates</b> this sort to an disjunctive sort and returns the resulting match.
     * @param other a {@link DisjunctiveSort} object
     * @return a {@link Match} object
     */
    abstract Match relate(DisjunctiveSort other);
    
    /**
     * <b>Relates</b> this sort to an recursive sort and returns the resulting match.
     * @param other a {@link RecursiveSort} object
     * @return a {@link Match} object
     */
    abstract Match relate(RecursiveSort other); 
    
    /** Getter for property attributeBase.
     * @return Value of property attributeBase.
     *
     */
//    public boolean isAttributeBase() {
//        return attributeBase;
//    }
    
    /** Setter for property attributeBase.
     * @param attributeBase New value of property attributeBase.
     *
     */
//    public void setAttributeBase(boolean attributeBase) {
//        this.attributeBase = attributeBase;
//    }
    
    /** Getter for property attributeWeight.
     * @return Value of property attributeWeight.
     *
     */
    public boolean isAttributeWeight() {
        return attributeWeight;
    }
    
    /** Setter for property attributeWeight.
     * @param attributeWeight New value of property attributeWeight.
     *
     */
    public void setAttributeWeight(boolean attributeWeight) {
        this.attributeWeight = attributeWeight;
    }
    
}
