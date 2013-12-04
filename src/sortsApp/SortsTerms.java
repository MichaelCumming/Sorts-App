/*
 * SortsTerms.java
 *
 * Created on May 27, 2004, 11:10 AM
 */

package sortsApp;

import java.util.List;
import sortsApp.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import cassis.sort.*;
import com.touchgraph.graphlayout.*;
import java.io.Serializable;

/**
 *
 * @author  cumming
 */
public class SortsTerms implements Serializable {
    /**Used for nodes and edges */
    public final static Color   SORT_NOT_DEFINED_COLOR = Color.lightGray;
    public final static Color   SORT_DEFINED_COLOR = Color.white;
    public final static Color   SORT_NOTE_COLOR = Color.pink;
    public final static int MAX = 1000;
    
    /** Creates a new instance of SortsTerms */
    public SortsTerms() {
        //test();
    }
    
    /**@since 15 Apr.2005 */
    public enum SORT_TYPE {
        NOT_DEFINED(        "u",    " UNDEF ",      "UNDEFINED",            NODE_TYPE_ELLIPSE, SORT_NOT_DEFINED_COLOR,  true, MAX, 0),
        //Primitive--------------
        LABEL(              "p",    "[Label]",      "Primitive : [Label]",  NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, 0, MAX),
        DATE(               "p",    "[Date]",       "Primitive : [Date]",   NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, 0, MAX),
        NUMERIC(            "p",    "[Numeric]",    "Primitive : [Numeric]",  NODE_TYPE_ROUNDRECT,  SORT_DEFINED_COLOR, false, 0, MAX),
        LINESEGMENT(        "l",    "[LineSegment]", "LineSegment",         NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, MAX, MAX),
        POINT(              "po",   "[Point]",      "Point",                NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, MAX, MAX),
        WEIGHT(             "w",    "[Weight] (1.0)", "Weight",             NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, MAX, MAX),
        FUNCTION(           "f",    "[Function]",   "Function",             NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, MAX, 1),
        PROPERTY(           "pr",   "[Property]",   "Property",             NODE_TYPE_ROUNDRECT,    SORT_DEFINED_COLOR, false, MAX, MAX),
        //Composite--------------
        SUM(                "s",    " + ",          "Sum",                  NODE_TYPE_RECTANGLE,    SORT_DEFINED_COLOR, true, MAX, MAX),
        ATTRIBUTE(          "att",  " ^ ",          "Attribute",            NODE_TYPE_RECTANGLE,    SORT_DEFINED_COLOR, true, 2, MAX),
        ATTRIBUTE_ANON(     "ANON", " ^ ",          "Attribute",            NODE_TYPE_RECTANGLE,    SORT_NOTE_COLOR,    true, 2, MAX),
        RECURSIVE(          "r",    " r ",          "Recursive",            NODE_TYPE_RECTANGLE,    SORT_DEFINED_COLOR, true, MAX, MAX);
        
        
        private String defaultPrefix;
        private String suffix;
        private String description;
        private int shape;
        private Color backColor;
        private boolean allowsChildren;
        private int maxChildren;
        /**Max number of individuals that can be attached to sNode */
        private int maxInds;
        
        SORT_TYPE(String defaultPrefix, String suffix, String description, int shape, Color backColor, boolean allowsChildren, int maxChildren, int maxInds) {
            this.defaultPrefix = defaultPrefix;
            this.suffix = suffix;
            this.description = description;
            this.shape = shape;
            this.backColor = backColor;
            this.allowsChildren = allowsChildren;
            this.maxChildren = maxChildren;
            this.maxInds = maxInds;
        }
        public String getDefaultPrefix() { return defaultPrefix; }
        public String getSortSuffix() { return suffix; }
        public String getDescription() { return description; }
        public int getShape() { return shape; }
        public Color getBackColor() { return backColor; }
        public boolean allowsChildren() { return allowsChildren; }
        public int getMaxChildren() { return maxChildren; }
        public int getMaxInds() { return maxInds; }
        
        public static Set <String> ALL_SORT_DESCRIPTIONS() {
            Set <String> result = new HashSet();
            for(SORT_TYPE t : SORT_TYPE.values()) {
                result.add(t.getDescription());
            }
            return result;
        }
    }
    
