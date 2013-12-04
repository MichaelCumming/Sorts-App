/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Individual.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 07.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.*;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.sort.*;
import blue.form.Form;
import blue.io.*;

/**
 * An <b>individual</b> is the representational unit of a {@link blue.sort.Sort}.
 * An <i>individual</i> specifies a data element with a single value; individuals
 * may be collected into {@link blue.form.Form}s. The value an individual may hold is dependent on the sort it belongs to.
 * Every {@link blue.sort.PrimitiveSort} is defined by its characteristic
 * individual, i.e., a representational abstraction of all individuals of the sort,
 * and by a description of the algebraic behavior of its forms. <p>
 * The <b>Individual</b> class is an abstract class specifying the common framework
 * for all characteristic individual classes. An instance of any characteristic
 * individual class defines an individual. Every individual belongs to a
 * {@link blue.sort.PrimitiveSort}; the individual is necessarily an instance of the sort's characteristic individual class.
 * An individual is an {@link blue.Element} with an optional reference {@link Key}
 * and an optional attribute {@link blue.form.Form}. The refernce key serves to
 * relate individuals. The optional attribute form must be consistent with the sort this individual belongs to.
 */
public abstract class Individual extends Element implements Parser {
    // constants

    /**
     * A constant for representing a <b>nil</b> individual. The condition upon which
     * an individual is nil is dependent on the sort this element belongs to.
     */
    static final String NIL = "nil";
    private static final char REFERENCE = '#';
    // representation
    private Form attribute;
    private Key key;
    private boolean referenced;
    private Keys keystore;
    // constructor

    /** Constructs a general <b>individual</b>. */
    Individual() {
        super(null);
        this.attribute = null;
        this.key = null;
        this.referenced = false;
        this.keystore = null;
    }

    /**
     * Constructs a general <b>individual</b> and specifies the sort it belongs to.
     * If this sort is an {@link blue.sort.AttributeSort}, then a form is created
     * for the weight sort of this attribute sort and assigned as an attribute to this individual.
     * @param sort a {@link blue.sort.Sort} object
     * @throws IllegalArgumentException if the specified sort's individual characteristic class is not equal to this class
     * @see blue.sort.Sort#newForm()
     * @see #setAttribute
     */
    Individual(Sort sort) throws IllegalArgumentException {
        super(sort);
        Sort base = sort.base();
        if (!(base instanceof SimpleSort) || (((SimpleSort)base).characteristic() != this.getClass()))
            throw new IllegalArgumentException("Sort specifies different characteristic individual");
        if (this.ofSort() instanceof AttributeSort) {
            this.attribute = ((AttributeSort)this.ofSort()).weight().newForm();
            this.attribute.setAssociate(this);
        } else
            this.attribute = null;
        this.key = null;
        this.referenced = false;
        try {
            this.keystore = sort.context().profile().keys();
        } catch (NullPointerException e) {
            this.keystore = null;
        }
    }
    // access methods

    /**
     * Returns the <b>keystore</b> for this individual. This keystore allows for
     * the referencing of individuals. A reference to an individual is represented
     * as a unique key string. These reference keys serve as indexing mechanism for the individuals in the keystore.
     * @return a {@link Keys} object
     */
    public final Keys keystore() { return this.keystore; }

    /**
     * Specifies that this individual is <b>referenced</b> by some other individual.
     * If an individual is referenced, any (SDL or graphical) description of this
     * individual will include this individual's reference key string.
     * @see #getReference
     */
    final void referenced() { this.referenced = true; }

    /**
     * Returns the <b>reference</b> key for this individual.
     * @return a {@link Key} object
     */
    final Key reference() { return this.key; }

    /**
     * <b>Gets a reference</b> key for this individual and returns its string value.
     * If necessary, a reference is first created and stored in the {@link #keystore}.
     * @return a string version of the individuals' reference key.
     * @see Key#getKey
     */
    public final String getReference() {
        this.referenced = true;
        if (this.key == null) this.key = this.keystore.deposit(this);
        return this.key.getKey();
    }

    /**
     * Checks if an <b>attribute is defined</b> for this individual.
     * @return <tt>true</tt> if an attribute is defined, <tt>false</tt> otherwise
     * @see #attribute()
     */
    public final boolean attrDefined() { return (this.attribute != null); }

