/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SdlVisitor.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 29.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.PrintStream;
import cassis.sort.Sort;
import cassis.sort.User;
import cassis.Element;
import cassis.ind.Individual;
import cassis.ind.Relation;
import cassis.ind.Function;
import cassis.ind.function.Functions;
import cassis.form.Form;
import cassis.parse.SDL;

/**
 * A <b>SDL Visitor</b> builds SDL descriptions of elements and/or sorts.
 * SDL (Sorts Description Language) descriptions are text-based descriptions
 * that are readable (and writeable) by the user and can be parsed
 * to reconstruct the original elements and/or sorts. Thus, a SDL-visitor can
 * be used to store element and sort descriptions in a file for later retrieval.
 * For this purpose, a SDL-visitor can be created to write to a
 * <tt>PrintStream</tt> or to return each resulting description as
 * a string.<br>
 * SDL descriptions can be written according to one of three styles:
 * {@link #DEFAULT}, {@link #COMPACT} and {@link #VERBOSE}. The default style
 * is an indented description where each individual is written on a separate
 * line, emphasizing readability. The compact style is a single-line
 * description, also omitting comma's between consecutive individuals or forms.
 * Finally, the verbose style adds to the default style for each form the
 * name or description of this form's sort.
 * <p>
 * The <b>SdlVisitor</b> class extends the {@link ElementVisitor} class and
 * implements the {@link SortVisitor}. As such, it implements the hierarchical
 * visitor pattern for both elements and sorts. An SDL-visitor specifies a
 * <tt>PrintStream</tt> and a style. If the printstream is <tt>null</tt>,
 * the the resulting description is stored as a string and can be retrieved
 * using the {@link #toString} method.
 * @see ParseReader
 */
public class SdlVisitor extends ElementVisitor implements SortVisitor, SDL {
    
    // constants
    
    /**
     * An integer value specifying a <b>default</b> SDL description style.
     */
    public static final int DEFAULT = 0;
    /**
     * An integer value specifying a <b>compact</b> SDL description style.
     */
    public static final int COMPACT = 1;
    /**
     * An integer value specifying a <b>verbose</b> SDL description style.
     */
    public static final int VERBOSE = 2;
    
    private static final int ITEM = 0;
    private static final int GROUP = 1;
    private static final int COMMA = 2;
    private static final int DEFINE = 3;
    
    // representation
    
    private int style, status;
    private PrintStream ps;
    private String tab, result;
    private boolean separator;
    private Stack tabs;
    private Hashtable functions, sorts;
    
    // constructors
    
    /**
     * Constructs a <b>SDL visitor</b> for the specified printstream and style.
     * The SDL description is automatically written to the printstream as it is
     * generated. The SDL description is generated according to one of the
     * following styles: compact, default and verbose.
     * @param ps a <tt>PrintStream</tt> object
     * @param style an integer value equal to {@link #COMPACT}, {@link #DEFAULT}
     * or {@link #VERBOSE}
     */
    public SdlVisitor(PrintStream ps, int style) {
        this.style = DEFAULT;
        if ((style > DEFAULT) && (style <= VERBOSE)) this.style = style;
        this.status = DEFINE;
        this.ps = ps;
        this.tab = this.result = "";
        this.separator = false;
        this.tabs = new Stack();
        this.functions = new Hashtable();
        this.sorts = new Hashtable();
    }
    /**
     * Constructs a <b>SDL visitor</b> for the specified printstream and
     * the default style. The SDL description is automatically written to the
     * printstream as it is generated.
     * @param ps a <tt>PrintStream</tt> object
     */
    public SdlVisitor(PrintStream ps) {
        this(ps, DEFAULT);
    }
    /**
     * Constructs a <b>SDL visitor</b> for the specified style. The SDL
     * description can be retrieved using the {@link #toString} method.
     * @param style an integer value equal to {@link #COMPACT}, {@link #DEFAULT}
     * or {@link #VERBOSE}
     */
    public SdlVisitor(int style) {
        this(null, style);
    }
    /**
     * Constructs a <b>SDL visitor</b> for the default style. The SDL
     * description can be retrieved using the {@link #toString} method.
     */
    public SdlVisitor() {
        this(null, DEFAULT);
    }
    