    /**Current: Used to define the .setVisibleRowCount (JList) and .setMaximumRowCount (JComboBox) */
    public static int DEFAULT_ROW_COUNT = SORT_TYPE.values().length;
    
    /**Current */
    public SORT_TYPE getTypeFromDescription(String desc) {
        for(SORT_TYPE t : SORT_TYPE.values()) {
            if (t.getDescription().equals(desc))
                return t;
        }
        return null;
    }
    /**Current */
    public SORT_TYPE getTypeFromSuffix(String suffix) {
        for(SORT_TYPE t : SORT_TYPE.values()) {
            if (t.getSortSuffix().equals(suffix))
                return t;
        }
        return null;
    }
    
    /**Current */
//    public static Set <String> ALL_SORT_DESCRIPTIONS() {
//        Set <String> result = new HashSet();
//        for(SORT_TYPE t : SORT_TYPE.values()) {
//            result.add(t.getDescription());
//        }
//        return result;
//    }
    
    /** Current: Used to draw touchgraph Nodes, @since 15 Apr.2005 */
    public SORT_TYPE getSortTypeUsingSort(Sort sort) {
        if (sort instanceof PrimitiveSort) {
            /**Definition is same as suffix for prim sorts. e.g. [Label] */
            String primSuffix = sort.definition();
            return getTypeFromSuffix(primSuffix);
        }
        if (sort instanceof DisjunctiveSort)
            return SORT_TYPE.SUM;
        if (sort instanceof AttributeSort)
            return SORT_TYPE.ATTRIBUTE;
        if (sort instanceof AspectsSort)
            return SORT_TYPE.PROPERTY;
        if (sort instanceof RecursiveSort)
            return SORT_TYPE.RECURSIVE;
        
        System.out.println("Error: no suitable sort type handled in SortsTerms");
        return null;
    }
    
    /**data used as default. See: GLPanel.addDemoGraph(graphName) */
    public static String        DEFAULT_DEMO_GRAPH = "dg1_03";
    
    public static int           GLPAGE_SCREEN_WIDTH = 475;
    public static int           GLPAGE_PANEL_DEPTH = 325;
    public static int           GLPAGE_MESSAGE_AREA_DEPTH = 125;
    
    public final static Color   GLPANEL_COLOR = new Color(204, 204, 204);
    public final static Color   TGPANEL_BACK_COLOR = Color.lightGray;
    public final static String  EMPTY_STRING = "";
    public final static String  NEWLINE = "\n";
    public final static Color   EDGE_DEFINED_COLOR = Color.white;
    
    
    public final static String  EDGE_ATTRIBUTE_WEIGHT = "weight_(right)";
    public final static String  EDGE_ATTRIBUTE_BASE = "base_(left)";
    public final static String  EDGE_ATTRIBUTES_OFF = "";
    
    public final static int     EDGE_DEFAULT_LENGTH = 500; //was 40
    
    /**These are defined by Touchgraph*/
    public final static int     NODE_TYPE_RECTANGLE = 1; //Node's type (=shape) is a Rectangle
    public final static int     NODE_TYPE_ROUNDRECT = 2; //Node's type (=shape) is a Round Rectangle.
    public final static int     NODE_TYPE_ELLIPSE = 3; //Node's type (=shape) is an Ellipse.
    public final static int     NODE_TYPE_CIRCLE = 4; //Node's type (=shape) is a Circle.
    
    
    public final static Color EDGE_MOUSE_OVER_COLOR = Color.black;
    
    //used?
    public static enum EDGE_TYPE {
        EDGE_NOT_DEFINED_TYPE   (Color.gray),
        EDGE_ERROR_TYPE         (Color.red),
        EDGE_SUM_TYPE           (Color.white),
        EDGE_ATTRIBUTE_TYPE     (Color.white),
        EDGE_PROPERTY_TYPE      (Color.white),
        EDGE_RECURSIVE_TYPE     (Color.white);
        
        private Color color ;
        
        EDGE_TYPE(Color color) {
            this.color = color;
        }
        public Color getColor() { return color; }
    }
    