    /**
     * Returns the <b>attribute</b> form of this individual.
     * @return an {@link blue.form.Form} object
     */
    public final Form attribute() { return this.attribute; }
    // methods

    /**
     * Sets the sort this individual belongs to. This method cannot be used to convert an individual to a different sort.
     * @param sort a sort
     * @exception IllegalOverwriteException if the individual has already been assigned a sort
     */
    protected final void setSort(Sort sort) throws IllegalOverwriteException {
        if (this.ofSort() != null)
            throw new IllegalOverwriteException("Sort already set");
        super.setSort(sort);
    }

    private void overwriteSort(Sort sort) {
        super.setSort(sort);
    }

    /**
     * <b>Sets the attribute</b> form to this individual. This attribute form must be
     * consistent with the individual's sort. Consistency may be achieved in two ways:
     * either the individual's sort is an {@link blue.sort.AttributeSort} and the
     * attribute form belongs to the weight sort of this attribute sort, or the
     * individual's sort is a simple sort and an attribute sort has already been
     * defined as a combination of the individual's sort and the form's sort under
     * the attribute operation. In the latter case, the individual's sort is replaced by the resulting attribute sort.
     * @param form a {@link blue.form.Form} object
     * @throws IllegalOverwriteException if the individual already has a non-nil
     * attribute, if the individual itself belongs to a form, if the specified
     * attribute form is <tt>null</tt>, or if the form is not consistent
     * @see blue.form.Form#setAssociate
     * @see blue.sort.AttributeSort#isDefined
     */
    public final void setAttribute(Form form) throws IllegalOverwriteException {
        if ((this.attribute != null) && !this.attribute.nil())
            throw new IllegalOverwriteException("Attribute form cannot be overwritten");
        if (this.used())
            throw new IllegalOverwriteException("Attribute form cannot be set if individual belongs to a form");
        if (form == null)
            throw new IllegalArgumentException("Attribute form cannot be set to null");
        Sort sort = this.ofSort();
        if (sort instanceof PrimitiveSort) {
            Sort newSort = AttributeSort.isDefined((PrimitiveSort)sort, form.ofSort());
            if (newSort == null)
                throw new IllegalArgumentException("Undefined sort");
            super.setSort(newSort);
        } else if (form.ofSort() != ((AttributeSort)sort).weight())
            throw new IllegalArgumentException("Attribute of erroneous sort");
        form.setAssociate(this);
        this.attribute = form;
    }

    /**
     * <b>Adds an attribute</b> form to this individual. If the individual has no
     * attribute defined, then this form is set to be the attribute form. Otherwise,
     * the form is added to the existing attribute form.
     * @param form a {@link blue.form.Form} object
     * @see #setAttribute(blue.form.Form)
     * @see blue.form.Form#add
     */
    public final void addAttribute(Form form) {
        if (this.attribute == null)
            this.setAttribute(form);
        else
            this.attribute.add(form);
    }

    /**
     * <b>Adds</b> an individual as <b>attribute</b> to this individual.
     * If this individual has no attribute defined, then a form is first created
     * for the specified individual and this form  set to be the attribute form.
     * Next, the specified individual is added to the (existing) attribute form.
     * @param form a {@link blue.form.Form} object
     * @see blue.sort.Sort#newForm()
     * @see #setAttribute(blue.form.Form)
     * @see blue.form.Form#add
     */
    public final void addAttribute(Individual ind) {
        if (this.attribute == null)
            this.setAttribute(ind.ofSort().newForm());
        this.attribute.add(ind);
    }
    // Individual interface methods

    /**
     * Tests whether this individual <b>equals</b> another object.
     * Two individuals are equal if these belong to the same sort, have equal values,
     * and have equal attribute forms (or none at all).
     * @param other the comparison object
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #equalValued
     * @see blue.form.Form#equals
     */
    public final boolean equals(Object other) {
        if (!(other instanceof Individual)) return false;
        if (!this.ofSort().equals(((Individual)other).ofSort())) return false;
        if (!this.equalValued((Individual)other)) return false;
        return ((this.attribute == null) || this.attribute.equals(((Individual)other).attribute));
    }