    // access methods
    
    /**
     * Returns the SDL description of this visitor if it wasn't written to
     * a printstream.
     * @return a <tt>String</tt>
     */
    public String toString() {
        return this.result;
    }
    
    /**
     * Returns an SDL description of the functions that were used in the SDL
     * description of this visitor.
     * @return a <tt>String</tt>
     */
    public String functionsToString() {
        String funcs = "";
        for (Enumeration e = this.functions.elements(); e.hasMoreElements(); ) {
            funcs += "// func " + (String) e.nextElement() + ";\n";
        }
        return funcs;
    }
    
    /**
     * <b>Clears</b> the SDL description and reinitializes this SDL-visitor.
     */
    public void clear() {
        this.status = DEFINE;
        this.tab = this.result = "";
        this.separator = false;
        this.tabs.clear();
        this.functions.clear();
        this.sorts.clear();
    }
    
    // methods
    
    private void print(String s) {
        if (this.ps == null)
            this.result += s;
        else this.ps.print(s);
    }
    
    /**
     * Builds a SDL file <b>header</b> description.
     */
    public void header() {
        this.print(HEADER + "\n\n");
    }
    
    /**
     * Builds a SDL file <b>header</b> description, including a specification
     * of the user profile name.
     * @param profile a {@link User} object
     */
    public void header(User profile) {
        this.print(HEADER + " [" + profile.name() + "]\n\n");
    }
    
    /**
     * Builds a SDL description of a <b>variable definition</b> of an element.
     * The variable name is preceded by the keyword 'form' or 'ind' and
     * a '$' sign, and followed by an '=' sign, a description of the element
     * and a ';' sign. If the element is a form, it is maximalized first.
     * @param name a variable name
     * @param element a {@link Element} object
     */
    public void defineVariable(String name, Element element) {
        if (element instanceof Form) {
            this.print('\n' + FORM_KEYWORD + ' ' + VARIABLE_PREFIX + name + " =");
            ((Form) element).maximalize();
        } else this.print('\n' + INDIVIDUAL_KEYWORD + ' ' + VARIABLE_PREFIX + name + " =");
        this.status = DEFINE;
        element.accept(this, null);
        this.print(";\n\n");
        this.status = DEFINE;
        this.separator = false;
    }
    
    /**
     * Returns the specified string enclosed in <b>parentheses</b>, if the
     * string contains at least one space.
     * @param s a <tt>String</tt> object
     */
    public static String parenthesize(String s) {
        if (s.indexOf(' ') < 0)
            return s;
        return '(' + s + ')';
    }
    
    private void write(Sort sort, String s) {
        if (this.separator) {
            if (this.style == DEFAULT)
                this.print(",\n");
            this.separator = false;
            this.status = COMMA;
        }
        switch(this.style) {
            case COMPACT:
                this.print(' ' + s);
                break;
            case VERBOSE:
                if (this.status == ITEM)
                    this.print(' ' + s);
                else this.print('\n' + this.tab + parenthesize(sort.toString()) + ": " + s);
                break;
            default: // case DEFAULT:
                if (this.status == DEFINE)
                    this.print(' ' + parenthesize(sort.toString()) + ": " + s);
                else if (this.status == COMMA)
                    this.print(this.tab + s);
                else
                    this.print(' ' + s);
                break;
        }
        this.status = ITEM;
    }
    
    // element-visitor methods
    
