/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sort.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import cassis.Thing;
import cassis.IllegalOverwriteException;
import cassis.form.Form;
import cassis.convert.Match;
import cassis.convert.Matches;
import cassis.visit.SortVisitor;

/** 
 * A <b>Sort</b> is a data representation type that supports algebraic
 * operations on its <i>forms</i>. Conceptually, a sort is a set of similar
 * models, i.e., {@link cassis.ind.Individual}s, that can be unifiably described.
 * The working definition includes a specification of the algebraic behavior
 * of its {@link cassis.form.Form}s, i.e., collections of <i>individuals</i>.
 * A sort may also be assigned a name; naming sorts serves for referencing
 * as well as to distinguish sorts that may otherwise be identical.
 * <p>
 * A <i>sort</i> can be either simple or composite.<br>
 * A {@link PrimitiveSort} defines a primitive type, and is defined by
 * a characteristic individual and a template for the algebraic behavior of
 * forms.<br>
 * An {@link Aspect} defines a single, unidirectional view of a type
 * linking two sorts (and defined as an {@link AspectsSort}).<br>
 * Both primitive sorts and aspects are simple; these always have a name
 * assigned. All other sorts are composite; their naming is optional.<br>
 * An {@link AttributeSort} specifies a composition of two sorts
 * under the <i>attribute</i> operation.<br>
 * A {@link DisjunctiveSort} specifies a disjunctive composition of sorts
 * under the <i>sum</i> operation.<br>
 * A ConjunctiveSort specifies a conjunctive composition of sorts
 * under the <i>grid</i> operation.<br>
 * A {@link RecursiveSort} specifies any sort that is recursively defined.
 * A recursive sort necessarily has a name assigned.
 * <p>
 * The <b>Sort</b> class is an abstract class specifying the common ground
 * for all sortal classes. This common representation includes the definition
 * of the sort, a canonical version of this definition, an optional name,
 * a timestamp of creation, and the following statistical information:
 * the number of named components, the number of simple components, and
 * the number of all components.<br>
 * Each sort is created within a specific context (see {@link Sorts});
 * the timestamp of creation corresponds to the sort's registration in this
 * context. The definition of a sort is dependent on the kind of sort;
 * commonly, the assignment operation is denoted by ':'.
 */
public abstract class Sort implements Thing {

    // representation

