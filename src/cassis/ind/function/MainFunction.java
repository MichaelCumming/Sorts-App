/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MainFunction.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind.function;

import com.eteks.parser.*;

import cassis.Thing;
import cassis.parse.*;

/**
 * A <b>main function</b> serves the definition of
 * a {@link cassis.ind.Function}. It specifies the necessary components
 * to compute that function's result given the values it applies to.
 * These components are an array of input <i>parameters</i>, an array of
 * intermediate <i>variables</i>, an array of <i>initial values</i> for
 * these intermediate variables, an array of <i>application expressions</i>
 * defined over the parameters and variables, one for each intermediate
 * variable, and a <i>result expression</i> defined over the intermediate
 * variables.<br>
 * For each application to an array of numerical values, all application
 * expressions are computed with the input parameters equal to these numerical
 * values. Following, the results of these expressions are assigned to the
 * respective intermediate variables all at once. The result expression
 * computes the final result of this function.
 * A few main-functions are predefined.
 * @see Functions
 */
public class MainFunction extends Function {

    // representation
    private String[] parameters, variables;
    private Object[] initials, intermediates, temps;
    private CompiledFunction[] application_fs;
    private CompiledFunction result_f;

    // constructors

    /**
     * Constructs a <b>MainFunction</b> from a function name, an array of
     * parameter names, an array of variable names, an array of initial
     * variable values, an array of application expressions, and a result
     * expression.
     * @param name a <tt>String</tt> object
     * @param parameters a <tt>String</tt> array
     * @param variables a <tt>String</tt> array
     * @param initials an <tt>Object</tt> array
     * @param application_xs a <tt>String</tt> array
     * @param result_x a <tt>String</tt> object
     * @throws IllegalArgumentException if a function, parameter, or variable
     * name is not a valid parser identifier, or the variable, initial value,
     * and expression arrays have different lengths
     * @throws CompilationException if the syntax of an expression is incorrect
     * or not accepted by the parser
     */
    MainFunction(String name, String[] parameters, String[] variables, Object[] initials, String[] application_xs, String result_x) throws IllegalArgumentException, CompilationException {
        super(name);
        if (parameters.length == 0)
            throw new IllegalArgumentException("Function must take at least one parameter");
        for (int n = 0; n < parameters.length; n++)
            if (!syntax.isValidIdentifier(parameters[n]))
                throw new IllegalArgumentException("Parameter name '" + parameters[n] + "' is not a valid identifier");
        if ((variables.length != initials.length) ||
            (variables.length != application_xs.length))
            throw new IllegalArgumentException("Variable, initial value, and expression arrays do not have equal lenghts");
        for (int n = 0; n < variables.length; n++)
            if (!syntax.isValidIdentifier(variables[n]))
                throw new IllegalArgumentException("Variable name '" + variables[n] + "' is not a valid identifier");

        this.parameters = parameters;
        StringBuffer parlist = new StringBuffer(parameters[0]);
        StringBuffer dummylist = new StringBuffer("_dummy0");
        for (int n = 1; n < parameters.length; n++) {
            parlist.append(',').append(parameters[n]);
            dummylist.append(',').append("_dummy").append(n);
        }
        this.variables = variables;
        StringBuffer varlist = new StringBuffer("(");
        for (int n = 0; n < variables.length; n++)
            varlist.append(variables[n]).append(',');
        this.initials = initials;
        this.intermediates = new Object[variables.length + parameters.length];
        this.temps = new Object[variables.length + parameters.length];
        this.application_fs = new CompiledFunction[variables.length];
        String fn;
        for (int n = 0; n < variables.length; n++) {
            fn = "a" + n + varlist.toString() + parlist.toString() + ") = " + application_xs[n];
            this.application_fs[n] = parser.compileFunction(fn);
        }
        fn = "r" + varlist.toString() + dummylist.toString() + ") = " + result_x;
        this.result_f = parser.compileFunction(fn);
    }

    // access methods

    /**
     * Returns the <b>parameter count</b> of this main-function.
     * @return an integer value
     */
    public int parameterCount() {
        return this.parameters.length;
    }
    
    // functions methods

