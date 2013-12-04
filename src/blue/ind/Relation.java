/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Relation.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import java.util.Vector;
import java.util.Hashtable;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.Sorts;

public abstract class Relation extends Individual implements Resolvable {
    // constants
    private static final Hashtable unresolved = new Hashtable();

    // constructors
    Relation() {
        super();
    }

    Relation(Sort sort) {
        super(sort);
    }

    // reference methods
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

    static void resolved(Individual ind) {
        Vector refs = (Vector)unresolved.remove(ind.getReference());
        if (refs == null) return;
        for (int n = 0; n < refs.size(); n++)
            ((Resolvable)refs.elementAt(n)).resolve(ind);
        refs.removeAllElements();
    }

    void unresolved(Key ref) {
        Vector refs = (Vector)unresolved.get(ref.getKey());
        if (refs == null) {
            refs = new Vector();
            unresolved.put(ref.getKey(), refs);
        }
        refs.addElement(this);
    }

    public static void addUnresolved(Key ref, Resolvable data) {
        Vector refs = (Vector)unresolved.get(ref.getKey());
        if (refs == null) {
            refs = new Vector();
            unresolved.put(ref.getKey(), refs);
        }
        refs.addElement(data);
    }

    public static void removeUnresolved(Key ref, Resolvable data) {
        Vector refs = (Vector)unresolved.get(ref.getKey());
        if (refs != null)
            refs.removeElement(data);
    }

    // Relation interface methods
    public abstract void setAssociate(Individual one);

    // write this relation to a STRING
    public String toString(Individual assoc) {
        String result = this.valueToString(assoc);
        if (this.attrDefined())
            result += ' ' + this.attribute().toString();
        return result;
    }

    abstract String valueToString(Individual assoc);

    // PRINT this relation to SDL
    public void print(SdlContext sdl, Individual assoc) {
        sdl.setSort(this.getAspect(assoc.ofSort()));
        sdl.write(this.valueToString(assoc));
        if (this.attrDefined())
            this.attribute().print(sdl);
    }

    abstract Sort getAspect(Sort sort);

    // WRITE this relation to VRML
    public void visualize(GraphicsContext gc, Individual assoc) {
        this.visualizeValue(gc, assoc);
        if (this.attrDefined())
            this.attribute().visualize(gc);
    }

    abstract void visualizeValue(GraphicsContext vrml, Individual assoc);

    // PARSE a string and initialize this relation to its value
    public static Individual parse(Sort sort, ParseReader reader, Individual assoc) throws ParseException {
        Relation result = (Relation)Individual.parseIndividual(sort, reader);
        result.parse(reader, assoc);
        if (reader.previewToken() == '{')
            result.attribute().parse(reader);
        return result;
    }

    abstract void parse(ParseReader reader, Individual assoc) throws ParseException;
}