    /**
     * Tests whether this individual has <b>equal value</b> to another individual.
     * Relies on the {@link #compare} method to achieve this result, by default.
     * @param other the comparison object
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    boolean equalValued(Individual other) {
        return (this.compare(other) == EQUAL);
    }

    /**
     * <b>Purges</b> this individual if it does not currently belong to a form.
     * Purging an individual purges the attribute form of this individual and sets the attribute form to null.
     * @see #used()
     * @see blue.form.Form#purge()
     */
    public void purge() {
        if (this.used() || (this.attribute == null)) return;
        this.attribute.purge();
        this.attribute = null;
    }

    /**
     * Converts this individual <b>to a string</b>. Relies on the {@link #valueToString} method to achieve this result.
     * If the individual is referenced, the reference key string (preceded with a
     * '#' character) is appended to the resulting string (separated with a space).
     * @return a <tt>String</tt> object
     * @see #valueToString()
     * @see #getReference()
     */
    public final String toString() {
        String result = this.valueToString();
        if (this.referenced)
            result = REFERENCE + this.getReference() + ' ' + result;
        return result;
    }

    /**
     * Converts the individual's <b>value to a string</b>.
     * @return a <tt>String</tt> object
     */
    abstract String valueToString();

    /**
     * Builds an SDL description of this individual within the specified context.
     * First, it assigns the individual's sort to the SDL context. Then, it writes
     * the optional reference key string (preceded with a '#' character), followed
     * by the individual's value string. Finally, it (recursively) builds the
     * description of the individual's attribute form, if any.
     * @param sdl an SDL context
     * @see blue.io.SdlContext
     * @see blue.io.SdlContext#setSort
     * @see blue.io.SdlContext#write
     * @see blue.form.Form#print
     */
    public void print(SdlContext sdl) {
        sdl.setSort(this.ofSort());
        if (this.referenced)
            sdl.write(REFERENCE + this.getReference());
        sdl.write(this.valueToString());
        if (this.attribute != null)
            this.attribute.print(sdl);
    }

    /**
     * Builds a graphical description of this individual within the specified context.
     * First, it builds a description of the individidual's value. Next, it registers
     * the optional reference key string. Finally, it (recursively) builds the
     * graphical description of the individual's attribute form, if any.
     * @param gc a graphics context
     * @see blue.io.GraphicsContext
     * @see #visualizeValue
     * @see blue.io.GraphicsContext#register
     * @see blue.form.Form#visualize
     */
    public final void visualize(GraphicsContext gc) {
        this.visualizeValue(gc);
        if (this.referenced)
            gc.register(this.getReference());
        if (this.attrDefined())
            this.attribute().visualize(gc);
    }

    /**
     * Builds a graphical description of this individual's value within the specified context.
     * @param gc a graphics context
     * @see blue.io.GraphicsContext
     */
    abstract void visualizeValue(GraphicsContext gc);

    /**
     * Generates a VRML prototype with the specified icon and definition string for a characteristic individual.
     * @param characteristic a characteristic individual class
     * @param icon an icon path
     * @param def a definition string
     * @see blue.io.vrml#Proto
     */
    final static void proto(Class characteristic, String icon, String def) {
        new blue.io.vrml.Proto(characteristic, icon, def);
    }

    /**
     * Constructs a VRML prototype with the specified icon for a characteristic
     * individual. The definition string is dependent on the name of the characteristic individual
     * @param characteristic a characteristic individual class
     * @param icon an icon path
     * @see blue.io.vrml#Proto
     */
    final static void proto(Class characteristic, String icon) {
        String name = characteristic.getName();
        int index = name.lastIndexOf('.');
        if (index > 0) name = name.substring(index + 1);
        new blue.io.vrml.Proto(characteristic, icon, proto(name));
    }

    private static String proto(String name) {
        String str = "PROTO " + name;
        str += " [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO " + name;
        str += "Shape [\n field SFVec3f mainTranslation 0 0.1 0\n field MFString value \"\"\n field SFFloat size 0.5 ] {\n Transform { translation IS mainTranslation\n  children [\n   Billboard {\n    children [\n";
        str += "     Shape {\n      geometry Text {\n       string IS value\n       fontStyle FontStyle {\n        size IS size\n        justify [\"MIDDLE\"] } } } ]\n    axisOfRotation 0 1 0 } ] } }\n\n";
        return str;
    }

