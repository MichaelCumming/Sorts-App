/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `DiscreteForm.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 15.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

import cassis.ind.Individual;

/**
 * A <b>discrete-form</b> specifies a form with a discrete operational behavioral,
 * corresponding a mathematical set: any two individuals in the form are either
 * identical or disjoint. A discrete form is <i>maximal</i> if no two individuals
 * are identical, and the individuals' attribute forms, if any, are also maximal.
 * <p>
 * The <b>DiscreteForm</b> class extends on the {@link MultiplyForm} class and
 * specifies the discrete operational behavior of forms.
 */
public class DiscreteForm extends MultiplyForm {

    /**
     * <b>Maximalizes</b> this discrete form by removing any identical copies of
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
	    if (this.current().equals(this.next())) {
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
     * Determines the <b>sum</b> of this discrete form with another form.
     * The sum of two discrete forms is the maximalized form of all individuals
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
		this.insertFrom((DiscreteForm) other); break;
	    case EQUAL:
		if (((Individual) this.current()).attrDefined())
		    ((Individual) this.current()).attribute().sum(((Individual) other.current()).attribute());
		this.toNext();
		other.delete();
		break;
	    }
	}
	if (!other.nil())
	    this.concatenate((DiscreteForm) other);
	return (!this.nil());
    }

    /**
     * Determines the <b>difference</b> of this discrete form with another form.
     * The difference of two discrete forms is the maximalized form of all individuals
     * from the first form that do not belong to the second form. If the individuals
     * have attribute forms, the difference of two identical individuals with
     * different attribute forms is the individual with an attribute form equal to
     * the difference of both attribute forms. If the attribute form is nil, the
     * individual is removed from the result.
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
     * Determines the <b>product</b> of this discrete form with another form.
     * The product of two discrete forms is the maximalized form of all individuals
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
     * Determines the <b>symmetric difference</b> of this discrete form with another
     * form. The symmetric difference of two discrete forms is the maximalized form
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
		this.insertFrom((DiscreteForm) other); break;
	    case EQUAL:
		if (!((Individual) this.current()).attrDefined() ||
		    !((Individual) this.current()).attribute().symdifference(((Individual) other.current()).attribute()))
		    this.delete();
		else this.toNext();
		other.delete();
	    }
	}
	if (!other.nil()) this.concatenate((DiscreteForm) other);
	return (!this.nil());
    }

    /**
     * <b>Partitions</b> this discrete form wrt another form.
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
			((DiscreteForm) common).append(ind);
		} else {
		    ((DiscreteForm) common).append(ind);
		    this.delete();
		    other.delete();
		}
	    }
	}
	return (!this.nil());
    }

    /**
     * Checks if this discrete form is a <b>part of</b> another form. This is the
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
     * <b>Adds</b> an individual to this discrete form. The resulting form is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     * @throws IllegalArgumentException if the form and individual are not of the same
     * sort
     */
    void add(Individual ind) {
	if (!this.ofSort().equals(ind.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();

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
}
