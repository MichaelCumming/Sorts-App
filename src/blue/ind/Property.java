/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Property.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 03.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  The PROPERTY class specifies the characteristic individual for
//  properties.
//  It extends on the Individual class and implements its methods
//  (see `Individual.java'). A property is represented as a pair
//  of individuals.

package blue.ind;

import blue.*;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.sort.SimpleSort;
import blue.sort.Aspect;
import blue.form.RelationalForm;

public class Property extends Relation {
    static {
        PrimitiveSort.register(Property.class, RelationalForm.class, Parameter.SORTSLINK);
        proto(Property.class, "icons/property.gif", proto());
    }

    // representation
    private Individual couple[];
    private Key unresolved;

    // constructors
    Property() {
        super();
        this.couple = null;
        this.unresolved = null;
    }

    private Property(Sort sort, Individual couple[], Key reference) {
        super(sort);
        this.couple = couple;
        this.unresolved = reference;
        if (reference != null) this.unresolved(reference);
    }

    public Property(Sort sort, Individual one, Individual two) {
        super(sort);
        if ((one == null) || (two == null))
            throw new IllegalArgumentException("Arguments must be both non-null");
        Aspect aspects[] = ((Aspect)sort).aspects();
        Argument arg = null;
        Sort base = sort.base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        this.couple = new Individual[2];
        if (sort.equals(aspects[0])) {
            if (!one.ofSort().base().equals(((Sort[]) arg.value()) [0]) ||
                !two.ofSort().base().equals(((Sort[]) arg.value()) [1]))
                    throw new IllegalArgumentException("Arguments are not of the appropriate sorts");
            this.couple[0] = one;
            this.couple[1] = two;
        } else {
            if (!one.ofSort().base().equals(((Sort[]) arg.value()) [1]) ||
                !two.ofSort().base().equals(((Sort[]) arg.value()) [0]))
                    throw new IllegalArgumentException("Arguments are not of the appropriate sorts");
            this.couple[1] = one;
            this.couple[0] = two;
        }
        one.referenced();
        two.referenced();
        this.unresolved = null;
    }

    // access methods
    public Key unresolved() { return this.unresolved; }

    public boolean nil() { return (this.couple == null); }

    Sort getAspect(Sort sort) {
        Aspect aspects[] = ((Aspect)this.ofSort().base()).aspects();
        Argument arg = null;
        Sort base = this.ofSort().base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        if (sort.base().equals(((Sort[]) arg.value()) [0]))
            return aspects[0];
        return aspects[1];
    }

    public Individual getAssociate(Individual one) throws UnresolvedReferenceException {
        if (this.unresolved != null)
            throw new UnresolvedReferenceException("unresolved reference " + this.unresolved.getKey());
        if (one.ofSort().equals(this.couple[0].ofSort()) && one.equalValued(this.couple[0]))
            return this.couple[1];
        if (one.ofSort().equals(this.couple[1].ofSort()) && one.equalValued(this.couple[1]))
            return this.couple[0];
        throw new IllegalArgumentException("Argument individual is not an associate");
    }

    public void setAssociate(Individual one) {
        if (this.used())
            throw new IllegalOverwriteException("associate cannot be overwritten");
        if ((this.couple[0] != null) && one.ofSort().equals(this.couple[0].ofSort()) && one.equalValued(this.couple[0])) {
            if ((this.couple[1] != null) && one.ofSort().equals(this.couple[1].ofSort()) && one.equalValued(this.couple[1]))
                throw new AmbiguityException("ambiguous associates");
            this.couple[0] = one;
        } else if ((this.couple[1] != null) && one.ofSort().equals(this.couple[1].ofSort()) && one.equalValued(this.couple[1]))
            this.couple[1] = one;
        else
            throw new IllegalArgumentException("Argument individual is not an associate");
    }

    // Relation interface methods
    // DUPLICATE this property
    public Element duplicate() {
        return new Property(this.ofSort().base(), this.couple, this.unresolved);
    }

    // compare two properties for EQUALITY
    boolean equalValued(Individual other) {
        // System.out.println("Property.equalValued()");
        if ((this.couple[0] != null) && !this.couple[0].equalValued(((Property)other).couple[0])) {
            // System.out.println(this.couple[0].toString());
            // System.out.println(((Property) other).couple[0].toString());
            return false;
        }
        if ((this.couple[1] != null) && !this.couple[1].equalValued(((Property)other).couple[1])) {
            // System.out.println(this.couple[1].toString());
            // System.out.println(((Property) other).couple[1].toString());
            return false;
        }
        if ((this.unresolved != null) && !this.unresolved.equalValued(((Property)other).unresolved)) {
            // System.out.println(this.unresolved.toString());
            // System.out.println(((Property) other).unresolved.toString());
            return false;
        }
        return true;
    }

    // COMPARE two properties for the purpose of sorting
    // returns one of EQUAL, LESS, GREATER, or FAILED
    public int compare(Thing other) {
        if (!(other instanceof Property)) return FAILED;
        if ((this.couple[0] == ((Property)other).couple[0]) && (this.couple[1] == ((Property)other).couple[1]) &&
            (this.unresolved == ((Property)other).unresolved))
                return EQUAL;
        int c;
        if (this.couple[0] != null) {
            if (((Property)other).couple[0] != null)
                c = this.couple[0].compare(((Property)other).couple[0]);
            else
                return LESS;
        } else if (((Property)other).couple[0] != null)
            return GREATER;
        else
            c = this.unresolved.compare(((Property)other).unresolved);
        if (c != EQUAL) return c;
        if (this.couple[1] != null) {
            if (((Property)other).couple[1] != null)
                c = this.couple[1].compare(((Property)other).couple[1]);
            else
                return LESS;
        } else if (((Property)other).couple[1] != null)
            return GREATER;
        else
            c = this.unresolved.compare(((Property)other).unresolved);
        if (c != EQUAL) return c;
        return LESS;
    }

