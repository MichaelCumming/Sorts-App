/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `IntervalForm.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 26.7.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  INTERVAL is a behavioral category for Sorts (see `Sort.java').
//  An interval sort is a multiply-associated sort for which
//  ...
//  Therefore, the IntervalForm class extends on the MultiplyForm
//  class (see `MultiplyForm.java') and implements the algebra methods
//  from the Form interface (see `Form.java').

//  ASSUMPTIONS: the following algebraic property is assumed on forms
//    a + b = b <=> a < b

package cassis.form;

import cassis.ind.Individual;
import cassis.ind.Geometry;

public final class IntervalForm extends MultiplyForm {

    // access methods

    private Geometry currentInd() { return (Geometry) this.elements.current(); }
    private Geometry prevInd() { return (Geometry) this.elements.previous(); }
    private Geometry nextInd() { return (Geometry) this.elements.next(); }

    // methods

    private void oneStepBack() {
	// if the current interval touches the previous and these have
	// the same attributes, these must be combined
	if (!this.elements.atBegin() && this.prevInd().touches(this.currentInd()) &&
	    this.prevInd().attribute().equals(this.currentInd().attribute())) {
	    this.prevInd().combine(this.currentInd());
	    this.delete();
	    this.elements.toPrev();
	}
    }

    private void oneStepForward() {
	// if the current interval touches the next and these have
	// the same attributes, these must be combined
	if (!this.elements.atEnd() && this.nextInd().touches(this.currentInd()) &&
	    this.nextInd().attribute().equals(this.currentInd().attribute())) {
	    this.nextInd().combine(this.currentInd());
	    this.delete();
	}
	else this.toNext();
    }

    // Form interface methods

