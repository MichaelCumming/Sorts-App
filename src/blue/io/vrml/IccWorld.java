/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `IccWorld.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 04.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io.vrml;

import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import blue.io.GraphicsContext;

public class IccWorld implements GraphicsContext {
    // representation
    private String base, inits;
    private StringBuffer data, scripts, routes;
    private IccGroup current;
    private Stack history;
    private Vector protos;
    private Hashtable targets;

    // constructor
    public IccWorld(String base) {
        this.base = base;
        this.inits = this.initialize(base);
        this.data = new StringBuffer();
        this.scripts = new StringBuffer();
        this.routes = new StringBuffer();
        this.current = null;
        this.history = new Stack();
        this.protos = new Vector();
        this.targets = new Hashtable();
    }

    // public methods
    public void clear() {
        this.data.setLength(0);
        this.scripts.setLength(0);
        this.routes.setLength(0);
        this.current = null;
        this.history.removeAllElements();
        this.protos.removeAllElements();
        this.targets.clear();
    }

    public void register(String reference) {
        Object target = this.targets.get(reference);
        if (target != null) {
            if (target instanceof String) return;
            for (int n = 0; n < ((Vector)target).size(); n++) {
                String source = (String)((Vector)target).elementAt(n);
                int index = source.lastIndexOf('_');
                String name = source.substring(0, index);
                int count = Integer.parseInt(source.substring(index + 1)) + 1;
                this.routes.append("ROUTE ").append(name).append("JUMP_TOUCH_").append(count).append(".isActive TO ");
                this.routes.append(this.current.instance().name("SHOW")).append(".set\n");
                this.routes.append("ROUTE ").append(name).append("JUMP_TOUCH_").append(count).append(".isActive TO ");
                this.routes.append(this.current.instance().name("SELECT")).append(".set\n\n");
            }
        }
        this.targets.put(reference, this.current.instance().name(""));
    }

    public String toString() {
        StringBuffer result = new StringBuffer(this.inits);
        for (int n = 0; n < this.protos.size(); n++)
            result.append(((Proto)this.protos.elementAt(n)).definition());
        result.append(MAINVIEWPT).append(this.data.toString());
        result.append(this.scripts.toString()).append(this.routes.toString());
        return result.toString();
    }

    public String[] toStringArray() {
        int size = this.protos.size() + 5;
        String[] result = new String[size];
        size = 0;
        result[size++] = this.inits;
        for (int n = 0; n < this.protos.size(); n++)
            result[size++] = ((Proto)this.protos.elementAt(n)).definition();
        result[size++] = MAINVIEWPT;
        result[size++] = this.data.toString();
        result[size++] = this.scripts.toString();
        result[size++] = this.routes.toString();
        return result;
    }

    public void beginGroup(Class characteristic, int count) {
        Proto proto = Proto.get(characteristic);
        if (proto == null)
            throw new IllegalArgumentException("Proto not found for '" + characteristic + "'");
        if (!this.protos.contains(proto)) this.protos.addElement(proto);
        if (this.current != null) {
            this.history.push(this.current);
            this.current = this.current.dependent(this.base, proto, count);
        } else
            this.current = new IccGroup(this.base, proto, count);
    }

    public void endGroup() {
        this.process();
        if (this.history.empty())
            this.current = null;
        else
            this.current = (IccGroup)this.history.pop();
    }

    public void point(blue.struct.Vector position) {
        this.process();
        // if (this.current.type != POINT)
        //    throw new InvalidMethodException("No current point");
        this.current.instance().point(position);
    }

    public void lineSegment(blue.struct.Vector tail, blue.struct.Vector head) { }

    public void label(String s) {
        this.process();
        this.current.instance().label(s);
    }

    public void jump(String reference) {
        this.process();
        Object target = this.targets.get(reference);
        if (target == null)
            this.targets.put(reference, target = new Vector());
        else if (target instanceof String) {
            this.current.instance().jump(reference, (String)target);
            return;
        }
        ((Vector)target).addElement(this.current.instance().name(""));
        this.current.instance().jump(reference, null);
    }

    public void satellite(String s) {
        this.process();
        this.current.instance().satellite(s);
    }

    public void link(String url, String s, String image) {
        this.process();
        this.current.instance().link(url, s, image);
    }

    // private methods
    private void process() {
        this.scripts.append(this.current.instance().controlScript());
        this.routes.append(this.current.instance().controlRoute());
        this.data.append(this.current.instance().controlPanel());
    }

    private static final String MAINVIEWPT = "DEF Main Viewpoint {\n position 4 4 15\n orientation 0 1 0 0\n description \"Main\" }\n\n";
    private static final String LRARRSHAPE = "  geometry IndexedFaceSet {\n   coord Coordinate {\n    point [ 0 0 0, 1 0 0, 1 1 0, 0 1 0 ] }\n   coordIndex [ 0, 1, 2, 3, -1 ]\n   solid FALSE } } }\n\n";
    private static final String UDARRSHAPE = "    geometry IndexedFaceSet {\n     coord Coordinate {\n      point [ 0 0 0, 1 0 0, 1 1 0, 0 1 0 ] }\n     coordIndex [ 0, 1, 2, 3, -1 ]\n     solid FALSE } },\n";
    private static final String UDIMGSHAPE = "    children [\n     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor 0.41 0.35 0.04 }\n       texture ImageTexture { url IS typeImg } }\n      geometry IndexedFaceSet {\n       coord Coordinate {\n        point [ 0 0 0, -1 0 0, -1 1 0, 0 1 0 ] }\n       coordIndex [ 0, 1, 2, 3, -1 ]\n       solid FALSE } } ] } ] } }\n\n";

    private static String initialize(String base) {
        // header
        StringBuffer buffer = new StringBuffer("#VRML V2.0 utf8\n\nNavigationInfo {\n avatarSize [0 1 1000]\n }\n\n");
        // control panel
        buffer.append("PROTO ControlPanel [\n field SFVec3f mainTranslation 0 0 0\n field SFVec3f indTranslation 0 0 0\n field MFNode children [] ] {\n");
        buffer.append(" Transform { translation IS mainTranslation\n  children [\n    Transform { translation IS indTranslation\n     children IS children } ] } }\n\n");
        // back panel
        buffer.append("PROTO BackPanel [\n field MFString myURL \"#Main\"\n field SFString myDescription \"Move to main viewpoint\" ] {\n");
        buffer.append(" Anchor {\n  children Shape {\n   appearance Appearance {\n    material Material {\n     diffuseColor 0.1 0.1 0.1 } }\n");
        buffer.append("   geometry IndexedFaceSet {\n    coord Coordinate {\n     point [ -0.5 0 0, 0 -0.5 0, 0.5 0 0, 0 0.5 0 ] }\n    coordIndex [ 0, 1, 2, 3, -1 ]\n    solid FALSE } }\n  url IS myURL\n  description IS myDescription } }\n\n");
        // left arrow
        buffer.append("PROTO LeftArrow [\n field MFNode children [] ] {\n Transform {\n  translation -0.5 -0.2 0.3\n  scale 0.2 0.4 1\n  children IS children } }\n\n");
        // left arrow shape
        buffer.append("PROTO LeftArrowShape [\n field MFString leftArrowImg \"").append(base);
        buffer.append("icons/leftarrow.gif\" ] {\n Shape {\n  appearance Appearance {\n   material Material {\n    diffuseColor 0.01 0.02 0.28 }\n   texture ImageTexture { url IS leftArrowImg } }\n").append(LRARRSHAPE);
        // right arrow
        buffer.append("PROTO RightArrow [\n field MFNode children [] ] {\n Transform {\n  translation 0.3 -0.2 0.3\n  scale 0.2 0.4 1\n  children IS children } }\n\n");
        // right arrow shape
        buffer.append("PROTO RightArrowShape [\n field MFString rightArrowImg \"").append(base);
        buffer.append("icons/rightarrow.gif\" ] {\n Shape {\n  appearance Appearance {\n   material Material {\n    diffuseColor 0.01 0.02 0.28 }\n   texture ImageTexture { url IS rightArrowImg } }\n").append(LRARRSHAPE);
        // up arrow
        buffer.append("PROTO UpArrow [\n field MFNode children [] ] {\n Transform {\n  translation -0.2 0.3 0.3\n  scale 0.4 0.2 1\n  children IS children } }\n\n");
        // up arrow shape
        buffer.append("PROTO UpArrowShape [\n field MFString upArrowImg \"").append(base);
        buffer.append("icons/uparrow.gif\"\n field MFString typeImg \"\" ] {\n Group {\n  children [\n   Shape {\n    appearance Appearance {\n     material Material {\n      diffuseColor 0.41 0.35 0.04 }\n     texture ImageTexture { url IS upArrowImg } }\n").append(UDARRSHAPE);
        buffer.append("   Transform { translation 1 -1.1 0\n").append(UDIMGSHAPE);
        // down arrow
        buffer.append("PROTO DownArrow [\n field MFNode children [] ] {\n Transform {\n  translation -0.2 -0.5 0.3\n  scale 0.4 0.2 1\n  children IS children } }\n\n");
        // down arrow shape
        buffer.append("PROTO DownArrowShape [\n field MFString downArrowImg \"").append(base);
        buffer.append("icons/downarrow.gif\"\n field MFString typeImg \"\" ] {\n Group {\n  children [\n   Shape {\n    appearance Appearance {\n     material Material {\n      diffuseColor 0.41 0.35 0.04 }\n     texture ImageTexture { url IS downArrowImg } }\n").append(UDARRSHAPE);
        buffer.append("   Transform { translation 1 1.1 0\n").append(UDIMGSHAPE);
        // disk
        buffer.append("PROTO Disk [\n field SFVec3f mainTranslation 0 0 0\n field MFNode children [] ] {\n Transform {\n  translation IS mainTranslation\n  children IS children } }\n\n");
        // disk shape
        buffer.append("PROTO DiskShape [] {\n Group {\n  children [\n   Transform { translation 0 -0.5 0\n    children [\n     Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor 0.41 0.35 0.04 } }\n");
        buffer.append("      geometry Cylinder {\n       radius 0.01\n       height 1 } } ] },\n");
        buffer.append("   Transform { translation 0 -1 0\n    children [\n     Shape {\n    appearance Appearance {\n       material Material {\n        diffuseColor 0.41 0.35 0.04 } }\n");
        buffer.append("      geometry Cylinder {\n       radius 0.5\n       height 0.04 } } ] } ] } }\n\n");
        //	buffer.append("PROTO ViewPoint [\n field SFVec3f position -1 0 5\n field SFRotation orientation 0 0 1 0\n field
        // SFString description "" ] {\n Viewpoint {\n  position IS position\n  orientation IS orientation\n  description IS description\n  } }\n\n");
        return buffer.toString();
    }
}