    public final static String      NODE_SORT_UNINITIALIZED_LABEL   = "AddLabel";
    public final static Font        NODE_SMALL_TAG_FONT             = new Font("Arial", Font.PLAIN, 9);
    public final static Color       NODE_BACK_FIXED_COLOR           = Color.red;
    public final static Color       NODE_BACK_SELECT_COLOR          = new Color(255, 224, 0); //yellow original
    public final static Color       NODE_BACK_SELECT2_COLOR         = new Color(255, 150, 0); //added 28.10.2003 MC
    public final static Color       NODE_BACK_DEFAULT_COLOR         = new Color(208, 96, 0);
    public final static Color       NODE_BACK_HILIGHT_COLOR         = Color.decode("#ffb200"); // altheim: new
    public final static Color       NODE_BORDER_DRAG_COLOR          = Color.black;
    public final static Color       NODE_BORDER_MOUSE_OVER_COLOR    = new Color(160, 160, 160);
    public final static Color       NODE_BORDER_INACTIVE_COLOR      = Color.gray;
    public final static Color       NODE_TEXT_COLOR                 = Color.black;
    public final static Font        NODE_TEXT_FONT                  = new Font("Arial", Font.PLAIN, 12);
    public final static int         NODE_DEFAULT_TYPE               = 0;
    
    
    /**Prints whether test results are as expected */
    public void doTest(int testNum, Object result, Object expected) {
        String resultString;
        if(result.equals(expected))
            resultString = "PASSED";
        else
            resultString = "FAILED";
        System.out.println(testNum + ". " + resultString);
    }
    
    public static void main(String args[]) {
        //System.out.println("GLTopFrame main(): line 349");
        new SortsTerms();
    }
    
    
//    public int getTypeFromSuffix(String suffix) {
//        Integer found = SORT_TYPES[getArrayPosition(SORT_TYPE_SUFFIXES, suffix)];
//        return found.intValue();
//    }
    
    
    /**OLD: (Use enums instead). Sort types */
//    public final static Integer[] SORT_TYPESx = new Integer[] {
//        new Integer(SORT_NOT_DEFINED_TYPE),
//                new Integer(NODE_SORT_PRIMITIVE_LABEL_TYPE),
//                new Integer(NODE_SORT_PRIMITIVE_DATE_TYPE),
//                new Integer(NODE_SORT_PRIMITIVE_NUMERIC_TYPE),
//
//                new Integer(NODE_SORT_LINESEGMENT_TYPE),
//                new Integer(NODE_SORT_POINT_TYPE),
//                new Integer(NODE_SORT_WEIGHT_TYPE),
//                new Integer(NODE_SORT_FUNCTION_TYPE),
//
//                new Integer(NODE_SORT_SUM_TYPE),
//                new Integer(NODE_SORT_ATTRIBUTE_TYPE),
//                new Integer(NODE_SORT_PROPERTY_TYPE),
//                new Integer(NODE_SORT_RECURSIVE_TYPE)
//    };
    
    //    public void test() {
//        doTest(1, getDescriptionFromSuffix(NODE_LABEL_SUFFIX), NODE_LABEL_DESCRIPTION);
//        doTest(2, getSuffixFromDescription(NODE_NUMERIC_DESCRIPTION), NODE_NUMERIC_SUFFIX);
//        doTest(3, new Integer(getTypeFromSuffix(NODE_RECURSIVE_SUFFIX)), new Integer(NODE_SORT_RECURSIVE_TYPE));
//        doTest(4, new Integer(getTypeFromDescription(NODE_PROPERTY_DESCRIPTION)), new Integer(NODE_SORT_PROPERTY_TYPE));
//        doTest(5, getSuffixFromType(NODE_SORT_PRIMITIVE_LABEL_TYPE), NODE_LABEL_SUFFIX);
//        doTest(6, getColorFromType(NODE_SORT_PRIMITIVE_LABEL_TYPE), NODE_SORT_PRIMITIVE_COLOR);
//        doTest(7, new Integer(getShapeFromType(NODE_SORT_SUM_TYPE)), new Integer(NODE_SORT_SUM_SHAPE));
//        doTest(8, getDefaultPrefixFromDescription(NODE_SUM_DESCRIPTION), NODE_SUM_DEFAULT);
//        doTest(9, new Integer(getEdgeTypeFromParentNodeType(NODE_SORT_PRIMITIVE_LABEL_TYPE)), new Integer(EDGE_ERROR_TYPE));
//        //doTest(10, getEdgeColorFromParentChildNodeType(SORT_NOT_DEFINED_TYPE), EDGE_UNDEFINED_COLOR);
//
//    }
    
