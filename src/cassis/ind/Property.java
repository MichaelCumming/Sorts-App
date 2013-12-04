/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Property.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.*;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.sort.SimpleSort;
import cassis.sort.Aspect;
import cassis.form.RelationalForm;

/**
 * A <p>property</b> is a relationship between two individuals where each direction
 * of the relationship is individually distinguished. A property is an individual of
 * an {@link cassis.sort.AspectsSort} and each direction of the relationship defines
 * an {@link cassis.sort.Aspect} of this sort.
 * <p>
 * The <b>Property</b> class specifies the characteristic individual for properties.
 * A property is represented as a pair of associated individuals.
 * An additional {@link Key} serves to hold the key reference corresponding
 * to an associate that only exists yet as a reference. 
 * This characteristic individual requires a pair of sorts as parameters.
 * These sorts define the associates that can be associated to properties
 * corresponding to this characteristic individual or sort.
 * Forms of numerics adhere to a relational behavior.
 * @see cassis.form.RelationalForm
 * @see cassis.struct.Parameter#SORTSLINK
 */
public class Property extends Relation {
    static {
	PrimitiveSort.register(Property.class, RelationalForm.class, Parameter.SORTSLINK);
	new cassis.visit.vrml.Proto(Property.class, "icons/property.gif");
    }

    // representation
    private Individual couple[];
    private Key unresolved;

    // constructors

    /**
     * Constructs a nondescript <b>Property</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * numeric. This property must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Property() {
	super();
	this.couple = null;
	this.unresolved = null;
    }
    private Property(Sort sort, Individual couple[], Key reference) {
	super(sort);
	this.couple = couple;
	this.unresolved = reference;
	if (reference != null) this.unresolved(reference);
    }
    /**
     * Constructs a <b>Property</b> from two individuals,
     * for the specified sort. This sort must allow for the property associating
     * these individuals to be an individual of this sort.
     * @param sort a {@link cassis.sort.Sort} object
     * @param one a first individual
     * @param two a second individual
     * @throws IllegalArgumentException if either argument individual is null
     * or these are not of the associated sorts
     */
    public Property(Sort sort, Individual one, Individual two) throws IllegalArgumentException {
	super(sort);
	if ((one == null) || (two == null))
	    throw new IllegalArgumentException("Arguments must be both non-null");
	Aspect aspects[] = ((Aspect) sort).aspects();
        Argument arg = null;
        Sort base = sort.base();
	if (base instanceof SimpleSort) arg = ((SimpleSort) base).arguments();
	this.couple = new Individual[2];

	if (sort.equals(aspects[0])) {
	    if (!one.ofSort().base().equals(((Sort[]) arg.value())[0]) ||
		!two.ofSort().base().equals(((Sort[]) arg.value())[1]))
		throw new IllegalArgumentException("Arguments are not of the appropriate sorts");
	    this.couple[0] = one;
	    this.couple[1] = two;
	} else {
	    if (!one.ofSort().base().equals(((Sort[]) arg.value())[1]) ||
		!two.ofSort().base().equals(((Sort[]) arg.value())[0]))
		throw new IllegalArgumentException("Arguments are not of the appropriate sorts");
	    this.couple[1] = one;
	    this.couple[0] = two;
	}
	one.referenced();
	two.referenced();
	this.unresolved = null;
    }

    // access methods

    /**
     * Returns this property's <b>unresolved</b> key reference, if any.
     * @return a {@link Key} object
     */
    public Key unresolved() { return this.unresolved; }

    /**
     * Tests whether this property equals <b>nil</b>, i.e., the pair of associates
     * is not specified.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.couple == null); }

    /**
     * <b>Gets the {@link cassis.sort.Aspect}</b> of this property's
     * {@link cassis.sort.AspectsSort} that does not equal the specified sort.
     * If the specified sort is not an aspect, any aspect of this property's sort
     * is returned.
     * @param sort a {@link cassis.sort.Sort} object
     * @return the resulting (aspect) sort
     */
    public Sort getAspect(Sort sort) {
        Aspect base = (Aspect) this.ofSort().base();
	Aspect aspects[] = base.aspects();
	if (sort.base().equals(((Sort[]) base.arguments().value())[0]))
	    return aspects[0];
	return aspects[1];
    }

