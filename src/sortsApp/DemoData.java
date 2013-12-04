/*
 * DemoData.java
 *
 * Created on 11 april 2005, 11:50
 */

package sortsApp;

import com.touchgraph.graphlayout.*;
import com.touchgraph.graphlayout.sNode.*;
import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import sortsApp.SortsTerms.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.table.*;
import cassis.Element;
import cassis.sort.User;
import cassis.form.Form;


/**
 *
 * @author cumming
 */
public class DemoData {
    private static TGPanel tgPanel;
    private static GraphLayoutTopFrame topFrame;
    private static SortsTerms terms = new SortsTerms();
    
    /**
     * Creates a new instance of DemoData
     */
    public DemoData(TGPanel tgPanel) {
        this.tgPanel = tgPanel;
        this.topFrame = tgPanel.getTopFrame();
    }
    
    /**Added to menu in GLTopFrame.addDemoGraphs()
     * @since 10.May.2005 */
    public enum DEMO_GRAPH_1 {
        /**examples taken from demo_29_3_2005.sdl (see sdl directory) */
        $step01("Step 1: elements",             "dg1_01",   "consider a plan of a room as a set of lines"),
        $step02("Step 2: elements ^ types",     "dg1_02",   "consider the lines dinstinguished by type, either wall, door, or window; therefore, we add a types attribute"),
        $step03("Step 3: types ^ elements",     "dg1_03",   "the former is classified primarily by geometry, we may also classify it primarily by type; therefore, we switch the order of the sorts"),
        $step04("Step 4: functions ^ types ^ elements",     "dg1_04",     "in that case, we probably want to count them; therefore, we add a function, with the types and elements as attribute; in this way, we only need to add a single function"),
        $step05("Step 5: types ^ functions ^ elements",     "dg1_05",     "but this only gives an overall count, better is to count them by type; therefore, we switch the order of the function(s) and types"),
        $step06("Step 6: functions ^ types ^ costs ^ elements",     "dg1_06",       "rather than counting them, we could also calculate their cost; for this, we need to add a cost per unit length for each type and replace the count function by a sum over product function"),
        $step07("Step 7: types ^ functions ^ costs ^ elements",     "dg1_07",       "or we can calculate the cost per type; for this, we switch the order of the functions and types"),
        $step08("Step 8: functions ^ positions ^ types ^ elements", "dg1_08",       "finally, we can consider other functions for other purposes; for example, we could calculate the distance from a certain point within the room to each of the elements"),
        $step09("Step 9: positions ^ types ^ elements ^ functions", "dg1_09",       "this only gives us the average distance; in order to determine teh distance to each element, we need to make the function(s) an attribute to the elements"),
        $step10("Step 10: functions ^ positions ^ types ^ elements ^ functions", "dg1_10",   "it could still be useful to also calculate the minimum distance; for this, we add another function"),
        $step11("Step 11: positions ^ types ^ functions ^ elements ^ functions", "dg1_11",   "or, we can calculate the minimum distance per type, e.g., for emergency safety reasons");
        
        private final String demoName;
        private final String methodName;
        private final String description;
        
        DEMO_GRAPH_1(String demoName, String methodName, String description) {
            this.demoName = demoName;
            this.methodName = methodName;
            this.description = description;
        }
        public String getDemoName() { return demoName; }
        public String getMethodName() { return methodName; }
        public String getDescription() { return description; }
        public static String getMenuName() { return "11 steps demo: 29_3_2005"; }
    }
    
