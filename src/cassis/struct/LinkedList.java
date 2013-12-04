/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `LinkedList.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A LIST is a doubly-linked list of objects.

package cassis.struct;

//  A LISTNODE is the nodal unit of a doubly-linked list.
//  It contains references to the previous and next listnodes.

final class ListNode implements Marker {

    // representation
    private Object content;
    private ListNode next, prev;

    // constructors

    ListNode(Object el) {
	this.prev = null;
	this.next = null;
	this.content = el;
    }

    ListNode(Object el, ListNode next) {
	this(el);
	if (next != null) next.prepend(this);
    }

    ListNode(ListNode prev, Object el) {
	this(el);
	if (prev != null) prev.append(this);
    }

    // access methods

    Object content() { return this.content; }

    ListNode next() { return this.next; }

    ListNode previous() { return this.prev; }

    // methods

    // PREPEND the specified node before this node
    void prepend(ListNode prev) {
	if (prev == null) return;

	prev.next = this;
	prev.prev = this.prev;
	if (this.prev != null) this.prev.next = prev;
	this.prev = prev;
    }

    // APPEND the specified node after this node
    void append(ListNode next) {
	if (next == null) return;

	next.prev = this;
	next.next = this.next;
	if (this.next != null) this.next.prev = next;
	this.next = next;
    }

    // LINK the specified node after this node
    void link(ListNode next) {
	if (next == null) return;

	this.next = next;
	next.prev = this;
    }

    // DELETE this node from its list and return the next node
    ListNode delete() {
	if (this.prev != null)
	    this.prev.next = this.next;
	if (this.next != null)
	    this.next.prev = this.prev;
	return this.next;
    }

    // CLEAN the links of this node
    void clean() {
	this.prev = null;
	this.next = null;
    }
}

//  The LINKED-LIST class implements a doubly-linked list with build-in
//  lead index. The build-in lead allows to hide the implementation
//  of the node, that is, no access to a single node is ever required.
//  The lead can be placed and moved anywhere within the list,
//  and the contents of the current lead node can be requested.
//  The insert and delete methods also use the lead to specify the position.
//  However, the prepend and append methods do not use or alter
//  the lead position.

public abstract class LinkedList {

    // representation
    private ListNode first, last, lead;
    private int length;

    // constructor

    LinkedList() { this.clear(); }

    // lead access methods

    public void toBegin() { this.lead = this.first; }
    public void toEnd() { this.lead = this.last; }
    public void toNext() { if (this.lead != null) this.lead = this.lead.next(); }
    public void toPrev() {
	if (this.lead == null)
	    this.lead = this.last;
	else this.lead = this.lead.previous();
    }

    public boolean atBegin() { return (this.lead == this.first); }
    public boolean atEnd() { return (this.lead == this.last); }
    public boolean beyond() { return (this.lead == null); }
    public Marker getMarker() { return this.lead; }

    Object currentObject() throws ListOutOfBoundsException {
	if (this.lead == null)
	    throw new ListOutOfBoundsException("Attempt to read beyond the list");
	return this.lead.content();
    }

    Object nextObject() throws ListOutOfBoundsException {
	if (this.lead == this.last || this.lead == null)
	    throw new ListOutOfBoundsException("Attempt to read beyond end of list");
	return this.lead.next().content();
    }

    Object previousObject() throws ListOutOfBoundsException {
	if (this.lead == this.first)
	    throw new ListOutOfBoundsException("Attempt to read beyond begin of list");
	if (this.lead == null)
	    return this.last.content();
	return this.lead.previous().content();
    }

    Object firstObject() throws ListOutOfBoundsException {
	if (this.first == null)
	    throw new ListOutOfBoundsException("Attempt to read an empty list");
	return this.first.content();
    }

    Object lastObject() throws ListOutOfBoundsException {
	if (this.last == null)
	    throw new ListOutOfBoundsException("Attempt to read an empty list");
	return this.last.content();
    }

    public boolean empty() { return (this.length == 0); }
    public int length() { return this.length; }

    public void returnTo(Marker lead) throws IllegalArgumentException {
	if (lead == null)
	      { this.lead = null; return; }
	if (!(lead instanceof ListNode))
	    throw new IllegalArgumentException("Marker of incorrect type");
	ListNode tmp = this.first;
	while ((tmp != (ListNode) lead) && (tmp != null))
	    tmp = tmp.next();
	if (tmp != null)
	    this.lead = tmp;
    }

    // List interface methods

    // PREPEND the object to the front of the list
    private void prepend(Object object) {
	this.first = new ListNode(object, this.first);
	if (this.last == null) this.last = this.first;
	this.length++;
    }

    // APPEND the object to the back of the list
    public void append(Object object) {
	this.last = new ListNode(this.last, object);
	if (this.first == null) this.first = this.last;
	this.length++;
    }

    // INSERT the object before the lead
    public void insert(Object object) {
	if (this.lead == this.first) {
	    this.first = new ListNode(object, this.first);
	    if (this.last == null) this.last = this.first;
	} else if (this.lead == null)
	    this.last = new ListNode(this.last, object);
	else
	    this.lead.prepend(new ListNode(object));
	this.length++;
    }

    // INSERT lead node FROM other list into this list at lead
    // If lead node equals null, set lead to first node
    public void insertFrom(LinkedList other) {
	if (other.empty()) return;
	if (other.beyond()) other.toBegin();
	ListNode node = other.lead;
	other.deleteLead();
	if (this.empty()) {
	    this.first = node;
	    this.last = node;
	    node.clean();
	} else if (this.lead == null)
	    this.lead.append(node);
	else {
	     if (this.lead == this.first)
		 this.first = node;
	     this.lead.prepend(node);
	 }
	this.length++;
    }
/*
    // EXTRACT a object from the front of the list
    Object extract() throws ListOutOfBoundsException {
	if (this.empty())
	    throw new ListOutOfBoundsException("Attempt to extract from empty list");
	Object object = this.first.content();
	this.first = this.first.delete();
	if (this.empty()) this.last = null;
	this.length--;
	return object;
    }
*/
    // DELETE the lead node from the list
    public void delete() { this.deleteLead(); }
    private void deleteLead() {
	if (this.lead == null) return;

	if (this.lead == this.first)
	    this.first = this.lead.next();
	if (this.lead == this.last)
	    this.last = this.lead.previous();
	this.lead = this.lead.delete();
	this.length--;
    }

    // DELETE the NEXT to lead node from the list
    public void deleteNext() {
	if ((this.lead == null) || (this.lead == this.last)) return;

	if (this.lead == this.last.previous())
	    this.last = this.lead;
	this.lead.next().delete();
	this.length--;
    }

    // CONCATENATE the specified list to the back of this list,
    // the specified list is cleared
    public void concatenate(LinkedList other) {
	if (other.empty()) return;

	if (this.empty())
	    this.first = other.first;
	else
	    this.last.link(other.first);
	this.last = other.last;
	this.length += other.length();
	other.clear();
    }

    // CLEAR this list
    private void clear() {
	this.first = null;
	this.last = null;
	this.lead = null;
	this.length = 0;
    }

    // PURGE this list
    public void purge() {
	this.lead = this.first;
	while (!this.empty()) this.delete();
    }
}
