/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Keys.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 10.3.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.util.Hashtable;

import cassis.Element;
import cassis.form.Form;
import cassis.sort.Sort;
import cassis.sort.SimpleSort;
import cassis.sort.User;

/**
 * The <b>Keys</b> class provides functionality for storing and maintaining reference
 * keys. A <tt>Keys</tt> instance is represented by the user profile it is defined
 * for, a sort of key individuals used to construct reference keys, and facilities
 * for storing keys' base identifiers and maximum offsets, and reference keys
 * together with the data elements these refer to.
 */
public class Keys {

    // representation
    private User profile;
    private Sort dataIDs;
    private Hashtable keys, storage;

    // constructor

    /**
     * Constructs a <b>Keys</b> instance for the specified user profile.
     * @param profile a {@link cassis.sort.User} object
     */
    public Keys(User profile) {
	super();
	this.profile = profile;
	if (User.intern == null)
	    this.dataIDs = profile.sorts().define(profile.name() + "IDs : [Key](" + profile.name() + ')');
	else this.dataIDs = User.intern.sorts().define(profile.name() + "IDs : [Key](" + profile.name() + ')');
	this.keys = new Hashtable();
	this.storage = new Hashtable();
    }

    // access methods

    /**
     * Returns the user <b>profile</b> this <tt>Keys</tt> instance is defined for.
     * @return a {@link cassis.sort.User} object
     */
    public User profile() { return this.profile; }

    /**
     * Returns the <tt>Keys</tt> instance defined for the specified user profile.
     * @param name a string specifying the user profile
     * @return a <tt>Keys</tt> object
     * @see cassis.sort.User#find
     * @see cassis.sort.User#keys()
     */
    static Keys find(String name) {
	User profile = User.find(name);
	if (profile == null) return null;
	return profile.keys();
    }

    /**
     * <b>Puts a key's</b> base identifier and maximum offset in this <tt>Keys</tt>
     * instance. This offset overwrites any earlier value for this base identifier.
     * @param key a base identifier string
     * @param value an integer offset
     */
    void putKey(String key, Long value) {
	this.keys.put(key, value);
    }
    /**
     * <b>Gets a key's</b> maximum offset from this <tt>Keys</tt> instance, given
     * the key's base identifier. This base identifier and offset must have been
     * put previously.
     * @param key a base identifier string
     * @return an integer offset
     * @see #putKey
     */
    Long getKey(String key) {
	return (Long) this.keys.get(key);
    }
    
    /**
     * <b>Generates a key</b> from a base identifier and integer offset. This key
     * belongs to the key sort of this <tt>Keys</tt> instance.
     * @param base a base identifier string
     * @param off an integer offset
     * @return a {@link Key} object
     * @see Key#Key(cassis.sort.Sort, String, long)
     */
    Key generateKey(String base, long off) {
	return new Key(this.dataIDs, base, off);
    }

    /**
     * <b>Deposits</b> a data element in this <tt>Keys</tt> instance and constructs
     * a reference key for this element. If the data element is an {@link Individual},
     * the key's base identifier is chosen as the element's base sort name.
     * Otherwise, the word "data" is used.
     * @param data an {@link Element} object
     * @return a {@link Key} object
     * @see Individual#ofSort()
     */
    Key deposit(Element data) {
	String base = "data";
	if (data instanceof Individual)
	    base = data.ofSort().base().toString();
	Key dataID = new Key(this.dataIDs, base);
	// this.storage.put(dataID.getKey(), data.duplicate());
	this.storage.put(dataID.getKey(), data);
	return dataID;
    }
    /**
     * <b>Deposits</b> a data element in this <tt>Keys</tt> instance and constructs
     * a reference key for this element using the specified base identifier and
     * integer offset.
     * @param data an {@link Element} object
     * @param base a base identifier string
     * @param off an integer offset
     * @return a {@link Key} object
     */
    Key deposit(Element data, String base, long off) {
	Key dataID = new Key(this.dataIDs, base, off);
	// this.storage.put(dataID.getKey(), data.duplicate());
	this.storage.put(dataID.getKey(), data);
	return dataID;
    }

    /**
     * <b>Retrieves</b> a data element corresponding the specified reference key.
     * It uses the identifier argument of the key's base sort in order to determine
     * which <tt>Keys</tt> instance to search.
     * @param dataID a {@link Key} object
     * @return an {@link Element} object
     * @see Key#ofSort()
     * @see cassis.sort.SimpleSort#arguments()
     */
    Element retrieve(Key dataID) {
        Sort base = dataID.ofSort().base();
        if (!(base instanceof SimpleSort)) return null;
	Keys self = find((String) ((SimpleSort) base).arguments().value());
	return (Element) self.storage.get(dataID.getKey());
    }
    /**
     * <b>Deposits</b> a data element corresponding the specified reference key.
     * It uses the identifier argument of the key's base sort in order to determine
     * which <tt>Keys</tt> instance to deposit the element in.
     * @param data an {@link Element} object
     * @param dataID a {@link Key} object
     * @see Key#ofSort()
     * @see cassis.sort.SimpleSort#arguments()
     */
    static void deposit(Element data, Key dataID) {
        Sort base = dataID.ofSort().base();
        if (!(base instanceof SimpleSort)) return;
	Keys self = find((String) ((SimpleSort) base).arguments().value());
	self.storage.put(dataID.getKey(), data);
    }

    /**
     * <b>Cleans up</b> this <tt>Keys</tt> instance by removing all base identifiers
     * and maximum offsets as well as all reference keys and data elements.
     */
    public void cleanup() {
	this.keys.clear();
        this.storage.clear();
    }
}
