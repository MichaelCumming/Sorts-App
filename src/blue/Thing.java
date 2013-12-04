/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Thing.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 06.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue;

/** The <b>Thing</b> interface extends on the <tt>Object</tt> interface, adding abstract methods for comparing things. */
public interface Thing {
    // constants

    /** The integer value resulting from a comparison of two <b>equal</b> things. */
    static final int EQUAL = 0;

    /**
     * The integer value resulting from a comparison of two things,
     * where the first argument is considered strictly <b>less</b> than the second.
     */
    static final int LESS = -1;

    /**
     * The integer value resulting from a comparison of two things,
     * where the first argument is considered strictly <b>greater</b> than the second.
     */
    static final int GREATER = 1;

    /** The integer value resulting from a comparison of two things that cannot be compared. */
    static final int FAILED = -2;
    // methods

    /**
     * <b>Compares</b> this thing with another thing.
     * @param other the comparison thing
     * @return an integer value equal to one of {@link #EQUAL}, {@link #LESS}, {@link #GREATER}, or {@link #FAILED}
     */
    int compare(Thing other);

    /**
     * Tests whether this thing is strictly <b>less than</b> another thing.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    boolean lessThan(Thing other);

    /**
     * Tests whether this thing is strictly <b>greater than</b> another thing.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    boolean greaterThan(Thing other);

    /**
     * Tests whether this thing is <b>less than or equal</b> to another thing.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    boolean lessOrEqual(Thing other);

    /**
     * Tests whether this thing is <b>greater than or equal</b> to another thing.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    boolean greaterOrEqual(Thing other);
}