    /**Repeats of primitives in order to align with the 7 sort types */
//    public final static Integer[] SORT_SHAPES = new Integer[] {
//        new Integer(SORT_NOT_DEFINED_SHAPE),
//                new Integer(NODE_SORT_PRIMITIVE_SHAPE),
//                new Integer(NODE_SORT_PRIMITIVE_SHAPE),
//                new Integer(NODE_SORT_PRIMITIVE_SHAPE),
//                new Integer(NODE_SORT_SUM_SHAPE),
//                new Integer(NODE_SORT_ATTRIBUTE_SHAPE),
//                new Integer(NODE_SORT_PROPERTY_SHAPE),
//                new Integer(NODE_SORT_RECURSIVE_SHAPE)
//    };
    
    /**Methods that make use of the above. ALL: 1-1 mapping */
//    public int getTypeFromSuffix(String suffix) {
//        Integer found = SORT_TYPES[getArrayPosition(SORT_TYPE_SUFFIXES, suffix)];
//        return found.intValue();
//    }
    
//    public String getSuffixFromDescription(String desc) {
//        return SORT_TYPE_SUFFIXES[getArrayPosition(SORT_TYPE_DESCRIPTIONS, desc)];
//    }
//    public String getDescriptionFromSuffix(String suffix) {
//        return SORT_TYPE_DESCRIPTIONS[getArrayPosition(SORT_TYPE_SUFFIXES, suffix)];
//    }
//    public String getSuffixFromType(int type) {
//        return SORT_TYPE_SUFFIXES[getArrayPosition(SORT_TYPES, new Integer(type))];
//    }
//    public Color getColorFromType(int type) {
//        return SORT_TYPE_COLORS[getArrayPosition(SORT_TYPES, new Integer(type))];
//    }
//    public int getShapeFromType(int type) {
//        Integer found = SORT_SHAPES[getArrayPosition(SORT_TYPES, new Integer(type))];
//        return found.intValue();
//    }
//    public String getDefaultPrefixFromDescription(String desc) {
//        return SORT_TYPE_DEFAULT_PREFIXES[getArrayPosition(SORT_TYPE_DESCRIPTIONS, desc)];
//    }
//    public int getEdgeTypeFromParentNodeType(int type) {
//        Integer found = EDGE_TYPES[getArrayPosition(SORT_TYPES, new Integer(type))];
//        return found.intValue();
//    }
//    public Color getEdgeColorFromParentChildNodeType(int parentNodeType, int childNodeType) {
//        if(childNodeType==SORT_NOT_DEFINED_TYPE) {
//            return EDGE_UNDEFINED_COLOR;
//        }
//        return EDGE_COLORS[getArrayPosition(SORT_TYPES, new Integer(parentNodeType))];
//    }
    
    
//    public String getAttributeEdgeMarking(Edge edge) {
//        /**Only attribute edges, that are secondAttributes, have markings.
//         * Note: this marking, not color, distinguishes second attribute edges. */
//        if(edge.isBaseEdge()) {
//            return "base"; //or 'weight'?
//        }
//        return null;
//    }
    
    //    /**Helper method used below.
//     * Assumes 1-1, or many-1 mappings. Doesn't work with 1-many mappings */
//    public int getArrayPosition(Object[] array, Object searchValue) {
//        for(int i = 0; i < array.length; i++) {
//            if(array[i].equals(searchValue))
//                return i;
//        }
//        System.out.println("ERROR: couldn't find " + searchValue);
//        return -1;
//    }
    
