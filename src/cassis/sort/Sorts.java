/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sorts.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.io.StringReader;

import cassis.IllegalOverwriteException;
import cassis.struct.Rational;
import cassis.parse.*;
import cassis.form.Form;
import cassis.visit.SdlVisitor;

/**
 * A <b>Sorts</b> specifies the context for defining {@link Sort}s. Sorts can be
 * defined as compositions of other sorts, as long as all belong to the same context.
 * Sorts within and between contexts can be related and matched.
 * <p>
 * The <b>Sorts</b> class represents a context by a {@link User} profile and containers
 * for storing sorts and matches, and a language for reporting on matches.
 */
public class Sorts implements Parsing {
    
    // representation   
    protected User profile;
    protected Hashtable attributesorts, aspects;
    protected Stack newbies;
    protected Vector timed, hanging;
    protected long timestamp = 1;
    
    // constructor
    
    /**
     * Creates a <b>sorts</b> context for the specified user profile.
     * @param profile a {@link User} object
     */
    public Sorts(User profile) {
        this.profile = profile;
        this.attributesorts = new Hashtable();
        this.aspects = new Hashtable();
        this.newbies = new Stack();
        this.hanging = new Vector();
        this.timed = new Vector();
        this.timestamp = 1;
    }
    
    // public access methods
    
    /**
     * Returns the user <b>profile</b> for this sorts context.
     * @return a {@link User} object
     */
    public User profile() { return this.profile; }
    
    /**
     * Returns the registered <b>sort</b> for the specified name, if any.
     * @param name a <tt>String</tt> object
     * @return a {@link Sort} object, or <tt>null</tt>
     * @see #register
     */
    public Sort sortOf(String name) {
        return (Sort) this.aspects.get(name);
    }
    
    /**
     * Returns an array of names of <b>new sorts</b>, i.e., sorts that were all
     * defined within this context in the last definition session.
     * @return a <tt>String</tt> array
     * @see #define
     */
    public String[] newSorts() {
        String[] array = new String[this.newbies.size()];
        this.newbies.copyInto(array);
        return array;
    }
    /**
     * Returns an array of names of <b>all sorts</b> defined within this context.
     * The array is ordered according to the time of definition.
     * @return a <tt>String</tt> array
     */
    public String[] allSorts() {
        String[] array = new String[this.timed.size()];
        for (int n = 0; n < this.timed.size(); n++)
            array[n] = this.timed.elementAt(n).toString();
        return array;
    }
    
    // methods
    
    /**
     * Defines a sort corresponding the specified definition. This definition must
     * start with a name or a parenthesized list of names, followed by a colon ':',
     * and form a valid defining expression. This expression may contain more
     * definitions, each enclosed in parentheses, as long as each definition also
     * forms a part of the definition of the main sort. All named sorts thus
     * defined in this session are stored for subsequent retrieval.
     * Any {@link cassis.parse.ParseException} is caught and a description of this
     * exception written to <tt>System.err</tt>.
     * @param definition a <tt>String</tt> object
     * @return a {@link Sort} object, or <tt>null</tt>
     * @see #definition
     * @see #newSorts
     */
    public Sort define(String definition) {
        try {
            this.newbies.removeAllElements();
            this.hanging.removeAllElements();
            ParseReader expression = new ParseReader(new StringReader(definition));
            if (expression.token() == '(')
                return new AspectsSort(this, expression);
            return this.definition(expression);
        } catch (ParseException e) {
            System.err.println("Caught ParseException: " + e.getMessage());
            this.undoNewSorts();
            return null;
        }
    }
    
    /**
     * Defines a sort corresponding the specified definition. This definition must
     * start with a name or a parenthesized list of names, followed by a colon ':',
     * and form a valid defining expression. This expression may contain more
     * definitions, each enclosed in parentheses, as long as each definition also
     * forms a part of the definition of the main sort. All named sorts thus
     * defined in this session are stored for subsequent retrieval.
     * Any {@link cassis.parse.ParseException} is caught and a description of this
     * exception written to <tt>System.err</tt>.
     * @param expression a {@link cassis.parse.ParseReader} object that presents
     * the sort's definition
     * @return a {@link Sort} object, or <tt>null</tt>
     * @see #definition
     * @see #newSorts
     * @see #cleanup
     */
    public Sort define(ParseReader expression) {
        try {
            this.newbies.removeAllElements();
            this.hanging.removeAllElements();
            if (expression.newToken() == '(')
                return new AspectsSort(this, expression);
            return this.definition(expression);
        } catch (ParseException e) {
            System.err.println("Caught ParseException: " + e.getMessage());
            this.undoNewSorts();
            return null;
        }
    }
    