    public void dg1_01() throws TGException {
        sNode p1 = (sNode)tgPanel.addNodeSorts(SORT_TYPE.LINESEGMENT, "elements");
        addDataToNamedNode("elements", DEMO_DATA_1.elements);
        tgPanel.setSelect(p1);
    }
    public void dg1_02() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"elements", "types"},
                new SORT_TYPE[] {SORT_TYPE.LINESEGMENT, SORT_TYPE.LABEL});
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
                addDataToNamedNode("types", DEMO_DATA_1.types);
    }
    public void dg1_03() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"types", "elements"},
                new SORT_TYPE[] {SORT_TYPE.LABEL, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_04() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"function_count", "types", "elements"},
                new SORT_TYPE[] {SORT_TYPE.FUNCTION, SORT_TYPE.LABEL, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("function_count", DEMO_DATA_1.countFunction);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_05() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"types", "function_count", "elements"},
                new SORT_TYPE[] {SORT_TYPE.LABEL, SORT_TYPE.FUNCTION, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("function_count", DEMO_DATA_1.countFunction);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_06() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"function_costPerLength", "types", "costs", "elements"},
                new SORT_TYPE[] {SORT_TYPE.FUNCTION, SORT_TYPE.LABEL, SORT_TYPE.WEIGHT, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("function_costPerLength", DEMO_DATA_1.costPerLengthFunction);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("costs", DEMO_DATA_1.costs);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_07() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"types", "function_costPerLength", "costs", "elements"},
                new SORT_TYPE[] {SORT_TYPE.LABEL, SORT_TYPE.FUNCTION, SORT_TYPE.WEIGHT, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("function_costPerLength", DEMO_DATA_1.costPerLengthFunction);
                addDataToNamedNode("costs", DEMO_DATA_1.costs);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_08() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"function_avgDistance", "positions", "types", "elements"},
                new SORT_TYPE[] {SORT_TYPE.FUNCTION, SORT_TYPE.POINT, SORT_TYPE.LABEL, SORT_TYPE.LINESEGMENT});
                addDataToNamedNode("function_avgDistance", DEMO_DATA_1.avgDistanceFunction);
                addDataToNamedNode("positions", DEMO_DATA_1.positions);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
    }
    public void dg1_09() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"positions", "types", "elements", "function_avgDistance"},
                new SORT_TYPE[] {SORT_TYPE.POINT, SORT_TYPE.LABEL, SORT_TYPE.LINESEGMENT, SORT_TYPE.FUNCTION});
                addDataToNamedNode("positions", DEMO_DATA_1.positions);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
                addDataToNamedNode("function_avgDistance", DEMO_DATA_1.avgDistanceFunction);
    }
    public void dg1_10() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"function_minDistance", "positions", "types", "elements", "function_avgDistance"},
                new SORT_TYPE[] {SORT_TYPE.FUNCTION, SORT_TYPE.POINT, SORT_TYPE.LABEL, SORT_TYPE.LINESEGMENT, SORT_TYPE.FUNCTION});
                addDataToNamedNode("function_minDistance", DEMO_DATA_1.minDistanceFunction);
                addDataToNamedNode("positions", DEMO_DATA_1.positions);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
                addDataToNamedNode("function_avgDistance", DEMO_DATA_1.avgDistanceFunction);
    }
    public void dg1_11() throws TGException {
        sNode root = makeAttributeGraphTopDown(new String[] {"positions", "types", "function_minDistance", "elements", "function_avgDistance"},
                new SORT_TYPE[] {SORT_TYPE.POINT, SORT_TYPE.LABEL, SORT_TYPE.FUNCTION, SORT_TYPE.LINESEGMENT, SORT_TYPE.FUNCTION});
                addDataToNamedNode("positions", DEMO_DATA_1.positions);
                addDataToNamedNode("types", DEMO_DATA_1.types);
                addDataToNamedNode("function_minDistance", DEMO_DATA_1.minDistanceFunction);
                addDataToNamedNode("elements", DEMO_DATA_1.elements);
                addDataToNamedNode("function_avgDistance", DEMO_DATA_1.avgDistanceFunction);
    }
    
    
    /**Added to menu in GLTopFrame.addDemoGraphs()
     * @since 17.May.2005 */
    public enum DEMO_GRAPH_2 {
        dg2_01("s1 : a + b + c + d + e", "dg2_01", "test"),
        dg2_02("s1 : p1 + p2 + p3", "dg2_02", "test"),
        dg2_03("dg2_03", "dg2_03", "test"),
        dg2_04("dg2_04", "dg2_04", "test");
        
        private final String demoName;
        private final String methodName;
        private final String description;
        
        DEMO_GRAPH_2(String demoName, String methodName, String description) {
            this.demoName = demoName;
            this.methodName = methodName;
            this.description = description;
        }
        public String getDemoName() { return demoName; }
        public String getMethodName() { return methodName; }
        public String getDescription() { return description; }
        public static String getMenuName() { return "Misc demos: 17_5_2005"; }
    }
    
    public void dg2_01() throws TGException {
        makeSumNodes(new String[] { "a", "b", "c", "d", "e" },
                new SORT_TYPE[] { SORT_TYPE.LABEL, SORT_TYPE.FUNCTION, SORT_TYPE.LINESEGMENT, SORT_TYPE.LABEL, SORT_TYPE.LABEL });
    }
    public void dg2_02() throws TGException {
        makeSumNodes(new String[] { "p1", "p2", "p3" },
                new SORT_TYPE[] { SORT_TYPE.LINESEGMENT, SORT_TYPE.LABEL, SORT_TYPE.LABEL });
    }
    public void dg2_03() throws TGException {
        Node s1 = tgPanel.addNodeSorts(SORT_TYPE.SUM, "log");
        Node a1_1 = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE, "actions");
        Node p1_2 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "comments");
        //System.out.println("sort def: p1_2 = " + p1_2.getSortDef());
        
        tgPanel.addEdge(s1, a1_1, terms.EDGE_DEFAULT_LENGTH);
        tgPanel.addEdge(s1, p1_2, terms.EDGE_DEFAULT_LENGTH);
        
        Node p1_1_1 = tgPanel.addNodeSorts(SORT_TYPE.DATE, "dates");
        //p1_1_1.setAttributeWeight(true);
        //System.out.println("sort def: p1_1_1 = " + p1_1_1.getSortDef());
        //
        Node p1_1_2 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "types");
        //System.out.println("sort def: p1_1_2 = " + p1_1_2.getSortDef());
        //
        Edge e = tgPanel.addEdge(a1_1, p1_1_1, terms.EDGE_DEFAULT_LENGTH);
        e.setLabel("test");
        //e.setSecondAttribute(true);
        //p1_1_2.setSecondAttribute(true);
        
        Edge e2 = tgPanel.addEdge(a1_1, p1_1_2, terms.EDGE_DEFAULT_LENGTH);
    }
    public void dg2_04() throws TGException {
        Node a1 = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE, "all_atts");
        Node a1_1 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "a");
        Node a1_2 = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE, "XX");
        Edge e11 = tgPanel.addEdge(a1, a1_1, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_BASE);
        Edge e12 = tgPanel.addEdge(a1, a1_2, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_WEIGHT);
        
        Node a1_1_1 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "b");
        Node a1_1_2 = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE, "XX");
        Edge e111 = tgPanel.addEdge(a1_2, a1_1_1, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_BASE);
        Edge e112 = tgPanel.addEdge(a1_2, a1_1_2, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_WEIGHT);
        
        Node a1_1_1_1 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "c");
        Node a1_1_1_2 = tgPanel.addNodeSorts(SORT_TYPE.LABEL, "d");
        Edge e1111 = tgPanel.addEdge(a1_1_2, a1_1_1_1, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_BASE);
        Edge e1112 = tgPanel.addEdge(a1_1_2, a1_1_1_2, terms.EDGE_DEFAULT_LENGTH, Edge.EDGE_ATTRIBUTE_WEIGHT);
    }
    public void dg2_05() throws TGException {
    }
    
    /**Adds individual packets of data for a selected node */
    public enum DEMO_DATA_1 {
        elements("elements", "dd1_elements", new String[]
        { "<<(10,10,0), (50,10,0)>",
                  " <<(10,40,0), (10,10,0)>",
                  " <<(50,10,0), (50,40,0)>",
                  " <<(50,40,0), (10,40,0)>",
                  " <<(25,10,0), (35,10,0)>",
                  " <<(50,20,0), (50,30,0)>",
                  " <<(40,40,0), (25,40,0)>" }),
                          
                          types("types", "dd1_types",
                          new String[] { "wall", "wall", "wall", "wall", "door", "window", "window" }),
                                  
                                  countFunction("countFunction", "dd1_countFunction",
                                  new String[] { "count(elements.length)" }),
                                          
                                          costs( "costs", "dd1_costs",
                                          new String[] { "0.5", "0.5", "0.5", "0.5", "0.6", "0.75", "0.75" }),
                                                  
                                                  positions( "positions", "dd1_positions",
                                                  new String [] { "((30,20,0)" } ),
                                                          
                                                          costPerLengthFunction("costPerLengthFunction", "dd1_costPerLengthFunction",
                                                          new String[] { "sum|product(costs.value, elements.length)" }),
                                                                  
                                                                  avgDistanceFunction("avgDistanceFunction", "dd1_avgDistanceFunction",
                                                                  new String[] { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" }),
                                                                          
                                                                          minDistanceFunction("minDistanceFunction", "dd1_minDistanceFunction",
                                                                          new String[] { "min(functions.value)" });
                                                                          
                                                                          private String demoName;
                                                                          private String methodName;
                                                                          private String[] data;
                                                                          
                                                                          DEMO_DATA_1(String demoName, String methodName, String[] data) {
                                                                              this.demoName = demoName;
                                                                              this.methodName = methodName;
                                                                              this.data = data;
                                                                          }
                                                                          public static String getMenuName() { return "Apply data to selected node"; }
                                                                          public String getDemoName() { return demoName; }
                                                                          public String getMethodName() { return methodName; }
                                                                          public String[] getData() { return data; }
    }
    
    public void dd1_elements() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.elements);
    }
    public void dd1_types() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.types);
    }
    public void dd1_countFunction() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.countFunction);
    }
    public void dd1_costs() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.costs);
    }
    public void dd1_positions() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.positions);
    }
    public void dd1_costPerLengthFunction() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.costPerLengthFunction);
    }
    public void dd1_avgDistanceFunction() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.avgDistanceFunction);
    }
    public void dd1_minDistanceFunction() {
        addDataToSelectNode(getSelectNode(), DEMO_DATA_1.minDistanceFunction);
    }
    
    /**@since 20.May 2005 */
    public sNode getSelectNode() {
        sNode selNode = (sNode)tgPanel.getSelect();
        if(selNode == null) {
            System.out.println("Error: selected node is null");
        }
        return selNode;
    }
    public void addDataToSelectNode(sNode node, DEMO_DATA_1 demo) {
        if(node != null) {
            String[] data = demo.getData();
            for(int i = 0; i < data.length; i++) {
                node.addDataString(data[i]);
            }
        }
    }
    
    /**Data combined together. Adds demo data to menu
     * @since 19.May 2005 */
    //USEFUL? probably not...
    public enum DEMO_DATA_2 {
        
        $step01("elements",
        new String [] { "elements" },
        new String [][]
        {{  "<(10,10,0), (50,10,0)>" },
         {  "<(10,40,0), (10,10,0)>" },
         {  "<(50,10,0), (50,40,0)>" },
         {  "<(50,40,0), (10,40,0)>"}},
         "dd2_01", ""),
         
         $step02("elements ^ types",
         new String [] { "elements", "types" },
         new String [][]
         {{  "<(10,10,0), (50,10,0)>", "{ wall }"},
          {  "<(10,40,0), (10,10,0)>", "{ wall }" },
          {  "<(50,10,0), (50,40,0)>", "{ wall }" },
          {  "<(50,40,0), (10,40,0)>", "{ wall }" },
          {  "<(25,10,0), (35,10,0)>", "{ door }" },
          {  "<(50,20,0), (50,30,0)>", "{ window }" },
          {  "<(40,40,0), (25,40,0)>", "{ window }" }},
          "dd2_02", ""),
          
          //$step03("types ^ elements",
          
          $step03("types ^ elements",
          new String [] { "types", "elements" },
          new String [][]
          {{ "{ wall }",   "<(10,10,0), (50,10,0)>" },
           { "{ wall }",   "<(10,40,0), (10,10,0)>" },
           { "{ wall }",   "<(50,10,0), (50,40,0)>" },
           { "{ wall }",   "<(50,40,0), (10,40,0)>" },
           { "{ door }",   "<(25,10,0), (35,10,0)>" },
           { "{ window }", "<(50,20,0), (50,30,0)>" },
           { "{ window }", "<(40,40,0), (25,40,0)>" }},
           "dd2_03", ""),
           
           $step04("functions ^ types ^ elements",
           new String [] { "functions", "types", "elements" },
           new String [][]
           {{ "count(elements.length)", "{ wall }",   "<(10,10,0), (50,10,0)>" },
            { "count(elements.length)", "{ wall }",   "<(10,40,0), (10,10,0)>" },
            { "count(elements.length)", "{ wall }",   "<(50,10,0), (50,40,0)>" },
            { "count(elements.length)", "{ wall }",   "<(50,40,0), (10,40,0)>" },
            { "count(elements.length)", "{ door }",   "<(25,10,0), (35,10,0)>" },
            { "count(elements.length)", "{ window }", "<(50,20,0), (50,30,0)>" },
            { "count(elements.length)", "{ window }", "<(40,40,0), (25,40,0)>" }},
            "dd2_04", ""),
            
            $step05("types ^ functions ^ elements",
            new String [] { "types", "functions", "elements" },
            new String [][]
            {{ "{ wall }",   "count(elements.length)",   "<(10,10,0), (50,10,0)>" },
             { "{ wall }",   "count(elements.length)",   "<(10,40,0), (10,10,0)>" },
             { "{ wall }",   "count(elements.length)",   "<(50,10,0), (50,40,0)>" },
             { "{ wall }",   "count(elements.length)",   "<(50,40,0), (10,40,0)>" },
             { "{ door }",   "count(elements.length)",   "<(25,10,0), (35,10,0)>" },
             { "{ window }", "count(elements.length)",   "<(50,20,0), (50,30,0)>" },
             { "{ window }", "count(elements.length)",   "<(40,40,0), (25,40,0)>" }},
             "dd2_05", ""),
             
             $step06("functions ^ types ^ costs ^ elements",
             new String [] { "functions", "types", "costs", "elements" },
             new String [][]
             {{ "sum|product(costs.value, elements.length)", "{ wall }",  "0.5",  "<(10,10,0), (50,10,0)>" },
              { "sum|product(costs.value, elements.length)", "{ wall }",  "0.5",  "<(10,40,0), (10,10,0)>" },
              { "sum|product(costs.value, elements.length)", "{ wall }",  "0.5",  "<(50,10,0), (50,40,0)>" },
              { "sum|product(costs.value, elements.length)", "{ wall }",  "0.5",  "<(50,40,0), (10,40,0)>" },
              { "sum|product(costs.value, elements.length)", "{ door }",  "0.6",  "<(25,10,0), (35,10,0)>" },
              { "sum|product(costs.value, elements.length)", "{ window }", "0.75", "<(50,20,0), (50,30,0)>" },
              { "sum|product(costs.value, elements.length)", "{ window }", "0.75", "<(40,40,0), (25,40,0)>" }},
              "dd2_06", ""),
              
              $step07("types ^ functions ^ costs ^ elements",
              new String [] { "types", "functions", "costs", "elements" },
              new String [][]
              {{ "{ wall }",   "sum|product(costs.value, elements.length)", "0.5",  "<(10,10,0), (50,10,0)>" },
               { "{ wall }",   "sum|product(costs.value, elements.length)", "0.5",  "<(10,40,0), (10,10,0)>" },
               { "{ wall }",   "sum|product(costs.value, elements.length)", "0.5",  "<(50,10,0), (50,40,0)>" },
               { "{ wall }",   "sum|product(costs.value, elements.length)", "0.5",  "<(50,40,0), (10,40,0)>" },
               { "{ door }",   "sum|product(costs.value, elements.length)", "0.6",  "<(25,10,0), (35,10,0)>" },
               { "{ window }", "sum|product(costs.value, elements.length)", "0.75", "<(50,20,0), (50,30,0)>" },
               { "{ window }", "sum|product(costs.value, elements.length)", "0.75", "<(40,40,0), (25,40,0)>" }},
               "dd2_07", ""),
               
               $step08("functions ^ positions ^ types ^ elements",
               new String [] { "functions", "positions", "types", "elements" },
               new String [][]
               {{ "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ wall }",   "<(10,10,0), (50,10,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ wall }",   "<(10,40,0), (10,10,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ wall }",   "<(50,10,0), (50,40,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ wall }",   "<(50,40,0), (10,40,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ door }",   "<(25,10,0), (35,10,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ window }", "<(50,20,0), (50,30,0)>" },
                { "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)", "(30,20,0)", "{ window }", "<(40,40,0), (25,40,0)>" }},
                "dd2_08", ""),
                
                $step09("positions ^ types ^ elements ^ functions",
                new String [] { "positions", "types", "elements", "functions" },
                new String [][]
                {{ "(30,20,0)", "{ wall }",   "<(10,10,0), (50,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ wall }",   "<(10,40,0), (10,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ wall }",   "<(50,10,0), (50,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ wall }",   "<(50,40,0), (10,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ door }",   "<(25,10,0), (35,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ window }", "<(50,20,0), (50,30,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                 { "(30,20,0)", "{ window }", "<(40,40,0), (25,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" }},
                 "dd2_09", ""),
                 
                 $step10("functions ^ positions ^ types ^ elements ^ functions",
                 new String [] { "functions", "positions", "types", "elements", "functions" },
                 new String [][]
                 {{ "min(functions.value)", "(30,20,0)", "{ wall }",   "<(10,10,0), (50,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ wall }",   "<(10,40,0), (10,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ wall }",   "<(50,10,0), (50,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ wall }",   "<(50,40,0), (10,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ door }",   "<(25,10,0), (35,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ window }", "<(50,20,0), (50,30,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                  { "min(functions.value)", "(30,20,0)", "{ window }", "<(40,40,0), (25,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" }},
                  "dd2_10", ""),
                  
                  $step11("positions ^ types ^ functions ^ elements ^ functions",
                  new String [] { "positions", "types", "functions", "elements", "functions" },
                  new String [][]
                  {{ "(30,20,0)", "{ wall }",   "min(functions.value)", "<(10,10,0), (50,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ wall }",   "min(functions.value)", "<(10,40,0), (10,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ wall }",   "min(functions.value)", "<(50,10,0), (50,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ wall }",   "min(functions.value)", "<(50,40,0), (10,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ door }",   "min(functions.value)", "<(25,10,0), (35,10,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ window }", "min(functions.value)", "<(50,20,0), (50,30,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" },
                   { "(30,20,0)", "{ window }", "min(functions.value)", "<(40,40,0), (25,40,0)>", "avg|dist2lnseg(positions.position, elements.getTail, elements.getHead)" }},
                   "dd2_11", "");
                   
                   private final String demoName;
                   private final String[] headings;
                   private final String[][] data;
                   private final String methodName;
                   private final String description;
                   
                   DEMO_DATA_2(String demoName, String[] headings, String[][] data, String methodName, String description) {
                       this.demoName = demoName;
                       this.headings = headings;
                       this.data = data;
                       this.methodName = methodName;
                       this.description = description;
                   }
                   public String getDemoName() { return demoName; }
                   public String[] getHeadings() { return headings; }
                   public String[][] getData() { return data; }
                   public String getMethodName() { return methodName; }
                   public String getDescription() { return description; }
                   public static String getMenuName() { return "Print form definition"; }
                   //public static String getMenuName() { return "11 steps demo: 29_3_2005"; }
                   
                   public String getFormPrefix() {
                       return "form " + name() + " = " + demoName + ": {\n ";
                   }
                   public String getFormSuffix() {
                       return " };";
                   }
                   /**Prints out the 2D table one row at a time */
                   public String dataArrayToString() {
                       String result = new String();
                       for(int i = 0; i < data.length; i++) {
                           for(int j = 0; j < data[i].length; j++) {
                               result += data[i][j] + "\n";
                           }
                       }
                       return result;
                   }
                   /**Prints out the final result from methods above */
                   public String getFormDef() {
                       return getFormPrefix() + dataArrayToString() + getFormSuffix();
                   }
    }
    
    public void dd2_01() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step01;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_02() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step02;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_03() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step03;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_04() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step04;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_05() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step05;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_06() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step06;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_07() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step07;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_08() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step08;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_09() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step09;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_10() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step10;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    public void dd2_11() {
        DEMO_DATA_2 e = DEMO_DATA_2.$step11;
        System.out.println(e.getFormDef());
        //makeAndShowTable(e.getDemoName(), e.getHeadings(), e.getData());
    }
    //----------------------------------------------------------------------------
    /**Table methods */
    
    /** Makes a table that holds all node data, repeated the necessary number of times
     * @since 12.July 2005 */
    public void makeTableOfNodeData(
            String tableTitle, java.util.List <sNode> nodes, sNode selNode, User profile, Form form) {
        int tableLength = maxTableLength(nodes);
        int tableWidth = nodes.size(); //each node (with data) gets a separate column
        //System.out.println("tableLength:" + tableLength + " tableWidth:" + tableWidth);
        String[] headings = getHeadings(nodes); //headings are node names
        String[][] data = null;
        
        /**Add new data to existing data, for each node */
        for(sNode node : nodes) {
            data = combine(data, node.getDataStringsAsArray());
        }
        //System.out.println("Printing data:");
        //printArray(data);
        makeAndShowTable(tableTitle, headings, data, selNode, profile, form);
    }
    
    /**@since 13.July 2005 */
    public void makeAndShowTable(
            String tableTitle, String[] headings, String[][] data, 
            final sNode node, final User profile, final Form form) {
        /**From "Learning Java", p.448 */
        final JFrame f = new JFrame(tableTitle);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { f.dispose(); }
        });
        f.setLayout(new BorderLayout());
        final JTable table = new JTable(data, headings);
        initColumnSizes(table, data);
        f.add(new JScrollPane(table));
        //f.setPreferredSize(new Dimension(400, 180));
        //f.setLocation(200, 200);
        JPanel buttonsPanel = new JPanel();
        
        JButton okButton = new JButton("Add Data");
        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        /**'Cancel' button action to perform */
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                f.dispose();
            }
        });
        /**'Add Data' button action to perform */
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("OK button pressed");
                addDataFromTable(table, node, profile, form);
            }
        });
        f.add(buttonsPanel, BorderLayout.SOUTH); //add to the bottom
        f.pack();
        f.show();
    }
    
    /**@since 15.July 2005 */
    public void addDataFromTable(JTable table, sNode node, User profile, Form form) {
        for (int col=0; col < table.getColumnCount(); col++) {
            for(int row=0; row < table.getRowCount(); row++) {
                String value = (String)table.getModel().getValueAt(row, col);
                //compare builder.line 106 for non-parsed data entry
                 java.util.List <String> childNames = node.getChildrenNames();
            /**Add all the sum elements */
            for(String sortName : childNames) {
                Element elem = profile.retrieve(sortName);
                form.add(elem);
            }
            }
        }
    }
    
    /**Note: doesn't work yet
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit(). */
    private void initColumnSizes(JTable table, String[][] data) {
        TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        int totalWidth=0;
        
        for (int i=0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            /**Header */
            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            /**Cells */
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(
                    table, table.getColumnName(i), false, false, 0, i);
            cellWidth = comp.getPreferredSize().width * 2; //multiplier
            //cellWidth = comp.getWidth();
            //System.out.println("col:" + i + " headerWidth:" + headerWidth + " cellWidth:" + cellWidth);
            int colWidth = Math.max(headerWidth, cellWidth);
            totalWidth = totalWidth + colWidth; //add up all the col widths
            column.setPreferredWidth(colWidth);
            /**Add a drop down list for each cell in the table. @since 15 July 2005 */
            setUpDropDownColumn(table, column, getColumnValues(data, i));
        }
        //System.out.println("totalWidth:" + totalWidth);
        table.setPreferredScrollableViewportSize(new Dimension(totalWidth, 120));
    }
    
    /**@since 15.July 2005 */
    public Set getColumnValues(String[][] data, int colNum) {
        Set <String> result = new HashSet <String>();
        for(int row=0; row < data.length; row++) {
            result.add(data[row][colNum]);
        }
        return result;
    }
    
    /**Copied from: TableRenderDemo.java
     * @since 15.July 2005 */
    public void setUpDropDownColumn(JTable table, TableColumn column, Set <String> values) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        for(String s : values) {
            comboBox.addItem(s);
        }
        column.setCellEditor(new DefaultCellEditor(comboBox));
        /**Set up tool tips for the drop down cells. */
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        column.setCellRenderer(renderer);
    }
    
    /**not useful */
    private void setColWidthsToHeader(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.sizeWidthToFit(); //according to header width
        }
    }
    
    /**@since 12.July 2005 */
    public String[] getHeadings(List <sNode> nodes) {
        String[] result = new String[nodes.size()];
        int row=0;
        for(sNode node : nodes) {
            result[row] = node.getLabel();
            row++;
        }
        return result;
    }
    
    /**@since 12.July 2005 */
    public int maxTableLength(List <sNode> nodes) {
        int max=0;
        for(sNode node : nodes) {
            max = Math.max(node.getDataStrings().size(), max);
        }
        return max;
    }
    
    /**COMPLETED.
     * @since 8 July 2005 */
    public static String[][] combine(String[][] existing, String[] toAdd) {
        if(existing==null || existing.length==0)
            return addToEmpty(toAdd);
        if(toAdd==null || toAdd.length==0)
            return existing;
        int existingL=existing.length, toAddL=toAdd.length;
        if(existingL == toAddL)
            return combineEquals(existing, toAdd);
        else if(toAddL > existingL)
            return addLongToShort(existing, toAdd);
        else
            return addShortToLong(existing, toAdd);
    }
    
    /**@since 12.July 2005 */
    public static String[][] addToEmpty(String[] toAdd) {
        //System.out.println("addToEmpty() used");
        String[][] result = new String[toAdd.length][1];
        for(int row=0; row < toAdd.length; row++) {
            result[row][0] = toAdd[row];
        }
        return result;
    }
    
    /**@since 11.July 2005 */
    public static String[][] combineEquals(String[][] existing, String[] toAdd) {
        //System.out.println("combineEquals() used");
        int newWidth = maxWidth(existing) + 1;
        String[][] result = new String[existing.length][newWidth];
        for(int row=0; row<existing.length; row++) {
            for(int col=0; col<existing[row].length; col++) {
                result[row][col] = existing[row][col];
            }
            /**Now add the final column value */
            result[row][newWidth-1] = toAdd[row];
        }
        return result;
    }
    
    /**'toAdd' requires repeats added
     * @since 11.July 2005 */
    public static String[][] addShortToLong(String[][] existingLong, String[] toAddShort) {
        //System.out.println("addShortToLong() used");
        /**provide space at the end for the 'toAdd' array */
        int newWidth = maxWidth(existingLong) + 1;
        int longL = existingLong.length;
        int shortL = toAddShort.length;
        int repeats; //the number of times the short values are repeated
        int remainder = longL % shortL; //the remainder after (shortL * repeats)
        String[][] result = new String[longL][newWidth];
        
        /**First, transfer 'existing' to 'result' */
        for(int i=0; i<existingLong.length; i++) {
            for(int j=0; j<existingLong[i].length; j++) {
                result[i][j] = existingLong[i][j];
            }
        }
        /** for each value in shortArray */
        for(int shortRow=0, resultRow=0; shortRow < shortL; shortRow++) {
            if(remainder>0) {
                repeats = longL/shortL + 1; //add an extra if there is a remainder left
                remainder--;
            } else {
                repeats = longL/shortL;
            }
            for(int rep=0; rep < repeats; rep++, resultRow++) { //add 'r' number of repeats
                result[resultRow][newWidth-1] = toAddShort[shortRow];
            }
        }
        return result;
    }
    
    /**'existing' requires repeats added.
     * @since 11.July 2005 */
    public static String[][] addLongToShort(String[][] existingShort, String[] toAddLong){
        //System.out.println("addLongToShort() used");
        /**provide space at the end for the 'toAdd' array */
        int newWidth = maxWidth(existingShort) + 1;
        int longL = toAddLong.length;
        int shortL = existingShort.length;
        int repeats; //the number of times the short values are repeated
        int remainder = longL % shortL; //the remainder after (shortL * repeats)
        String[][] result = new String[longL][newWidth];
        
        /** For each row in 'existingShort' */
        for(int shortRow=0, resultRow=0; shortRow<shortL; shortRow++) {
            if(remainder>0) {
                repeats = longL/shortL + 1; //add an extra if there is a remainder left
                remainder--;
            } else {
                repeats = longL/shortL;
            }
            for(int rep=0; rep<repeats; rep++, resultRow++) { //for each repeat (possibly includes one remainder)
                for(int col=0; col < maxWidth(existingShort); col++) { //for each col in one row
                    result[resultRow][col] = existingShort[shortRow][col];
                }
                /**Add the values of 'toAddLong' to 'result'  */
                result[resultRow][newWidth-1] = toAddLong[resultRow];
            }
        }
        return result;
    }
    
    /**TEST HERE
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String[][] a1 = {
            {"a1", "b1", "c1", "d1"},
            {"a2", "b2", "c2", "d2"},
            {"a3", "b3", "c3", "d3"},
            {"a4", "b4", "c4", "d4"},
            {"a5", "b5", "c5", "d5"},
            {"a6", "b6", "c6", "d6"},
            {"a7", "b7", "c7", "d7"},
            {"a8", "b8", "c8", "d8"}
        };
        String[][] a2 = {
            {"a1", "b1", "c1", "d1"},
            {"a2", "b2", "c2"},
            {"a3"},
            {"a4", "b4", "c4", "d4"}
        };
        String[][] a3 = {
            {"a1", "b1", "c1", "d1"},
            {"a2", "b2", "c2", "d2"},
            {"a3", "b3", "c3", "d3"}
        };
        String[][] empty = new String[0][0];
        String[] b1 = {"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] b2 = {"x", "y", "z", "zz"};
        String[] b3 = {"x", "y", "z"};
        String[] b4 = {"Z"};
        
        //a2 = addNulls(a2, 8);
        //printArray(a2);
        //b1 = addNulls(b1, 6);
        //printArray(combine(a2, b3));
        //printArray(b1);
        //repeats(25, 102);
        //printArray(addRepeats(b1, b2)); //8 July 2005
        printArray(combine(null, b4));
        //repeatTest2(8, 4, 2);
    }
    
    /**Prints out a 2D String array */
    public static void printArray(String[][] input) {
        for(int i=0; i<input.length; i++) {
            for(int j=0; j<input[i].length; j++) {
                System.out.print(input[i][j].toString() + " ");
            }
            System.out.println("");
        }
    }
    
    /**Print out a 1D String array*/
    public static void printArray(String[] input) {
        for(int i=0; i<input.length; i++) {
            System.out.print(input[i] + " ");
        }
    }
    
    /**Finds maximum width of a (possibly non-rectangular) array
     * @since 1 July 2005*/
    public static int maxWidth(String[][] input) {
        int max=0;
        for(int i=0; i < input.length; i++) {
            max = Math.max(input[i].length, max);
        }
        return max;
    }
    
    /**Adds a demo graph by name (according to the name of a method in DemoData
     * @since 11 Apr.2005 */
    public void invokeDemoMethod(String methodName) {
        try {
            /**invoke the method named 'methodName' */
            Method method = this.getClass().getMethod(methodName);
            /**Empty parameters */
            Object[] params = new Object[0];
            if(method != null) {
                method.invoke(this, params);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**Makes a sum graph with s_1 as the top node */
    public static void makeSumNodes(String[] childNames, SORT_TYPE[] childTypes)
    throws TGException {
        if(childNames.length != childTypes.length) {
            System.out.println("ERROR: array sizes don't match");
            return;
        }
        Node parentNode = tgPanel.addNodeSorts(SORT_TYPE.SUM, "s_1");
        tgPanel.setSelect(parentNode);
        
        for(int i = 0; i < childNames.length; i++) {
            SORT_TYPE curType = childTypes[i];
            String curName = childNames[i];
            Node childNode = tgPanel.addNodeSorts(curType, curName);
            Edge edge = tgPanel.addEdge(parentNode, childNode, terms.EDGE_DEFAULT_LENGTH);
        }
    }
    
    /**Makes attributes nodes (top-dowm) of @since 12.May 2005 */
    public static void makeAttributeNodes(
            Node parentNode, SORT_TYPE baseType, String baseName, SORT_TYPE weightType, String weightName)
            throws TGException {
        Node baseChildNode = tgPanel.addNodeSorts(baseType, baseName);
        Node weightChildNode = tgPanel.addNodeSorts(weightType, weightName);
        addAttributeEdges(parentNode, baseChildNode, weightChildNode);
    }
    
    /**For automatically putting data into nodes when graph is built.
     * @since 27.May 2005 */
    public Node addNodeWithData(SORT_TYPE type, String name) throws TGException {
        sNode newNode = tgPanel.addNodeSorts(type, name);
        addDataToSelectNode(newNode, DEMO_DATA_1.elements);
        return newNode;
    }
    
    /**Finds a demo with demoName that is equal to a node name.
     * @since 27.May 2005 */
    public void addDataToNodeAccordingToName(sNode node) {
        for(DEMO_DATA_1 demo : DEMO_DATA_1.values()) {
            if(node.getLabel().equals(demo.getDemoName())) {
                addDataToSelectNode(node, demo);
            }
        }
    }
    
    /**Constructs an arbitrarily large graph.  Array position is left to right. i.e. a^b^c -> {a,b,c}.
     * NodeName position must correspond to nodeType position.
     * All connecting attribute nodes are type ATTRIBUTE_ANON */
    public sNode makeAttributeGraphTopDown(String[] nodeNames, SORT_TYPE[] nodeTypes)
    throws TGException {
        if(nodeNames.length != nodeTypes.length) {
            System.out.println("ERROR: array sizes don't match");
            return null;
        }
        sNode parentNode = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE_ANON, topFrame.getNewAnonLabel(tgPanel));
        tgPanel.setSelect(parentNode);
        
        for(int i = 0; i < nodeNames.length; i++) {
            /**If not at bottom, make one base node and one ANON weight node */
            if(!lastTwo(nodeNames, i)) {
                SORT_TYPE baseType = nodeTypes[i];
                String baseName = nodeNames[i];
                
                sNode baseNode = tgPanel.addNodeSorts(baseType, baseName);
                //addDataToNodeAccordingToName(baseNode);
                sNode weightNode = tgPanel.addNodeSorts(SORT_TYPE.ATTRIBUTE_ANON, topFrame.getNewAnonLabel(tgPanel));
                addAttributeEdges(parentNode, baseNode, weightNode);
                parentNode = weightNode;
                /**If at bottom, makes two last nodes */
            } else {
                SORT_TYPE baseType = nodeTypes[i];
                String baseName = nodeNames[i];
                SORT_TYPE weightType = nodeTypes[i+1];
                String weightName = nodeNames[i+1];
                makeAttributeNodes(parentNode, baseType, baseName, weightType, weightName);
                break;
            }
        }
        //System.out.println("parent name: " + parentNode.getLabel());
        tgPanel.updateDrawPos(parentNode);
        tgPanel.resetDamper();
        
        return parentNode;
    }
    
    /**Recursive method. @since 1.June 2005 */
    public sNode findNodeInGraphByName(String name) {
        for(Node n : tgPanel.getNodesCollection()) {
            if(n.getLabel().equals(name))
                return (sNode)n;
        }
        return null;
    }
    
    /**@since 1.June 2005 */
    public void addDataToNamedNode(String nodeName, DEMO_DATA_1 data) {
        sNode nodeFound = findNodeInGraphByName(nodeName);
        if(nodeFound != null) {
            addDataToSelectNode(nodeFound, data);
        } else {
            System.out.println("Error: node NOT found: " + nodeName);
        }
    }
    
    public boolean lastTwo(Object[] array, int i) {
        return i > (array.length - 3);
    }
    
    public static void addAttributeEdges(Node parent, Node baseNode, Node weightNode) {
        Edge baseEdge = tgPanel.addEdge(parent, baseNode, terms.EDGE_DEFAULT_LENGTH);
        baseEdge.setBaseEdge();
        
        Edge weightEdge = tgPanel.addEdge(parent, weightNode, terms.EDGE_DEFAULT_LENGTH);
        weightEdge.setWeightEdge();
    }
    
    /**Draws edges from a single parent to multiple children */
    public static void addChildEdges(Node parent, Node[] children) {
        Collection <Node> childList = Arrays.asList(children);
        for(Node child : childList) {
            Edge e = tgPanel.addEdge(parent, child, terms.EDGE_DEFAULT_LENGTH);
        }
    }
    
    /**UNUSED (DON'T use. use enum approach instead.) Retreives all the sortGraph method names in DemoGraphNames -- these are the demo graph names
     * See also: constructDemoGraph(graphName) in GLPanel */
    public Set getDemoGraphNames() {
        Method[] methods = this.getClass().getMethods();
        Set result = new HashSet();
        
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if(name.startsWith("sortsGraph")) {
                result.add(name);
            }
        }
        return result;
    }
    
    
//     /**OLD test. Takes any two numbers and finds how to divide the table up */
//    public static void repeatsTestCalculate(int a, int b) {
//        int shortL, longL;
//        if(a < b) {
//            shortL = a; longL = b;
//        } else {
//            shortL = b; longL = a;
//        }
//        int repeats = longL / shortL;
//        int remainder = longL % shortL;
//        System.out.println("shortLength: " + shortL);
//        System.out.println("longLength: " + longL);
//        System.out.println("Repeats: " + repeats);
//        System.out.println("remainder: " + remainder);
//    }
    
    /**OLD test. */
//    public static void repeatTest1(String[] a, String[] b) {
//        int aL = a.length; int bL = b.length;
//        int shortL, longL;
//        String[] shortArray, longArray;
//
//        if(aL < bL) {
//            shortL = aL; longL = bL;
//            shortArray = a; longArray = b;
//        } else {
//            shortL = bL; longL = aL;
//            shortArray = b; longArray = a;
//        }
//        int repeats = longL / shortL; //the number of times the short values are repeated
//        int remainder = longL % shortL; //the remainder after (numShort * repeats)
//        int checkSum = repeats*shortL + remainder; //should always equal longL
//
//        System.out.print("LongLength:" + longL);
//        System.out.println(" ShortLength:" + shortL);
//        System.out.println("CheckSum:" + checkSum);
//        System.out.print("Repeats:" + repeats);
//        System.out.println(" Remainder:" + remainder);
//
//        /**Print out results
//         * NOTE: n declared in outer loop, but incremented in inner loop
//         * n=total num rows; i=position in shortA; j=just a counter */
//        //int i, n;
//        for(int i=0, n=0; i<shortL; i++) { //for each value in shortArray
//            for(int j=0; j<repeats; j++, n++) { //add the repeats
//                System.out.println(longArray[n] + " " + shortArray[i]);
//            }
//            /**now distribute the remainders, if any*/
//            if(remainder>0) {
//                System.out.println(longArray[n++] + " " + shortArray[i]);
//                remainder--;
//            }
//        }
//    }
    
    /**OLD test. Used for attribute sorts
     * //     * @since 1 July 2005 */
//    public static String[][] combineX(String [][] existing, String[] toAdd) {
//        int length =  Math.max(existing.length, toAdd.length); //use whatever's the longest
//        int width = maxWidth(existing) + 1; //add one space to put the 'add' array at the end
//        /**Make the existing array rectangular with all empty spaces filled */
//        existing = addNulls(existing, length);
//        toAdd = addNulls(toAdd, length);
//        String[][] result = new String[length][width];
//
//        for(int i = 0; i < toAdd.length; i++) {
//            for(int j = 0; j < width; j++) {
//                /**If at the last cell */
//                if(j == width-1) {
//                    result[i][j] = toAdd[i];
//                } else {
//                    result[i][j] = existing[i][j];
//                }
//            }
//        }
//        return result;
//    }
    
    /**OLD test. Returns a String[][] */
//    public static String[][] addRepeats2D(String[][] existing, String[] toAdd) {
//        int existingL = existing.length; int toAddL = toAdd.length;
//        int shortL, longL;
//        //String[] shortArray, longArray;
//
//        if(existingL < toAddL) {
//            shortL = existingL; longL = toAddL;
//            //shortArray = a; longArray = b;
//        } else {
//            shortL = toAddL; longL = existingL;
//            //shortArray = b; longArray = a;
//        }
//        int repeats = longL / shortL; //the number of times the short values are repeated
//        int remainder = longL % shortL; //the remainder after (shortL * repeats)
//        String[][] result = new String[longL][maxWidth(existing)+1];
//
//        /**NOTE: 'row' declared in outer loop, but incremented in inner loop
//         * row=total num rows; k=position in shortA; j=just a counter */
//        for(int k=0, row=0; k<shortL; k++) { //for each value in shortArray
//            for(int j=0; j<repeats; j++, row++) { //add the repeats
//                //result[row][0] = longArray[row];
//                //result[row][1] = shortArray[k];
//                //System.out.println(longArray[row] + " " + shortArray[k]);
//            }
//            /**now distribute the remainders, if any, one at a time */
//            if(remainder>0) {
//                //result[row][0] = longArray[row];
//                //result[row][1] = shortArray[k];
//                //System.out.println(longArray[row] + " " + shortArray[k]);
//                row++; remainder--; //previous loop increments 'row' before it gets here
//            }
//        }
//        return result;
//    }
    
    /**OLD test code. Returns a String[][] */
//    public static String[][] addRepeats(String[] a, String[] b) {
//        int aL = a.length; int bL = b.length;
//        int shortL, longL;
//        String[] shortArray, longArray;
//
//        if(aL < bL) {
//            shortL = aL; longL = bL;
//            shortArray = a; longArray = b;
//        } else {
//            shortL = bL; longL = aL;
//            shortArray = b; longArray = a;
//        }
//        int repeats = longL / shortL; //the number of times the short values are repeated
//        int remainder = longL % shortL; //the remainder after (shortL * repeats)
//        String[][] result = new String[longL][2];
//
//        /**NOTE: n declared in outer loop, but incremented in inner loop
//         * n=total num rows; i=position in shortA; j=just a counter */
//        for(int i=0, n=0; i<shortL; i++) { //for each value in shortArray
//            for(int j=0; j<repeats; j++, n++) { //add the repeats
//                result[n][0] = longArray[n];
//                result[n][1] = shortArray[i];
//                //System.out.println(longArray[n] + " " + shortArray[i]);
//            }
//            /**now distribute the remainders, if any, one at a time */
//            if(remainder>0) {
//                result[n][0] = longArray[n];
//                result[n][1] = shortArray[i];
//                //System.out.println(longArray[n] + " " + shortArray[i]);
//                n++; remainder--; //previous loop increments 'n' before it gets here
//            }
//        }
//        return result;
//    }
    
    /**NOT completed. List-based rather than array-based */
//    public static List <String> addRepeats(List <String> a, List <String> b) {
//        List <String> shortList, longList;
//        int shortL, longL;
//        if(a.size() < b.size()) {
//            shortList = a; longList = b;
//            shortL = a.size(); longL = b.size();
//        } else {
//            shortList = b; longList = a;
//            shortL = b.size(); longL = a.size();
//        }
//        int repeats = longL / shortL; //the number of times the short values are repeated
//        int remainder = longL % shortL; //the remainder after (shortL * repeats)
//        List result = new ArrayList();
//
//        for(String s1 : shortList) {
//            for(String s2 : shortList) {
//
//            }
//        }
//        return result;
//    }
    
    
    /**USED? Adds a NULL string to empty cells of a 2D array */
    public static String[][] addNulls(String[][] input, int newLength) {
        int maxWidth = maxWidth(input);
        int leftOutL = newLength - input.length;
        //System.out.println("maxWidth: " + maxWidth);
        String[][] result = new String[newLength][maxWidth];
        /**First, copy all existing into a new array*/
        for(int i = 0; i < input.length; i++) {
            int leftOutW = maxWidth - input[i].length;
            for(int j = 0; j < input[i].length; j++) {
                result[i][j] = input[i][j];
                /**for each row, fill in empty spaces with NULLs if needed */
                if(leftOutW > 0) {
                    for(int k = input[i].length; k < maxWidth; k++) {
                        result[i][k] = "Na";
                    }
                }
            }
        }
        if(leftOutL > 0) {
            for(int i = input.length; i < newLength; i++) {
                for(int j = 0; j < maxWidth; j++) {
                    result[i][j] = "Nb";
                }
            }
        }
        return result;
    }
    
    /**USED? */
    public static String[] addNulls(String[] input, int newLength) {
        String [] result = new String[newLength];
        int leftOut = newLength - input.length;
        for(int i = 0; i < input.length; i++) {
            result[i] = input[i];
        }
        if(leftOut > 0) {
            for(int j = input.length; j < newLength; j++) {
                result[j] = "Nc";
            }
        }
        return result;
    }
    
}