    // MAXIMALIZE an interval form
    public void maximalize() {
	if (this.isMaximal()) return;
	this.maximal = true;
	if (this.nil()) return;
	this.order();

	Individual temp[] = new Individual[2];
	this.toBegin();
	while (!this.atEnd()) {

	    // these have different descriptors or are disjoint		"- _"
	    if (!this.currentInd().coEquals(this.nextInd()) ||
		this.currentInd().disjoint(this.nextInd()))
		this.toNext();
	    // or, these are coincident					"=="
	    else if (this.current().compare(this.next()) == EQUAL) {
		if (this.nextInd().attrDefined())
		    this.nextInd().attribute().sum(this.currentInd().attribute());
		this.delete();
		this.oneStepBack();
	    // or, these have either no attributes or identical attributes
	    } else if (!this.currentInd().attrDefined() ||
		     this.currentInd().attribute().equals(this.nextInd().attribute())) {
		this.nextInd().combine(this.currentInd());
		this.delete();
	    // or, these touch						"-_"
	    } else if (this.currentInd().touches(this.nextInd()))
		this.toNext();
	    // or, this contains the next				"-=/-=-"
	    else if (this.currentInd().contains(this.nextInd())) {
		if (this.nextInd().attribute().partOf(this.currentInd().attribute()))
		    this.deleteNext();
		else {
		    // reduce this interval with respect to the next interval
		    // the result may be more than one interval
		    this.currentInd().complement(this.nextInd(), temp);
		    (temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
		    this.insert(temp[0]);
		    this.nextInd().attribute().sum(this.currentInd().attribute());
		    this.delete();
		    if (temp[1] != null) {
			(temp[1]).setAttribute((Form) (temp[0]).attribute().duplicate());
			this.insertInto(temp[1]);
		    }
		}
	    // or, the next contains this				"=_"
	    } else if (this.nextInd().contains(this.currentInd())) {
		if (this.currentInd().attribute().partOf(this.nextInd().attribute()))
		    this.delete();
		else {
		    // reduce the next interval with respect to this interval
		    this.nextInd().complement(this.currentInd(), temp);
		    (temp[0]).setAttribute((Form) this.nextInd().attribute().duplicate());
		    this.currentInd().attribute().sum(this.nextInd().attribute());
		    this.deleteNext();
		    this.insertInto(temp[0]);
		}
		this.oneStepBack();
	    // else, these overlap					"-=_"
	    } else if (this.currentInd().attribute().partOf(this.nextInd().attribute())) {
		// reduce the current interval with respect to the next
		this.currentInd().complement(this.nextInd(), temp);
		(temp[0]).setAttribute(this.currentInd().attribute());
		this.delete();
		this.insert(temp[0]);
	    } else if (this.nextInd().attribute().partOf(this.currentInd().attribute())) {
		// reduce the next interval with respect to the current
		this.nextInd().complement(this.currentInd(), temp);
		(temp[0]).setAttribute(this.nextInd().attribute());
		this.deleteNext();
		this.insertInto(temp[0]);
	    } else {
		// reduce both intervals with respect to each other
		// and separate the common interval
		this.currentInd().complement(this.nextInd(), temp);
		(temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
		this.insert(temp[0]);

		this.nextInd().complement(this.currentInd(), temp);
		(temp[0]).setAttribute((Form) this.nextInd().attribute().duplicate());

		this.nextInd().common(this.currentInd());
		this.nextInd().attribute().sum(this.currentInd().attribute());
		this.delete();
		this.insertInto(temp[0]);
	    }
	}
    }

    // returns the SUM of two multiply forms
    public boolean sum(Form other)
    { return this.sum((MultiplyForm) other); }
    private boolean sum(MultiplyForm other) {
	if (this == other) return (!this.elements.empty());
	if (this.getClass() != other.getClass())
	    throw new IllegalArgumentException("Arguments are not of the same class");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	this.order();
	other.order();

	if (this.nil())
	    this.concatenate(other);
	else if (!other.nil()) {
	    // first merge the two forms together
	    // then maximalize the result
	    this.merge(other);
	    //  this.maximalize();
	}
	return (!this.nil());
    }

    // returns the DIFFERENCE of two interval forms
    public boolean difference(Form other)
    { return this.difference((IntervalForm) other); }
    private boolean difference(IntervalForm other) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	Individual temp[] = new Individual[2];
	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.currentInd().coCompare(other.currentInd())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:

		// these are disjoint or touch				"- _/_ -/-_/_-"
		if (this.currentInd().disjoint(other.currentInd()) ||
		    this.currentInd().touches(other.currentInd())) {
		    if (this.current().lessThan(other.current()))
			this.toNext();
		    else
			other.toNext();
		// or, these are coincident				"=="
		} else if (this.current().compare(other.current()) == EQUAL) {
		    if (this.currentInd().attrDefined() &&
			this.currentInd().attribute().difference(other.currentInd().attribute())) {
			this.oneStepBack();
			this.oneStepForward();
		    } else
			this.delete();
		    other.toNext();
		// or, this contains the other				"-=/-=-/=-"
		} else if (this.currentInd().contains(other.currentInd())) {
		    this.currentInd().complement(other.currentInd(), temp);
		    if (this.currentInd().attrDefined()) {
			temp[0].setAttribute((Form) this.currentInd().attribute().duplicate());
			if (!this.currentInd().attribute().difference(other.currentInd().attribute())) {
			    this.delete();
			    this.insert(temp[0]);
			    if (temp[1] != null) {
				temp[1].setAttribute((Form) temp[0].attribute().duplicate());
				this.insert(temp[1]);
				this.elements.toPrev();
			    }
			} else if (!this.currentInd().attribute().equals((temp[0]).attribute())) {
			    this.currentInd().common(other.currentInd());
			    if (temp[0].greaterThan(this.current())) {
				this.oneStepBack();
				this.toNext();
				this.insert(temp[0]);
				this.elements.toPrev();
			    } else if (temp[1] != null) {
				temp[1].setAttribute((Form) temp[0].attribute().duplicate());
				this.insert(temp[0]);
				this.toNext();
				this.insert(temp[1]);
				this.elements.toPrev();
			    } else {
				this.insert(temp[0]);
				this.oneStepForward();
			    }
			}
		    } else {
			this.delete();
			this.insert(temp[0]);
			if (temp[1] != null) this.insert(temp[1]);
			this.elements.toPrev();
		    }
		    other.toNext();
		// or, the other contains this				"_=/_=_/=_"
		} else if (other.currentInd().contains(this.currentInd())) {
		    if (this.currentInd().attrDefined() &&
			this.currentInd().attribute().difference(other.currentInd().attribute())) {
			this.oneStepBack();
			this.oneStepForward();
		    } else
			this.delete();
		// or, these overlap					"-=_"
		// and this interval is less than the other interval
		} else if (this.current().lessThan(other.current())) {
		    this.currentInd().complement(other.currentInd(), temp);
		    if (!this.currentInd().attrDefined()) {
			this.delete();
			this.insert(temp[0]);
		    } else {
			(temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
			if (!this.currentInd().attribute().difference(other.currentInd().attribute())) {
			    this.delete();
			    this.insert(temp[0]);
			} else if (!this.currentInd().attribute().equals((temp[0]).attribute())) {
			    this.currentInd().common(other.currentInd());
			    this.insert(temp[0]);
			    this.toNext();
			} else
			    this.toNext();
		    }
		// else, these overlap					"_=-"
		// and this interval is greater than the other interval
		} else {
		    this.currentInd().complement(other.currentInd(), temp);
		    if (!this.currentInd().attrDefined()) {
			this.delete();
			this.insert(temp[0]);
			this.elements.toPrev();
		    } else {
			(temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
			if (!this.currentInd().attribute().difference(other.currentInd().attribute())) {
			    this.delete();
			    this.insert(temp[0]);
			    this.elements.toPrev();
			} else if (!this.currentInd().attribute().equals((temp[0]).attribute())) {
			    this.currentInd().common(other.currentInd());
			    this.oneStepBack();
			    this.toNext();
			    this.insert(temp[0]);
			    this.elements.toPrev();
			}
		    }
		    other.toNext();
		}
		break;
	    }
	}
	return (!this.nil());
    }

    // returns the PRODUCT of two interval forms
    public boolean product(Form other)
    { return this.product((IntervalForm) other); }
    private boolean product(IntervalForm other) {
	if (this == other) return (!this.nil());
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.currentInd().coCompare(other.currentInd())) {
	    case LESS:
		this.delete(); break;
	    case GREATER:
		other.delete(); break;
	    case EQUAL:

		// these are disjoint or touch				"- _/_ -/-_/_-"
		if (this.currentInd().disjoint(other.currentInd()) ||
		    this.currentInd().touches(other.currentInd())) {
		    if (this.current().lessThan(other.current()))
			this.delete();
		    else
			other.delete();
		// or, these are coincident				"=="
		} else if (this.current().compare(other.current()) == EQUAL) {
		    if (this.currentInd().attrDefined()) {
			if (this.currentInd().attribute().product(other.currentInd().attribute())) {
			    this.oneStepBack();
			    this.toNext();
			} else
			    this.delete();
		    } else
			this.toNext();
		    other.delete();
		// or, this contains the other				"-=/-=-/=-"
		} else if (this.currentInd().contains(other.currentInd())) {
		    if (other.currentInd().attrDefined()) {
			if (other.currentInd().attribute().product((Form) this.currentInd().attribute().duplicate())) {
			    this.insertFrom(other);
			    this.elements.toPrev();
			    this.oneStepBack();
			    this.toNext();
			} else
			    other.delete();
		    } else
			this.insertFrom(other);
		// or, the other contains this				"_=/_=_/=_"
		} else if (other.currentInd().contains(this.currentInd())) {
		    if (this.currentInd().attrDefined()) {
			if (this.currentInd().attribute().product((Form) other.currentInd().attribute().duplicate())) {
			    this.oneStepBack();
			    this.toNext();
			} else
			    this.delete();
		    } else
			this.toNext();
		// or, these overlap					"_=-"
		// and this interval is greater than the other interval
		} else if (this.current().greaterThan(other.current())) {
		    other.currentInd().common(this.currentInd());
		    if (other.currentInd().attrDefined()) {
			if (other.currentInd().attribute().product((Form) this.currentInd().attribute().duplicate())) {
			    this.insertFrom(other);
			    this.elements.toPrev();
			    this.oneStepBack();
			    this.toNext();
			} else
			    other.delete();
		    } else
			this.insertFrom(other);
		// else, these overlap					"-=_"
		// and this interval is less than the other interval
		} else {
		    this.currentInd().common(other.currentInd());
		    if (this.currentInd().attrDefined()) {
			if (this.currentInd().attribute().product((Form) other.currentInd().attribute().duplicate())) {
			    this.oneStepBack();
			    this.toNext();
			} else
			    this.delete();
		    } else
			this.toNext();
		}
	    }
	}
	while (!this.beyond()) this.delete();
	while (!other.beyond()) other.delete();
	return (!this.nil());
    }

    // returns the SYMmetric DIFFERENCE of two interval forms
    public boolean symdifference(Form other)
    { return this.symdifference((IntervalForm) other); }
    private boolean symdifference(IntervalForm other) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	if (this.nil()) this.concatenate(other);
	if (other.nil()) return (!this.nil());

	// first merge the two forms together,
	// then remove the common parts of overlapping intervals
	// while combining intervals that touch
	this.merge(other);

	Individual temp[] = new Individual[2];
	this.toBegin();
	while (!this.elements.atEnd() && !this.beyond()) {

	    // these have different descriptors or are disjoint		"- _"
	    if (!this.currentInd().coEquals(this.nextInd()) ||
		this.currentInd().disjoint(this.nextInd()))
		this.toNext();
	    // or, these are coincident					"=="
	    else if (this.current().compare(this.next()) == EQUAL) {
		if (!this.nextInd().attrDefined() ||
		    !this.nextInd().attribute().symdifference(this.currentInd().attribute())) {
		    this.delete();
		    this.delete();
		} else {
		    this.delete();
		    this.oneStepBack();
		    this.oneStepForward();
		}
	    // or, these touch						"-_"
	    } else if (this.currentInd().touches(this.nextInd())) {
		if (!this.nextInd().attrDefined() ||
		    this.nextInd().attribute().equals(this.currentInd().attribute())) {
		    this.nextInd().combine(this.currentInd());
		    this.delete();
		} else
		    this.toNext();
	    // or, this contains the next				"-=/-=-"
	    } else if (this.currentInd().contains(this.nextInd())) {
		// reduce this interval with respect to the next interval
		// the result may be more than one interval
		this.currentInd().complement(this.nextInd(), temp);
		if (this.currentInd().attrDefined()) {
		    (temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
		    this.nextInd().attribute().symdifference(this.currentInd().attribute());
		    this.delete();
		    this.insert(temp[0]);
		    if (temp[1] != null) {
			(temp[1]).setAttribute((Form) (temp[0]).attribute().duplicate());
			this.insertInto(temp[1]);
			if (this.currentInd().attribute().nil())
			    this.delete();
		    } else {
			if (this.currentInd().attribute().nil())
			    this.delete();
			else
			    this.oneStepForward();
		    }
		} else {
		    this.delete();
		    this.delete();
		    this.insert(temp[0]);
		    if (temp[1] != null) this.insertInto(temp[1]);
		    this.elements.toPrev();
		}
	    // or, the next contains this				"=_"
	    } else if (this.nextInd().contains(this.currentInd())) {
		// reduce the next interval with respect to this interval
		this.nextInd().complement(this.currentInd(), temp);
		if (this.nextInd().attrDefined()) {
		    (temp[0]).setAttribute((Form) this.nextInd().attribute().duplicate());
		    if (!this.currentInd().attribute().symdifference(this.nextInd().attribute()))
			this.delete();
		    else {
			this.oneStepBack();
			this.toNext();
		    }
		} else
		    this.delete();
		this.delete();
		if ((temp[0]).lessOrEqual(this.current())) {
		    this.insert(temp[0]);
		    this.elements.toPrev();
		} else
		    this.insertInto(temp[0]);
	    // else, these overlap					"-=_"
	    } else {
		Individual el;
		this.nextInd().complement(this.currentInd(), temp);
		el = temp[0];
		this.currentInd().complement(this.nextInd(), temp);
		temp[1] = el;
		if (!this.currentInd().attrDefined()) {
		    this.delete();
		    this.insert(temp[0]);
		    this.delete();
		    this.insert(temp[1]);
		    this.elements.toPrev();
		} else {
		    (temp[0]).setAttribute((Form) this.currentInd().attribute().duplicate());
		    (temp[1]).setAttribute((Form) this.nextInd().attribute().duplicate());
		    if (!this.currentInd().attribute().symdifference(this.nextInd().attribute())) {
			this.delete();
			this.insert(temp[0]);
			this.delete();
			this.insert(temp[1]);
			this.elements.toPrev();
		    } else if (this.currentInd().attribute().equals((temp[0]).attribute())) {
			this.toNext();
			this.delete();
			this.insert(temp[1]);
			this.elements.toPrev();
		    } else if (this.nextInd().attribute().equals((temp[1]).attribute())) {
			this.delete();
			this.insert(temp[0]);
			this.toNext();
		    } else {
			this.currentInd().common(other.currentInd());
			this.insert(temp[0]);
			this.toNext();
			this.delete();
			this.insert(temp[1]);
			this.elements.toPrev();
		    }
		}
	    }
	}
	return (!this.nil());
    }

    // PARTITION two interval forms with respect to one another
    public boolean partition(Form other, Form common)
    { return this.partition((IntervalForm) other, (IntervalForm) common); }
    private boolean partition(IntervalForm other, IntervalForm common) {
	if (this == other)
	    throw new IllegalArgumentException("Arguments are identical");
	if (!this.ofSort().equals(other.ofSort()) ||
	    !this.ofSort().equals(common.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();
	if (!common.nil()) common.purge();

	Individual temp[] = new Individual[2];
	Geometry ind;
	IntervalForm first, second;
	Form form;

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.currentInd().coCompare(other.currentInd())) {
	    case LESS:
		this.toNext(); break;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:

		// these are disjoint or touch				"- _/_ -/-_/_-"
		if (this.currentInd().disjoint(other.currentInd()) ||
		    this.currentInd().touches(other.currentInd())) {
		    if (this.current().lessThan(other.current()))
			this.toNext();
		    else
			other.toNext();
		    break;
		}
		// or, these are coincident				"=="
		if (this.current().compare(other.current()) == EQUAL) {
		    if (this.currentInd().attrDefined()) {
			form = this.currentInd().attribute().ofSort().newForm();
			this.currentInd().attribute().partition(other.currentInd().attribute(), form);
			if (!form.nil()) {
			    ind = (Geometry) this.current().duplicate();
			    ind.setAttribute(form);
			    common.append(ind);
			}
			if (this.currentInd().attribute().nil())
			    this.delete();
			else this.toNext();
			if (other.currentInd().attribute().nil())
			    other.delete();
			else other.toNext();
		    } else {
			common.append((Individual) this.current());
			this.delete();
			other.delete();
		    }
		    break;
		}

		// or, this contains the other				"-=/-=-/=-"
		// or the other contains this				"_=/_=_/=_"
		if (this.currentInd().contains(other.currentInd())) {
		    first = this; 
		    second = other;
		} else if (other.currentInd().contains(this.currentInd())) {
			first = other; 
			second = this;
		} else first = second = null;

		if (first != null) {
		    first.currentInd().complement(second.currentInd(), temp);
		    if (first.currentInd().attrDefined()) {
			temp[0].setAttribute((Form) first.currentInd().attribute().duplicate());
			form = first.currentInd().attribute().ofSort().newForm();
			first.currentInd().attribute().partition(second.currentInd().attribute(), form);
			if (!first.currentInd().attribute().equals(temp[0].attribute())) {
			    first.currentInd().common(second.currentInd());
			    if (temp[0].greaterThan(first.current())) {
				first.oneStepBack();
				first.toNext();
				first.insert(temp[0]);
				first.elements.toPrev();
			    } else if (temp[1] != null) {
				temp[1].setAttribute((Form) temp[0].attribute().duplicate());
				first.insert(temp[0]);
				first.toNext();
				first.insert(temp[1]);
				first.elements.toPrev();
			    } else {
				first.insert(temp[0]);
				first.oneStepForward();
			    }
			} else {
			    first.insert(temp[0]);
			    if (first.currentInd().attribute().nil())
				first.delete();
			    else first.toNext();
			    if (temp[1] != null) {
				temp[1].setAttribute((Form) temp[0].attribute().duplicate());
				first.insert(temp[1]);
				first.elements.toPrev();
			    }
			}
			if (!form.nil()) {
			    ind = (Geometry) second.current().duplicate();
			    ind.setAttribute(form);
			    common.append(ind);
			}
			if (second.currentInd().attribute().nil())
			    second.delete();
			else second.toNext();
		    } else {
			first.delete();
			first.insert(temp[0]);
			if (temp[1] != null) first.insert(temp[1]);
			first.elements.toPrev();
			common.append((Individual) second.current());
			second.delete();
		    }
		// or, these overlap				"-=_"
		} else {
		    other.currentInd().complement(this.currentInd(), temp);
		    ind = (Geometry) temp[0];
		    this.currentInd().complement(other.currentInd(), temp);
		    temp[1] = ind;
		    if (this.currentInd().attrDefined()) {
			temp[0].setAttribute((Form) this.currentInd().attribute().duplicate());
			temp[1].setAttribute((Form) other.currentInd().attribute().duplicate());
			form = this.currentInd().attribute().ofSort().newForm();
			this.currentInd().attribute().partition(other.currentInd().attribute(), form);
			if (!form.nil()) {
			    ind = (Geometry) this.current().duplicate();
			    ind.common(other.currentInd());
			    ind.setAttribute(form);
			    common.append(ind);
			}
			if (!this.currentInd().attribute().equals(temp[0].attribute())) {
			    this.currentInd().common(other.currentInd());
			    if (temp[0].greaterThan(this.current())) {
				this.oneStepBack();
				this.toNext();
				this.insert(temp[0]);
				this.elements.toPrev();
			    } else {
				this.insert(temp[0]);
				this.oneStepForward();
			    }
			}
			if (!other.currentInd().attribute().equals(temp[1].attribute())) {
			    other.currentInd().common(this.currentInd());
			    if (temp[1].greaterThan(other.current())) {
				other.oneStepBack();
				other.toNext();
				other.insert(temp[1]);
				other.elements.toPrev();
			    } else {
				other.insert(temp[0]);
				other.oneStepForward();
			    }
			}
		    } else {
			ind = this.currentInd();
			this.delete();
			this.insert(temp[0]);
			ind.common(other.currentInd());
			common.append(ind);
			other.delete();
			other.insert(temp[1]);
			if (this.previous().greaterThan(other.previous()))
			    this.elements.toPrev();
			else other.elements.toPrev();
		    }
		}
		break;
	    }
	}
	return (!this.nil());
    }

    // checks if one interval form is a PART OF another interval form
    public boolean partOf(Form other)
    { return this.partOf((IntervalForm) other); }
    private boolean partOf(IntervalForm other) {
        if (this == other) return true;
	if (!this.ofSort().equals(other.ofSort()))
	    throw new IllegalArgumentException("Arguments are not of the same sort");

	if (!this.isMaximal()) this.maximalize();
	if (!other.isMaximal()) other.maximalize();

	boolean flag = false;
	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    switch (this.currentInd().coCompare(other.currentInd())) {
	    case LESS:
		return false;
	    case GREATER:
		other.toNext(); break;
	    case EQUAL:
		// these are disjoint or touch				"- _/-_/_-/_ -"
		if (this.currentInd().disjoint(other.currentInd()) ||
		    this.currentInd().touches(other.currentInd())) {
		    if (this.current().greaterThan(other.current()))
			other.toNext();
		    else
			return false;
		// or, these are coincident				"=="
		} else if (this.current().compare(other.current()) == EQUAL) {
		    if (!this.currentInd().attrDefined() ||
			this.currentInd().attribute().partOf(other.currentInd().attribute())) {
			this.toNext();
			other.toNext();
		    } else
			return false;
		
		// or, the other contains this				"_=/_=_/=_"
		} else if (other.currentInd().contains(this.currentInd())) {
		    if (!this.currentInd().attrDefined() ||
			this.currentInd().attribute().partOf(other.currentInd().attribute()))
			this.toNext();
		    else
			return false;
		// else, these overlap or this contains the other
		// and this is greater than the other			"_=-/=-"
		} else if (this.current().greaterThan(other.current())) {
		    if (!this.currentInd().attrDefined() ||
			this.currentInd().attribute().partOf(other.currentInd().attribute())) {
			flag = true;
			other.toNext();
		    } else
			return false;
		// else, these overlap or this contains the other
		// and this is less than the other			"-=/-=-/-=_"
		} else if (flag && other.currentInd().touches(other.prevInd())) {
		    if (!this.currentInd().attrDefined() ||
			this.currentInd().attribute().partOf(other.currentInd().attribute()))
			if (!this.currentInd().contains(other.currentInd()) ||
			    this.currentInd().aligns(other.currentInd())) {
			    flag = false;
			    this.toNext();
			} else other.toNext();
		} else
		    return false;
		break;
	    }
	}
	return (this.beyond());
    }
}
