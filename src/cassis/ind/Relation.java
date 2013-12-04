/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Relation.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.util.Vector;
import java.util.Hashtable;

import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.Sorts;

/**
 * A <b>relation</b> is an abstract relationship between two individuals, denoted
 * associates wrt to the relation. If a relation belongs to an
 * {@link cassis.ind.Individual#attribute} form, the associate to this attribute form
 * must be one of both associate individuals. A relation is itself an individual of
 * an {@link cassis.sort.AspectsSort}.
 * <p>
 * The <b>Relation</b> class defines a framework for relational characteristic
 * individuals. A relation has no representation but specifies additional methods
 * for describing and parsing relations wrt one of the associates.
 * The <tt>Relation</tt> class also provides methods for storing and maintaining
 * unresolved relations, that is, relations of which one of the associated
 * individuals is only known by its reference key.
 */
public abstract class Relation extends Individual implements Resolvable {

    // constants
    private static final Hashtable unresolved = new Hashtable();

    // constructors

    /**
     * Constructs a nondescript <b>Relation</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * subclass object.
     */
    Relation() {
	super();
    }
    /**
     * Constructs a nondescript <b>Relation</b>. This constructor exists for
     * the purpose of subclassing this class.
     * @param sort a {@link cassis.sort.Sort} object
     */
    Relation(Sort sort) {
	super(sort);
    }

    // reference methods

    /**
     * Constructs a key within the specified keys context, thereby parsing the
     * specified definition and checking it for consistency. If the definition starts
     * with an identifier, the keys context specified by this name is considered
     * instead of the specified keys context. The definition must continue with a '-'
     * followed by the key's base identifier, followed by a '-', and concluding with
     * the key's integer offset.
     * @param context a {@link Keys} object
     * @param reader a {@link cassis.parse.ParseReader} object that presents
     * the key's definition
     * @return the resulting {@link Key} object
     * @throws ParseException if the definition is inconsistent or invalid
     * @see Keys#find
     * @see Keys#generateKey
     */
    static Key parseReference(Keys context, ParseReader reader) throws ParseException {
	if (reader.newToken() == IDENTIFIER) {
	    context = Keys.find(reader.tokenString());
	    if (context == null)
		throw new ParseException(reader, "Context undefined");
	    reader.newToken();
	}
	if (reader.token() != '-')
	    throw new ParseException(reader, "'-' expected");
	if (reader.newToken() != IDENTIFIER)
	    throw new ParseException(reader, "Identifier expected");
	String base = reader.tokenString();
	if (reader.newToken() != '-')
	    throw new ParseException(reader, "'-' expected");
	if (reader.newToken() != NUMBER)
	    throw new ParseException(reader, "Number expected");
	long offset;
	try {
	    offset = Long.parseLong(reader.tokenString());
	} catch (NumberFormatException e) {
	    throw new ParseException(reader, "Integral number expected");
	}
	return context.generateKey(base, offset);
    }

    /**
     * <b>Resolves</b> all (resolvable) entities that were unresolved wrt the
     * specified individual.
     * @param ind an {@link Individual} object
     */
    static void resolved(Individual ind) {
	Vector refs = (Vector) unresolved.remove(ind.getReference());
	if (refs == null) return;
	for (int n = 0; n < refs.size(); n++)
	    ((Resolvable) refs.elementAt(n)).resolve(ind);
	refs.removeAllElements();
    }
    /**
     * Adds this relation to the list of <b>unresolved</b> entities wrt the
     * specified reference key.
     * @param ref a {@link Key} object
     */
    void unresolved(Key ref) {
	Vector refs = (Vector) unresolved.get(ref.getKey());
	if (refs == null) {
	    refs = new Vector();
	    unresolved.put(ref.getKey(), refs);
	}
	refs.addElement(this);
    }

    /**
     * <b>Adds the unresolved</b> data entity to the list of unresolved entities
     * wrt the specified reference key.
     * @param ref a {@link Key} object
     * @param data a {@link Resolvable} object
     */
    public static void addUnresolved(Key ref, Resolvable data) {
	Vector refs = (Vector) unresolved.get(ref.getKey());
	if (refs == null) {
	    refs = new Vector();
	    unresolved.put(ref.getKey(), refs);
	}
	refs.addElement(data);
    }
    /**
     * <b>Removes from the unresolved</b> entities list, wrt the specified reference
     * key, the specified (resolvable) data entity.
     * @param ref a {@link Key} object
     * @param data a {@link Resolvable} object
     */
    public static void removeUnresolved(Key ref, Resolvable data) {
	Vector refs = (Vector) unresolved.get(ref.getKey());
	if (refs != null)
	    refs.removeElement(data);
    }

    // Relation interface methods

    /**
     * <b>Sets an associate</b> of this relation to be the specified individual.
     * @param one an {@link Individual} object
     */
    public abstract void setAssociate(Individual one);

    /**
     * <b>Gets the {@link cassis.sort.Aspect}</b> of this relation's
     * {@link cassis.sort.AspectsSort} that does not equal the specified sort.
     * @param sort a {@link cassis.sort.Sort} object
     * @return the resulting (aspect) sort
     */
    public abstract Sort getAspect(Sort sort);

    /**
     * Constructs a relation of the specified sort and corresponding
     * the specified definition. First, the relation is constructed using the
     * {@link Individual#parseIndividual} method. Next, the relation's value is
     * parsed and assigned using the {@link #parse(cassis.parse.ParseReader, Individual)} method.
     * If the relation's definition is followed by a curly bracketed expression,
     * this is (recursively) parsed as the relation's attribute form.
     * @param sort a {@link cassis.sort.Sort} object
     * @param reader a {@link cassis.parse.ParseReader} object that expresses
     * the relation's definition
     * @param assoc an (associated) individual
     * @return the resulting (relational) individual
     * @throws ParseException if the definition is inconsistent with the
     * specified sort or is invalid
     * @see cassis.form.Form#parse
     */
    public static Individual parse(Sort sort, ParseReader reader, Individual assoc) throws ParseException {
	Relation result = (Relation) Individual.parseIndividual(sort, reader);
	result.parse(reader, assoc);
	if (reader.previewToken() == '{')
	    result.attribute().parse(reader);
	return result;
    }
    /**
     * <b>Parses</b> the specified reader and assigns the resulting value to this
     * relation.
     * @param reader a {@link cassis.parse.ParseReader} object that expresses
     * the relation's value
     * @param assoc an (associated) individual
     * @throws ParseException if the reader's expression is invalid
     */
    abstract void parse(ParseReader reader, Individual assoc) throws ParseException;
}