    /**
     * <b>Gets the associate</b> of this property that does not equal the specified
     * individual.
     * @param ind an {@link Individual} object
     * @return the associated individual
     * @throws UnresolvedReferenceException if one of the associates is unresolved
     * @throws IllegalArgumentException if the specified individual is not an associate
     */
    public Individual getAssociate(Individual ind) throws UnresolvedReferenceException, IllegalArgumentException {
	if (this.unresolved != null)
	    throw new UnresolvedReferenceException("unresolved reference " + this.unresolved.getKey());
	if (ind.ofSort().equals(this.couple[0].ofSort()) &&
	    ind.equalValued(this.couple[0]))
	    return this.couple[1];
	if (ind.ofSort().equals(this.couple[1].ofSort()) &&
	    ind.equalValued(this.couple[1]))
	    return this.couple[0];
	throw new IllegalArgumentException("Argument individual is not an associate");
    }
    /**
     * <b>Sets the associate</b> of this property to be the specified individual.
     * The associate that has equal value to the specified individual is overwritten.
     * @param ind an {@link Individual} object
     * @throws IllegalOverwriteException if this property belongs to a
     * {@link cassis.form.Form}
     * @throws AmbiguityException both associates have equal value to the specified
     * individual
     * @throws IllegalArgumentException if the specified individual is not an associate
     */
    public void setAssociate(Individual ind) throws IllegalOverwriteException, AmbiguityException {
	if (this.used())
	    throw new IllegalOverwriteException("associate cannot be overwritten");
	if ((this.couple[0] != null) &&
	    ind.ofSort().equals(this.couple[0].ofSort()) &&
	    ind.equalValued(this.couple[0])) {
	    if ((this.couple[1] != null) &&
		ind.ofSort().equals(this.couple[1].ofSort()) &&
		ind.equalValued(this.couple[1]))
		throw new AmbiguityException("ambiguous associates");
	    this.couple[0] = ind;
	} else if ((this.couple[1] != null) &&
		   ind.ofSort().equals(this.couple[1].ofSort()) &&
		   ind.equalValued(this.couple[1]))
	    this.couple[1] = ind;
	else throw new IllegalArgumentException("Argument individual is not an associate");
    }

    // Relation interface methods

    /**
     * <b>Duplicates</b> this property. It returns a new individual with
     * the same specifications, defined for the base sort of this property's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Property(this.ofSort().base(), this.couple, this.unresolved);
    }

    /**
     * Checks whether this property has <b>equal value</b> to another individual.
     * This condition applies if both properties share associates with
     * equal values or unresolved keys with equal values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a property
     */
    boolean equalValued(Individual other) {
	// System.out.println("Property.equalValued()");
	if ((this.couple[0] != null) &&
	    !this.couple[0].equalValued(((Property) other).couple[0])) {
	    // System.out.println(this.couple[0].toString());
	    // System.out.println(((Property) other).couple[0].toString());
	    return false;
	}
	if ((this.couple[1] != null) &&
	    !this.couple[1].equalValued(((Property) other).couple[1])) {
	    // System.out.println(this.couple[1].toString());
	    // System.out.println(((Property) other).couple[1].toString());
	    return false;
	}
	if ((this.unresolved != null) &&
	    !this.unresolved.equalValued(((Property) other).unresolved)) {
	    // System.out.println(this.unresolved.toString());
	    // System.out.println(((Property) other).unresolved.toString());
	    return false;
	}
	return true;
    }

    /**
     * Compares this property to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a property.
     * Otherwise the result is defined by comparing the associates and
     * unresolved keys of both properties.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Property)) return FAILED;
	if ((this.couple[0] == ((Property) other).couple[0]) &&
	    (this.couple[1] == ((Property) other).couple[1]) &&
	    (this.unresolved == ((Property) other).unresolved))
	    return EQUAL;
	int c;
	if (this.couple[0] != null) {
	    if (((Property) other).couple[0] != null)
		c = this.couple[0].compare(((Property) other).couple[0]);
	    else return LESS;
	} else if (((Property) other).couple[0] != null)
	    return GREATER;
	else c = this.unresolved.compare(((Property) other).unresolved);
	if (c != EQUAL) return c;
	if (this.couple[1] != null) {
	    if (((Property) other).couple[1] != null)
		c = this.couple[1].compare(((Property) other).couple[1]);
	    else return LESS;
	} else if (((Property) other).couple[1] != null)
	    return GREATER;
	else c = this.unresolved.compare(((Property) other).unresolved);
	if (c != EQUAL) return c;
	return LESS;
    }

    /**
     * <b>Purges</b> this property if it does not currently belong to a form.
     * Purging a property removes any references to its unresolved character and
     * purges the attribute form of this property.
     * @see #used()
     * @see Relation#removeUnresolved
     * @see cassis.form.Form#purge()
     */
    public void purge() {
	if (this.used()) return;
	if (this.unresolved != null)
	    Relation.removeUnresolved(this.unresolved, this);
	if (this.attrDefined())
	    this.attribute().purge();
    }

