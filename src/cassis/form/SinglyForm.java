/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SinglyForm.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

import cassis.*;
import cassis.ind.Individual;
import cassis.sort.*;
import cassis.convert.Match;
import cassis.convert.Converter;
import cassis.parse.*;
import cassis.visit.ElementVisitor;

public abstract class SinglyForm extends Form {

    // representation
    private Individual individual;
    private boolean beyond;

    // constructors

    SinglyForm() { this(null); }

    private SinglyForm(Sort sort) {
	super(sort);
	this.individual = null;
	this.beyond = true;
    }

    // access methods

    public Individual individual() { return this.individual; }

    // methods

    void setIndividual(Individual ind) {
	Sort sort = ind.ofSort();
	    
	if (!this.ofSort().equals(sort)) {
	    Match match = sort.match(this.ofSort());
            System.out.println(match);
	    if (!match.isConcordant() || !match.isEquivalent())
		throw new IllegalArgumentException("Arguments are not of similar sorts");
	    ind = (Individual) ind.convert(this.ofSort());
	} else if (ind.used() && (sort instanceof AttributeSort))
	    ind = (Individual) ind.convert(sort);

	if (this.individual != null)
	    this.individual.delUse();
	this.individual = ind;
	this.individual.addUse();
    }

    // Check whether this singly form EQUALS the other singly form
    public boolean equals(Object other) {
	if (!(other instanceof SinglyForm) ||
	    !this.ofSort().equals(((Form) other).ofSort()))
	    return false;

	if ((this.individual != null) &&
	    (((SinglyForm) other).individual != null))
	    return this.individual.equals(((SinglyForm) other).individual);
	return (this.individual == ((SinglyForm) other).individual);
    }

    // COMPARE two singly forms
    // and return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
	return this.ofSort().compare(((Form) other).ofSort());
    }

    // DUPLICATE this singly form
    public Element duplicate() {
	SinglyForm dup = (SinglyForm) this.ofSort().newForm();
	dup.duplicate(this);
	return dup;
    }

    // DUPLICATE the specified singly form into this singly form
    public void duplicate(Form other) {
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");
	this.setIndividual((Individual) ((SinglyForm) other).individual.convert(this.ofSort()));
    }

    public String toString() {
	if (this.nil()) return "{ }";
	return "{" + this.individual.toString(this.associate()) + " }";
    }

    /**
     * <b>Accepts</b> a visitor to this singly-form wrt an associate
     * individual. Calls the <tt>visitEnter</tt> method of the visitor.
     * If the result is <tt>true</tt>, sends the visitor to the individual
     * in this singly-form, if there is one. Then, calls the <tt>visitLeave</tt>
     * method of the visitor.
     * @param visitor an {@link ElementVisitor} object
     * @param assoc an {@link Individual} object that is the associate
     * to this form (ignored)
     * @see ElementVisitor#visitEnter(cassis.form.Form)
     * @see ElementVisitor#visitLeave(cassis.form.Form)
     */
    public void accept(ElementVisitor visitor, Individual assoc) {
        if (visitor.visitEnter(this) && !this.nil())
            this.individual().accept(visitor, this.associate());
        visitor.visitLeave(this);
    }

    // PARSE a string and initialize this form to its value
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() == '$') {
            if (reader.newToken() != Parsing.IDENTIFIER)
                throw new ParseException(reader, "identifier expected");
            String var = reader.tokenString();
            Form form = (Form) this.ofSort().context().profile().retrieve(var);
            this.setIndividual((Individual) ((Form) Converter.convert(this.ofSort(), form)).current());
        } else if (reader.token() == '{') {
            if (reader.previewToken() != '}')
                this.setIndividual(Individual.parse(this.ofSort(), reader));
            reader.newToken();
        } else throw new ParseException(reader, "'{' or '$' expected");
    }

    // Form interface methods

    public Element current() { return this.individual; }
    public Element first() { return this.individual; }
    public void toBegin() { this.beyond = false; }
    public void toNext() { this.beyond = true; }
    public boolean beyond() { return this.beyond; }

    public boolean isMaximal() { return true; }
    public void maximalize() { }

    // The NIL singly form contains an individual with empty attribute
    public boolean nil() {
	if (this.individual == null) return true;
	if (this.individual.attrDefined())
	    return this.individual.attribute().nil();
	return this.individual.nil();
    }

    public int size() {
	if (this.nil()) return 0;
	return 1;
    }

    // DELETE a singly form
    public void delete() {
	if (!this.beyond) this.purge();
    }

    // PURGE a singly form
    public void purge() {
	if (this.individual == null) return;
	this.individual.delUse();
	this.individual.purge();
	this.individual = null;
    }

    // PREPAREs to add an individual to a multiply form
    public void prepare(Individual ind) throws IllegalArgumentException {
	Sort sort = ind.ofSort();
	    
	if (!this.ofSort().equals(sort))
	    throw new IllegalArgumentException("Arguments are not of the same sort");
	else if (ind.used() && (sort instanceof AttributeSort))
	    ind = (Individual) ind.convert(sort);
    }

    // PREPAREs two multiply forms for an algebraic operation
    public void prepare(Form other) throws IllegalArgumentException {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");
    }

    // PREPAREs to partition two multiply forms with respect to one another
    public void prepare(Form other, Form common) throws IllegalArgumentException {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()) ||
	    !this.ofSort().equals(common.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");
    }

    public Form convert(Sort sort, Individual assoc) throws IllegalArgumentException {
	SinglyForm result = (SinglyForm) sort.newForm();
	result.setAssociate(assoc);
	result.setIndividual((Individual) this.individual.convert(sort));
	return result;
    }
}
