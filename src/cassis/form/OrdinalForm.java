/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `OrdinalForm.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 11.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  ORDINAL is is a behavioral category for sorts (see `Sort.java').
//  An ordinal sort is a singly-associated sort ..
//  The OrdinalForm class implements the algebra mehods
//  from the Form interface (see `Form.java').

package cassis.form;

import cassis.Element;
import cassis.ind.Individual;

public final class OrdinalForm extends SinglyForm {

    // constructor

    public OrdinalForm() {
	super();
    }

    // Form interface methods

    // returns the SUM of two ordinal forms
    public boolean sum(Form other)
    { return this.sum((OrdinalForm) other); }
    private boolean sum(OrdinalForm other) {
	this.prepare(other);

	if (this.nil())
	    this.setIndividual(other.individual());
	else if (!other.nil()) {
	    this.individual().combine(other.individual());
	    if (this.individual().attrDefined())
		this.individual().attribute().sum(other.individual().attribute());
	}
	if (this.nil()) this.purge();
	other.purge();
	return (this.individual() != null);
    }

    // returns the DIFFERENCE of two ordinal forms
    public boolean difference(Form other)
    { return this.difference((OrdinalForm) other); }
    private boolean difference(OrdinalForm other) {
	this.prepare(other);

	if (this.nil() ||
	    (!other.nil() &&
	     (!this.individual().complement(other.individual()) ||
	      (this.individual().attrDefined() &&
	       !this.individual().attribute().difference(other.individual().attribute())))))
	    this.purge();
	return (this.individual() != null);
    }

    // returns the PRODUCT of two ordinal forms
    public boolean product(Form other)
    { return this.product((OrdinalForm) other); }
    private boolean product(OrdinalForm other) {
	this.prepare(other);

	if (this.nil() || other.nil() ||
	    !this.individual().common(other.individual()) ||
	    (this.individual().attrDefined() &&
	     !this.individual().attribute().product(other.individual().attribute())))
	    this.purge();
	other.purge();
	return (this.individual() != null);
    }

    // returns the SYMmetric DIFFERENCE of two ordinal forms
    public boolean symdifference(Form other)
    { return this.symdifference((OrdinalForm) other); }
    private boolean symdifference(OrdinalForm other) {
	this.prepare(other);

	if (this.nil())
	    this.setIndividual(other.individual());
	else if (!other.nil()) {
	    this.individual().combine(other.individual());
	    if (this.individual().attrDefined())
		this.individual().attribute().symdifference(other.individual().attribute());
	}
	if (this.nil()) this.purge();
	other.purge();
	return (this.individual() != null);
    }

    // returns the PARTITION of two ordinal forms
    public boolean partition(Form other, Form common)
    { return this.partition((OrdinalForm) other, (OrdinalForm) common); }
    private boolean partition(OrdinalForm other, OrdinalForm common) {
	this.prepare(other, common);

	if (common.individual() != null) common.purge();
	if (this.nil() || other.nil())
	    return (!this.nil());

	Individual ind = (Individual) other.individual().duplicate();
	ind.common(this.individual());
	other.individual().complement(this.individual());
	this.individual().complement(other.individual());
	if (this.individual().attrDefined()) {
	    ind.setAttribute(this.individual().attribute().ofSort().newForm());
	    this.individual().attribute().partition(other.individual().attribute(), ind.attribute());
	    if (!ind.attribute().nil()) common.setIndividual(ind);
	} else
	    if (!ind.nil()) common.setIndividual(ind);
	if (this.nil()) this.purge();
	if (other.nil()) other.purge();
	return (this.individual() != null);
    }

    // checks if one ordinal form is a PART OF another ordinal form
    public boolean partOf(Form other)
    { return this.partOf((OrdinalForm) other); }
    private boolean partOf(OrdinalForm other) {
	this.prepare(other);

	if (other.nil()) return false;
	if (this.nil()) return true;
	return (other.individual().contains(this.individual()) &&
		(!this.individual().attrDefined() ||
		 this.individual().attribute().partOf(this.individual().attribute())));
    }

     // ADD an individual to this form
    public void add(Element data) {
	if (data instanceof Individual)
	    this.add((Individual) data);
	else this.add(((Form) data).current());
    }
    private void add(Individual ind) {
	this.prepare(ind);

	if (this.nil())
	    this.setIndividual(ind);
	else {
	    this.individual().combine(ind);
	    if (this.individual().attrDefined())
		this.individual().attribute().sum(ind.attribute());
	}
    }
}