    /**
     * Converts this property's <b>value to a string</b> wrt an associated individual.
     * The result is the reference key string for the other associate.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {@link Individual} object
     * @return a <tt>String</tt> object
     * @see #parse(cassis.parse.ParseReader, Individual)
     */
    public String toString(Individual assoc) {
        if (assoc == null) {
            if (this.unresolved == null)
                return '(' + this.couple[0].getReference() + ", " + this.couple[1].getReference() + ')';
            if (this.couple[0] == null)
                return '(' + this.unresolved.getKey() + ", " + this.couple[1].getReference() + ')';
            return '(' + this.couple[0].getReference() + ", " + this.unresolved.getKey() + ')';
        }
	try {
	    return this.getAssociate(assoc).getReference();
	} catch (UnresolvedReferenceException e) {
	    return this.unresolved.getKey();
	}
    }

    /**
     * <b>Resolves</b> this property given an associated individual. If this
     * property's unresolved key equals the individual's reference key,
     * the unresolved key is removed and the individual assigned as an associate.
     * @param ind an {@link Individual} object
     * @throws IllegalArgumentException if the property is not unresolved or the
     * unresolved key does not equal the individual's reference key
     */
    public void resolve(Individual ind) throws IllegalArgumentException {
	if ((this.unresolved == null) || (ind.reference() == null) ||
	    !this.unresolved.equals(ind.reference()))
	    throw new IllegalArgumentException("Individual does not unresolve reference");
	if (this.couple[0] == null)
	    this.couple[0] = ind;
	else this.couple[1] = ind;
	this.unresolved = null;
    }

    /**
     * Reads an SDL description of a property from a {@link cassis.parse.ParseReader} object
     * wrt to an associated individual, and assigns the value of this description to
     * this property. This description consists of a reference key corresponding to
     * the other associate.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a property
     * @see #toString
     */
    void parse(ParseReader reader, Individual assoc) throws ParseException {
	if (this.reference() != null)
	    throw new ParseException(reader, "Property cannot be referenced");
	Key ref = Relation.parseReference(this.keystore(), reader);
	Individual ind = (Individual) this.keystore().retrieve(ref);
	if (ind == null) {
	    this.unresolved = ref;
	    this.unresolved(ref);
	}
	Aspect aspects[] = ((Aspect) this.ofSort().base()).aspects();
	this.couple = new Individual[2];
	if (this.ofSort().equals(aspects[0])) {
	    this.couple[0] = assoc;
	    this.couple[1] = ind;
	} else {
	    this.couple[1] = assoc;
	    this.couple[0] = ind;
	}
    }
    /**
     * Reads an SDL description of a property from a {@link cassis.parse.ParseReader}
     * object and assigns the value to this property. This description consists of a
     * parenthesized pair of reference keys corresponding to the associates,
     * separated by a comma.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a property
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (this.reference() != null)
	    throw new ParseException(reader, "Property cannot be referenced");
	if (reader.newToken() != '(')
	    throw new ParseException(reader, "'(' expected");
	Key ref = Relation.parseReference(this.keystore(), reader);
	Individual one = (Individual) this.keystore().retrieve(ref);
	if (one == null) {
	    this.unresolved = ref;
	    this.unresolved(ref);
	}
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	ref = Relation.parseReference(this.keystore(), reader);
	Individual two = (Individual) this.keystore().retrieve(ref);
	if (two == null) {
	    if (one == null)
		throw new ParseException(reader, "Too many unresolved references");
	    this.unresolved = ref;
	    this.unresolved(ref);
	}
	if (reader.newToken() != ')')
	    throw new ParseException(reader, "')' expected");
	Aspect aspects[] = ((Aspect) this.ofSort()).aspects();
        Argument arg = null;
        Sort base = this.ofSort().base();
	if (base instanceof SimpleSort) arg = ((SimpleSort) base).arguments();
	this.couple = new Individual[2];
	if (this.ofSort().base().equals(aspects[0])) {
	    if (((one != null) && !one.ofSort().base().equals(((Sort[]) arg.value())[0])) ||
		((two != null) && !two.ofSort().base().equals(((Sort[]) arg.value())[1])))
		throw new ParseException(reader, "Arguments are not of the appropriate sorts");
	    this.couple[0] = one;
	    this.couple[1] = two;
	} else {
	    if (((one != null) && !one.ofSort().base().equals(((Sort[]) arg.value())[1])) ||
		((two != null) && !two.ofSort().base().equals(((Sort[]) arg.value())[0])))
		throw new ParseException(reader, "Arguments are not of the appropriate sorts");
	    this.couple[1] = one;
	    this.couple[0] = two;
	}
    }    
}