    /**
     * Searches for and <b>retrieves</b> a sort within this context that corresponds
     * to the specified expression of a sort.
     * @param expression a {@link cassis.parse.ParseReader} object that presents
     * the sort's definition (without assignment)
     * @return a {@link Sort} object
     * @see #expression
     * @throws ParseException if the defining expression is invalid
     */
    public Sort retrieve(ParseReader expression) throws ParseException {
        try {
            this.newbies.removeAllElements();
            this.hanging.removeAllElements();
            expression.newToken();
            return this.expression(expression);
        } catch (ParseException e) {
            this.undoNewSorts();
            throw new ParseException(expression, e.toString());
        }
    }
    
    /**
     * Parses a <b>definition</b> and returns the corresponding sort.
     * A valid definition has the following form:<tt>
     * <br>definition := name ':' expression |
     * <br>              name ':' primitive |
     * <br>              name ':' primitive '(' parameters ')' </tt>
     * @param expr a {@link cassis.parse.ParseReader} object that presents
     * the definition
     * @return a {@link Sort} object
     * @throws ParseException if the defining expression is invalid
     */
    Sort definition(ParseReader expr) throws ParseException {
        if ((expr.token() != IDENTIFIER) ||
                (expr.previewToken() != ':'))
            throw new ParseException(expr, "Expected an identifier, followed by ':'");
        
        Sort result;
        String name = expr.tokenString();
        try {
            this.hook(name);
        } catch (IllegalArgumentException e) {
            throw new ParseException(expr, "This name has already been specified");
        }
        expr.newToken();
        
        if (expr.newToken() == '[') {
            result = (Sort) new PrimitiveSort(this, name, expr);
            this.unhook(result);
            return result;
        }
        result = this.expression(expr);
        // try {
        result = this.unhook(result);
        // } catch (IllegalArgumentException e) {
        //     throw new ParseException(expr, "Cyclic definition");
        // }
        if (result.isNamed() && !result.name().equals(name))
            result = result.duplicate(name);
        else try {
            result.assign(name);
        } catch (IllegalOverwriteException e) {
            throw new ParseException(expr, "This name has already been assigned a sort");
        }
        return result;
    }
    /**
     * Parses an <b>expression</b> and returns the corresponding sort.
     * A valid expression has the following form:<tt>
     * <br>expression := term |
     * <br>              term '+' expression
     * <br>term := factor |
     * <br>        factor '^' term
     * <br>factor := '(' definition ') |
     * <br>          '(' expression ') |
     * <br>          name</tt>
     * @param expr a {@link cassis.parse.ParseReader} object that presents
     * the sort's definition
     * @return a {@link Sort} object
     * @throws ParseException if the defining expression is invalid
     */
    Sort expression(ParseReader expr) throws ParseException {
        Sort result = term(expr), temp;
        while (expr.token() == '+') {
            expr.newToken();
            temp = term(expr);
            result = result.sum(temp);
        }
        return result;
    }
    
    // parses a TERM and returns the corresponding sort
    private Sort term(ParseReader expr) throws ParseException {
        Sort result = factor(expr), temp;
        while (expr.token() == '^') {
            expr.newToken();
            temp = factor(expr);
            result = result.combine(temp);
        }
        return result;
    }
    
    // parses a FACTOR and returns the corresponding sort
    private Sort factor(ParseReader expr) throws ParseException {
        Sort result;
        if (expr.token() == '(') {
            expr.newToken();
            if ((expr.token() == IDENTIFIER) &&
                    (expr.previewToken() == ':'))
                result = definition(expr);
            else if ((expr.token() == IDENTIFIER) &&
                    (expr.previewToken() == ','))
                result = new AspectsSort(this, expr);
            else
                result = expression(expr);
            if (expr.token() != ')')
                throw new ParseException(expr, "')' expected");
        } else if (expr.token() == IDENTIFIER) {
            String name = expr.tokenString();
            result = this.sortOf(name);
            if (result == null) result = this.hooked(name);
            if (result == null)
                throw new ParseException(expr, "This sort is not recognized");
        } else
            throw new ParseException(expr, "Expected an identifier or a parenthesized expression");
        expr.newToken();
        return result;
    }
    
    /**
     * Creates a <b>new form</b> for the sort with the specified name.
     * @return a {@link cassis.form.Form} corresponding the specified sort
     * @throws IllegalArgumentException if no sort exists within this context
     * with the specified name
     * @see Sort#newForm
     */
    public Form newForm(String name) throws IllegalArgumentException {
        Sort sort = this.sortOf(name);
        if (sort == null)
            throw new IllegalArgumentException("Sort name undefined");
        return sort.newForm();
    }
    
