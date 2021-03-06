/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `List.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  The LIST class extends the LinkedList class to allow for
//  sorted lists. It extends its implementation of the List interface.
//  It provides sort and merge methods as well as a list print method.
//  It uses the greaterThan comparison method on things
//  for the purpose of sorting.
//  Extra features:
//  When a thing is inserted into a list, the SortedList methods
//  will check if the list remains sorted or not.

package blue.struct;

import blue.Thing;

public final class List extends LinkedList {
    // representation
    private boolean sorted;

    // constructor
    public List() {
        super();
        sorted = true;
    }

    // access methods
    public Thing current() throws ListOutOfBoundsException {
        return (Thing)this.currentObject();
    }

    public Thing next() throws ListOutOfBoundsException {
        return (Thing)this.nextObject();
    }

    public Thing previous() throws ListOutOfBoundsException {
        return (Thing)this.previousObject();
    }

    public Thing first() throws ListOutOfBoundsException {
        return (Thing)this.firstObject();
    }

    public Thing last() throws ListOutOfBoundsException {
        return (Thing)this.lastObject();
    }

    // methods
    // check is this list is SORTED
    public boolean sorted() {
        return this.sorted;
    }

    // FORCE this list to be SORTED next time
    public void forceSorting() {
        this.sorted = false;
    }

    // APPEND the thing to the back of the list
    // set sorted to false if necessary
    public void append(Thing thing) {
        if (!this.empty() && !thing.greaterOrEqual(this.last()))
            this.sorted = false;
        super.append(thing);
    }

    // INSERT the thing before the lead
    // set sorted to false if necessary
    public void insert(Thing thing) {
        if ((!this.atBegin() && !thing.greaterOrEqual(this.previous())) ||
            (!this.beyond() && !thing.lessOrEqual(this.current())))
                this.sorted = false;
        super.insert(thing);
    }

    // INSERT the thing INTO this list at the appropriate location
    // The thing is never inserted before the previous to the lead
    // and the lead is remembered
    public void insertInto(Thing thing) {
        this.sort();
        Marker pebble = this.getMarker();
        while (!this.beyond() && this.current().lessThan(thing))
            this.toNext();
        super.insert(thing);
        this.returnTo(pebble);
    }

    // INSERT lead node FROM other list into this list at lead
    // If lead node equals null, set lead to first node
    // set sorted to false if necessary
    public void insertFrom(List other) {
        if (other.empty()) return;
        if (other.beyond()) other.toBegin();
        if ((!this.atBegin() && !other.current().greaterOrEqual(this.previous())) ||
            (!this.beyond() && !other.current().lessOrEqual(this.current())))
                this.sorted = false;
        super.insertFrom(other);
    }

    private void superInsertFrom(List other) {
        super.insertFrom(other);
    }

    // DELETE the lead node from the list
    // set sorted to true if empty
    public void delete() {
        super.delete();
        if (this.empty()) this.sorted = true;
    }

    // PURGE this list
    // set sorted to true
    public void purge() {
        super.purge();
        this.sorted = true;
    }

    // CONCATENATE the specified list to the back of this list,
    // the specified list is purged
    // set sorted to false if necessary
    public void concatenate(List other) {
        if (other.empty()) return;
        if (!other.sorted || (!this.empty() && !other.first().greaterOrEqual(this.last())))
            this.sorted = false;
        super.concatenate(other);
    }

    private void superConcatenate(List other) {
        super.concatenate(other);
    }

    // check if this list EQUALS the other list
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof List)) return false;
        return this.equals((List)other);
    }

    public boolean equals(List other) {
        if (!this.sorted) this.sort();
        if (!other.sorted) other.sort();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond() && this.current().equals(other.current())) {
            this.toNext();
            other.toNext();
        }
        return (this.beyond() && other.beyond());
    }

    /*
    // COMPARE two lists for the purpose of sorting
    // returns one of EQUAL, LESS, or GREATER
    public int compare(Thing other) {
	if (!(other instanceof List)) return FAILED;
	return this.compare((List) other);
    }
    private int compare(List other) {
	int c;

	if (!this.sorted) this.sort();
	if (!other.sorted) other.sort();

	this.toBegin();
	other.toBegin();
	while (!this.beyond() && !other.beyond()) {
	    c = this.current().compare(other.current());
	    if (c != EQUAL) return c;
	    this.toNext();
	    other.toNext();
	}
	if (this.beyond() && other.beyond()) return EQUAL;
	if (this.beyond()) return LESS;
	return GREATER;
    }
    */

    // check whether this list CONTAINS the specified thing
    public boolean contains(Thing thing) {
        if (!this.sorted) this.sort();
        this.toBegin();
        while (!this.beyond())
            switch (thing.compare(this.current())) {
                case Thing.LESS:
                    return false;
                case Thing.EQUAL:
                    return true;
                case Thing.GREATER:
                    this.toNext();
                    break;
            }
        return false;
    }

    // DUPLICATE the other list into this list
    public void duplicate(List other) {
        other.toBegin();
        while (!other.beyond()) {
            super.append(other.currentObject());
            other.toNext();
        }
        if (other.sorted && (this.length() == other.length()))
            this.sorted = true;
        else if (this.length() != other.length())
            this.sorted = false;
    }

    // MERGE the other list with this list
    public void merge(List other) {
        if (!this.sorted) this.sort();
        if (!other.sorted) other.sort();
        this.superMerge(other);
    }

    private void superMerge(List other) {
        if (!other.empty()) {
            if (this.empty())
                super.concatenate(other);
            else {
                this.toBegin();
                other.toBegin();
                while (!this.beyond() && !other.empty()) {
                    if (this.current().greaterThan(other.current()))
                        super.insertFrom(other);
                    else
                        this.toNext();
                }
                if (!other.empty())
                    super.concatenate(other);
            }
        }
    }

    // SORT this list
    public void sort() {
        if (this.sorted) return;
        if (this.length() > 1) {
            List array[], current;
            int n;
            array = new List[(int)(Math.ceil(Math.log((double)this.length()) / Math.log(2.0)))];
            for (n = 0; n < array.length; n++)
                array[n] = new List();
            current = new List();
            while (this.length() > 1) {
                this.toBegin();
                current.superInsertFrom(this);
                current.addtobinomialcomb(array);
            }
            for (n = 0; n < array.length; n++)
                this.superMerge(array[n]);
        }
        this.sorted = true;
    }

    private void addtobinomialcomb(List array[]) {
        int n;
        for (n = 0; ; n++) {
            if (array[n].empty()) {
                array[n].superConcatenate(this);
                break;
            } else
                this.superMerge(array[n]);
        }
    }

    // REDUCE this list by removing all duplicate elements
    public void reduce() {
        if (this.empty()) return;
        if (!this.sorted) this.sort();
        this.toBegin();
        while (!this.atEnd()) {
            if (this.currentObject().equals(this.nextObject()))
                super.delete();
            else
                this.toNext();
        }
        if (this.empty()) this.sorted = true;
    }

    public String toString() {
        String result = "{";
        if (!this.empty()) {
            Marker pebble = this.getMarker();
            this.toBegin();
            result += this.currentObject().toString();
            while (!this.atEnd()) {
                this.toNext();
                result += ", " + this.currentObject().toString();
            }
            this.returnTo(pebble);
        }
        return result + '}';
    }
}
