/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AspectsSort.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import java.util.Vector;

import cassis.struct.Parameter;
import cassis.parse.*;

/**
 * An <b>Aspects-Sort</b> defines a linking of two {@link Sort}s.
 * It is similar to a {@link PrimitiveSort} in that it depends on the
 * specification of a characteristic individual (i.e., {@link cassis.ind.Relation})
 * and a template specifying the algebraic behavior of its forms
 * (i.e., {@link cassis.form.RelationalForm}). However, an <i>aspects-sort</i> is not
 * a sort that can be used as such in compositions with other sorts. Instead,
 * an aspects-sort defines two {@link cassis.sort.Aspect}s, corresponding to
 * both sorts that it links, that each represent a separate, unidirectional view
 * of this linking. These <i>aspects</i> specify simple sorts, and are defined
 * automatically as the result of the definition of an aspects-sort. Individual
 * aspects can be used within the definition of other sorts. Each aspect is
 * identified by a unique name.
 * <p>
 * The <b>AspectsSort</b> class extends on the {@link PrimitiveSort} class.
 * It represents an aspects sort additionally by the array of aspect names.
 * The definition of an aspects-sort is similar to the definition of a primitive
 * sort, where the argument list specifies both sorts to be linked, and the name
 * that is assigned to the sort is replaced by a list of aspect names separated
 * by comma's and enclosed within parentheses, such that the order of the aspect
 * names corresponds to the order of the argument sorts (see also
 * {@link PrimitiveSort}).<br>
 * <tt>definition := '(' names ')' ':' primitive '(' names ')'
 * <br>names := name ',' name
 * <br>primitive := '[' charind ']'</tt>
 */
public class AspectsSort extends PrimitiveSort {

    // representation

    private Aspect aspects[];

    // constructor

    /**
     * Creates an <b>aspects-sort</b> according to the specified definition,
     * and within the specified context. This definition includes the assignment.
     * @param context a {@link Sorts} object
     * @param expr a {@link cassis.parse.ParseReader} object that presents
     * the sort's definition
     * @throws ParseException if the reader does not present a proper definition
     */
    AspectsSort(Sorts context, ParseReader expr) throws ParseException {
        super(context);
	if (expr.token() != '(')
	    throw new ParseException(expr, "'(' expected");

	Vector tuple = new Vector();
	while (true) {
	    if (expr.newToken() != IDENTIFIER)
		throw new ParseException(expr, "Expected an identifier");
	    tuple.addElement(expr.tokenString());
	    if (expr.newToken() != ',') break;
	}
	if (expr.token() != ')')
	    throw new ParseException(expr, "')' expected");
	if (expr.newToken() != ':')
	    throw new ParseException(expr, "':' expected");

	expr.newToken();
	Parameter parameter = characteristic(expr);
	int aspectcount = parameter.cardinality();
	if (aspectcount == 0)
	    throw new ParseException(expr, "This characteristic Individual accepts no aspects");
	if (aspectcount < tuple.size())
	    throw new ParseException(expr, "Too few aspects specified");
	if (aspectcount > tuple.size())
	    throw new ParseException(expr, "Too many aspects specified");

	expr.newToken();
	this.arguments(context, parameter, expr);

	String[] names = new String[tuple.size()];
	tuple.copyInto(names);
	this.aspects = new Aspect[names.length];
	this.assign(names);
	for (int n = 0; n < names.length; n++) {
	    this.aspects[n] = new Aspect(this.context(), this);
	    this.aspects[n].assign(names[n]);
	    this.aspects[n].canonical = names[n];
            this.aspects[n].definition = this.definition;
	}
    }

    // access methods

    /**
     * Returns an array of all <b>aspects</b> belonging to this aspects-sort.
     * @return an {@link Aspect} array
     */
    Aspect[] aspects() { return this.aspects; }
    /**
     * Returns the <b>argument</b> sort of this aspects-sort that corresponds
     * to the specified aspect.
     * @return a {@link Sort} object
     */
   Sort argument(Aspect aspect) {
        int n = 0;
        for (; (aspect != this.aspects[n]) && (n < this.aspects.length); n++);
        if (n >= this.aspects.length)
            throw new IllegalArgumentException("Invalid Aspect specified");
        return ((Sort[]) this.arguments().value())[n];
    }

    // methods

    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be duplicated.
     * @throws IllegalArgumentException always
     */
    Sort duplicate(String name) {
	throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
    /**
     * Throws an illegal-argument-exception. An aspects-sort cannot be combined
     * with any other sort.
     * @throws IllegalArgumentException always
     */
    public Sort combine(Sort other) {
	throw new IllegalArgumentException("Aspects sort '" + this.canonical + "'");
    }
}
