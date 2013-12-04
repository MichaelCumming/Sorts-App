/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AuxFunction.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind.function;

import com.eteks.parser.*;

import cassis.Thing;
import cassis.parse.*;

/**
 * An <b>aux(iliary) function</b> serves the definition of an
 * auxiliary function to a {@link cassis.ind.Function}. It specifies
 * the necessary components to compute an intermediate input value to XXX
 * These components are an array of input <i>parameters</i>, a <i>result
 * expression</i> defined over the input parameters, and a parser to parse and
 * compute this expression.
 * For each application to an array of numerical values, the result
 * expression is computed with the input parameters equal to these numerical
 * values.
 * A few auxiliary function definitions are predefined.
 * @see Functions
 */
public class AuxFunction extends Function {
    
    // representation
    private String[] parameters;
    private CompiledFunction result_f;

    // constructors

    /**
     * Constructs an <b>AuxFunction</b> from a function name, an array of
     * parameter names, and a result expression.
     * @param name a <tt>String</tt> object
     * @param parameters a <tt>String</tt> array
     * @param result_x a <tt>String</tt> object
     * @throws IllegalArgumentException if a function or parameter name is not
     * a valid parser identifier
     * @throws CompilationException if the syntax of the result expression is
     * incorrect or not accepted by the parser
     */
    AuxFunction(String name, String[] parameters, String result_x) throws IllegalArgumentException, CompilationException {
        super(name);
        if (parameters.length == 0)
            throw new IllegalArgumentException("Function must take at least one parameter");
        for (int n = 0; n < parameters.length; n++)
            if (!syntax.isValidIdentifier(parameters[n]))
                throw new IllegalArgumentException("Parameter name '" + parameters[n] + "' is not a valid identifier");

        this.parameters = parameters;
        StringBuffer parlist = new StringBuffer(parameters[0]);
        for (int n = 1; n < parameters.length; n++)
            parlist.append(',').append(parameters[n]);
        String fn = "r(" + parlist.toString() + ") = " + result_x;
        // System.out.println(fn);
        this.result_f = parser.compileFunction(fn);
    }

    // access methods

    /**
     * Returns the <b>parameter count</b> for this function definition.
     * @return an integer value
     */
    public int parameterCount() {
        return this.parameters.length;
    }
    
    // functions methods

    /**
     * <b>Applies</b> this auxiliary function to an array of values.
     * Evaluates the result expression, given the specified values as
     * input paremeters, and returns the resulting value.
     * @param values an array of doubles
     * @return a double
     */
    public Object apply(Object[] values) {
        return this.result_f.computeFunction(interpreter, values);
    }

    /**
     * Converts this auxiliary function definition <b>to a string</b>.
     * The result is the function name, followed by the parameter names within
     * parentheses and separated by comma's, the symbol '=' and the result
     * expression. This string can be included in an SDL description
     * and subsequently parsed to reveal the original auxiliary function
     * definition.
     * @return a <tt>String</tt> object
     * @see #parse
     */
     public String toString() {
         StringBuffer result = new StringBuffer(this.name());
         result.append('(').append(this.parameters[0]);
         for (int n = 1; n < this.parameters.length; n++)
             result.append(", ").append(this.parameters[n]);
         String def = this.result_f.getDefinition();
         def = def.substring(def.indexOf('=') + 2);
         result.append(") = ").append(def);
         return result.toString();
     }

    /**
     * Reads an SDL description of an auxiliary function definition from a
     * {@link cassis.parse.ParseReader} object and assigns the resulting
     * specifications to this auxiliary function definition. This description
     * must start with XXX
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe an
     * auxiliary function definition
     * @see #toString()
     */
    void parse(ParseReader reader) throws ParseException {
    }

    // thing interface methods

    /**
     * Compares this auxiliary function definition to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not an
     * auxiliary function definition. Otherwise the result is defined by
     * comparing the auxiliary function definition names, array lengths,
     * parameter names, and result expressions, in that order.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof AuxFunction)) return FAILED;
	int c = this.name().compareTo(((Function) other).name());
	if (c == 0) c = this.parameters.length - ((AuxFunction) other).parameters.length;
        if (c == 0)
            for (int n = 0; (c == 0) && (n < this.parameters.length); n++)
                c = this.parameters[n].compareTo(((AuxFunction) other).parameters[n]);
	if (c == 0) c = this.result_f.getDefinition().compareTo(((AuxFunction) other).result_f.getDefinition());
	return (c < 0) ? LESS : ((c > 0) ? GREATER : EQUAL);
    }
}
