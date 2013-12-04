/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Proto.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 2.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io.vrml;

import java.util.Hashtable;
import java.util.Enumeration;

public class Proto {
    // constants
    private static final Hashtable protos = new Hashtable();
    // representations
    private String name;
    private String icon;
    private String definition;
    private int count;

    // constructor
    public Proto(Class characteristic, String icon, String definition) {
        this.name = characteristic.getName();
        this.name = this.name.substring(this.name.lastIndexOf('.') + 1);
        this.icon = icon;
        this.definition = definition;
        this.count = 0;
        protos.put(characteristic, this);
    }

    // access methods
    String name() { return this.name; }

    String icon() { return this.icon; }

    String definition() { return this.definition; }

    String instance() {
        this.count++;
        return this.name + this.count;
    }

    static Proto get(Class characteristic) {
        return (Proto)protos.get(characteristic);
    }

    static void initialize() {
        for (Enumeration e = protos.elements(); e.hasMoreElements(); )
            ((Proto)e.nextElement()).count = 0;
    }

    public static String define(String name, double radius, String color) {
        String str = "PROTO " + name;
        str += " [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO " + name + "Shape [\n";
        str += " field SFVec3f mainTranslation 0 0 0\n";
        str += " field SFVec3f basepoint 0 0 0\n";
        str += " field SFVec3f firstpt   0 0 0\n";
        str += " field SFVec3f secondpt  0 0 0\n";
        str += " field SFVec3f thirdpt   0 0 0\n";
        str += " field SFVec3f fourthpt  0 0 0\n";
        str += " field MFString value \"\"\n  field SFFloat size 0.5 ] {\n";
        str += " Transform { translation IS mainTranslation\n";
        str += "  children [\n";
        if (radius == 0.0) {
            str += "   Billboard {\n    children [\n     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor "
                + color;
            str += " } }\n      geometry Text {\n       string IS value\n       fontStyle FontStyle {\n        size IS size\n        justify [\"END\"] } } } ]\n    axisOfRotation 0 1 0 } ] } }\n\n";
        } else {
            str += "   Group {\n    children [\n     Billboard {\n      children [\n       Transform { translation 0 0.1 0\n        children [\n";
            str += "         Shape {\n          appearance Appearance {\n           material Material {\n            diffuseColor "
                + color;
            str += "} }\n          geometry Text {\n           string IS value\n           fontStyle FontStyle {\n            size IS size\n            justify [\"END\"] } } } ] } ]\n      axisOfRotation 0 1 0 }\n";
            str += "     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor " + color;
            str += " } }\n      geometry Sphere { radius " + radius;
            str += " } } ] } ] } }\n\n";
        }
        return str;
    }
}
