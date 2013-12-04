/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `PrimitiveSort.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import java.util.Hashtable;
import blue.IllegalOverwriteException;
import blue.struct.Argument;
import blue.struct.Parameter;
import blue.io.*;
import blue.form.Form;

/**
 * A <b>Primitive Sort</b> is a simple {@link Sort} defined by
 * a characteristic individual (see also {@link blue.ind.Individual})
 * and by a template that specifies the algebraic behavior of its {@link blue.form.Form}s, i.e., collections of individuals.
 * A primitive sort also always has a name assigned. <p> The <b>PrimitiveSort</b> class represents a primitive sort
 * additionally by its chacteristic individual class, behavior class, and corresponding arguments.
 * It implements the {@link SimpleSort} and {@blue.io.Parser} interfaces.
 * The definition of a primitive sort specifies the name of the characteristic individual (i.e., the full class name) within
 * square brackets, possibly followed by an argument list separated by comma's and enclosed within parentheses.<br>
 * <tt>definition := name ':' primitive | <br>              name ':' primitive '(' parameters ')'
 * <br>primitive := '[' charind ']' <br>parameters := value | value ',' parameters</tt>
 */
public class PrimitiveSort extends Sort implements SimpleSort, Parser {
    private final static Hashtable categories = new Hashtable();
    private final static Hashtable parameters = new Hashtable();
    // representation
    private Class characteristic;
    private Argument arguments;
    // constructors

    /**
     * Creates a general <b>primitive sort</b> within the specified context.
     * @param context a {@link Sorts} object
     */
    PrimitiveSort(Sorts context) {
        super(context);
        this.characteristic = null;
        this.arguments = null;
        this.setStats(0, 1, 1);
    }

    /**
     * Creates a <b>primitive sort</b> according to the specified definition,
     * within the specified context and with the specified name. This definition does not include the name assignment.
     * @param context a {@link Sorts} object
     * @param name a <tt>String</tt> object
     * @param expression a {@link blue.io.ParseReader} object that presents the sort's definition
     * @throws ParseException if the reader does not present a proper definition
     */
    PrimitiveSort(Sorts context, String name, ParseReader expression) throws ParseException {
        this(context);
        Parameter parameter = characteristic(expression);
        if (this.characteristic() == blue.form.RelationalForm.class)
            throw new ParseException(expression, "This characteristic individual specifies multiple Aspects");
        expression.newToken();
        if (parameter != Parameter.NONE)
            this.arguments(context, parameter, expression);
        if (expression.token() == '(')
            throw new ParseException(expression, "This characteristic individual allows no arguments");
        this.assign(name);
    }
    // parse methods

    /**
     * Parse the <b>characteristic</b> individual's class name.
     * @param expression a {@link blue.io.ParseReader} object that presents the class name within square brackets
     * @throws ParseException if the reader does not present a proper class name
     */
    Parameter characteristic(ParseReader expression) throws ParseException {
        if (expression.token() != '[')
            throw new ParseException(expression, "'[' expected");
        if (expression.newToken() != IDENTIFIER)
            throw new ParseException(expression, "Expected an identifier");
        String name = expression.tokenString();
        try {
            this.characteristic = Class.forName("blue.ind." + name);
        } catch (ClassNotFoundException e) {
            System.out.println("blue.ind." + name);
            throw new ParseException(expression, "This characteristic individual is not recognized");
        }
        if (expression.newToken() != ']')
            throw new ParseException(expression, "']' expected");
        this.canonical = this.definition = '[' + name + ']';
        if (categories.get(this.characteristic) == null)
            throw new ParseException(expression, "This characteristic individual is not recognized");
        return (Parameter)parameters.get(this.characteristic);
    }

    /**
     * Parse an <b>argument</b> list.
     * @param expression a {@link blue.io.ParseReader} object that presents an argument list
     * @throws ParseException if the reader does not present a proper argument list
     * @see blue.struct.Parameter
     */
    void arguments(Sorts context, Parameter parameter, ParseReader expression) throws ParseException {
        if (parameter == Parameter.NONE)
            throw new ParseException(expression, "This characteristic individual allows no arguments");
        if (expression.token() != '(') {
            if (parameter.optional()) return;
            throw new ParseException(expression, "Expected one or more arguments enclosed with parentheses");
        }
        if (parameter == Parameter.IDENTIFIER) {
            if (expression.newToken() != IDENTIFIER)
                throw new ParseException(expression, "Expected an identifier argument");
            String name = expression.tokenString();
            this.arguments = parameter.argument(name);
            this.definition += " (" + name + ')';
        } else if (parameter == Parameter.UPPERBOUND) {
            if (expression.newToken() != NUMBER)
                throw new ParseException(expression, "Expected a numeric argument");
            Double value = Double.valueOf(expression.tokenString());
            this.arguments = parameter.argument(value);
            this.definition += " (" + value.toString() + ')';
        } else if (parameter == Parameter.LOCALE) {
            if (expression.newToken() != IDENTIFIER)
                throw new ParseException(expression, "Expected an identifier argument");
            String codes[] = new String[2];
            codes[0] = expression.tokenString();
            if ((expression.newToken() != ',') || (expression.newToken() != IDENTIFIER))
                throw new ParseException(expression, "Expected another identifier argument, preceded by ','");
            codes[1] = expression.tokenString();
            this.arguments = parameter.argument(codes);
            this.definition += " (" + codes[0] + ", " + codes[1] + ')';
        } else if (parameter == Parameter.SORTSLINK) {
            if (expression.newToken() != IDENTIFIER)
                throw new ParseException(expression, "Expected an identifier argument");
            Sort sorts[] = new Sort[2];
            sorts[0] = context.sortOf(expression.tokenString());
            if ((expression.newToken() != ',') || (expression.newToken() != IDENTIFIER))
                throw new ParseException(expression, "Expected another identifier argument, preceded by ','");
            sorts[1] = context.sortOf(expression.tokenString());
            this.arguments = parameter.argument(sorts);
            this.definition += " (" + sorts[0].name() + ", " + sorts[1].name() + ')';
        }
        if (expression.newToken() == ',')
            throw new ParseException(expression, "Too many arguments specified");
        if (expression.token() != ')')
            throw new ParseException(expression, "')' expected");
        expression.newToken();
        this.canonical = this.definition;
    }
    // access methods

    /**
     * Returns the class specifying the <b>behavioral</b> template corresponding a characteristic individual's class.
     * @return a <tt>Class</tt> object
     */
    static Class behavior(Class characteristic) { return (Class)categories.get(characteristic); }

    /**
     * Returns the class specifying the <b>characteristic</b> individual for this primitive sort.
     * @return a <tt>Class</tt> object
     */
    public Class characteristic() { return this.characteristic; }

    /**
     * Returns the <b>arguments</b> of this primitive sort.
     * @return an {@link blue.struct.Argument} object
     */
    public Argument arguments() { return this.arguments; }
    // methods

    /**
     * <b>Registers</b> a characteristic individual with its behavioral
     * template and parameters. Only a registered characteristic individual can be used to define a primitive sort.
     * @param characteristic a class extending {@link blue.ind.Individual}
     * @param behavior a class extending {@link blue.form.Form}
     * @param parameter a {@link blue.struct.Parameter} object
     */
    public static void register(Class characteristic, Class behavior, Parameter parameter) {
        categories.put(characteristic, behavior);
        parameters.put(characteristic, parameter);
    }

    /**
     * Creates a <b>duplicate</b> of this primitive sort and assigns it
     * the specified name. If this name equals the sort's name, this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
        if (this.name().equals(name)) return this;
        PrimitiveSort result = new PrimitiveSort(this.context());
        result.assign(name);
        result.characteristic = this.characteristic;
        result.definition = this.name();
        result.canonical = this.canonical;
        result.arguments = this.arguments;
        return result;
    }

    /**
     * Creates a <b>new form</b> for this primitive sort.
     * @return a {@link blue.form.Form} corresponding this sort
     * @see #newForm(Sort)
     */
    public Form newForm() { return this.newForm(this); }

    /**
     * Creates a <b>new form</b> for this primitive sort as a form for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * Any exception is caught and a description of this exception written to <tt>System.err</tt>.
     * @param parent a {@link Sort} object
     * @return a {@link blue.form.Form} corresponding this sort
     * @see blue.form.Form#setSort
     */
    Form newForm(Sort sort) {
        try {
            Form form = (Form)behavior(this.characteristic()).newInstance();
            form.setSort(sort);
            return form;
        } catch (Exception e) {
            System.err.println("Sort.newForm(): Form not created");
            System.err.println("Caught " + e.getMessage());
        }
        return null;
    }

    /**
     * <b>Visualize</b> this primitive sort using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public void visualize(GraphicsContext gc) {
        gc.beginGroup(AttributeSort.class, 1);
        gc.label(this.toString());
        gc.endGroup();
    }

    /**
     * <b>Combines</b> this primitive sort with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    Sort combine(Sort other) throws IllegalArgumentException {
        if ((this.context() != other.context()) || (other instanceof AspectsSort))
            throw new IllegalArgumentException("AttributeSort.combine: illegal arguments");
        return new AttributeSort(this, other);
    }

    /**
     * <b>Relates</b> this primitive sort to an aspect and returns the resulting match.
     * If no match has already been stored, the primitive sort is related to the aspect's
     * argument sort and the resulting match stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(Aspect other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = this.relate(other.argument());
        if (result.isIncongruous()) return result;
        result = result.assign(this, other);
        if (result.isSimilar()) result.conversion();
        return Match.none;
    }

    /**
     * <b>Relates</b> this primitive sort to a primitive sort and returns
     * the resulting match. Two primitive sorts are denoted equivalent if their
     * canonical definitions are identical. Two primitive sorts are denoted
     * convertible if these share the same characteristic individual but their arguments differ. Otherwise, no match exists.
     * @param other a {@link PrimitiveSort} object
     * @return a {@link Match} object
     */
    Match relate(PrimitiveSort other) {
        if (this == other)
            return Match.identical(this);
        if (this.canonical == other.canonical)
            return Match.equivalent(this, other);
        if (!this.characteristic.equals(other.characteristic))
            return Match.none;
        return Match.convertible(this, other);
    }

    /**
     * <b>Relates</b> this primitive sort to an attribute sort and returns
     * the resulting match. If no match has already been stored, the attribute sort
     * is instead related to the primitive sort. If a match is found for this, it is reversed then stored.
     * @param other an {@link AttributeSort} object
     * @return a {@link Match} object
     * @see AttributeSort#relate(PrimitiveSort)
     */
    Match relate(AttributeSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }

    /**
     * <b>Relates</b> this primitive sort to a disjunctive sort and returns
     * the resulting match. If no match has already been stored, the disjunctive sort
     * is instead related to the primitive sort. If a match is found for this, it is reversed then stored.
     * @param other a {@link DisjunctiveSort} object
     * @return a {@link Match} object
     * @see DisjunctiveSort#relate(PrimitiveSort)
     */
    Match relate(DisjunctiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }

    /**
     * <b>Relates</b> this primitive sort to a recursive sort and returns
     * the resulting match. If no match has already been stored, the recursive sort
     * is instead related to the primitive sort. If a match is found for this, it is reversed then stored.
     * @param other a {@link RecursiveSort} object
     * @return a {@link Match} object
     * @see RecursiveSort#relate(PrimitiveSort)
     */
    Match relate(RecursiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
}
