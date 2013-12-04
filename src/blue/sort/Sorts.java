/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sorts.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.IllegalOverwriteException;
import blue.form.Form;
import blue.io.*;
import java.util.*;
import blue.struct.Rational;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.TGPanel;
import java.io.*;
import sortsApp.*;


/**
 * A <b>Sorts</b> specifies the context for defining {@link Sort}s. Sorts can be
 * defined as compositions of other sorts, as long as all belong to the same context.
 * Sorts within and between contexts can be related and matched. <p>
 * The <b>Sorts</b> class represents a context by a {@link User} profile and containers
 * for storing sorts and matches, and a language for reporting on matches.
 */
public class Sorts implements Matching, Parser {
    // constants
    
    /** A constant to represent the use of <b>english</b> for reporting on matches. */
    public static final int ENGLISH = 0;
    
    /** A constant to represent the use of <b>german</b> for reporting on matches. */
    public static final int DEUTSCH = 1;
    
    /** A constant to represent the use of <b>dutch</b> for reporting on matches. */
    public static final int NEDERLANDS = 1;
    // representation
    private User profile;
    private Hashtable matches, attributesorts, aspects;
    private Stack newbies;
    private Vector timed, hanging;
    private long timestamp = 1;
    private int language = ENGLISH;
    private String[] levelTerms = new String[6];
    private String[] gradeTerms = new String[4];
    private String[] conversionTerms = new String[3];
    //
    private TGPanel tgPanel;
    private GraphLayoutPage glPage;
    protected GraphLayoutTopFrame topFrame;
    // constructor
    
    /**
     * Creates a <b>sorts</b> context for the specified user profile.
     * The default language for reporting on matches is english.
     * @param profile a {@link User} object
     */
    public Sorts(User profile) {
        this.profile = profile;
        this.matches = new Hashtable();
        this.attributesorts = new Hashtable();
        this.aspects = new Hashtable();
        this.newbies = new Stack();
        this.hanging = new Vector();
        this.timed = new Vector();
        this.timestamp = 1;
        this.setLanguage(ENGLISH);
    }
    
