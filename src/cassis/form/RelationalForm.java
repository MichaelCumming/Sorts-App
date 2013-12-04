/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `RelationalForm.java'                                     *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  RELATIONAL is a behavioral category for sorts (see `Sort.java').

package cassis.form;

import cassis.Element;
import cassis.UnresolvedReferenceException;
import cassis.struct.Marker;
import cassis.ind.Individual;
import cassis.ind.Relation;
import cassis.ind.Property;
import cassis.ind.Resolvable;
import cassis.sort.Sort;
import cassis.sort.Aspect;
import cassis.parse.*;

public class RelationalForm extends MultiplyForm implements Resolvable {

    // representation

    private int unresolved;

    // constructor

    public RelationalForm() {
	super();
	this.unresolved = 0;
    }

    // methods
    
    // determine the attribute FORM of the ASSOCIATE individual of this property
    private RelationalForm associateForm(Relation prop) throws UnresolvedReferenceException {
	Form assoc = ((Property) prop).getAssociate(this.associate()).attribute();
	if (assoc instanceof MetaForm) {
	    Sort sort = prop.ofSort().base();
            // if (sort != prop.ofSort()) throw new IllegalArgumentException("xxx");
	    Aspect[] aspects = ((Aspect) sort).aspects();
	    if (sort.equals(aspects[0]))
		sort = aspects[1];
	    else sort = aspects[0];
	    MetaForm form = (MetaForm) assoc;
	    Marker pebble = form.elements.getMarker();
	    form.toBegin();
	    while (!form.beyond() && !form.current().ofSort().equals(sort))
		form.toNext();
	    if (form.beyond()) {
		assoc = sort.newForm();
		form.add(assoc);
	    } else
		assoc = (Form) form.current();
	    form.elements.returnTo(pebble);
	}
	return (RelationalForm) assoc;
    }

