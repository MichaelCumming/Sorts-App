/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `RelationalForm.java'                                     *
 * written by: Rudi Stouffs                                  *
 * last modified: 04.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  RELATIONAL is a behavioral category for sorts (see `Sort.java').

package blue.form;

import blue.Element;
import blue.UnresolvedReferenceException;
import blue.struct.Marker;
import blue.ind.Individual;
import blue.ind.Relation;
import blue.ind.Property;
import blue.ind.Resolvable;
import blue.sort.Sort;
import blue.sort.Aspect;
import blue.io.*;

public class RelationalForm extends MultiplyForm implements Resolvable {
    // representation
    private int unresolved;

    // constructor
    public RelationalForm() {
        super();
        this.unresolved = 0;
    }

    // access methods
    private Relation currentRel() { return (Relation)this.elements.current(); }

    private Relation nextRel() { return (Relation)this.elements.next(); }

    // methods
    // determine the attribute FORM of the ASSOCIATE individual of this property
    private RelationalForm associateForm(Relation prop) throws UnresolvedReferenceException {
        Form assoc = ((Property)prop).getAssociate(this.associate()).attribute();
        if (assoc instanceof MetaForm) {
            Sort sort = prop.ofSort().base();
            // if (sort != prop.ofSort()) throw new IllegalArgumentException("xxx");
            Aspect[] aspects = ((Aspect)sort).aspects();
            if (sort.equals(aspects[0]))
                sort = aspects[1];
            else
                sort = aspects[0];
            MetaForm form = (MetaForm)assoc;
            Marker pebble = form.elements.getMarker();
            // source of errors?...
            form.toBegin();
            while (!form.beyond() && !form.current().ofSort().equals(sort))
                form.toNext();
            if (form.beyond()) {
                assoc = sort.newForm();
                form.add(assoc);
            } else
                assoc = (Form)form.current();
            form.elements.returnTo(pebble);
            // source of errors?...
        }
        return (RelationalForm)assoc;
    }

    // DUPLICATE the specified relational form into this relational form
    public void duplicate(Form form) { this.duplicate((RelationalForm)form); }

    private void duplicate(RelationalForm other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (this.elements.empty()) {
            if (!other.isMaximal()) other.maximalize();
            this.maximal = true;
        } else
            this.maximal = false;
        other.toBegin();
        while (!other.beyond()) {
            Individual ind = (Individual)((Individual)other.current()).convert(this.ofSort());
            if (this.associate() != null)
                ((Relation)ind).setAssociate(this.associate());
            this.append(ind);
            other.toNext();
        }
    }

    // PRINT this individual to SDL
    public void print(SdlContext sdl) {
        if (this.associate() == null) {
            super.print(sdl);
            return;
        }
        if (!this.isMaximal()) this.maximalize();
        sdl.setSort(this.ofSort());
        sdl.beginGroup();
        if (!this.elements.empty()) {
            // Marker pebble = this.getMarker();
            this.toBegin();
            this.currentRel().print(sdl, this.associate());
            while (!this.elements.atEnd()) {
                sdl.separator();
                this.toNext();
                this.currentRel().print(sdl, this.associate());
            }
            // this.returnTo(pebble);
        }
        sdl.endGroup();
    }

    public String toString() {
        if (this.associate() == null)
            return super.toString();
        String result = "{";
        if (!this.elements.empty()) {
            Marker pebble = this.elements.getMarker();
            this.toBegin();
            result += this.currentRel().toString(this.associate());
            while (!this.elements.atEnd()) {
                this.toNext();
                result += ", " + this.currentRel().toString(this.associate());
            }
            this.elements.returnTo(pebble);
        }
        return result + '}';
    }

    public void visualize(GraphicsContext gc) {
        if (this.nil()) return;
        this.toBegin();
        gc.beginGroup(this.current().getClass(), this.elements.length());
        while (!this.beyond()) {
            if (this.associate() != null)
                this.currentRel().visualize(gc, this.associate());
            else
                this.currentRel().visualize(gc);
            this.toNext();
        }
        gc.endGroup();
    }