    /**
     * Retrieves an <b>attribute sort</b> by its description, if it has been stored
     * previously.
     * @param description a <tt>String</tt> object describing this sort
     * @return an {@link AttributeSort} object
     * @see #putAttributeSort
     */
    AttributeSort getAttributeSort(String description) {
        return (AttributeSort) this.attributesorts.get(description);
    }
    /**
     * Stores an <b>attribute sort</b> by its description.
     * @param description a <tt>String</tt> object describing this sort
     * @param sort an {@link AttributeSort} object
     * @see #getAttributeSort
     */
    void putAttributeSort(String description, AttributeSort sort) {
        this.attributesorts.put(description, sort);
    }
    
    /**
     * Stores a sort in the order of its time of inclusion.
     * @param instance an {@link Sort} object
     */
    void addTimed(Sort instance) {
        this.timed.addElement(instance);
    }
    /**
     * <b>Registers</b> a sort into this context. This stores the sort in the order
     * of its time of registration, makes it retrievable by its name, and adds it to
     * the list of new sorts defined in the current session.
     * @param name a <tt>String</tt> object
     * @param instance an {@link Sort} object
     * @see #addTimed
     * @see #sortOf
     * @see #newSorts
     */
    long register(String name, Sort instance) {
        this.timed.addElement(instance);
        this.aspects.put(name, instance);
        this.newbies.push(name);
        return timestamp++;
    }
    
    /**
     * <b>Prints</b> all sorts defined within this context to the specified SDL
     * context, in order of registration.
     * @param sdl a {@link cassis.io.SdlContext} object
     */
    public void print(SdlVisitor visitor) {
        for (int n = 0; n < this.timed.size(); n++) {
            ((Sort) this.timed.elementAt(n)).accept(visitor);
        }
    }
    
    /**
     * <b>Undoes any new sorts</b> created within the current session. Removes these
     * from both the registration list and the names list.
     */
    public void undoNewSorts() {
        while (!this.newbies.empty()) {
            this.timed.removeElement(this.aspects.get((String) this.newbies.peek()));
            this.aspects.remove((String) this.newbies.pop());
        }
    }
    
    /**
     * <b>Cleans up</b> this context by clearing all lists, i.e., matches,
     * attribute sorts, names, and registration list.
     */
    public void cleanup() {
        this.attributesorts.clear();
        this.aspects.clear();
        this.timed.removeAllElements();
        this.timestamp = 0;
        this.hanging.removeAllElements();
    }
    
    // hook methods
    
    /**
     * Stores a <b>hook</b> to the specified name for subsequent lookup.
     * Hooks to names of not yet defined sorts serve the definition of
     * {@link RecursiveSort}s. By storing a hook, the name is reserved
     * without registering it yet.
     * @param name a <tt>String</tt> object
     * @throws IllegalArgumentException if this name has already been hooked
     */
    void hook(String name) throws IllegalArgumentException {
        for (int n = 0; n < this.hanging.size(); n++) {
            Object hook =  this.hanging.elementAt(n);
            if ((hook instanceof String) && ((String) hook).equals(name))
                throw new IllegalArgumentException("name has already been specified");
            if ((hook instanceof Sort) && ((Sort) hook).name().equals(name))
                throw new IllegalArgumentException("name has already been specified");
        }
        this.hanging.addElement(name);
    }
    /**
     * Checks if the specified name has been <b>hooked</b>. When called for the first
     * time with this name, a recursive sort is created and associated with this hook.
     * This object is returned, also at subsequent calls with this name.
     * @param name a <tt>String</tt> object
     * @return a {@link RecursiveSort} object
     * @see #hook
     * @see RecursiveSort#RecursiveSort(Sorts, String)
     */
    RecursiveSort hooked(String name) {
        for (int n = 0; n < this.hanging.size(); n++) {
            Object hook =  this.hanging.elementAt(n);
            if ((hook instanceof String) && ((String) hook).equals(name)) {
                hook = new RecursiveSort(this, name);
                this.hanging.setElementAt(hook, n);
                return (RecursiveSort) hook;
            }
            if ((hook instanceof RecursiveSort) && ((Sort) hook).name().equals(name))
                return (RecursiveSort) hook;
        }
        return null;
    }
    
    /**
     * Removes the <b>hook</b> to the specified name. If a recursive sort has been
     * associated with this hook, then, the specified sort is assigned as instance
     * to this recursive sort. This recursive sort is returned. Otherwise, the
     * specified sort is returned.
     * @param sort a {@link Sort} object
     * @return a {@link Sort} object
     * @throws IllegalArgumentException if the specified sort equals the resursive
     * sort associated with the hook.
     * @see #hooked
     */
    Sort unhook(Sort sort) throws IllegalArgumentException {
        Object hook = this.hanging.lastElement();
        this.hanging.removeElementAt(this.hanging.size() - 1);
        if (hook == sort)
            throw new IllegalArgumentException("Cyclic definition");
        if (hook instanceof Sort) {
            ((RecursiveSort) hook).setInstance(sort);
            return ((RecursiveSort) hook);
        }
        return sort;
    }
    
}
