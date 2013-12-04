/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Date.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.util.Locale;
import java.text.DateFormat;
import cassis.Thing;
import cassis.Element;
import cassis.parse.*;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.sort.Sort;
import cassis.sort.SimpleSort;
import cassis.sort.PrimitiveSort;
import cassis.form.DiscreteForm;

/**
 * A <b>date</b> is a numerical representation of an instant in time.
 * <p>
 * The <b>Date</b> class defines the characteristic individual for
 * dates. A date is represented as a number of milliseconds
 * since the standard base time 1/1/1970 00:00:00 GMT. An additional
 * <i>nil</i> flag specifies a nil value for a date.
 * This characteristic individual accepts two parameters that specify the
 * ISO language (ISO-639) and country (ISO-3166) codes for locale-sensitive
 * formatting of a date to a string. If no parameters are specified,
 * the date's string representation equals the number of milliseconds.
 * Forms of dates adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 * @see java.text.DateFormat
 * @see java.util.Locale
 */
public class Date extends Individual {
    static {
        PrimitiveSort.register(Date.class, DiscreteForm.class, Parameter.LOCALE);
        new cassis.visit.vrml.Proto(Date.class, "icons/date.gif");
    }
    
    // representation
    private long time;
    private String formatted;
    private boolean nil;
    
    // constructors
    
    /**
     * Constructs a nondescript <b>Date</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * date. This date must subsequently be assigned a sort and value.
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
     * Constructs a <b>Date</b> from a number of milliseconds,
     * for the specified sort. The sort must allow for dates as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param time number of milliseconds since 1/1/1970 00:00:00 GMT
     */
    public Date(Sort sort, long time) {
        this(sort, time, null, false);
    }
    /**
     * Constructs a <b>Date</b> from a <tt>java.util.Date</tt> representation,
     * for the specified sort. The sort must allow for dates as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param date a <tt>java.util.Date</tt> object
     */
    public Date(Sort sort, java.util.Date date) {
        this(sort, date.getTime(), null, false);
    }
    
    // access method
    
    /**
     * Returns the number of milliseconds since 1/1/1970 00:00:00 GMT for this date.
     * @return a long
     */
    public long time() {
        return this.time;
    }
    /**
     * Returns a <tt>java.util.Date</tt> representation of this date.
     * @return a <tt>java.util.Date</tt> object
     */
    public java.util.Date getDate() {
        return new java.util.Date(this.time);
    }
    
    /**
     * Tests whether this date equals <b>nil</b>, i.e., the nil flag is raised.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return this.nil; }
    
    // Individual interface methods
    
    /**
     * <b>Duplicates</b> this date. It returns a new individual with
     * the same time value, defined for the base sort of this date's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
        return new Date(this.ofSort().base(), this.time, null, this.nil);
    }
    
    /**
     * Checks whether this date has <b>equal value</b> to another individual.
     * This condition applies if both dates have equal time values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a date
     */
    boolean equalValued(Individual other) {
        return this.time == ((Date) other).time;
    }
    
    /**
     * Compares this date to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a date.
     * Otherwise the result is defined by comparing the time values of both dates.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof Date)) return FAILED;
        long c = this.time - ((Date) other).time;
        if (c < 0) return LESS;
        if (c > 0) return GREATER;
        return EQUAL;
    }
    
    /**
     * Converts the date's <b>value to a string</b>. The result depends on the
     * sort´s parameters, which specify the locale-sensitive string formatting.
     * The formatted string is enclosed with quotes. On the other hand,
     * if no parameters were specified, the string specifies the exact number of
     * milliseconds. In any case, this string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
        if (this.nil()) return NIL;
        if (this.formatted != null) return this.formatted;
        Argument arg = null;
        Sort base = this.ofSort().base();
        if (base instanceof SimpleSort) arg = ((SimpleSort) base).arguments();
        if (arg == null)
            return String.valueOf(this.time);
        // return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(new java.util.Date(ind.time));
        String[] codes = (String[]) arg.value();
        Locale locale = new Locale(codes[0], codes[1]);
        this.formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale).format(new java.util.Date(this.time));
        this.formatted = '"' + this.formatted + '"';
        return this.formatted;
    }
    
    /**
     * Reads an SDL description of a date from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this date. This description consists of
     * an integer number or a quoted string representing a date.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a date
     * @see #toString
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
        if (base instanceof SimpleSort) arg = ((SimpleSort) base).arguments();
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