    // PARSE a string and initialize this form to its value
    public void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != '{')
	    throw new ParseException(reader, "'{' expected");
	while (reader.previewToken() != '}') {
	    if (this.associate() != null)
		this.append(Relation.parse(this.ofSort(), reader, this.associate()));
	    else this.append(Individual.parse(this.ofSort(), reader));
	    if (reader.previewToken() == ',') reader.newToken();
	}
	reader.newToken();
    }

    /**
     * <b>Concatenates</b> another form to this relational form. Each individual
     * of the other relational form is deleted from the other form and appended
     * to this form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void concatenate(MultiplyForm other) {
	if ((this == other) || other.nil()) return;

        Relation ind;
	other.toBegin();
	while (!other.beyond()) {
	    ind = (Relation) other.current();
	    other.delete();
	    if (this.associate() != null) ind.setAssociate(this.associate());
	    super.append(ind);
	}
    }

    /**
     * <b>Merges</b> this relational form with another form. Each individual of the
     * other relational form is deleted from the other form and inserted in order
     * into this form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void merge(MultiplyForm other) {
	if ((this == other) || other.nil()) return;
	this.order();
	other.order();

        Relation ind;
	other.toBegin();
	while (!other.beyond()) {
	    ind = (Relation) other.current();
	    other.delete();
	    if (this.associate() != null) ind.setAssociate(this.associate());
	    super.insertInto(ind);
	}
    }

    /**
     * <b>Inserts from</b> another form the current individual into this relational
     * form at its lead.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void insertFrom(MultiplyForm other) {
	if ((this == other) || other.nil() || other.beyond()) return;
	Relation ind = (Relation) other.current();
	other.delete();
	if (this.associate() != null) ind.setAssociate(this.associate());
	super.insert(ind);
    }

    /**
     * <b>Inserts</b> an individual into this relational form at its lead.
     * xxx xxx
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     */
    void insert(Individual ind) {
	if (ind.used() && ind.attrDefined())
	    ind = (Individual) ind.convert(this.ofSort());
	if (this.associate() != null)
	    ((Relation) ind).setAssociate(this.associate());
	try {
	    RelationalForm assoc = this.associateForm((Relation) ind);
	    Marker pebble = assoc.elements.getMarker();
	    assoc.toBegin();
	    assoc.elements.insertInto(ind);
	    ind.addUse();
	    assoc.elements.returnTo(pebble);
	} catch (UnresolvedReferenceException e) {
	    this.unresolved++;
	    Relation.addUnresolved(((Property) ind).unresolved(), this);
	}
	super.insert(ind);
    }

    /**
     * <b>Inserts into</b> this relational form an individual in order.
     * The lead of this form's element list remains unaltered.
     * xxx xxx
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     */
    void insertInto(Individual ind) {
	if (ind.used() && ind.attrDefined())
	    ind = (Individual) ind.convert(this.ofSort());
	if (this.associate() != null)
	    ((Relation) ind).setAssociate(this.associate());
	try {
	    RelationalForm assoc = this.associateForm((Relation) ind);
	    Marker pebble = assoc.elements.getMarker();
	    assoc.toBegin();
	    assoc.elements.insertInto(ind);
	    ind.addUse();
	    assoc.elements.returnTo(pebble);
	} catch (UnresolvedReferenceException e) {
	    this.unresolved++;
	    Relation.addUnresolved(((Property) ind).unresolved(), this);
	}
	super.insertInto(ind);
    }

    /**
     * <b>Appends</b> an individual onto this relational form.
     * xxx xxx
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     */
    void append(Individual ind) {
        if (ind.used() && ind.attrDefined())
	    ind = (Individual) ind.convert(this.ofSort());
	if (this.associate() != null)
	    ((Relation) ind).setAssociate(this.associate());
	try {
	    RelationalForm assoc = this.associateForm((Relation) ind);
	    Marker pebble = this.elements.getMarker();
	    assoc.toBegin();
	    assoc.elements.insertInto(ind);
	    ind.addUse();
	    this.elements.returnTo(pebble);
	} catch (UnresolvedReferenceException e) {
	    this.unresolved++;
	    Relation.addUnresolved(((Property) ind).unresolved(), this);
	    // System.out.println("unresolved reference " + ((Property) ind).unresolved().getKey());
	}
	super.elements.append(ind);
    }

    // DELETE a relation from this form
    public void delete() {
	try {
	    RelationalForm assoc = this.associateForm((Relation) this.current());
	    Marker pebble = assoc.elements.getMarker();
	    assoc.toBegin();
	    while (!assoc.beyond() &&
		   (this.current().compare(assoc.current()) != EQUAL))
		assoc.toNext();
	    if (assoc.beyond())
		throw new InconsistentFormException("Inconsistency in relational form");
	    assoc.current().delUse();
	    assoc.elements.delete();
	    assoc.elements.returnTo(pebble);
	} catch (UnresolvedReferenceException e) {
	    this.unresolved--;
	    Relation.removeUnresolved(((Property) this.current()).unresolved(), this);
	}
	super.delete();
    }

    // PURGE a relational form
    public void purge() {
	this.toBegin();
	while (!this.nil()) {
	    Element current = this.current();
	    this.delete();
	    if (!current.used()) current.purge();
	}
	if (this.unresolved != 0)
	    throw new InconsistentFormException("Inconsistency in relational form");
    }

    // operational behavior methods

    /**
     * <b>Maximalizes</b> this relational form by removing any identical copies of
     * individuals from the form, and maximalizing any attribute forms.
     * If identical individuals have different attributes, their attributes are
     * combined under the operation of sum. All operations on attribute forms
     * adhere to the behavior of the attribute sort.
     * @see cassis.ind.Individual#attribute()
     */
    public void maximalize() {
	if (this.isMaximal()) return;
	this.maximal = true;
	if (this.nil()) return;
	this.order();

	this.toBegin();
	while (!this.atEnd()) {
	    if (this.current().compare(this.next()) == EQUAL) {
		if (((Individual) this.next()).attrDefined())
		    ((Individual) this.next()).attribute().sum(((Individual) this.current()).attribute());
		this.delete();
	    } else {
		if (((Individual) this.current()).attrDefined())
		    ((Individual) this.current()).attribute().maximalize();
		this.toNext();
	    }
	}
    }

    /**
     * Determines the <b>sum</b> of this relational form with another form.
     * The sum of two relational forms is the maximalized form of all individuals
     * from either form. The result is contained in this form.
     * The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are not of the same sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean sum(Form other) {
	if (this == other) return (!this.nil());
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		this.insertFrom((RelationalForm) other); break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined())
		    ((Individual) this.current()).attribute().sum(((Individual) other.current()).attribute());
		this.toNext();
		other.delete();
		break;
	    default:
	    }
	}
	if (!other.nil())
	    this.concatenate((RelationalForm) other);
	return (!this.nil());
    }

    /**
     * Determines the <b>difference</b> of this relational form with another form.
     * The difference of two relational forms is the maximalized form of all
     * individuals from the first form that do not belong to the second form.
     * If the individuals have attribute forms, the difference of two identical
     * individuals with different attribute forms is the individual with
     * an attribute form equal to the difference of both attribute forms.
     * If the attribute form is nil, the individual is removed from the result.
     * The result is contained in this form. The other form is maximalized.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are the same form or are not
     * of the same sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean difference(Form other) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined() &&
		    ((Individual) this.current()).attribute().difference(((Individual) other.current()).attribute()))
		    this.toNext();
		else
		    this.delete();
		other.toNext();
		break;
	    }
	}
	return (!this.nil());
    }

    /**
     * Determines the <b>product</b> of this relational form with another form.
     * The product of two relational forms is the maximalized form of all individuals
     * that belong to both forms. If the individuals have attribute forms,
     * the product of two identical individuals with different attribute forms is
     * the individual with an attribute form equal to the product of both attribute
     * forms. If the attribute form is nil, the individual is removed from the result.
     * The result is contained in this form. The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are not of the same sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean product(Form other) {
	if (this == other) return (!this.nil());
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		this.delete(); break;
	    case GREATER:
		other.delete(); break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined() &&
		    !((Individual) this.current()).attribute().product(((Individual) other.current()).attribute()))
		    this.delete();
		else
		    this.toNext();
		other.delete();
		break;
	    }
	}
	while (!this.beyond()) this.delete();
	other.purge();
	return (!this.nil());
    }

    /**
     * Determines the <b>symmetric difference</b> of this relational form with another
     * form. The symmetric difference of two relational forms is the maximalized form
     * of all individuals from either form that do not belong to the other form.
     * If the individuals have attribute forms, the symmetric difference of two
     * identical individuals with different attribute forms is the individual with
     * an attribute form equal to the symmetric difference of both attribute forms.
     * If the attribute form is nil, the individual is removed from the result.
     * The result is contained in this form. The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are the same form or are not
     * of the same sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean symdifference(Form other) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		this.insertFrom((RelationalForm) other); break;
	    case EQUAL:
		if (!((Individual) this.current()).attrDefined() ||
		    !((Individual) this.current()).attribute().symdifference(((Individual) other.current()).attribute()))
		    this.delete();
		else this.toNext();
		other.delete();
	    }
	}
	if (!other.nil()) this.concatenate((RelationalForm) other);
	return (!this.nil());
    }

    /**
     * <b>Partitions</b> this relational form wrt another form.
     * The result is specified in three parts: the first part corresponds to the
     * difference of this form with the other form; the second part to the difference
     * of the other form with this form; and the third part to the product of both
     * forms. The first part of the result is contained in this form, the second part
     * in the other form, and the third part in the common form.
     * @param other a {@link Form} object
     * @param common a {@link Form} object
     * @return <tt>true</tt> if this form is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if this and the other form are the same form
     * or if all forms are not of the same sort
     * @see #nil()
     */
    public boolean partition(Form other, Form common) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()) ||
	    !this.ofSort().equals(common.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();
	if (!common.nil()) common.purge();

	Individual ind;
	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:
		ind = (Individual) ((Individual) this.current()).convert(this.current().ofSort());
		if (((Individual) this.current()).attrDefined()) {
		    ((Individual) this.current()).attribute().partition(((Individual) other.current()).attribute(),
							    ind.attribute());
		    if (((Individual) this.current()).attribute().nil())
			this.delete();
		    else this.toNext();
		    if (((Individual) other.current()).attribute().nil())
			other.delete();
		    else other.toNext();
		    if (!ind.attribute().nil())
			((RelationalForm) common).append(ind);
		} else {
		    ((RelationalForm) common).append(ind);
		    this.delete();
		    other.delete();
		}
	    }
	}
	return (!this.nil());
    }

    /**
     * Checks if this relational form is a <b>part of</b> another form. This is the
     * case if each individual of this form is also an individual of the other form,
     * and its attribute form, if any, is a part of the attribute form of the
     * identical individual from the other form. Both forms are maximalized.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the condition holds, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are not of the same sort
     * @see #maximalize()
     */
    public boolean partOf(Form other) {
        if (this == other) return true;
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

        this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.current().compare(other.current())) {
	    case LESS:
		return false;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined() &&
		    !((Individual) this.current()).attribute().partOf(((Individual) other.current()).attribute()))
		    return false;
		else {
		    this.toNext();
		    other.toNext();
		}
	    }
	}
	return (this.beyond());
    }

    /**
     * <b>Adds</b> an individual to this relational form.
     * The resulting form is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     * @throws IllegalArgumentException if the form and individual are not of the same
     * sort
     */
    void add(Individual ind) {
	if (!this.ofSort().equals(ind.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();

	if (ind.used()) ind = (Individual) ind.convert(this.ofSort());
	if (this.associate() != null)
	    ((Relation) ind).setAssociate(this.associate());
	this.toBegin();
	while (!this.beyond())
	    switch (this.current().compare(ind)) {
	    case LESS:
		this.toNext();
		break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined())
		    ((Individual) this.current()).attribute().sum(ind.attribute());
		return;
	    case GREATER:
		this.insert(ind);
		return;
	    }
	this.append(ind);
    }

    public void resolve(Individual ind) {
	this.toBegin();
	while (!this.beyond())
	    try {
		((Property) this.current()).getAssociate(ind);
		break;
	    } catch (IllegalArgumentException e) {
		this.toNext();
	    } catch (UnresolvedReferenceException e) {
		this.toNext();
	    }
	if (this.beyond())
	    throw new InconsistentFormException("Relation to resolve is not found");
	try {
	    RelationalForm assoc = this.associateForm((Relation) this.current());
	    assoc.toBegin();
	    assoc.elements.insertInto(this.current().duplicate());
	} catch (UnresolvedReferenceException e) {
	    throw new InconsistentFormException("resolve");
	}
	if ((!this.atBegin() && this.previous().greaterThan(this.current())) ||
	    (!this.atEnd() && this.next().lessThan(this.current())))
	    this.elements.forceOrdering();
    }

    public Form convert(Sort sort, Individual assoc) throws IllegalArgumentException {
	MultiplyForm result = (MultiplyForm) sort.newForm();
	if (assoc != null) result.setAssociate(assoc);
	this.toBegin();
	while (!this.beyond()) {
	    Individual ind = (Individual) ((Individual) this.current()).convert(sort);
	    if (assoc != null) ((Relation) ind).setAssociate((Individual) assoc);
	    result.append(ind);
	    this.toNext();
	}
	return result;
    }
}