    /**
     * <b>Enters (visits)</b> an individual wrt an associate individual and
     * builds an SDL description of this individual. It calls the
     * {@link Individual#toString} method of this individual and wraps
     * the resulting description corresponding to the style that is being used.
     * @param ind the {@link Individual} object being visited
     * @param assoc an associate {@link Individual} object of the
     * individual being visited
     * @return <tt>true</tt>
     */
    public boolean visitEnter(Individual ind, Individual assoc) {
        Sort sort = ind.ofSort();
        if (ind instanceof Function) {
            Functions funcs = sort.context().profile().functions();
            String fn = ((Function) ind).mainFunction();
            if (!this.functions.containsKey(fn))
                this.functions.put(fn, funcs.getFunction(fn).toString());
            fn = ((Function) ind).auxFunction();
            if ((fn != null) && !this.functions.containsKey(fn))
                this.functions.put(fn, funcs.getFunction(fn).toString());
        }
        if (ind instanceof Relation)
            sort = ((Relation) ind).getAspect(assoc.ofSort());
        if (ind.isReferenced())
            this.write(sort, REFERENCE_PREFIX + ind.getReference());
        this.write(sort, ind.toString(assoc));
        return true;
    }
    /**
     * <b>Leaves (visits)</b> an individual. Depending on the style and
     * the next visiting action, a separator may be added to the description.
     * @param ind the {@link Individual} object being visited
     */
    public void visitLeave(Individual ind) {
        this.separator = true;
    }
    
    /**
     * <b>Enters (visits)</b> a form and starts building an SDL description of
     * this form. This may include a description of the form's sort.
     * @param form the {@link Form} object being visited
     * @return <tt>true</tt>
     */
    public boolean visitEnter(Form form) {
        if (this.separator) {
            if (this.style == DEFAULT)
                this.print(",\n");
            this.separator = false;
            this.status = COMMA;
        }
        this.tabs.push(this.tab);
        switch(this.style) {
            case COMPACT:
                if (this.status != ITEM)
                    this.print(' ' + parenthesize(form.ofSort().toString()) + ": {");
                else this.print(" {");
                break;
            case VERBOSE:
                if (this.status == ITEM)
                    this.tab += "  ";
                this.print('\n' + this.tab + parenthesize(form.ofSort().toString()) + ": {");
                this.tab += "  ";
                break;
            default: // case DEFAULT:
                String temp;
                if (this.status == COMMA)
                    temp = this.tab + parenthesize(form.ofSort().toString()) + ":\n";
                //temp = this.tab + ":\n";
                else if (this.status != ITEM)
                    temp = ' ' + parenthesize(form.ofSort().toString()) + ":\n";
                else temp = "\n";
                if (this.status != DEFINE) {
                    this.tab += "  ";
                    temp += this.tab;
                }
                this.tab += "  ";
                this.print(temp + '{');
                break;
        }
        this.status = GROUP;
        return true;
    }
    /**
     * <b>Leaves (visits)</b> a form and ends the SDL description of this form.
     * Depending on the style and the next visiting action, a separator may
     * be added to the description.
     * @param form the {@link Form} object being visited
     */
    public void visitLeave(Form form) {
        this.tab = (String) this.tabs.pop();
        if (this.style == VERBOSE) {
            if (this.status == COMMA)
                this.print("\n  " + this.tab + '}');
            else this.print('\n' + this.tab + '}');
        } else this.print(" }");
        this.status = COMMA;
        this.separator = true;
    }
    
    // sort-visitor methods
    
    /**
     * <b>Enters (visits)</b> a sort.
     * Returns <tt>true</tt> if the sort is not named or it has not been marked
     * yet as described by this visitor, returns <tt>false</tt> otherwise.
     * @param sort the {@link Sort} object being visited
     * @return a <tt>boolean</tt> value
     */
    public boolean visitEnter(Sort sort) {
        return (!sort.isNamed() || !this.sorts.containsKey(sort.toString()));
    }
    /**
     * <b>Leaves (visits)</b> a sort and builds a description of this sort
     * if it is a named sort.
     * The keyword 'sort' is followed by the name of the sort, a ':' sign,
     * the definition of the sort, and a ';' sign.
     * The sort is marked as described by this visitor.
     * @param sort the {@link Sort} object being visited
     */
    public void visitLeave(Sort sort) {
        if (!sort.isNamed() || this.sorts.containsKey(sort.toString())) return;
        this.print(SORT_KEYWORD + ' ' + sort.toString() + " :");
        this.status = ITEM;
        this.write(sort, sort.definition());
        this.print(";\n");
        this.sorts.put(sort.toString(), sort.definition());
        this.status = DEFINE;
        this.separator = false;
    }
}