    //    public final static String SORT_NOT_DEFINED_DEFAULT = "u";
//    public final static String NODE_LABEL_DEFAULT = "p";
//    public final static String NODE_DATE_DEFAULT = "p";
//    public final static String NODE_NUMERIC_DEFAULT = "p";
//    public final static String NODE_SUM_DEFAULT = "s";
//    public final static String NODE_ATTRIBUTE_DEFAULT = "a";
//    public final static String NODE_PROPERTY_DEFAULT = "pr";
//    public final static String NODE_RECURSIVE_DEFAULT = "r";
    
//    public final static String[] SORT_TYPE_DEFAULT_PREFIXES = new String[] {
//        SORT_NOT_DEFINED_DEFAULT,
//                NODE_LABEL_DEFAULT,
//                NODE_DATE_DEFAULT,
//                NODE_NUMERIC_DEFAULT,
//                NODE_SUM_DEFAULT,
//                NODE_ATTRIBUTE_DEFAULT,
//                NODE_PROPERTY_DEFAULT,
//                NODE_RECURSIVE_DEFAULT
//    };
    
//    public final static Color NODE_SORT_PRIMITIVE_COLOR = SORT_DEFINED_COLOR;
//    public final static Color NODE_SORT_SUM_COLOR = SORT_DEFINED_COLOR;
//    public final static Color NODE_SORT_ATTRIBUTE_COLOR = SORT_DEFINED_COLOR;
//    public final static Color NODE_SORT_PROPERTY_COLOR = SORT_DEFINED_COLOR;
//    public final static Color NODE_SORT_RECURSIVE_COLOR = SORT_DEFINED_COLOR;
//
//    public final static Color[] SORT_TYPE_COLORS = new Color[] {
//        SORT_NOT_DEFINED_COLOR,
//                NODE_SORT_PRIMITIVE_COLOR,
//                NODE_SORT_PRIMITIVE_COLOR,
//                NODE_SORT_PRIMITIVE_COLOR,
//                NODE_SORT_SUM_COLOR,
//                NODE_SORT_ATTRIBUTE_COLOR,
//                NODE_SORT_PROPERTY_COLOR,
//                NODE_SORT_RECURSIVE_COLOR
//    };
    
    /**The rest is specific to the SortsDemo application */
//    public final static int SORT_NOT_DEFINED_SHAPE = NODE_TYPE_RECTANGLE;
//    public final static int NODE_SORT_PRIMITIVE_SHAPE = NODE_TYPE_ELLIPSE;
//    public final static int NODE_SORT_SUM_SHAPE = NODE_TYPE_RECTANGLE;
//    public final static int NODE_SORT_ATTRIBUTE_SHAPE = NODE_TYPE_ROUNDRECT;
//    public final static int NODE_SORT_PROPERTY_SHAPE = NODE_TYPE_RECTANGLE;
//    public final static int NODE_SORT_RECURSIVE_SHAPE = NODE_TYPE_RECTANGLE;
    
    /**Sort types */
//    public final static int SORT_NOT_DEFINED_TYPE = 0;
//    public final static int NODE_SORT_PRIMITIVE_LABEL_TYPE = 1;
//    public final static int NODE_SORT_PRIMITIVE_DATE_TYPE = 2;
//    public final static int NODE_SORT_PRIMITIVE_NUMERIC_TYPE = 3;
//
//    public final static int NODE_SORT_LINESEGMENT_TYPE = 4;
//    public final static int NODE_SORT_POINT_TYPE = 5;
//    public final static int NODE_SORT_WEIGHT_TYPE = 6;
//    public final static int NODE_SORT_FUNCTION_TYPE = 7;
//
//    public final static int NODE_SORT_SUM_TYPE = 8;
//    public final static int NODE_SORT_ATTRIBUTE_TYPE = 9;
//    public final static int NODE_SORT_PROPERTY_TYPE = 10;
//    public final static int NODE_SORT_RECURSIVE_TYPE = 11;
    
    /**Edges are defined by their parent types */
//    public final static int EDGE_NOT_DEFINED_TYPE = 0;
//    /**Edges that shouldn't exist */
//    public final static int EDGE_ERROR_TYPE = 1;
//    /**Edge with a sum node as parent */
//    public final static int EDGE_SUM_TYPE = 2;
//    public final static int EDGE_ATTRIBUTE_TYPE = 3;
//    public final static int EDGE_PROPERTY_TYPE = 4;
//    public final static int EDGE_RECURSIVE_TYPE = 5;
    
    /**Goal: map these edge types to node sort types */
//    public final static Integer[] EDGE_TYPES = new Integer[] {
//        new Integer(EDGE_NOT_DEFINED_TYPE),
//                /**Edges cannot have primitive nodes as parents */
//                new Integer(EDGE_ERROR_TYPE),
//                new Integer(EDGE_ERROR_TYPE),
//                new Integer(EDGE_ERROR_TYPE),
//                new Integer(EDGE_SUM_TYPE),
//                /**base and weight are defined by boolean attributes */
//                new Integer(EDGE_ATTRIBUTE_TYPE),
//                /**No such thing as a property node, however, there are property edges */
//                new Integer(EDGE_PROPERTY_TYPE),
//                new Integer(EDGE_RECURSIVE_TYPE)
//    };
    