    /**
     * <b>Initializes</b> this main-function for application.
     * It assigns each of the intermediate variables its initial value.
     */
    public void initialize() {
        for (int n = 0; n < this.variables.length; n++)
            this.intermediates[n] = this.initials[n];
    }

    /**
     * <b>Applies</b> this main-function to an array of values.
     * These values are assigned to the function definition's input paremeters,
     * upon which all application expression are evaluated and their results
     * are assigned to the respective intermediate variables.
     * @param values an <tt>Object</tt> array
     */
    public void apply(Object[] values) {
        for (int n = 0; n < values.length; n++)
            this.intermediates[this.variables.length + n] = values[n];
        Object[] inters = this.temps;
        for (int n = 0; n < this.variables.length; n++)
            inters[n] = this.application_fs[n].computeFunction(interpreter, this.intermediates);
        this.temps = this.intermediates;
        this.intermediates = inters;
    }

    /**
     * <b>Computes</b> the result of this main-function.
     * The result expression uis evaluated and the resulting value is returned.
     * @return an <tt>Object</tt>
     */
    public Object compute() {
        for (int n = 0; n < this.parameters.length; n++)
            this.intermediates[this.variables.length + n] = Functions.ZERO;
        return this.result_f.computeFunction(interpreter, this.intermediates);
    }

    /**
     * Converts this main-function <b>to a string</b>. The result is
     * the keyword 'func' followed by the function name, the parameter names
     * within parentheses and separated by comma's, the symbol '=', the result
     * expression, the symbol ':', and a list enclosed within curly brackets.
     * This list consists of, for each intermediate variable, a description of
     * its initial value and a description of its application expression. All
     * descriptions are separated by comma's. An initial value description
     * consists of the variable name followed by a '0' within parentheses,
     * the symbol '=', and its initial value. The expressions 'inf' and '-inf'
     * are used to denote positive and negative infinity, respectively.
     * The description of an application expression consists of the respective
     * variable name followed by '+1' within parentheses the symbol '=',
     * and the application expression. This string can be included in an SDL
     * description and subsequently parsed to reveal the original function
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
         result.append(") = ").append(def).append(" : {");
         for (int n = 0; n < this.variables.length; n++) {
             if (n > 0) result.append(", ");
             result.append(this.variables[n]).append("(0) = ");
             if (this.initials[n] == Functions.POS_INF)
                 result.append("inf, ");
             else if (this.initials[n] == Functions.NEG_INF)
                 result.append("-inf, ");
             else result.append(this.initials[n]).append(", ");
             result.append(this.variables[n]).append("(+1) = ");
             def = this.application_fs[n].getDefinition();
             def = def.substring(def.indexOf('=') + 2);
             result.append(def);
         }
         result.append('}');
         return result.toString();
     }

    /**
     * Reads an SDL description of a main function from a
     * {@link cassis.parse.ParseReader} object and assigns the resulting
     * specifications to this main function definition. This description must
     * start with XXX
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a
     * main function definition
     * @see #toString()
     */
    void parse(ParseReader reader) throws ParseException {
    }

    // thing interface methods

    /**
     * Compares this main function definition to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a
     * main function definition. Otherwise the result is defined by comparing
     * the function definition names, array lengths, parameter names, variable
     * names, application expressions, and result expressions, in that order.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof MainFunction)) return FAILED;
	int c = this.name().compareTo(((Function) other).name());
	if (c == 0) c = this.parameters.length - ((MainFunction) other).parameters.length;
        if (c == 0)
            for (int n = 0; (c == 0) && (n < this.parameters.length); n++)
                c = this.parameters[n].compareTo(((MainFunction) other).parameters[n]);
	if (c == 0) c = this.variables.length - ((MainFunction) other).variables.length;
        if (c == 0)
            for (int n = 0; (c == 0) && (n < this.variables.length); n++)
                c = this.variables[n].compareTo(((MainFunction) other).variables[n]);
        if (c == 0)
            for (int n = 0; (c == 0) && (n < this.variables.length); n++)
                c = this.application_fs[n].getDefinition().compareTo(((MainFunction) other).application_fs[n].getDefinition());
	if (c == 0) c = this.result_f.getDefinition().compareTo(((MainFunction) other).result_f.getDefinition());
	return (c < 0) ? LESS : ((c > 0) ? GREATER : EQUAL);
    }
}