    /**
     * Constructs an individual of the specified sort and corresponding the specified
     * definition. First, the individual is constructed using the
     * {@link #parseIndividual} method. Next, the individual's value is parsed and
     * assigned using the {@link #parse(blue.io.ParseReader)} method.
     * If a reference key is specified for this individual, any references to this
     * individual are resolved. If the individual's definition is followed by a
     * curly bracketed expression, this is (recursively) parsed as the individual's attribute form.
     * @param sort a {@link blue.sort.Sort} object
     * @param s a {@link blue.io.ParseReader} object that presents the individual's definition
     * @return the resulting individual
     * @throws ParseException if the definition is inconsistent with the specified
     * sort or is invalid, or if the optional reference key has already been assigned to a different individual
     * @see Relation#resolved
     * @see blue.form.Form#parse
     */
    public final static Individual parse(Sort sort, ParseReader s) throws ParseException {
        Individual result = Individual.parseIndividual(sort, s), ref = null;
        if ((s.previewToken() == IDENTIFIER) && s.previewString().equals(NIL))
            s.newToken();
        else {
            result.parse(s);
            if (result.key != null) {
                ref = (Individual)result.keystore.retrieve(result.key);
                if (ref == null) {
                    result.referenced = true;
                    Keys.deposit(result, result.key);
                    Relation.resolved(result);
                } else if (!sort.equals(ref.ofSort()) || !result.equalValued(ref))
                    throw new ParseException(s, "Different individuals are assigned the same reference");
            }
        }
        if (s.previewToken() == '{') {
            if (result.attribute == null)
                throw new ParseException(s, "Sort of individual does not allow for attribute form");
            result.attribute.parse(s);
            if ((ref != null) && !result.attribute.equals(ref.attribute)) {
                // System.out.println(result.toString());
                // System.out.println(ref.toString());
                throw new ParseException(s, "Individuals with different attribute forms are assigned the same reference");
            }
        }
        if (ref != null) {
            if (result.attribute != null) result.attribute.purge();
            return ref;
        }
        return result;
    }

    /**
     * Constructs an individual of the specified sort, thereby parsing the specified
     * definition and checking it for consistency. If this definition starts with an
     * identifier enclosed in square brackets, this identifier must name the specified
     * sort's context. If the definition continues with an identifier followed by a
     * colon, this identifier must name the specified sort or its base sort.
     * The individual is constructed as an instance of the specified sort's
     * characteristic individual class. If the specified sort is an attribute sort,
     * then an attribute form is created and assigned correspondingly.
     * If the definition continues with a '#' character, the corresponding reference
     * is parsed and assigned to this individual.
     * @param sort a {@link blue.sort.Sort} object
     * @param s a {@link blue.io.ParseReader} object that presents the individual's definition
     * @return the resulting individual
     * @throws ParseException if the definition is inconsistent with the specified sort
     * @see #setAttribute
     * @see Relation#parseReference
     */
    static Individual parseIndividual(Sort sort, ParseReader s) throws ParseException {
        // user
        if (s.previewToken() == '[') {
            s.newToken();
            if (s.newToken() != IDENTIFIER)
                throw new ParseException(s, "Context identifier expected");
            if (User.find(s.tokenString()) != sort.context().profile())
                throw new ParseException(s, "Explicit and implicit contexts differ");
            if (s.newToken() != ']')
                throw new ParseException(s, "']' expected");
        }
        // sort
        if ((s.previewToken() == IDENTIFIER) && (s.previewTrailer() == ':')) {
            s.newToken();
            String base = s.tokenString();
            if (!base.equals(sort.toString()) && !base.equals(sort.base().toString()))
                throw new ParseException(s, "Explicit and implicit sorts differ");
            s.newToken();
        }
        // individual
        Individual result;
        try {
            result = (Individual)((SimpleSort)sort.base()).characteristic().newInstance();
        } catch (Exception e) {
            throw new ParseException(s, e.getMessage());
        }
        result.setSort(sort);
        if (sort instanceof AttributeSort) {
            result.attribute = ((AttributeSort)sort).weight().newForm();
            result.attribute.setAssociate(result);
        } else if ((sort instanceof RecursiveSort) && (((RecursiveSort)sort).instance() instanceof AttributeSort)) {
            result.attribute = ((AttributeSort)((RecursiveSort)sort).instance()).weight().newForm();
            result.attribute.setAssociate(result);
        }
        result.keystore = sort.context().profile().keys();
        // reference
        if (s.previewToken() == REFERENCE) {
            s.newToken();
            result.key = Relation.parseReference(result.keystore, s);
        }
        return result;
    }