    /**Only three colors for edges, to avoid information overload */
//    public final static Color EDGE_UNDEFINED_COLOR = Color.gray;
    //public final static Color EDGE_DEFINED_COLOR = Color.white;
//    public final static Color EDGE_ERROR_COLOR = Color.red;
    
    
//    public final static Color[] EDGE_COLORS = new Color[] {
//        EDGE_UNDEFINED_COLOR,
//                EDGE_ERROR_COLOR,
//                EDGE_ERROR_COLOR,
//                EDGE_ERROR_COLOR,
//                EDGE_DEFINED_COLOR,
//                EDGE_DEFINED_COLOR,
//                EDGE_DEFINED_COLOR,
//                EDGE_DEFINED_COLOR
//    };
    
    /**Descriptions and their suffixes are mapped 1-1 */
//    public final static String SORT_NOT_DEFINED_DESCRIPTION = "UNDEFINED";
//    public final static String NODE_LABEL_DESCRIPTION = "Primitive : [Label]";
//    public final static String NODE_DATE_DESCRIPTION = "Primitive : [Date]";
//    public final static String NODE_NUMERIC_DESCRIPTION = "Primitive : [Numeric]";
//    public final static String NODE_SUM_DESCRIPTION = "Sum";
//    public final static String NODE_ATTRIBUTE_DESCRIPTION = "Attribute";
//    public final static String NODE_PROPERTY_DESCRIPTION = "Property";
//    public final static String NODE_RECURSIVE_DESCRIPTION = "Recursive";
//
//    public enum SORT_TYPE_DESCRIPTIONSx {
//
//
//    }
//
//    public final static String[] SORT_TYPE_DESCRIPTIONS = new String[] {
//        SORT_NOT_DEFINED_DESCRIPTION,
//                NODE_LABEL_DESCRIPTION,
//                NODE_DATE_DESCRIPTION,
//                NODE_NUMERIC_DESCRIPTION,
//                NODE_SUM_DESCRIPTION,
//                NODE_ATTRIBUTE_DESCRIPTION,
//                NODE_PROPERTY_DESCRIPTION,
//                NODE_RECURSIVE_DESCRIPTION
//    };
    
    //public final static String SORT_NOT_DEFINED_SUFFIX = " UNDEF ";
//    public final static String NODE_LABEL_SUFFIX = "[Label]";
//    public final static String NODE_DATE_SUFFIX = "[Date]";
//    public final static String NODE_NUMERIC_SUFFIX = "[Numeric]";
//    public final static String NODE_SUM_SUFFIX = " + ";
//    public final static String NODE_ATTRIBUTE_SUFFIX = " ^ ";
//    public final static String NODE_PROPERTY_SUFFIX = " p ";
//    public final static String NODE_RECURSIVE_SUFFIX = " r ";
//
//    public final static String[] SORT_TYPE_SUFFIXES = new String[] {
//        SORT_NOT_DEFINED_SUFFIX,
//                NODE_LABEL_SUFFIX,
//                NODE_DATE_SUFFIX,
//                NODE_NUMERIC_SUFFIX,
//                NODE_SUM_SUFFIX,
//                NODE_ATTRIBUTE_SUFFIX,
//                NODE_PROPERTY_SUFFIX,
//                NODE_RECURSIVE_SUFFIX
//    };
    
    //
    
//
//public final static Color EDGE_DEFAULT_COLOR = Color.decode("#0000B0");
    
//    public final static int EDGE_SORT_PRIMITIVE_TYPE = 0;
//    public final static int EDGE_SORT_ATTRIBUTE_TYPE = 1;
//    public final static int EDGE_SORT_SUM_TYPE = 2;
//    public static final int EDGE_SORT_PROPERTY_TYPE = 3;
    
//    public final static Color EDGE_SORT_PRIMITIVE_COLOR = Color.red;
//    public final static Color EDGE_SORT_ATTRIBUTE_COLOR = Color.orange;
//    public final static Color EDGE_SORT_SUM_COLOR = Color.yellow;
//    public final static Color EDGE_SORT_PROPERTY_COLOR = Color.red;
//    public final static Color EDGE_SECOND_ATTRIBUTE_COLOR = Color.red;
    
    
}
