/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `IccObject.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit.vrml;

import cassis.struct.Coord;
import cassis.struct.Vector;

class IccObject {
    // constants
    static final int LABEL = 0;
    static final int SATELLITE = 1;
    static final int LINK = 2;
    static final int JUMP = 3;

    // representation

    private IccGroup group, attribute;
    private Vector position;
    private String offset, label;
    private String linkUrl, imageUrl, target;
    private int count, type;
    private String chain[];

    // constructor

    IccObject(IccGroup group) {
	this.group = group;
	this.attribute = null;
	this.position = null;
	this.offset = null;
	this.label = null;
	this.count = 0;
	this.type = LABEL;
	this.chain = new String[5];
	this.target = null;
    }

    // access methods

    Vector baseposition() {
	return this.position;
    }

    // methods

    private static String approx(double value) {
	String result = Double.toString(value);
	int n = result.indexOf('.');
	if ((n < 0) || (n + 4 > result.length()))
	    return result;
	return result.substring(0, n + 4);
    }

    private static String translation(Vector position) {
//	return approx(position.w().scale(position.x()).doubleValue()) + ' ' +
//	    approx(position.w().scale(position.y()).doubleValue()) + ' ' +
//		approx(position.w().scale(position.z()).doubleValue());
	return position.w().scale(position.x()).doubleValue() + " " +
	    position.w().scale(position.y()).doubleValue() + " " +
		position.w().scale(position.z()).doubleValue();
    }

    private String translation() {
	return translation(this.position);
    }

    void setAttribute(IccGroup group) {
        this.attribute = group;
    }

    void point(Vector position) {
	this.count++;
	this.position = position;
    }

    void label(String label) {
	this.count++;

	Vector base;
	if (this.group.associate() != null)
	    base = this.group.associate().position;
	else 
	    base = new Vector(new Coord(4), new Coord(this.group.maxcount()/2 + 4), Coord.ZERO);

	int x, z;
	x = mod(this.count, 4) - 1;
	if (x == 2) x = 0;
	z = mod(this.count, 4) - 2;
	if (z == -2) z = 0;

	this.position =
	    base.add(new Vector(new Coord(10*x), new Coord(-5*this.count - 1),
				new Coord(10*z), new Coord(5)));
	this.offset = (-2*x) + " 1 " + (-2*z);
	this.chain[0] = (-2*x) + " 0 " + (-2*z);
	this.label = label;
	this.type = LABEL;
    }

    void jump(String label, String target) {
	this.count++;

	Vector base;
	if (this.group.associate() != null)
	    base = this.group.associate().position;
	else 
	    base = new Vector(new Coord(4), new Coord(this.group.maxcount()/2 + 4), Coord.ZERO);

	int x, z;
	x = mod(this.count, 4) - 1;
	if (x == 2) x = 0;
	z = mod(this.count, 4) - 2;
	if (z == -2) z = 0;

	this.position =
	    base.add(new Vector(new Coord(10*x), new Coord(-5*this.count - 1),
				new Coord(10*z), new Coord(5)));
	this.offset = (-2*x) + " 1 " + (-2*z);
	this.chain[0] = (-2*x) + " 0 " + (-2*z);
	this.type = JUMP;
	this.label = label;
	this.target = target;
    }

    void satellite(String label) {
	this.count++;

	Vector base;
	if (this.group.associate() != null)
	    base = this.group.associate().position;
	else
	    base = new Vector(new Coord(4), new Coord(4), Coord.ZERO);

	double angle = Math.PI * 2 / this.group.maxcount() * (this.count-1);
	int radius = this.group.maxcount() * 3;
	long x, z;
	x = Math.round(Math.sin(angle) * radius);
	z = Math.round(Math.cos(angle) * radius);

	this.position = base.add(new Vector(new Coord(x), Coord.ZERO,
					    new Coord(z), new Coord(2)));
	this.offset = "0 0 0";
	this.chain[0] = (-x/2) + " 0 " + (-z/2);
	double xn, zn;
	for (int n = 1; n < 5; n++) {
	    angle += Math.PI / 2 / this.group.maxcount();
	    xn = Math.sin(angle) * radius - (double) x;
	    zn = Math.cos(angle) * radius - (double) z;
	    this.chain[n] = approx(xn/2.0) + " 0 " + approx(zn/2.0);
	}
	this.label = label;
	this.type = SATELLITE;
    }

    void link(String url, String label, String image) {
	this.count++;

	Vector base;
	if (this.group.associate() != null)
	    base = this.group.associate().position;
	else 
	    base = new Vector(new Coord(4), new Coord(this.group.maxcount()/2 + 4), Coord.ZERO);

	int x, z;
	x = mod(this.count, 4) - 1;
	if (x == 2) x = 0;
	z = mod(this.count, 4) - 2;
	if (z == -2) z = 0;

	this.position =
	    base.add(new Vector(new Coord(10*x), new Coord(-5*this.count - 1),
				new Coord(10*z), new Coord(5)));
	this.offset = (-2*x) + " 1 " + (-2*z);
	this.chain[0] = (-2*x) + " 0 " + (-2*z);

	this.linkUrl = url;
	this.imageUrl = image;
	this.label = label;
	this.type = LINK;
    }

    private static int mod(int value, int quotient) {
	return value - (value/quotient)*quotient;
    }

    String name(String type) {
	return this.name(type, this.count);
    }
    private String name(String type, int count) {
	return this.group.name() + "_" + type + "_" + count;
    }

    private void controlArrow(StringBuffer buffer, String dir, String icon) {
	buffer.append("       ").append(dir).append("Arrow {\n        children [\n         ").append(dir);
	if (icon != null)
	    buffer.append("ArrowShape { typeImg \"").append(icon).append("\" }\n         DEF ");
	else buffer.append("ArrowShape { }\n         DEF ");
	buffer.append(this.name(dir.charAt(0) + "ARROW_TOUCH")).append(" TouchSensor { } ] },\n");
    }

    String controlPanel() {
	if (this.count == 0) return "";

	StringBuffer buffer = new StringBuffer("ControlPanel { mainTranslation ").append(this.translation());
	buffer.append("\n children [\n  Transform {\n   children [\n    DEF ").append(this.name("PANEL"));
	buffer.append(" Switch {\n     choice Billboard {\n      children [\n       BackPanel { },\n");

	// arrows
	if (this.count != 1)
	    this.controlArrow(buffer, "Left", null);
	if (this.count != this.group.maxcount())
	    this.controlArrow(buffer, "Right", null);
	if (this.group.associate() != null) 
	    this.controlArrow(buffer, "Up", this.group.associate().group.icon());
	if (this.attribute != null)
	    this.controlArrow(buffer, "Down", this.attribute.icon());

	buffer.append("      ]\n      axisOfRotation 0 1 0 }\n");
	if ((this.group.associate() != null) || (this.count > 1))
	    buffer.append("     whichChoice -1 }\n    ");
	else buffer.append("     whichChoice 0 }\n    ");
	buffer.append(this.group.proto());
	buffer.append(" {\n     children [\n      DEF ").append(this.name("ITEM")).append(" Switch {\n       choice [\n        Group {\n         children [\n          ");
	buffer.append("Group {\n           children [\n            ");
	buffer.append(this.group.proto());
	if (this.type == LINK) {
	    buffer.append("Shape { textLabel \"").append(this.label).append("\"\n           linkURL \"").append(this.linkUrl);
	    if (this.imageUrl != null)
		buffer.append("\"\n           imageURL \"").append(this.imageUrl).append("\" }");
	    else buffer.append("\" }");
	} else if (this.label != null)
	    buffer.append("Shape { value \"").append(this.label).append("\" }");
        else
	    buffer.append("Shape { }");
	if (this.type == JUMP)
	    buffer.append("\n      DEF ").append(this.name("JUMP_TOUCH")).append(" TouchSensor { } ");
	buffer.append(" ] }\n          Group {\n           children [\n            Shape {\n             appearance Appearance {\n              material Material {\n");
	if (this.type == SATELLITE)
	    buffer.append("               diffuseColor 0.6 0.15 0.15 } }\n");
	else
	    buffer.append("               diffuseColor 0.41 0.35 0.04 } }\n");
	buffer.append("             geometry Extrusion {\n              spine [ 0 0 0, ").append(this.chain[0]).append(" ]\n              crossSection [ -0.02 -0.02, -0.02 0.02, 0.02 0.02, 0.02 -0.02, -0.02 -0.02 ]\n              solid FALSE } }\n");
	buffer.append("            Shape {\n             appearance Appearance {\n              material Material {\n");
	if (this.type == SATELLITE) {
	    buffer.append("               diffuseColor 0.6 0.15 0.15 } }\n             geometry IndexedLineSet {\n              coord Coordinate {\n               point [ 0 0 0, ");
	    buffer.append(this.chain[1]).append(", ").append(this.chain[2]).append(", ").append(this.chain[3]).append(", ").append(this.chain[4]);
	    buffer.append(" ] }\n              coordIndex [ 0 1 2 3 4 ] } } ] } ] } ]\n");
	} else {
	    buffer.append("               diffuseColor 0.41 0.35 0.04 } }\n             geometry Sphere { radius 0.2 } } ] }\n          Disk { mainTranslation ");
	    buffer.append(this.offset).append("\n           children [\n            DiskShape { } ] } ] } ]\n");
	}
	if (this.group.associate() != null) {
	    IccObject assoc = this.group.associate();
	    if ((assoc.group.associate() == null) &&
		(assoc.count == 1))
		buffer.append("       whichChoice 0 }\n      DEF ");
	    else buffer.append("       whichChoice -1 }\n      DEF ");
	} else buffer.append("       whichChoice 0 }\n      DEF ");

	buffer.append(this.name("TOUCH")).append(" TouchSensor { } ] },\n    DEF ");
	buffer.append(this.name("VIEW")).append(" Viewpoint {\n");
	if (this.position.x().isZero() && this.position.z().isZero())
	    buffer.append("     position 0 0 6");
	else {
	    double x = position.w().scale(position.x()).doubleValue();
	    double z = position.w().scale(position.z()).doubleValue();
	    double angle = Math.atan2(x, z);
	    buffer.append("     position ").append(approx(6.0 * Math.sin(angle)));
	    buffer.append(" 0 ").append(approx(6.0 * Math.cos(angle))).append('\n');
	    buffer.append("     orientation 0 1 0 ").append(approx(angle));
	}
	return buffer.append(" } ] } ] }\n\n").toString();
    }

    private static final String SCRIPT = " Script {\n url \"vrmlscript:\n function set(bool, eventTime) {\n  if (bool) { settrue = TRUE; on = 0; off = -1; } }\"\n eventIn SFBool set\n eventOut SFBool settrue\n eventOut SFInt32 on\n eventOut SFInt32 off\n}\n\n";

    String controlScript() {
	if (this.count == 0) return "";

	StringBuffer buffer = new StringBuffer("DEF ");
	buffer.append(this.name("SELECT")).append(SCRIPT);
	buffer.append("DEF ").append(this.name("DESELECT")).append(SCRIPT);
	buffer.append("DEF ").append(this.name("SHOW")).append(SCRIPT);
	buffer.append("DEF ").append(this.name("HIDE")).append(SCRIPT);
	return buffer.toString();
    }

    String controlRoute() {
	if (this.count == 0) return "";
	StringBuffer buffer;

	// touch
	buffer = new StringBuffer("ROUTE ").append(this.name("TOUCH")).append(".isActive TO ").append(this.name("SELECT")).append(".set\n");
	if (this.group.associate() != null)
	    buffer.append("ROUTE ").append(this.name("UARROW_TOUCH")).append(".isActive TO ").append(this.group.associate().name("SELECT")).append(".set\n");
	if (this.count > 1)
	    buffer.append("ROUTE ").append(this.name("LARROW_TOUCH")).append(".isActive TO ").append(this.name("SELECT", this.count - 1)).append(".set\n");
	if (this.count < this.group.maxcount())
	    buffer.append("ROUTE ").append(this.name("RARROW_TOUCH")).append(".isActive TO ").append(this.name("SELECT", this.count + 1)).append(".set\n");
	if (this.attribute != null)
	    buffer.append("ROUTE ").append(this.name("DARROW_TOUCH")).append(".isActive TO ").append(this.attribute.instance().name("SELECT", 1)).append(".set\n");

	// select
	String select = "ROUTE " + this.name("SELECT");
	buffer.append(select).append(".on TO ").append(this.name("PANEL")).append(".whichChoice\n");
	buffer.append(select).append(".settrue TO ").append(this.name("VIEW")).append(".set_bind\n");
	if (this.group.associate() != null)
	    buffer.append(select).append(".off TO ").append(this.group.associate().name("PANEL")).append(".whichChoice\n");
	for (int n = 1; n <= this.group.maxcount(); n++) {
	    if (n == this.count) continue;
	    buffer.append(select).append(".settrue TO ").append(this.name("DESELECT", n)).append(".set\n");
	    buffer.append(select).append(".on TO ").append(this.name("ITEM", n)).append(".whichChoice\n");
	}
	if (this.attribute != null)
	    for (int n = 1; n <= this.attribute.maxcount(); n++) {
		buffer.append(select).append(".settrue TO ").append(this.attribute.instance().name("DESELECT", n)).append(".set\n");
		buffer.append(select).append(".on TO ").append(this.attribute.instance().name("ITEM", n)).append(".whichChoice\n");
	    }

	// deselect
	String deselect = "ROUTE " + this.name("DESELECT");
	buffer.append(deselect).append(".off TO ").append(this.name("PANEL")).append(".whichChoice\n");
	if (this.attribute != null)
	    for (int n = 1; n <= this.attribute.maxcount(); n++) {
		buffer.append(deselect).append(".settrue TO ").append(this.attribute.instance().name("DESELECT", n)).append(".set\n");
		buffer.append(deselect).append(".off TO ").append(this.attribute.instance().name("ITEM", n)).append(".whichChoice\n");
	    }
/*
	// hide
	String hide = "ROUTE ").append(this.name("HIDE"));
	if (this.group.associate() != null)
	    buffer.append(hide).append(".settrue TO ").append(this.group.associate().name("HIDE")).append(".set\n");
	for (int n = 1; n <= this.group.maxcount(); n++)
	    buffer.append(hide).append(".off TO ").append(this.name("ITEM", n)).append(".whichChoice\n");
*/
	// hide
	String hide = "ROUTE " + this.name("HIDE");
	buffer.append(hide).append(".off TO ").append(this.name("ITEM")).append(".whichChoice\n");
	if (this.count < this.group.maxcount())
	    buffer.append(hide).append(".settrue TO ").append(this.name("HIDE", this.count + 1)).append(".set\n");

	// show
	String show = "ROUTE " + this.name("SHOW");
	if (this.group.associate() != null)
	    buffer.append(show).append(".settrue TO ").append(this.group.associate().name("SHOW")).append(".set\n");
	for (int n = 1; n <= this.group.maxcount(); n++)
	    buffer.append(show).append(".on TO ").append(this.name("ITEM", n)).append(".whichChoice\n");

	// jump
	if (this.type == JUMP) {
	    buffer.append("ROUTE ").append(this.name("JUMP_TOUCH")).append(".isActive TO ").append(this.name("DESELECT")).append(".set\n");
	    buffer.append("ROUTE ").append(this.name("JUMP_TOUCH")).append(".isActive TO ").append(this.name("HIDE", 1)).append(".set\n");
	    if (this.target != null) {
		int index = this.target.lastIndexOf('_');
		String name = this.target.substring(0, index);
		String count = this.target.substring(index);
		buffer.append("ROUTE ").append(this.name("JUMP_TOUCH")).append(".isActive TO ").append(name).append("SHOW").append(count).append(".set\n");
		buffer.append("ROUTE ").append(this.name("JUMP_TOUCH")).append(".isActive TO ").append(name).append("SELECT").append(count).append(".set\n");
	    }
	}

	return buffer.append('\n').toString();
    }
}
