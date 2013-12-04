/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Function.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind.function;

import com.eteks.parser.*;

import cassis.Thing;

/**
 * A <b>function</b> is an expression, or a composition of expressions, over
 * one or more arguments, which can be evaluated when a value is specified for
 * each of the arguments. An expression may contain literals, variable
 * identifiers, operators, constants, conditions and functions.
 * Literals may be any decimal or integer number.
 * Each argument to the function specifies a variable identifier.
 * Operands (and parameters) may be instances of <tt>Double</tt> and
 * <tt>Vector3d</tt>.<br>
 * The syntax of the parser requires all expressions to be written in PASCAL.
 * It supports the following operators, constants and functions:<br>
 * - unary operators : + -<br>
 * - binary operators : ^ XPROD * / MOD + - >= <= > < = <> AND XOR OR
 * (from the highest to the lowest priority)<br>
 * - condition : IF THEN ELSE<br>
 * - constants : PI FALSE TRUE<br>
 * - functions : LN LOG EXP SQR SQRT COS SIN TAN ACOS ASIN ATAN COSH SINH TANH
 * INT ABS OPP NOT MAG<br>
 * Variable identifiers may contain letters, digits, and the character '_'.
 * The first character can't be a digit. This syntax isn't case sensitive. 
 * No type checking is done during the parsing of an expression.
 * <p>
 * The <b>Function</b> class is an abstract class, specifying the syntax to
 * which all function expressions must comply, the parser that is used to parse
 * these function expressions and the interpreter that is used to interpret and
 * evaluate these function expressions. A function is minimally represented by
 * a function name that must be a valid identifier according to a syntax,
 * and a number of parameters.
 * @see Functions
 * @see Functions#Functions
 */
public abstract class Function implements Thing {
    
    // constants
    /**
     * The <b>syntax</b> to which all function expressions must comply.
     */
    static final Syntax syntax = new VectorSyntax();
    /**
     * The <b>parser</b> that is used to parse function expressions.
     */
    static final FunctionParser parser = new FunctionParser(syntax);
    /**
     * The <b>interpreter</b> that is used to interpret and evaluate function
     * expressions.
     */
    static final Interpreter interpreter = new VectorInterpreter();

    // representation
    private String name;

    /*
     * Constructs a <b>Function</b> from a function name.
     * @param name a <tt>String</tt> object
     * @throws IllegalArgumentException if the function name is not a valid
     * identifier according to the syntax.
     */
    public Function(String name) throws IllegalArgumentException {
        if (!syntax.isValidIdentifier(name))
            throw new IllegalArgumentException("Function name is not a valid identifier");
        this.name = name;
    }

    // access methods

    /**
     * Returns the <b>name</b> of this function.
     * @return a <tt>String</tt> object
     */
    public String name() {
        return this.name;
    }

    /**
     * Returns the <b>parameter count</b> of this function.
     * @return an integer value
     */
    public abstract int parameterCount();

    // thing interface methods

    /**
     * Tests whether this function is strictly <b>less than</b> another thing.
     * The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessThan(Thing other) {
	return (this.compare(other) == LESS);
    }
    /**
     * Tests whether this function is strictly <b>greater than</b> another
     * thing. The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterThan(Thing other) {
	return (this.compare(other) == GREATER);
    }
    /**
     * Tests whether this function is <b>less than or equal</b> to another
     * thing. The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == LESS) || (c == EQUAL));
    }
    /**
     * Tests whether this function is <b>greater than or equal</b> to another
     * thing. The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == GREATER) || (c == EQUAL));
    }
}
