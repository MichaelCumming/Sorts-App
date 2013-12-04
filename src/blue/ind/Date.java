/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Date.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 03.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import java.util.Locale;
import java.text.DateFormat;
import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.sort.Sort;
import blue.sort.SimpleSort;
import blue.sort.PrimitiveSort;
import blue.form.DiscreteForm;

/**
 * The <tt>Date</tt> class defines the characteristic individual for
 * instants in time. A date is represented as a number of milliseconds
 * since the standard base time 1/1/1970 00:00:00 GMT and a nil flag. <p> Forms of dates adhere to a discrete behavior.
 * This characteristic individual accepts two arguments that specify the
 * ISO language (ISO-639) and country (ISO-3166) codes for locale-sensitive
 * formatting of a date to a string. Instead, if no arguments are specified,
 * the string representation of a date specifies the number of milliseconds.
 * @see blue.form.DiscreteForm
 * @see java.text.DateFormat
 * @see java.util.Locale
 */
public class Date extends Individual {
    static {
        PrimitiveSort.register(Date.class, DiscreteForm.class, Parameter.LOCALE);
        proto(Date.class, "icons/date.gif");
    }

    // representation
    private long time;
    private String formatted;
    private boolean nil;
    // constructors

    /**
     * Constructs a nondescript <tt>Date</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * date. This date must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    Date() {
        super();
        this.time = 0;
        this.formatted = "";
        this.nil = true;
    }

    private Date(Sort sort, long time, String formatted, boolean nil) {
        super(sort);
        this.time = time;
        this.formatted = formatted;
        this.nil = nil;
    }

    /**
     * Constructs a <tt>Date</tt> with the specified instant in time,
     * for the specified sort. The sort must allow for dates as individuals.
     * @param sort a sort
     * @param time the milliseconds since 1/1/1970 00:00:00 GMT
     * @see Individual#Individual
     */
    public Date(Sort sort, long time) {
        this(sort, time, null, false);
    }

    /**
     * Constructs a <tt>Date</tt> with the specified date expressed as a java.util.Date object, for the specified sort.
     * The sort must allow for dates as individuals.
     * @param sort a sort
     * @param date a <tt>java.util.Date</tt> object
     * @see Individual#Individual
     */
    public Date(Sort sort, java.util.Date date) {
        this(sort, date.getTime(), null, false);
    }
    // access method

    /**
     * Returns the number of milliseconds since 1/1/1970 00:00:00 GMT represented by this <tt>Date</tt> object.
     * @return a long
     */
    public long time() {
        return this.time;
    }

    /**
     * Returns this date as a <tt>java.util.Date</tt> object.
     * @return a <tt>java.util.Date</tt> object
     */
    public java.util.Date getDate() {
        return new java.util.Date(this.time);
    }

    /**
     * Tests whether this individual equals nil. A Date is a nil individual if the nil flag is raised.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return this.nil; }
    // Individual interface methods

    /**
     * Duplicates this individual. It returns a new <tt>Date</tt> with
     * the same time value, defined for the base sort of this date's sort.
     * @return a duplicate date
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Date(this.ofSort().base(), this.time, null, this.nil);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a date. The result is <tt>true</tt> if and only if both dates have equal time values.
     * @param other a date
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Date</tt> object.
     */
    boolean equalValued(Individual other) {
        return this.time == ((Date)other).time;
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Date</tt> object. Otherwise the result is defined by comparing both dates' time values.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Date)) return FAILED;
        long c = this.time - ((Date)other).time;
        if (c < 0) return LESS;
        if (c > 0) return GREATER;
        return EQUAL;
    }

    /**
     * Creates a string representation of this individual's value.
     * The result depends on the sort´s arguments, which specify the
     * locale-sensitive string formatting. In the case that no arguments
     * were specified, the string specifies the exact number of milliseconds.
     * In either case, the date's string that can be included as is in an SDL
     * description and can be subsequently parsed to reveal the original value.
     * @return a string representation of this date, quoted if formatted as a date
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        if (this.formatted != null) return this.formatted;
        Argument arg = null;
        Sort base = this.ofSort().base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        if (arg == null)
            return String.valueOf(this.time);
        String[] codes = (String[]) arg.value();
        Locale locale = new Locale(codes[0], codes[1]);
        this.formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale).format(
            new java.util.Date(this.time));
        this.formatted = '"' + this.formatted + '"';
        return this.formatted;
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this label's string as its text.
     * @param gc a VRML context
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.nil()) gc.label(NIL);
        else if (this.formatted != null)
            gc.label(this.formatted.substring(1, this.formatted.length() - 1));
        else {
            Argument arg = null;
            Sort base = this.ofSort().base();
            if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
            if (arg == null)
                gc.label(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(
                    new java.util.Date(this.time)));
            else {
                String[] codes = (String[]) arg.value();
                Locale locale = new Locale(codes[0], codes[1]);
                this.formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale).format(
                    new java.util.Date(this.time));
                gc.label(this.formatted);
                this.formatted = '"' + this.formatted + '"';
            }
        }
    }

    /**
     * Reads a number or string token from a <tt>ParseReader</tt> object
     * and assigns the value to this date. If the token is of type
     * <tt>STRING</tt>, it's value is unquoted and parsed to a date.
     * @param reader a token reader
     * @exception ParseException Occurs when no integer or string token could be read.
     * @see #NUMBER
     * @see #STRING
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() == NUMBER) {
            this.time = Long.valueOf(reader.tokenString()).longValue();
            this.nil = false;
            this.formatted = null;
            return;
        }
        if (reader.token() != STRING)
            throw new ParseException(reader, "number or string expected");
        this.formatted = reader.tokenString();
        Argument arg = null;
        Sort base = this.ofSort().base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        DateFormat df = null;
        if (arg == null)
            df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        else {
            String[] codes = (String[]) arg.value();
            df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, new Locale(codes[0], codes[1]));
        }
        try {
            this.time = df.parse(this.formatted.substring(1, this.formatted.length() - 1)).getTime();
        } catch (java.text.ParseException e) {
            throw new ParseException(reader, "string does not specify a date");
        }
        this.nil = false;
    }
}