    // PARSE a string and initialize this form to its value
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != '{')
            throw new ParseException(reader, "'{' expected");
        while (reader.previewToken() != '}') {
            if (this.associate() != null)
                this.append(Relation.parse(this.ofSort(), reader, this.associate()));
            else
                this.append(Individual.parse(this.ofSort(), reader));
            if (reader.previewToken() == ',') reader.newToken();
        }
        reader.newToken();
    }

    // SortedList interface methods
    // The following methods overwrite the same methods specified
    // in the MultiplyForm class. These additionally ensure
    // ...
    // CONCATENATE the other relational form to this relational form
    public void concatenate(MultiplyForm other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        other.toBegin();
        while (!other.beyond()) {
            Relation ind = (Relation)other.current();
            other.delete();
            if (this.associate() != null) ind.setAssociate(this.associate());
            this.append(ind);
        }
    }

    // MERGE the other relational form with this relational form
    public void merge(MultiplyForm other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        other.toBegin();
        while (!other.beyond()) {
            Relation ind = (Relation)other.current();
            other.delete();
            if (this.associate() != null) ind.setAssociate(this.associate());
            this.insertInto(ind);
        }
    }

    // INSERT the current relation FROM the other form into this form
    public void insertFrom(MultiplyForm other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        Relation ind = (Relation)other.current();
        other.delete();
        if (this.associate() != null) ind.setAssociate(this.associate());
        this.insert(ind);
    }

    // INSERT a relation into this form
    public void insert(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (ind.used() && ind.attrDefined())
            ind = (Individual)ind.convert(this.ofSort());
        if (this.associate() != null)
            ((Relation)ind).setAssociate(this.associate());
        try {
            RelationalForm assoc = this.associateForm((Relation)ind);
            Marker pebble = assoc.elements.getMarker();
            assoc.toBegin();
            assoc.elements.insertInto(ind);
            ind.addUse();
            assoc.elements.returnTo(pebble);
        } catch (UnresolvedReferenceException e) {
            this.unresolved++;
            Relation.addUnresolved(((Property)ind).unresolved(), this);
        }
        this.elements.insert(ind);
        ind.addUse();
    }

    // INSERT a relation INTO this form, at the appropriate location
    // the lead position is maintained
    public void insertInto(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (ind.used() && ind.attrDefined())
            ind = (Individual)ind.convert(this.ofSort());
        if (this.associate() != null)
            ((Relation)ind).setAssociate(this.associate());
        try {
            RelationalForm assoc = this.associateForm((Relation)ind);
            Marker pebble = assoc.elements.getMarker();
            assoc.toBegin();
            assoc.elements.insertInto(ind);
            ind.addUse();
            assoc.elements.returnTo(pebble);
        } catch (UnresolvedReferenceException e) {
            this.unresolved++;
            Relation.addUnresolved(((Property)ind).unresolved(), this);
        }
        this.elements.insertInto(ind);
        ind.addUse();
    }

    // APPEND a relation to this form
    public void append(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (this.isMaximal() && !this.elements.empty() && !ind.greaterThan(this.last()))
            this.maximal = false;
        if (ind.used() && ind.attrDefined())
            ind = (Individual)ind.convert(this.ofSort());
        if (this.associate() != null)
            ((Relation)ind).setAssociate(this.associate());
        try {
            RelationalForm assoc = this.associateForm((Relation)ind);
            Marker pebble = this.elements.getMarker();
            assoc.toBegin();
            assoc.elements.insertInto(ind);
            ind.addUse();
            this.elements.returnTo(pebble);
        } catch (UnresolvedReferenceException e) {
            this.unresolved++;
            Relation.addUnresolved(((Property)ind).unresolved(), this);
            // System.out.println("unresolved reference " + ((Property) ind).unresolved().getKey());
        }
        this.elements.append(ind);
        ind.addUse();
    }

    // DELETE a relation from this form
    public void delete() {
        try {
            RelationalForm assoc = this.associateForm(this.currentRel());
            Marker pebble = assoc.elements.getMarker();
            assoc.toBegin();
            while (!assoc.beyond() && (this.current().compare(assoc.current()) != EQUAL))
                assoc.toNext();
            if (assoc.beyond())
                throw new InconsistentFormException("Inconsistency in relational form");
            assoc.current().delUse();
            assoc.elements.delete();
            assoc.elements.returnTo(pebble);
        } catch (UnresolvedReferenceException e) {
            this.unresolved--;
            Relation.removeUnresolved(((Property)this.current()).unresolved(), this);
        }
        this.current().delUse();
        this.elements.delete();
    }

    // PURGE a relational form
    public void purge() {
        this.toBegin();
        while (!this.elements.empty()) {
            Element current = this.current();
            this.delete();
            if (!current.used()) current.purge();
        }
        if (this.unresolved != 0)
            throw new InconsistentFormException("Inconsistency in relational form");
    }

    // Form interface methods
    // MAXIMALIZE a relational form
    public void maximalize() {
        if (this.isMaximal()) return;
        this.prepare();
        if (this.elements.empty()) return;
        this.toBegin();
        while (!this.elements.atEnd()) {
            if (this.current().compare(this.next()) == EQUAL) {
                if (this.nextRel().attrDefined())
                    this.nextRel().attribute().sum(this.currentRel().attribute());
                this.delete();
            } else {
                if (this.currentRel().attrDefined())
                    this.currentRel().attribute().maximalize();
                this.toNext();
            }
        }
    }

    // returns the SUM of two relational forms
    public boolean sum(Form form) {
        if (this == form) return (!this.elements.empty());
        this.prepare(form);
        RelationalForm other = (RelationalForm)form;
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom(other);
                    break;
                case EQUAL:
                    if (this.currentRel().attrDefined())
                        this.currentRel().attribute().sum(other.currentRel().attribute());
                    this.toNext();
                    other.delete();
                    break;
                default:
            }
        }
        if (!other.elements.empty())
            this.concatenate(other);
        return (!this.elements.empty());
    }

    // returns the DIFFERENCE of two relational forms
    public boolean difference(Form form) {
        if (this == form)
            throw new IllegalArgumentException("Arguments are identical");
        this.prepare(form);
        RelationalForm other = (RelationalForm)form;
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
                    if (this.currentRel().attrDefined() &&
                        this.currentRel().attribute().difference(other.currentRel().attribute()))
                            this.toNext();
                    else
                        this.delete();
                    other.toNext();
                    break;
            }
        }
        return (!this.elements.empty());
    }

    // returns the PRODUCT of two relational forms
    public boolean product(Form form) {
        if (this == form) return (!this.elements.empty());
        this.prepare(form);
        RelationalForm other = (RelationalForm)form;
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
                    if (this.currentRel().attrDefined() &&
                        !this.currentRel().attribute().product(other.currentRel().attribute()))
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

    // returns the SYMmetric DIFFERENCE of two relational forms
    public boolean symdifference(Form form) {
        if (this == form) {
            this.purge();
            return false;
        }
        this.prepare(form);
        RelationalForm other = (RelationalForm)form;
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom(other);
                    break;
                case EQUAL:
                    if (!this.currentRel().attrDefined() ||
                        !this.currentRel().attribute().symdifference(other.currentRel().attribute()))
                            this.delete();
                    else
                        this.toNext();
                    other.delete();
            }
        }
        if (!other.elements.empty()) this.concatenate(other);
        return (!this.elements.empty());
    }

    // PARTITION two relational forms with respect to one another
    public boolean partition(Form form1, Form form2) {
        if (this == form1) {
            ((RelationalForm)form2).concatenate(this);
            return false;
        }
        this.prepare(form1, form2);
        RelationalForm other = (RelationalForm)form1;
        RelationalForm common = (RelationalForm)form2;
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
                    Individual ind = (Individual)this.currentRel().convert(this.current().ofSort());
                    if (this.currentRel().attrDefined()) {
                        this.currentRel().attribute().partition(other.currentRel().attribute(), ind.attribute());
                        if (this.currentRel().attribute().nil())
                            this.delete();
                        else
                            this.toNext();
                        if (other.currentRel().attribute().nil())
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

    // checks if one relational form is a PART OF another relational form
    public boolean partOf(Form form) {
        if (this == form) return true;
        this.prepare(form);
        RelationalForm other = (RelationalForm)form;
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
                    if (this.currentRel().attrDefined() &&
                        !this.currentRel().attribute().partOf(other.currentRel().attribute()))
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
        if (ind.used()) ind = (Individual)ind.convert(this.ofSort());
        if (this.associate() != null)
            ((Relation)ind).setAssociate(this.associate());
        this.toBegin();
        while (!this.beyond())
            switch (this.current().compare(ind)) {
                case LESS:
                    this.toNext();
                    break;
                case EQUAL:
                    if (this.currentRel().attrDefined())
                        this.currentRel().attribute().sum(ind.attribute());
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
                ((Property)this.current()).getAssociate(ind);
                break;
            } catch (IllegalArgumentException e) {
                this.toNext();
            } catch (UnresolvedReferenceException e) {
                this.toNext();
            }
        if (this.beyond())
            throw new InconsistentFormException("Relation to resolve is not found");
        try {
            RelationalForm assoc = this.associateForm(this.currentRel());
            assoc.toBegin();
            assoc.elements.insertInto(this.currentRel().duplicate());
        } catch (UnresolvedReferenceException e) {
            throw new InconsistentFormException("resolve");
        }
        if ((!this.elements.atBegin() && this.previous().greaterThan(this.current())) ||
            (!this.elements.atEnd() && this.next().lessThan(this.current())))
                this.elements.forceSorting();
    }

    public Form convert(Sort sort, Element assoc) throws IllegalArgumentException {
        MultiplyForm result = (MultiplyForm)sort.newForm();
        if (assoc != null) result.setAssociate(assoc);
        this.toBegin();
        while (!this.beyond()) {
            Individual ind = (Individual)((Individual)this.current()).convert(sort);
            if (assoc != null) ((Relation)ind).setAssociate((Individual)assoc);
            result.append(ind);
            this.toNext();
        }
        return result;
    }
}
