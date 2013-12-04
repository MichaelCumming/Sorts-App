/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SDL.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.parse;

/**
 * The <b>SDL</b> interface specifies a number of constants for the purpose
 * of building or parsing SDL descriptions.
 */
public interface SDL {
    
    /**
     * A SDL file <b>header</b> string specifying the current SDL version.
     */
    public final static String HEADER = "#SDL V1.1a";
    /**
     * A character <b>prefix</b> for a SDL <b>reference</b> descriptor.
     */
    public final static char REFERENCE_PREFIX = '#';
    /**
     * A character <b>prefix</b> for a SDL <b>variable</b> name.
     */
    public final static char VARIABLE_PREFIX = '$';
    /**
     * A <b>keyword</b> string for a SDL <b>sort</b> definition.
     */
    public final static String SORT_KEYWORD = "sort";
    /**
     * A <b>keyword</b> string for a SDL <b>individual</b> definition.
     */
    public final static String INDIVIDUAL_KEYWORD = "ind";
    /**
     * A <b>keyword</b> string for a SDL <b>form</b> definition.
     */
    public final static String FORM_KEYWORD = "form";
}
