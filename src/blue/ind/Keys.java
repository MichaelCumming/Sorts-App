/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Keys.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import java.util.Hashtable;
import blue.Element;
import blue.form.Form;
import blue.sort.Sort;
import blue.sort.SimpleSort;
import blue.sort.User;

public class Keys {
    // representation
    private User profile;
    private Sort dataIDs;
    private Hashtable keys, storage;

    // constructor
    public Keys(User profile) {
        super();
        this.profile = profile;
        if (User.intern == null)
            this.dataIDs = profile.sorts().define(profile.name() + "IDs : [Key](" + profile.name() + ')');
        else
            this.dataIDs = User.intern.sorts().define(profile.name() + "IDs : [Key](" + profile.name() + ')');
        this.keys = new Hashtable();
        this.storage = new Hashtable();
    }

    // access methods
    public User profile() { return this.profile; }

    static Keys find(String name) {
        User profile = User.find(name);
        if (profile == null) return null;
        return profile.keys();
    }

    void putKey(String key, Long value) {
        this.keys.put(key, value);
    }

    Long getKey(String key) {
        return (Long)this.keys.get(key);
    }

    Key generateKey(String base, long off) {
        return new Key(this.dataIDs, base, off);
    }

    Key deposit(Element data) {
        String base = "data";
        if (data instanceof Individual)
            base = data.ofSort().base().toString();
        Key dataID = new Key(this.dataIDs, base);
        // this.storage.put(dataID.getKey(), data.duplicate());
        this.storage.put(dataID.getKey(), data);
        return dataID;
    }

    Key deposit(Element data, String base, long off) {
        Key dataID = new Key(this.dataIDs, base, off);
        // this.storage.put(dataID.getKey(), data.duplicate());
        this.storage.put(dataID.getKey(), data);
        return dataID;
    }

    Element retrieve(Key dataID) {
        Sort base = dataID.ofSort().base();
        if (!(base instanceof SimpleSort)) return null;
        Keys self = find((String)((SimpleSort)base).arguments().value());
        return (Element)self.storage.get(dataID.getKey());
    }

    static void deposit(Element data, Key dataID) {
        Sort base = dataID.ofSort().base();
        if (!(base instanceof SimpleSort)) return;
        Keys self = find((String)((SimpleSort)base).arguments().value());
        self.storage.put(dataID.getKey(), data);
    }

    // sorts interface methods
    public void cleanup() {
        this.keys.clear();
    }
}
