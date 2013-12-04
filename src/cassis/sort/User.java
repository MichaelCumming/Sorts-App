/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `User.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 24.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Enumeration;

import cassis.Element;
import cassis.AmbiguityException;
import cassis.form.Form;
import cassis.ind.Individual;
import cassis.ind.Keys;
import cassis.ind.function.Functions;
import cassis.parse.*;

public class User implements SDL {
    
    // constants
    private static final Character INDIVIDUAL = new Character('i');
    public static final Character FORM = new Character('f');
    private static final Hashtable users = new Hashtable();
    public static final User intern = new User();
    
    // representation
    private String name;
    private Sorts sorts;
    private Keys keys;
    public Hashtable storage;
    private Functions functions;
    
    /**constructor */
    private User() {
        super();
        this.name = "_intern";
        this.sorts = new Sorts(this);
        this.keys = new Keys(this);
        this.storage = null;
        this.functions = new Functions(this);
    }
    
    public User(String name) throws IllegalArgumentException {
        super();
        if (!ParseReader.isIdentifier(name))
            throw new IllegalArgumentException("Name must be an identifier");
        if (find(name) != null)
            throw new IllegalArgumentException("Name already defined");
        this.name = name;
        users.put(name, this);
        this.sorts = new Sorts(this);
        this.keys = new Keys(this);
        this.storage = new Hashtable();
        this.functions = new Functions(this);
    }
    
    /** Access methods */
    public String name() { return this.name; }
    public Sorts sorts() { return this.sorts; }
    public Keys keys() { return this.keys; }
    public Functions functions() { return this.functions; }
    
    public static User find(String name) {
        return (User) users.get(name);
    }
    
    public void define(String name) {
        // this.storage.put(name, );
    }
    
    public void deposit(String name, Element data) {
        this.storage.put(name, data);
    }
    
    public Element retrieve(String name) {
        Object result = this.storage.get(name);
        if (result != null) {
            if (result instanceof Element) return (Element) result;
            return null;
        }
        Enumeration enum_ = users.elements();
        while (enum_.hasMoreElements()) {
            if (result == null)
                result = ((User) enum_.nextElement()).storage.get(name);
            else if (((User) enum_.nextElement()).storage.get(name) != null)
                throw new AmbiguityException("Ambiguous variable name");
        }
        System.out.println(">>Attempting to retrieve element: \n" + name + " (cassis.sort.User line 96)");
        if (result == null)
            throw new IllegalArgumentException("Variable name: " + name + " unrecognized");
        if (result instanceof Element) return (Element) result;
        return null;
    }
    
    public void update(String name, Element data) {
        if (this.storage.get(name) != null) {
            this.storage.put(name, data);
            return;
        }
        User self = null, temp;
        Enumeration enum_ = users.elements();
        while (enum_.hasMoreElements()) {
            temp = (User) enum_.nextElement();
            if (temp.storage.get(name) != null) {
                if (self != null)
                    throw new AmbiguityException("Ambiguous variable name");
                self = temp;
            }
        }
        if (self == null)
            throw new IllegalArgumentException("Variable name unrecognized");
        self.storage.put(name, data);
    }
    
    public void cleanup() {
        this.keys.cleanup();
        this.functions.cleanup();
        this.sorts.cleanup();
        this.storage.clear();
    }
    
    private void parseForm(ParseReader reader) throws ParseException {
        // parse variable name
        if (reader.newToken() != VARIABLE_PREFIX)
            throw new ParseException(reader, "'" + VARIABLE_PREFIX + "' expected");
        if (reader.newToken() != Parsing.IDENTIFIER)
            throw new ParseException(reader, "Expected a variable name");
        String var = reader.tokenString();
        if (reader.newToken() == ';') {
            this.storage.put(var, FORM);
            return;
        } else if (reader.token() != '=')
            throw new ParseException(reader, "'=' expected");
        // parse sort expression
        Sort sort;
        if (reader.newToken() == Parsing.IDENTIFIER)
            sort = this.sorts.sortOf(reader.tokenString());
        else if (reader.token() == '(') {
            sort = this.sorts.retrieve(reader);
            if (reader.token() != ')')
                throw new ParseException(reader, "')' expected");
        } else
            throw new ParseException(reader, "Expected a sort name or a parenthesized sort expression, followed by ':'");
        // parse sort name
//	if (reader.newToken() != Parsing.IDENTIFIER)
//	    throw new ParseException(reader, "Expected a sort name");
//	Sort sort = this.sorts.sortOf(reader.tokenString());
        if (sort == null)
            throw new ParseException(reader, "Sort name or expression not recognized");
        if (reader.newToken() != ':')
            throw new ParseException(reader, "Missing ':'");
        // parse form data
        Form data = sort.newForm();
        data.parse(reader);
        this.storage.put(var, data);
        reader.newToken();
    }
    
    private void parseInd(ParseReader reader) throws ParseException {
        // parse variable name
        if (reader.newToken() != VARIABLE_PREFIX)
            throw new ParseException(reader, "'" + VARIABLE_PREFIX + "' expected");
        if (reader.newToken() != Parsing.IDENTIFIER)
            throw new ParseException(reader, "Expected a variable name");
        String var = reader.tokenString();
        if (reader.newToken() == ';') {
            this.storage.put(var, INDIVIDUAL);
            return;
        } else if (reader.token() != '=')
            throw new ParseException(reader, "'=' expected");
        // parse sort expression
        Sort sort;
        if (reader.newToken() == Parsing.IDENTIFIER)
            sort = this.sorts.sortOf(reader.tokenString());
        else if (reader.token() == '(') {
            sort = this.sorts.retrieve(reader);
            if (reader.token() != ')')
                throw new ParseException(reader, "')' expected");
        } else
            throw new ParseException(reader, "Expected a sort name or a parenthesized sort expression, followed by ':'");
        // parse sort name
//	if (reader.newToken() != Parsing.IDENTIFIER)
//	    throw new ParseException(reader, "Expected a sort name");
//	Sort sort = this.sorts.sortOf(reader.tokenString());
        if (sort == null)
            throw new ParseException(reader, "Sort name or expression not recognized");
        if (reader.newToken() != ':')
            throw new ParseException(reader, "Missing ':'");
        // parse individual data
        Individual data = Individual.parse(sort, reader);
        this.storage.put(var, data);
        reader.newToken();
    }
    
    private void parse(ParseReader reader) throws ParseException {
        while (reader.token() != 0) {
            if (reader.token() != Parsing.IDENTIFIER)
                throw new ParseException(reader, "Expected a keyword");
            String keyword = reader.tokenString();
            if (keyword.equals(SORT_KEYWORD))
                this.sorts.define(reader);
            else if (keyword.equals(FORM_KEYWORD))
                this.parseForm(reader);
            else if (keyword.equals(INDIVIDUAL_KEYWORD))
                this.parseInd(reader);
            else
                throw new ParseException(reader, "Keyword unrecognized");
            if (reader.token() != ';')
                throw new ParseException(reader, "Missing ';'");
            reader.newToken();
        }
    }
    
    public void parse(String s) throws ParseException {
        this.parse(new ParseReader(new StringReader(s)));
    }
    
    public void parse(InputStream s) throws ParseException {
        this.parse(new ParseReader(new InputStreamReader(s)));
    }
    
    public void parse(Reader s) throws ParseException {
        this.parse(new ParseReader(s));
    }
    
}