    /**
     * <b>Parses</b> the specified reader and assigns the resulting value to this individual.
     * @params a {@link blue.io.ParseReader} object that expresses the individual's value
     * @throws ParseException if the reader's expression is invalid
     */
    abstract void parse(ParseReader reader) throws ParseException;

    /**
     * <b>Converts</b> this individual to another sort, if both sorts are matched
     * similar. The individual is duplicated and assigned to belong to the new sort.
     * If this new sort is an attribute sort, either the existing attribute form is
     * (recursively) converted, or a new attribute form is created and assigned.
     * @param sort a {@link blue.sort.Sort} object
     * @return an {@link blue.Element} object
     * @throws IllegalArgumentException if both sorts do not match as appropriate
     * @see blue.sort.Match#isLocallySimilar
     * @see blue.form.Form#convert
     */
    public final Element convert(Sort sort) throws IllegalArgumentException {
        Match match = this.ofSort().relate(sort);
        if (match.isLocallySimilar()) {
            Individual result = (Individual)this.duplicate();
            result.overwriteSort(sort);
            if (sort instanceof AttributeSort) {
                if (this.attribute != null)
                    result.attribute = this.attribute.convert(((AttributeSort)sort).weight(), result);
                else
                    result.attribute = ((AttributeSort)sort).weight().newForm();
                result.attribute.setAssociate(result);
            }
            result.keystore = sort.context().profile().keys();
            return result;

/*
	} else if (match.isLocallyConvertible()) {
//		   && (match.localPermutations() == 1)) {
	    Form result = sort.newForm();
	    Form attr = this.attribute();
	    attr.toBegin();
	    while (!attr.beyond()) {
	    }
	    return result;
 */
        }
        //	if (sort.base().characteristic() != this.getClass())
        throw new IllegalArgumentException("Sort specifies different characteristic individual");
    }

    /**
     * Checks if this individual's value <b>contains</b> another individual's value.
     * By default, this is the case if both individuals have equal values.
     * @param other another individual
     * @return <tt>true</tt> if the condition applies, <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean contains(Individual other) {
        return this.equalValued(other);
    }

    /**
     * <b>Combines</b> this individual with another individual, if both values
     * combine into a single value. By default, this is the case if both individuals
     * have equal values. Then, the combination is equal to the first individual.
     * @param other another individual
     * @return <tt>true</tt> if the condition applies, <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean combine(Individual other) {
        return this.equalValued(other);
    }

    /**
     * Determines the <b>common</b> part of this individual with another
     * individual, if one exists. By default, this is the case if both individuals
     * have equal values. Then, the common part is equal to the first individual.
     * @param other another individual
     * @return <tt>true</tt> if the condition applies, <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean common(Individual other) {
        return this.equalValued(other);
    }

    /**
     * Determines the <b>complement</b> part of this individual wrt another
     * individual, if one exists. By default, this is the case if both individuals
     * do not have equal values. Then, the complement is equal to the first individual.
     * @param other another individual
     * @return <tt>true</tt> if the condition applies, <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean complement(Individual other) {
        return (!this.equalValued(other));
    }

    /**
     * Determines the <b>complement</b> parts of this individual wrt another
     * individual, if one exists. By default, this is the case if both individuals
     * do not have equal values. Then, the complement is equal to the first individual.
     * @param other another individual
     * @param result an array of individuals; unaltered
     * @return <tt>true</tt> if the condition applies, <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean complement(Individual other, Individual result[]) {
        return (!this.equalValued(other));
    }
}

/*
	Match match = this.sort.relate(sort);
	if (match.isLocallySimilar()) {
	} else if (match.isLocallyConvertible() &&
		   (match.localPermutations() == 1)) {
		attr = this.current().attribute();
		attr.toBegin();
		while (!attr.beyond()) {
		
	}
*/
