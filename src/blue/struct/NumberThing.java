/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `NumberThing.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.struct;

import blue.Thing;

/**
 * The <tt>NumberThing</tt> class provides an implementation of the <tt>Thing</tt> interface to numbers.
 * It implements as final the use counter and all related methods.
 * It also implements as final all specific comparison methods in terms of the general <tt>compare</tt> method.
 */
public abstract class NumberThing extends Number implements Thing {
    // representation
    private int uses;
    // constructor

    /**
     * Constructs a <tt>NumberThing</tt> and initializes its use counter
     * to zero (specifying that the thing is currently not used).
     */
    public NumberThing() { super(); this.uses = 0; }
    // methods

    /** Increments the use counter of this thing. */
    public final void addUse() { this.uses++; }

    /**
     * Decrements the use counter of this thing.
     * @exception IndexOutOfBoundsException Occurs when an attempt is made to decrement a counter with zero value.
     */
    public void delUse() throws IndexOutOfBoundsException {
        if (this.uses-- <= 0)
            throw new IndexOutOfBoundsException("negative use value");
    }

    /**
     * Determines from the use counter whether this thing is being used.
     * @return <tt>true</tt> if this thing is being used, <tt>false</tt> otherwise
     */
    public boolean used() { return (this.uses <= 0); }

    /**
     * Tests whether this thing is strictly less than a specified other thing.
     * Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }

    /**
     * Tests whether this thing is strictly greater than a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }

    /**
     * Tests whether this thing is less than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }

    /**
     * Tests whether this thing is greater than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }
}
