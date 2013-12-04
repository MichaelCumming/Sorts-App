/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `DiscreteForm.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 27.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  DISCRETE is a behavioral category for sorts (see `Sort.java').
//  A discrete sort is a multiply-associated sort for which
//  any two individuals are either identical or disjoint.
//  Therefore, the DiscreteForm class extends on the MultiplyForm
//  class (see `MultiplyForm.java') and implements the algebra mehods
//  from the Form interface (see `Form.java').

package blue.form;

import blue.ind.Individual;
import blue.sort.Sort;

public class DiscreteForm extends MultiplyForm {
    
    //added 31.10.2003 MC no constructor existed prior. WHY not?
    public DiscreteForm() {
        super();
    }
    
    // Form interface methods
    // MAXIMALIZE a discrete form
    public void maximalize() {
        if (this.isMaximal()) return;
        this.prepare();
        if (this.elements.empty()) return;
        this.toBegin();
        while (!this.elements.atEnd()) {
            if (this.current().equals(this.next())) {
                if (((Individual)this.next()).attrDefined())
                    ((Individual)this.next()).attribute().sum(((Individual)this.current()).attribute());
                this.delete();
            } else {
                if (((Individual)this.current()).attrDefined())
                    ((Individual)this.current()).attribute().maximalize();
                this.toNext();
            }
        }
    }
    
    // returns the SUM of two discrete forms
    public boolean sum(Form other) {
        this.prepare((DiscreteForm)other);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom((DiscreteForm)other);
                    break;
                case EQUAL:
                    if (((Individual)this.current()).attrDefined())
                        ((Individual)this.current()).attribute().sum(((Individual)other.current()).attribute());
                    this.toNext();
                    other.delete();
                    break;
            }
        }
        if (!((DiscreteForm)other).elements.empty())
            this.concatenate((DiscreteForm)other);
        return (!this.elements.empty());
    }
    
    // returns the DIFFERENCE of two discrete forms
    public boolean difference(Form other) {
        this.prepare((DiscreteForm)other);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    other.toNext();
                    break;
                case EQUAL:
                    if (((Individual)this.current()).attrDefined() &&
                    ((Individual)this.current()).attribute().difference(((Individual)other.current()).attribute()))
                        this.toNext();
                    else
                        this.delete();
                    other.toNext();
                    break;
            }
        }
        return (!this.elements.empty());
    }
    
    // returns the PRODUCT of two discrete forms
    public boolean product(Form other) {
        this.prepare((DiscreteForm)other);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.delete();
                    break;
                case GREATER:
                    other.delete();
                    break;
                case EQUAL:
                    if (((Individual)this.current()).attrDefined() &&
                    !((Individual)this.current()).attribute().product(((Individual)other.current()).attribute()))
                        this.delete();
                    else
                        this.toNext();
                    other.delete();
                    break;
            }
        }
        while (!this.beyond()) this.delete();
        other.purge();
        return (!this.elements.empty());
    }
    
    // returns the SYMmetric DIFFERENCE of two discrete forms
    public boolean symdifference(Form other) {
        this.prepare((DiscreteForm)other);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom((DiscreteForm)other);
                    break;
                case EQUAL:
                    if (!((Individual)this.current()).attrDefined() ||
                    !((Individual)this.current()).attribute().symdifference(((Individual)other.current()).attribute()))
                        this.delete();
                    else
                        this.toNext();
                    other.delete();
            }
        }
        if (!((DiscreteForm)other).elements.empty()) this.concatenate((DiscreteForm)other);
        return (!this.elements.empty());
    }
    
    // PARTITION two discrete forms with respect to one another
    public boolean partition(Form other, Form common) { return this.partition((DiscreteForm)other, (DiscreteForm)common); }
    
    private boolean partition(DiscreteForm other, DiscreteForm common) {
        Individual ind;
        this.prepare(other, common);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    other.toNext();
                    break;
                case EQUAL:
                    ind = (Individual)((Individual)this.current()).convert(this.current().ofSort());
                    if (((Individual)this.current()).attrDefined()) {
                        ((Individual)this.current()).attribute().partition(((Individual)other.current()).attribute(), ind.attribute());
                        if (((Individual)this.current()).attribute().nil())
                            this.delete();
                        else
                            this.toNext();
                        if (((Individual)other.current()).attribute().nil())
                            other.delete();
                        else
                            other.toNext();
                        if (!ind.attribute().nil())
                            common.append(ind);
                    } else {
                        common.append(ind);
                        this.delete();
                        other.delete();
                    }
            }
        }
        return (!this.elements.empty());
    }
    
    // checks if one discrete form is a PART OF another discrete form
    public boolean partOf(Form other) {
        this.prepare((DiscreteForm)other);
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    return false;
                case GREATER:
                    other.toNext();
                    break;
                case EQUAL:
                    if (((Individual)this.current()).attrDefined() &&
                    !((Individual)this.current()).attribute().partOf(((Individual)other.current()).attribute()))
                        return false;
                    else {
                        this.toNext();
                        other.toNext();
                    }
            }
        }
        return (this.beyond());
    }
    
    // ADD an individual to this form
    void add(Individual ind) {
        this.prepare(ind);
        this.toBegin();
        while (!this.beyond())
            switch (this.current().compare(ind)) {
                case LESS:
                    this.toNext();
                    break;
                case EQUAL:
                    if (((Individual)this.current()).attrDefined())
                        ((Individual)this.current()).attribute().sum(ind.attribute());
                    return;
                case GREATER:
                    this.insert(ind);
                    return;
            }
            this.append(ind);
    }
}
