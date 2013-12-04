/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `User.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Enumeration;
import blue.Element;
import blue.AmbiguityException;
import blue.form.Form;
import blue.ind.Individual;
import blue.ind.Keys;
import blue.io.*;

public class User {
    // constants
    private static final Character INDIVIDUAL = new Character('i');
    private static final Character FORM = new Character('f');
    private static final Hashtable users = new Hashtable();
    public static final User intern = new User();
    // representation
    private String name;
    private Sorts sorts;
    private Keys keys;
    private Hashtable storage;

    // constructor
    private User() {
        super();
        this.name = "_intern";
        this.sorts = new Sorts(this);
        this.keys = new Keys(this);
        this.storage = null;
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
    }

    // access methods
    public String name() { return this.name; }

    public Sorts sorts() { return this.sorts; }

    public Keys keys() { return this.keys; }

    public static User find(String name) {
        return (User)users.get(name);
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
            if (result instanceof Element) return (Element)result;
            return null;
        }
        Enumeration e = users.elements();
        while (e.hasMoreElements()) {
            if (result == null)
                result = ((User)e.nextElement()).storage.get(name);
            else if (((User)e.nextElement()).storage.get(name) != null)
                throw new AmbiguityException("Ambiguous variable name");
        }
        if (result == null)
            throw new IllegalArgumentException("Variable name unrecognized");
        if (result instanceof Element) return (Element)result;
        return null;
    }

    public void update(String name, Element data) {
        if (this.storage.get(name) != null) {
            this.storage.put(name, data);
            return;
        }
        User self = null, temp;
        Enumeration e = users.elements();
        while (e.hasMoreElements()) {
            temp = (User)e.nextElement();
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
        this.sorts.cleanup();
        this.storage.clear();
        System.out.println("User profile cleaned up");
    }

    private void parseForm(ParseReader reader) throws ParseException {
        // parse variable name
        if (reader.newToken() != '$')
            throw new ParseException(reader, "'$' expected");
        if (reader.newToken() != Parser.IDENTIFIER)
            throw new ParseException(reader, "Expected a variable name");
        String var = reader.tokenString();
        if (reader.newToken() == ';') {
            this.storage.put(var, FORM);
            return;
        } else if (reader.token() != '=')
            throw new ParseException(reader, "'=' expected");
        // parse sort name
        if (reader.newToken() != Parser.IDENTIFIER)
            throw new ParseException(reader, "Expected a sort name");
        Sort sort = this.sorts.sortOf(reader.tokenString());
        if (sort == null)
            throw new ParseException(reader, "Sort name not recognized");
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
        if (reader.newToken() != '$')
            throw new ParseException(reader, "'$' expected");
        if (reader.newToken() != Parser.IDENTIFIER)
            throw new ParseException(reader, "Expected a variable name");
        String var = reader.tokenString();
        if (reader.newToken() == ';') {
            this.storage.put(var, INDIVIDUAL);
            return;
        } else if (reader.token() != '=')
            throw new ParseException(reader, "'=' expected");
        // parse sort name
        if (reader.newToken() != Parser.IDENTIFIER)
            throw new ParseException(reader, "Expected a sort name");
        Sort sort = this.sorts.sortOf(reader.tokenString());
        if (sort == null)
            throw new ParseException(reader, "Sort name not recognized");
        if (reader.newToken() != ':')
            throw new ParseException(reader, "Missing ':'");
        // parse individual data
        Individual data = Individual.parse(sort, reader);
        this.storage.put(var, data);
        reader.newToken();
    }

    public void parse(String s) throws ParseException {
        ParseReader reader = new ParseReader(new StringReader(s));
        while (reader.token() != 0) {
            if (reader.token() != Parser.IDENTIFIER)
                throw new ParseException(reader, "Expected a keyword");
            String keyword = reader.tokenString();
            if (keyword.equals("sort"))
                this.sorts.define(reader);
            else if (keyword.equals("form"))
                this.parseForm(reader);
            else if (keyword.equals("ind"))
                this.parseInd(reader);
            else
                throw new ParseException(reader, "Keyword unrecognized");
            if (reader.token() != ';')
                throw new ParseException(reader, "Missing ';'");
            reader.newToken();
        }
    }

    public void parse(InputStream s) throws ParseException {
        ParseReader reader = new ParseReader(new InputStreamReader(s));
        while (reader.token() != 0) {
            if (reader.token() != Parser.IDENTIFIER)
                throw new ParseException(reader, "Expected a keyword");
            String keyword = reader.tokenString();
            if (keyword.equals("sort"))
                this.sorts.define(reader);
            else if (keyword.equals("form"))
                this.parseForm(reader);
            else if (keyword.equals("ind"))
                this.parseInd(reader);
            else
                throw new ParseException(reader, "Keyword unrecognized");
            if (reader.token() != ';')
                throw new ParseException(reader, "Missing ';'");
            reader.newToken();
        }
    }

    public void parse(Reader s) throws ParseException {
        ParseReader reader = new ParseReader(s);
        while (reader.token() != 0) {
            if (reader.token() != Parser.IDENTIFIER)
                throw new ParseException(reader, "Expected a keyword");
            String keyword = reader.tokenString();
            // where the sort definitions are parsed
            if (keyword.equals("sort"))
                this.sorts.define(reader);
            // reads in the sorts definitions, then generates instances of each one
            // from the bottom up...not completed, yet
            else if (keyword.equals("generateInds")) {
                this.sorts.generateSdl(this, "latest");
                return;
            }
            else if (keyword.equals("form"))
                this.parseForm(reader);
            else if (keyword.equals("ind"))
                this.parseInd(reader);
            else
                throw new ParseException(reader, "Keyword unrecognized");
            if (reader.token() != ';')
                throw new ParseException(reader, "Missing ';'");
            reader.newToken();
        }
    }
}
        }
    }
}