    /** added 26.09.2003 MC */
    public Sorts(User profile, TGPanel tgPanel) {
        this.profile = profile;
        this.matches = new Hashtable();
        this.attributesorts = new Hashtable();
        this.aspects = new Hashtable();
        this.newbies = new Stack();
        this.hanging = new Vector();
        this.timed = new Vector();
        this.timestamp = 1;
        this.setLanguage(ENGLISH);
        this.tgPanel = tgPanel;
        //this.glPage = tgPanel.getGlPage();
        
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
        return (Sort)this.aspects.get(name);
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
    
    /**
     * Converts a sorts vector into a sorts array. In reverse order of creation: = top nodes first
     * @author Michael Cumming
     * @see 27.May.2003
     */
    public Sort[] allSortsArray() {
        Sort[] array = new Sort[this.timed.size()];
        for (int n = this.timed.size() - 1; n >= 0; n--)
            array[n] = (Sort)this.timed.elementAt(n);
        return array;
    }
    
    /**
     * Returns the last sort defined - assuming bottom-up definition.
     * @since 20.06.2003
     * @author Michael Cumming
     */
    public Sort topSort() { //get first non-aspect sort from end of vector
        Vector t = this.getTimed();
        if (t != null && t.size() > 0) {
            for (int i = t.size()-1; i >= 0; i--) {
                Sort curS = (Sort)t.elementAt(i);
                if (!(curS instanceof AspectsSort) && !(curS instanceof Aspect))
                    return curS;
            }
        }
        return null;
    }
    
    //modified 26.09.2003 MC
    //moved from 'Node'
    public void printAllAspects()  { //Sorts allS) {
        HashSet allA = this.getAllAspects(); //this method eliminates duplicate aspects
        System.out.println("Print all aspects---------");
        for (Iterator i = allA.iterator(); i.hasNext(); ) {
            Aspect curA = (Aspect)i.next();
            System.out.println("curA: " + curA.toString());
        }
    }
    
    //    public Sort topSortOld() {
    //        if (this.timed != null && timed.size() > 0) {
    //            return (Sort)this.timed.lastElement();
    //        }
    //        return null;
    //    }
    
    /**
     * returns an array of names + types of all sorts defined within this context.
     * The array is ordered according to the time of definition.
     * @since 05.11.2002
     * @author Michael Cumming
     */
    public String[] allSortsS() {
        String[] array = new String[this.timed.size()];
        for (int n = 0; n < this.timed.size(); n++) {
            Sort temp = (Sort)this.timed.elementAt(n);
            array[n] = temp.toString() + " : " + temp.definition();
        }
        return array;
    }
    
    /**
     * @since 19.06.2003
     * @author Michael Cumming
     */
    public void printAllSorts() {
        if (this != null) {
            String[] sortArray = this.allSortsS();
            //printMessage("Contents of current profile:");
            printMessage("Contents of current profile:");
            for (int n = 0; n < sortArray.length; n++) {
                //System.out.println(sortArray[n]);
                printMessage(sortArray[n]);
            }
        }
        else
            System.out.println("Sorts are null in profile");
    }
    
    //28.Aug.2003 MC used for testing
    public void printAllBasesWeights() {
        for (int n = 0; n < this.timed.size(); n++) {
            Sort s = (Sort)this.timed.elementAt(n);
            if (s instanceof AttributeSort) {
                AttributeSort aS = (AttributeSort)s;
                System.out.println("TEST base sort: " + aS.base().toString());
                System.out.println("TEST weight sort: " + aS.weight().toString());
            }
        }
    }
    
    //28.Aug.2003 MC used for testing
    public void printAllDisComponents() {
        for (int n = 0; n < this.timed.size(); n++) {
            Sort s = (Sort)this.timed.elementAt(n);
            if (s instanceof DisjunctiveSort) {
                DisjunctiveSort dS = (DisjunctiveSort)s;
                dS.toEnd();
                for (dS.toBegin(); !dS.atEnd(); dS.toNext()) {
                    System.out.println("TEST disj.comp: " + dS.current().toString());
                }
            }
        }
    }
    
    //29.Aug.2003 MC used for testing
    public void printAllAspectsSorts() {
        System.out.println("All aspect sorts:----");
        for (int n = 0; n < this.timed.size(); n++) {
            Sort s = (Sort)this.timed.elementAt(n);
            if (s instanceof Aspect) {
                Aspect a = (Aspect)s;
                System.out.println("TEST Aspect: " + a.toString());
                System.out.println("Aspect s.def(): " + a.definition());
                System.out.println("Aspect s.1stPart(): " + a.getFirstPartDef());
                System.out.println("Aspect s.2ndPart(): " + a.getSecondPartDef());
                //System.out.println("Aspect s.name(): " + s.name());
                if (s instanceof PrimitiveSort) System.out.println(s.toString()+ " is also Prim sort");
            }
        }
    }
    //29.Aug.2003 MC
    //since HashSet, eliminates duplicates
    public HashSet getAllAspects() {
        HashSet allA = new HashSet();
        for (int n = 0; n < this.timed.size(); n++) {
            Sort s = (Sort)this.timed.elementAt(n);
            if (s instanceof Aspect) {
                System.out.println("Aspect found: toS: " + s.toString() + "; def: " + s.definition());
                allA.add(s);
            }
        }
        return allA;
    }
    
    /**
     * Defines a sort corresponding the specified definition. This definition must
     * start with a name or a parenthesized list of names, followed by a colon ':',
     * and form a valid defining expression. This expression may contain more
     * definitions, each enclosed in parentheses, as long as each definition also
     * forms a part of the definition of the main sort. All sorts thus defined
     * in this session are stored for subsequent retrieval. Any {@link blue.io.ParseException} is caught and a description of
     * this exception written to <tt>System.err</tt>.
     * @param definition a <tt>String</tt> object
     * @return a {@link Sort} object, or <tt>null</tt>
     * @see #definition
     * @see #newSorts
     */
    //used in sorts demo by Michael Cumming. June 2003
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
    
    //1.Sept.2003 MC
    public Sort defineButFirstCheck(String def) {
        if(!sortNameAlreadyDefined(def))
            return define(def);
        else {
            System.out.println("Sorts line.269. Sort def already defined");
            return null;
        }
    }
    
    //1.Sept.2003 MC
    public boolean sortNameAlreadyDefined(String sDef0) { //careful which sort def you feed this method
        Vector allS = this.timed;
        for (int n = 0; n < allS.size(); n++) {
            String sDef1 = (String)allS.elementAt(n).toString(); //eg. p1; p1_has_p2
            if(sDef0.equals(sDef1)) {
                System.out.println(sDef0 + " == " + sDef1);
                return true; //sort name already defined
            }
        }
        return false;
    }
    
    public boolean checkEdgeBeforeDefinition(Edge e) {
        //String src2destS = e.get
        //String dest2srcS;
        return false;
        
        
    }
    
    //used in sorts demo by Michael Cumming. June 2003
    //    public Sort define(String definition, boolean isSecondAttribute) {
    //        try {
    //            this.newbies.removeAllElements();
    //            this.hanging.removeAllElements();
    //            ParseReader expression = new ParseReader(new StringReader(definition));
    //            if (expression.token() == '(') {
    //                Sort s1 = new AspectsSort(this, expression);
    //                s1.setSecondAttribute(isSecondAttribute);
    //                return s1;
    //            }
    //            Sort s2 = this.definition(expression);
    //            s2.setSecondAttribute(isSecondAttribute);
    //            return s2;
    //        } catch (ParseException e) {
    //            System.err.println("Caught ParseException: " + e.getMessage());
    //            this.undoNewSorts();
    //            return null;
    //        }
    //    }
    
    /**
     * Defines a sort corresponding the specified definition. This definition must
     * start with a name or a parenthesized list of names, followed by a colon ':',
     * and form a valid defining expression. This expression may contain more
     * definitions, each enclosed in parentheses, as long as each definition also
     * forms a part of the definition of the main sort. All sorts thus defined
     * in this session are stored for subsequent retrieval. Any {@link blue.io.ParseException} is caught and a description of
     * this exception written to <tt>System.err</tt>.
     * @param expression a {@link blue.io.ParseReader} object that presents the sort's definition
     * @return a {@link Sort} object, or <tt>null</tt>
     * @see #definition
     * @see #newSorts
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
     * Searches for and <b>retrieves</b> a sort within this context that corresponds to the specified expression of a sort.
     * @param expression a {@link blue.io.ParseReader} object that presents the sort's definition (without assignment)
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
     * Parses a <b>definition</b> and returns the corresponding sort. A valid definition has the following form:<tt>
     * <br>definition := name ':' expression | <br> name ':' primitive | <br> name ':' primitive '(' parameters ')' </tt>
     * @param expression a {@link blue.io.ParseReader} object that presents the definition
     * @return a {@link Sort} object
     * @throws ParseException if the defining expression is invalid
     */
    Sort definition(ParseReader expr) throws ParseException {
        if ((expr.token() != IDENTIFIER) || (expr.previewToken() != ':'))
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
            result = (Sort)new PrimitiveSort(this, name, expr);
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
        else
            try {
                result.assign(name);
            } catch (IllegalOverwriteException e) {
                throw new ParseException(expr, "This name has already been assigned a sort");
            }
        return result;
    }
    
    /**
     * Parses an <b>expression</b> and returns the corresponding sort. A valid expression has the following form:<tt>
     * <br>expression := term | <br>              term '+' expression <br>term := factor | <br>        factor '^' term
     * <br>factor := '(' definition ') | <br>          '(' expression ') | <br>          name</tt>
     * @param expression a {@link blue.io.ParseReader} object that presents the sort's definition
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
    
    /** Parses a TERM and returns the corresponding sort */
    private Sort term(ParseReader expr) throws ParseException {
        Sort result = factor(expr), temp;
        while (expr.token() == '^') {
            expr.newToken();
            temp = factor(expr);
            result = result.combine(temp);
        }
        return result;
    }
    
    /** Parses a FACTOR and returns the corresponding sort */
    private Sort factor(ParseReader expr) throws ParseException {
        Sort result;
        if (expr.token() == '(') {
            expr.newToken();
            if ((expr.token() == IDENTIFIER) && (expr.previewToken() == ':'))
                result = definition(expr);
            else if ((expr.token() == IDENTIFIER) && (expr.previewToken() == ','))
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
     * @return a {@link blue.form.Form} corresponding the specified sort
     * @throws IllegalArgumentException if no sort exists within this context with the specified name
     * @see Sort#newForm
     */
    public Form newForm(String name) throws IllegalArgumentException {
        Sort sort = this.sortOf(name);
        if (sort == null)
            throw new IllegalArgumentException("Sort name undefined");
        return sort.newForm();
    }
    
    /**
     * Retrieves an <b>attribute sort</b> by its description, if it has been stored previously.
     * @param description a <tt>String</tt> object describing this sort
     * @return an {@link AttributeSort} object
     * @see #putAttributeSort
     */
    AttributeSort getAttributeSort(String description) {
        return (AttributeSort)this.attributesorts.get(description);
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
     * <b>Prints</b> all sorts defined within this context to the specified SDL context, in order of registration.
     * @param sdl a {@link blue.io.SdlContext} object
     */
    public void print(SdlContext sdl) {
        for (int n = 0; n < this.timed.size(); n++) {
            Sort current = (Sort)this.timed.elementAt(n);
            sdl.define(SdlContext.SORT, current.name());
            sdl.write(current.definition());
            sdl.endStatement();
        }
    }
    
    /**
     * <b>Undoes any new sorts</b> created within the current session. Removes these
     * from both the registration list and the names list.
     */
    void undoNewSorts() {
        while (!this.newbies.empty()) {
            this.timed.removeElement(this.aspects.get((String)this.newbies.peek()));
            this.aspects.remove((String)this.newbies.pop());
        }
    }
    
    /** <b>Cleans up</b> this context by clearing all lists, i.e., matches, attribute sorts, names, and registration list. */
    public void cleanup() {
        this.matches.clear();
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
     * {@link RecursiveSort}s. By storing a hook, the name is reserved without registering it yet.
     * @param name a <tt>String</tt> object
     * @throws IllegalArgumentException if this name has already been hooked
     */
    void hook(String name) throws IllegalArgumentException {
        for (int n = 0; n < this.hanging.size(); n++) {
            Object hook = this.hanging.elementAt(n);
            if ((hook instanceof String) && ((String)hook).equals(name))
                throw new IllegalArgumentException("name has already been specified");
            if ((hook instanceof Sort) && ((Sort)hook).name().equals(name))
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
            Object hook = this.hanging.elementAt(n);
            if ((hook instanceof String) && ((String)hook).equals(name)) {
                hook = new RecursiveSort(this, name);
                this.hanging.setElementAt(hook, n);
                return (RecursiveSort)hook;
            }
            if ((hook instanceof RecursiveSort) && ((Sort)hook).name().equals(name))
                return (RecursiveSort)hook;
        }
        return null;
    }
    
    /**
     * Removes the <b>hook</b> to the specified name. If a recursive sort has been
     * associated with this hook, then, the specified sort is assigned as instance
     * to this recursive sort. This recursive sort is returned. Otherwise, the specified sort is returned.
     * @param sort a {@link Sort} object
     * @return a {@link Sort} object
     * @throws IllegalArgumentException if the specified sort equals the resursive sort associated with the hook.
     * @see #hooked
     */
    Sort unhook(Sort sort) throws IllegalArgumentException {
        Object hook = this.hanging.lastElement();
        this.hanging.removeElementAt(this.hanging.size() - 1);
        if (hook == sort)
            throw new IllegalArgumentException("Cyclic definition");
        if (hook instanceof Sort) {
            ((RecursiveSort)hook).setInstance(sort);
            return ((RecursiveSort)hook);
        }
        return sort;
    }
    // matching methods
    
    /**
     * Retrieves the <b>match</b> corresponding the specified description.
     * @param description a <tt>String</tt> object
     * @return a {@link Match} object
     * @see #putMatch
     */
    Match getMatch(String description) {
        return (Match)this.matches.get(description);
    }
    
    /**
     * Stores the specified <b>match</b> with the specified description.
     * @param description a <tt>String</tt> object
     * @param match a {@link Match} object
     * @see #getMatch
     */
    void putMatch(String description, Match match) {
        this.matches.put(description, match);
    }
    
    /**
     * <b>Removes the match</b> corresponding the specified description.
     * @param description a <tt>String</tt> object
     * @see #putMatch
     */
    void removeMatch(String description) {
        this.matches.remove(description);
    }
    
    /**
     * Sets the language for reporting on matches within this context.
     * @param language an integer representing the selected language
     * @see #ENGLISH
     * @see #DEUTSCH
     * @see #NEDERLANDS
     */
    public void setLanguage(int language) {
        this.language = language;
        if (language == NEDERLANDS) {
            this.levelTerms[equivalentLevel] = " is gelijkwaardig met ";
            this.levelTerms[stronglySimLevel] = " is sterk gelijkaardig aan ";
            this.levelTerms[weaklySimLevel] = " is zwak gelijkaardig aan ";
            this.levelTerms[convertibleLevel] = " is omvormbaar tot ";
            this.levelTerms[incompleteLevel] = " is gedeeltelijk omvormbaar tot ";
            this.levelTerms[incongruousLevel] = "zonder overeenkomst";
            this.gradeTerms[concordantGrade] = " stemt overeen met ";
            this.gradeTerms[partOfGrade] = " maakt deel uit van ";
            this.gradeTerms[subsumptiveGrade] = " omvat ";
            this.gradeTerms[partialGrade] = " komt gedeeltelijk overeen met ";
            this.conversionTerms[0] = " (durch uitbreidingen)";
            this.conversionTerms[1] = " (door reducties)";
            this.conversionTerms[2] = " (door uitbreidingen en reducties)";
        } else if (language == DEUTSCH) {
            this.levelTerms[equivalentLevel] = " ist gleichwertig mit ";
            this.levelTerms[stronglySimLevel] = " ist stark aehnlich mit ";
            this.levelTerms[weaklySimLevel] = " ist schwach aehnlich mit ";
            this.levelTerms[convertibleLevel] = " ist verwandelbar in ";
            this.levelTerms[incompleteLevel] = " ist teilweise verwandelbar in ";
            this.levelTerms[incongruousLevel] = "ohne Entsprechung";
            this.gradeTerms[concordantGrade] = " stimmt ueberein mit ";
            this.gradeTerms[partOfGrade] = " ist enthalten in ";
            this.gradeTerms[subsumptiveGrade] = " enthalt ";
            this.gradeTerms[partialGrade] = " teilweise entsprecht ";
            this.conversionTerms[0] = " (durch Erweiterungen)";
            this.conversionTerms[1] = " (durch Einschraenkungen)";
            this.conversionTerms[2] = " (durch Erweiterungen und Einschraenkungen)";
        } else {
            this.levelTerms[equivalentLevel] = " is equivalent to ";
            this.levelTerms[stronglySimLevel] = " is strongly similar to ";
            this.levelTerms[weaklySimLevel] = " is weakly similar to ";
            this.levelTerms[convertibleLevel] = " is convertible to ";
            this.levelTerms[incompleteLevel] = " is partially convertible to ";
            this.levelTerms[incongruousLevel] = "no match";
            this.gradeTerms[concordantGrade] = " is concordant to ";
            this.gradeTerms[partOfGrade] = " is subsumed by ";
            this.gradeTerms[subsumptiveGrade] = " subsumes ";
            this.gradeTerms[partialGrade] = " partially matches ";
            this.conversionTerms[0] = " (through augmentations)";
            this.conversionTerms[1] = " (through diminutions)";
            this.conversionTerms[2] = " (through augmentations and diminutions)";
        }
    }
    
    /**
     * Returns a description reporting on the specified match.
     * @param match a {@link Match} object
     * @return a <tt>String</tt> object
     * @see #setLanguage
     */
    String matchToString(Match match) {
        if (match.isIncongruous())
            return levelTerms[incongruousLevel];
        if (!match.isConcordant() && !((match.lhs() instanceof AttributeSort) && (match.rhs() instanceof AttributeSort)))
            return match.lhs().toString() + gradeTerms[match.grade()] + match.rhs().toString();
        StringBuffer result = new StringBuffer(match.lhs().toString());
        result.append(levelTerms[match.level()]).append(match.rhs().toString());
        if (!match.isConvertible()) {
            if ((match.stats.augs > 0) && (match.stats.dims > 0))
                result.append(conversionTerms[2]);
            else if (match.stats.augs > 0)
                result.append(conversionTerms[0]);
            else if (match.stats.dims > 0)
                result.append(conversionTerms[1]);
        }
        Rational decimal = match.decimalLevel();
        result.append(" [").append(match.level() + decimal.doubleValue()).append('#').append(decimal.toString()).append("] ");
        match.stats.toString(result);
        return result.toString();
    }
    
    /**
     * generates a full .sdl file from an .sdl file that has sort definitions (the top half), but no forms (the bottom half)
     * --.sdl file must contain the keyword "generateInds". This function is called from User.parse(reader s)
     * @since 06.11.2002
     * @author Michael Cumming
     */
    public void generateSdl(User profile, String exampleName) {
        // sortArrayBoth has both sort.toString() + ":" + sort.definition()
        String[] sortArrayBoth = profile.sorts().allSortsS();
        // sortArray has just sort.toString() (from the function: Sorts.allSorts())
        String[] sortArray = profile.sorts().allSorts();
        Sort top = profile.sorts().sortOf("typetree");
        try {
            File file = new File("g:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\inputFiles\\" +
            exampleName + "Output.sdl");
            BufferedWriter aOut = new BufferedWriter(new FileWriter(file));
            aOut.write("#SDL V1.0a [mc] \n\n");
            // this prints out the sorts definitions (top half of an .sdl file)
            for (int n = 0; n < sortArrayBoth.length; n++) {
                aOut.write("sort " + sortArrayBoth[n]);
                Sort temp = profile.sorts().sortOf(sortArray[n]);
                if (temp != null) {
                    aOut.write(" // MATCH to top = " + java.lang.Integer.toString(temp.compare(top)));
                }
                aOut.write(" " + sortType(temp));
                aOut.write("\n");
            }
            //aOut.write("\n");
            // this prints out the form definitions (bottom half of an .sdl file)
            aOut.write("form $top = " + sortArray[sortArray.length - 1] + " :\n");
            // print out the sorts backwards = most important ones first
            for (int n = sortArray.length - 1; n > -1; n--) {
                aOut.write("\"" + sortArray[n] + "\"" + "\n");
            }
            //aOut.write(sdl.toString());
            aOut.flush();
            aOut.close();
        } catch (IOException e) {
            System.out.println(e);
            LogFile.logErr(e.toString(), e.getMessage());
        }
    }
    
    public String sortType(Sort sort) {
        if (sort instanceof PrimitiveSort)
            return "PrimitiveSort";
        if (sort instanceof AttributeSort)
            return "AttributeSort";
        if (sort instanceof DisjunctiveSort)
            return "DisjunctiveSort";
        if (sort instanceof AspectsSort)
            return "AspectsSort";
        if (sort instanceof RecursiveSort)
            return "RecursiveSort";
        if (sort instanceof SimpleSort)
            return "SimpleSort";
        return "SomeOtherSort";
    }
    
    /** Getter for property timed.
     * @return Value of property timed.
     * 27.Aug.2003 MC
     */
    public Vector getTimed() {
        return timed;
    }
    
    /** Setter for property timed.
     * @param timed New value of property timed.
     * 27.Aug.2003 MC
     */
    public void setTimed(Vector timed) {
        this.timed = timed;
    }
    
    /** Getter for property glPage.
     * @return Value of property glPage.
     *
     */
    public GraphLayoutPage getGlDemo() {
        return glPage;
    }
    
    /** Setter for property glPage.
     * @param glPage New value of property glPage.
     *
     */
    public void setGlDemo(GraphLayoutPage glPage) {
        this.glPage = glPage;
    }
    
    /** Getter for property topFrame.
     * @return Value of property topFrame.
     *
     */
    public sortsApp.GraphLayoutTopFrame getTopFrame() {
        return topFrame;
    }
    
    /** Setter for property topFrame.
     * @param topFrame New value of property topFrame.
     *
     */
    public void setTopFrame(sortsApp.GraphLayoutTopFrame topFrame) {
        this.topFrame = topFrame;
    }
    
    public void printMessage(String message) {
        if (topFrame == null) {
            //this.topFrame = tgPanel.getTopFrame();
        }
        topFrame.printMessage(message);
    }
    
}
