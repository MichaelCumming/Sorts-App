/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Proto.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 2.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit.vrml;

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

    private Proto(Class characteristic, String icon, String definition) {
	this.name = characteristic.getName();
        this.name = this.name.substring(this.name.lastIndexOf('.') + 1);
	this.icon = icon;
	this.definition = definition;
	this.count = 0;
	protos.put(characteristic, this);
    }
    /**
     * Constructs a VRML prototype with the specified icon for a characteristic
     * individual. The definition string is dependent on the name of the
     * characteristic individual
     * @param characteristic a characteristic individual class
     * @param icon an icon path
     * @see cassis.visit.vrml#Proto
     */
    public Proto(Class characteristic, String icon) {
        this(characteristic, icon, null);
        if (characteristic == cassis.sort.AttributeSort.class)
            this.definition = define("AttributeSort", 0.2, "0.6 0.15 0.15");
        else if (characteristic == cassis.sort.DisjunctiveSort.class)
            this.definition = define("DisjunctiveSort", 0.2, "0.6 0.15 0.15");
        else if (characteristic == cassis.ind.Url.class)
            this.definition = urlProto();
        else if (characteristic == cassis.ind.ImageUrl.class)
            this.definition = imageUrlProto();
        else if (characteristic == cassis.ind.Property.class)
            this.definition = propertyProto();
        else if (characteristic == cassis.ind.Point.class) {
            String str = "PROTO Point [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
            str += "PROTO PointShape [] {\n Shape {\n  appearance Appearance {\n   material Material {\n    diffuseColor 0.41 0.35 0.04 } }\n  geometry Sphere { radius 0.2 } } }\n\n";
            this.definition = str;
        } else {
            String name = characteristic.getName();
            int index = name.lastIndexOf('.');
            if (index > 0) name = name.substring(index + 1);
            this.definition = proto(name);
        }
    }
    private static String urlProto() {
	String str = "PROTO Url [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";

	str += "PROTO UrlShape [\n field SFVec3f textTranslation 0.4 0.4 0.4\n field SFColor textColor 0.32 0.4 0.698\n field MFString linkURL \"\"\n field SFString linkDescription \"view the page\"\n field MFString textLabel \"\"\n field SFFloat textSize 0.5 ] {\n";
	str += " Anchor {\n  children Billboard {\n   children [\n    Transform { translation IS textTranslation\n     children Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor IS textColor } }\n";
	str += "      geometry Text {\n       string IS textLabel\n       fontStyle FontStyle {\n        size IS textSize\n        justify [\"MIDDLE\"] } } } } ]\n   axisOfRotation 0 1 0 }\n";
	str += "  parameter [ \"target=IMAGE_PAGE\" ]\n  url IS linkURL\n  description IS linkDescription } }\n\n";
	return str;
    }
    private static String propertyProto() {
	String str = "PROTO Property [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";

	str += "PROTO PropertyShape [\n field SFVec3f mainTranslation 0.4 0.4 0.4\n field SFColor color 0.1 0.6 0.6\n field MFString value \"\"\n field SFFloat size 0.5 ] {\n";
	str += " Transform { translation IS mainTranslation\n  children [\n   Billboard {\n    children Shape {\n     appearance Appearance {\n      material Material {\n       diffuseColor IS color } }\n";
	str += "     geometry Text {\n      string IS value\n      fontStyle FontStyle {\n       size IS size\n       justify [\"MIDDLE\"] } } }\n    axisOfRotation 0 1 0 }\n";
	str += "   Shape {\n    appearance Appearance {\n     material Material {\n      diffuseColor IS color } }\n    geometry Cone {\n     bottomRadius 0.18\n     height 0.27 } } ] } }\n\n";
	return str;
    }
    private static String imageUrlProto() {
	String str = "PROTO ImageUrl [\n field SFVec3f mainTranslation  -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";

	str += "PROTO ImageUrlShape [\n field SFVec3f textTranslation 1 0.7 0.4\n field SFColor panelColor 0.5 0.5 0.5\n field SFColor textColor 0.32 0.4 0.698\n field MFString imageURL \"icons/default.gif\"\n";
	str += " field MFString linkURL \"\"\n field SFString linkDescription \"view the image\"\n field MFVec3f panelFrame [0 0 0, 2 0 0, 2 1.5 0,  0 1.5 0]\n field MFString textLabel \"\"\n field SFFloat textSize 0.5\n]\n{\n";
	str += " Anchor {\n  children Billboard {\n   children [\n    Shape {\n     appearance Appearance {\n      material Material {\n       ambientIntensity 0.25\n       diffuseColor IS panelColor\n";
	str += "       specularColor 0.0955906 0.0955906 0.0955906\n       emissiveColor 0 0 0\n       shininess 0.078125\n       transparency 0 }\n      texture ImageTexture {\n       url IS imageURL } }\n";
	str += "     geometry IndexedFaceSet {\n      coord Coordinate {\n       point IS panelFrame }\n      coordIndex [ 0, 1, 2, 3, -1]\n      solid FALSE\n } }\n";
	str += "    Transform { translation IS textTranslation\n     children Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor IS textColor } }\n";
	str += "      geometry Text {\n       string IS textLabel\n       fontStyle FontStyle {\n        size IS textSize\n        justify [\"MIDDLE\"] } } } } ]\n   axisOfRotation 0 1 0 }\n";
	str += "  parameter [ \"target=IMAGE_PAGE\" ]\n  url IS linkURL\n  description IS linkDescription } }\n\n";
	return str;
    }
    private static String proto(String name) {
	String str = "PROTO " + name;
	str += " [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";

	str += "PROTO " + name;
	str += "Shape [\n field SFVec3f mainTranslation 0 0.1 0\n field MFString value \"\"\n field SFFloat size 0.5 ] {\n Transform { translation IS mainTranslation\n  children [\n   Billboard {\n    children [\n";
	str += "     Shape {\n      geometry Text {\n       string IS value\n       fontStyle FontStyle {\n        size IS size\n        justify [\"MIDDLE\"] } } } ]\n    axisOfRotation 0 1 0 } ] } }\n\n";
	return str;
    }
    private static String define(String name, double radius, String color) {
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
	    str += "   Billboard {\n    children [\n     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor " + color;
	    str += " } }\n      geometry Text {\n       string IS value\n       fontStyle FontStyle {\n        size IS size\n        justify [\"END\"] } } } ]\n    axisOfRotation 0 1 0 } ] } }\n\n";
	} else {
	    str += "   Group {\n    children [\n     Billboard {\n      children [\n       Transform { translation 0 0.1 0\n        children [\n";
	    str += "         Shape {\n          appearance Appearance {\n           material Material {\n            diffuseColor " + color;
	    str += "} }\n          geometry Text {\n           string IS value\n           fontStyle FontStyle {\n            size IS size\n            justify [\"END\"] } } } ] } ]\n      axisOfRotation 0 1 0 }\n";
	    str += "     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor " + color;
	    str += " } }\n      geometry Sphere { radius " + radius;
	    str += " } } ] } ] } }\n\n";
	}
	return str;
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
	return (Proto) protos.get(characteristic);
    }

    static void initialize() {
	for (Enumeration e = protos.elements() ; e.hasMoreElements() ;)
	    ((Proto) e.nextElement()).count = 0;
    }
}