    private Sorts context;
    private String name;
    private long stamp;
    private int named_comps, simple_comps, all_comps;
    // private boolean recursive;
    /**
     * The <b>definition</b> of this sort, excluding the optional naming.
     */
    String definition; 
    /**
     * A <b>canonical</b> version of this sort's definition.
     * @see #definition
     */
    String canonical;

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
	// this.recursive = false;
    }
    /**
     * Creates a general <b>sort</b> within the specified context,
     * and assigns it the specified name.
     * @param context a {@link Sorts} object
     * @param name a <tt>String</tt> object
     */
    Sort(Sorts context, String name) {
	this(context);
        this.name = name;
    }

    // methods

    /**
     * <b>Assigns</b> a name to this sort.
     * Also registers this sort in its context, and stores the resulting timestamp.
     * @param name a <tt>String</tt> object
     * @throws IllegalOverwriteException if this name has already been assigned
     * to a sort
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
     * @throws IllegalOverwriteException if any of these names has already
     * been assigned to a sort
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
    public int names() { return this.named_comps; }
    /**
     * Returns the number of <b>simple</b> components of this sort.
     * @return an integer value
     */
    public int simples() { return this.simple_comps; }
    /**
     * Returns the total number of <b>components</b> of this sort.
     * @return an integer value
     */
    public int components() { return this.all_comps; }
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
     * Augments the number of named, simple, and all components for this sort
     * with the respective numbers for the other sort.
     * @param other a <tt>Sort</tt> object
     */
    void includeStats(Sort other) {
	this.named_comps += other.named_comps;
	this.simple_comps += other.simple_comps;
	this.all_comps += other.all_comps;
    }

    public String canonical() { return this.canonical; }

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
     * <b>Accepts</b> a sort-visitor to this sort.
     * First, the <tt>visitEnter</tt> method of the sort-visitor is called.
     * Then, the <tt>visitLeave</tt> method of the sort-visitor is called.
     * @param visitor an {@link cassis.visit.SortVisitor} object
     * @see cassis.visit.SortVisitor#visitEnter
     * @see cassis.visit.SortVisitor#visitLeave
     */
    public void accept(cassis.visit.SortVisitor visitor) {
        visitor.visitEnter(this);
        visitor.visitLeave(this);
    }

    /**
     * Returns the <b>definition</b> of this sort.
     * @return a <tt>String</tt> object
     */
    public String definition() { return this.definition; }

    /**
     * Returns the <b>base</b> sort of this sort. A sort is identical to its base
     * unless it is an {@link cassis.sort.AttributeSort} or
     * a {@link cassis.sort.RecursiveSort}; in the former case the base denotes
     * the left operand of the attribute operator; in the latter case the base
     * denotes the sort's instance or its base.
     * @return a <tt>Sort</tt> object
     * @see cassis.sort.AttributeSort#base
     * @see cassis.sort.RecursiveSort#instance
     */
    public Sort base() { return this; }

    /**
     * Creates a <b>new form</b> for this sort.
     * @return a {@link cassis.form.Form} corresponding this sort
     */
    public abstract Form newForm();
    /**
     * Creates a <b>new form</b> for this sort as a form for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * @param parent a <tt>Sort</tt> object
     * @return a {@link cassis.form.Form} corresponding this sort
     * @see cassis.form.Form#setSort
     */
    abstract Form newForm(Sort parent);

    /**
     * Checks if this sort <b>equals</b> another sort.
     * @param other an <tt>Object</tt> to compare this sort with
     * @return <tt>true</tt> if the argument is a sort, both sorts share
     * the same context, and these have identical descriptions;
     * <tt>false</tt> otherwise
     * @see #toString
     */
    public boolean equals(Object other) {
	if (!(other instanceof Sort) || (this.context != ((Sort) other).context))
	    return false;
	return this.toString().equals(((Sort) other).toString());
    }

    /**
     * <b>Compares</b> this sort with another sort for the purpose of sorting.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return {@link #FAILED} if the argument is not a sort or both sorts
     * do not share the same context; otherwise one of {@link #EQUAL},
     * {@link #LESS}, or {@link #GREATER}, dependent on the string comparison
     * of their descriptions
     * @see #toString
     */
    public int compare(Thing other) {
	if (!(other instanceof Sort) || (this.context != ((Sort) other).context))
	    return FAILED;

	int answer = this.toString().compareTo(((Sort) other).toString());
	if (answer < 0) return LESS;
	if (answer > 0) return GREATER;
	return EQUAL;
    }

    /**
     * Checks whether this sort's description is strictly <b>less than</b>
     * the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessThan(Thing other) {
	return (this.compare(other) == LESS);
    }
    /**
     * Checks whether this sort's description is strictly <b>greater than</b>
     * the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterThan(Thing other) {
	return (this.compare(other) == GREATER);
    }
    /**
     * Checks whether this sort's description is <b>less than or equal</b>
     * the description of another sort.
     * @param other a <tt>Thing</tt> to compare this sort with
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == LESS) || (c == EQUAL));
    }
    /**
     * Checks whether this sort's description is <b>greater than or equal</b>
     * the description of another sort.
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
     * @return <tt>true</tt> if this sort contains the other,
     * <tt>false</tt> otherwise
     */
    public boolean contains(Sort other) {
	return false;
    }

    /**
     * Checks whether this sort is a <b>part of</b> another sort,
     * that is, the other sort contains this sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort is a part of the other,
     * <tt>false</tt> otherwise
     * @see cassis.sort.Sort#contains
     */
    public boolean partOf(Sort other) {
	return other.contains(this);
    }

    /**
     * Combines this sort with another sort under the operation of <b>sum</b>.
     * Both sorts may be used to construct the resulting sort, depending on whether 
     * these are {@link DisjunctiveSort}s and don't have a name.
     * Otherwise, a new disjunctive sort is created and both operand sorts are
     * inserted into this.
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
	    result = (DisjunctiveSort) this;
	    if ((other instanceof DisjunctiveSort) && !other.isNamed())
		result.merge((DisjunctiveSort) other);
	    else result.insert(other);
	    return result;
	}
	if ((other instanceof DisjunctiveSort) && !other.isNamed())
	    result = (DisjunctiveSort) other;
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
    public abstract Sort combine(Sort other);

    /**
     * Compares this sort to another sort and returns the resulting
     * <b>match</b>.
     * @param other a <tt>Sort</tt> object
     * @return a {@link cassis.convert.Match} object
     * @throws IllegalArgumentException if the sorts cannot be related, e.g.,
     * these do not share the same context
     */
    public Match match(Sort other) throws IllegalArgumentException {
        return Matches.compare(this, other);
    }
}
