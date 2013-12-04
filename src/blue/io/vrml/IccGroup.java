/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `IccGroup.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io.vrml;

import blue.struct.Vector;

class IccGroup {
    // representation
    private IccObject instance, associate;
    private String name, proto, icon;
    private int count;
    private String base;
    private Vector baseposition;

    // constructor
    IccGroup(String base, Proto proto, int count) {
        this.base = base;
        this.associate = null;
        this.instance = new IccObject(this);
        this.name = proto.instance();
        this.proto = proto.name();
        this.icon = proto.icon();
        this.count = count;
        this.baseposition = null;
    }

    // access methods
    IccObject associate() { return this.associate; }

    IccObject instance() { return this.instance; }

    String name() { return this.name; }

    String proto() { return this.proto; }

    String icon() { return this.base + this.icon; }

    int maxcount() { return this.count; }

    // methods
    IccGroup dependent(String base, Proto proto, int count) {
        IccGroup result = new IccGroup(base, proto, count);
        result.associate = this.instance;
        this.baseposition = result.associate.baseposition();
        this.instance.setAttribute(result);
        return result;
    }
}