    // PURGE the attribute form of this property,
    // unless this individual is used
    public void purge() {
        if (this.used()) return;
        if (this.unresolved != null)
            Relation.removeUnresolved(this.unresolved, this);
        if (this.attrDefined())
            this.attribute().purge();
    }

    // VALUE convert this property inTO a STRING
    String valueToString(Individual assoc) {
        try {
            return this.getAssociate(assoc).getReference();
        } catch (UnresolvedReferenceException e) {
            return this.unresolved.getKey();
        }
    }

    String valueToString() {
        if (this.unresolved == null)
            return '(' + this.couple[0].getReference() + ", " + this.couple[1].getReference() + ')';
        if (this.couple[0] == null)
            return '(' + this.unresolved.getKey() + ", " + this.couple[1].getReference() + ')';
        return '(' + this.couple[0].getReference() + ", " + this.unresolved.getKey() + ')';
    }

    // SIMPLY write this property to VRML
    void visualizeValue(GraphicsContext gc, Individual assoc) {
        try {
            gc.jump(this.getAssociate(assoc).getReference());
        } catch (UnresolvedReferenceException e) {
            gc.label(this.unresolved.getKey());
        }
    }

    void visualizeValue(GraphicsContext gc) {
        gc.label(this.valueToString());
    }

    static String proto() {
        String str = "PROTO Property [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO PropertyShape [\n field SFVec3f mainTranslation 0.4 0.4 0.4\n field SFColor color 0.1 0.6 0.6\n field MFString value \"\"\n field SFFloat size 0.5 ] {\n";
        str += " Transform { translation IS mainTranslation\n  children [\n   Billboard {\n    children Shape {\n     appearance Appearance {\n      material Material {\n       diffuseColor IS color } }\n";
        str += "     geometry Text {\n      string IS value\n      fontStyle FontStyle {\n       size IS size\n       justify [\"MIDDLE\"] } } }\n    axisOfRotation 0 1 0 }\n";
        str += "   Shape {\n    appearance Appearance {\n     material Material {\n      diffuseColor IS color } }\n    geometry Cone {\n     bottomRadius 0.18\n     height 0.27 } } ] } }\n\n";
        return str;
    }

    public void resolve(Individual ind) {
        if ((this.unresolved == null) || (ind.reference() == null) || !this.unresolved.equals(ind.reference()))
            throw new IllegalArgumentException("Individual does not unresolve reference");
        if (this.couple[0] == null)
            this.couple[0] = ind;
        else
            this.couple[1] = ind;
        this.unresolved = null;
    }

    // PARSE a string and initialize this property to its value
    void parse(ParseReader reader, Individual assoc) throws ParseException {
        if (this.reference() != null)
            throw new ParseException(reader, "Property cannot be referenced");
        Key ref = Relation.parseReference(this.keystore(), reader);
        Individual ind = (Individual)this.keystore().retrieve(ref);
        if (ind == null) {
            this.unresolved = ref;
            this.unresolved(ref);
        }
        Aspect aspects[] = ((Aspect)this.ofSort().base()).aspects();
        this.couple = new Individual[2];
        if (this.ofSort().equals(aspects[0])) {
            this.couple[0] = assoc;
            this.couple[1] = ind;
        } else {
            this.couple[1] = assoc;
            this.couple[0] = ind;
        }
    }

    void parse(ParseReader reader) throws ParseException {
        if (this.reference() != null)
            throw new ParseException(reader, "Property cannot be referenced");
        if (reader.newToken() != '(')
            throw new ParseException(reader, "'(' expected");
        Key ref = Relation.parseReference(this.keystore(), reader);
        Individual one = (Individual)this.keystore().retrieve(ref);
        if (one == null) {
            this.unresolved = ref;
            this.unresolved(ref);
        }
        if (reader.newToken() != ',')
            throw new ParseException(reader, "',' expected");
        ref = Relation.parseReference(this.keystore(), reader);
        Individual two = (Individual)this.keystore().retrieve(ref);
        if (two == null) {
            if (one == null)
                throw new ParseException(reader, "Too many unresolved references");
            this.unresolved = ref;
            this.unresolved(ref);
        }
        if (reader.newToken() != ')')
            throw new ParseException(reader, "')' expected");
        Aspect aspects[] = ((Aspect)this.ofSort()).aspects();
        Argument arg = null;
        Sort base = this.ofSort().base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        this.couple = new Individual[2];
        if (this.ofSort().base().equals(aspects[0])) {
            if (((one != null) && !one.ofSort().base().equals(((Sort[]) arg.value()) [0])) ||
                ((two != null) && !two.ofSort().base().equals(((Sort[]) arg.value()) [1])))
                    throw new ParseException(reader, "Arguments are not of the appropriate sorts");
            this.couple[0] = one;
            this.couple[1] = two;
        } else {
            if (((one != null) && !one.ofSort().base().equals(((Sort[]) arg.value()) [1])) ||
                ((two != null) && !two.ofSort().base().equals(((Sort[]) arg.value()) [0])))
                    throw new ParseException(reader, "Arguments are not of the appropriate sorts");
            this.couple[1] = one;
            this.couple[0] = two;
        }
    }
}
