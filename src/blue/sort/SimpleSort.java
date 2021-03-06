/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SimpleSort.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 08.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

/**
 * A <b>SimpleSort</b> <p> The <b>SimpleSort</b> interface specifies additional access methods common
 * to primitive sorts and aspects.
 */
public interface SimpleSort {
    /**
     * Returns the class specifying the <b>characteristic</b> individual for this simple sort.
     * @return a <tt>Class</tt> object
     */
    public Class characteristic();

    /**
     * Returns the arguments of this simple sort.
     * @return an {@link blue.struct.Argument} object
     */
    public blue.struct.Argument arguments();
}
