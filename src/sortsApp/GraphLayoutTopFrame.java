/*
 * GraphLayoutTopFrame.java
 *
 * Created on September 26, 2003, 11:25 AM
 */

package sortsApp;

import cassis.Element;
import cassis.convert.*;
import cassis.form.*;
import cassis.ind.*;
import cassis.ind.Label;
import cassis.io.*;
import cassis.parse.*;
import cassis.visit.SdlVisitor;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import com.touchgraph.graphlayout.graphelements.VisibleLocality;
import com.touchgraph.graphlayout.sNode.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import cassis.sort.Sort;
import cassis.sort.SortsMC;
import java.util.Iterator;
import com.touchgraph.graphlayout.*;
import java.awt.*;
import cassis.sort.*;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;
import sortsApp.DemoData.DEMO_GRAPH_1;
import sortsApp.DemoData.DEMO_GRAPH_2;
import sortsApp.DemoData.DEMO_DATA_1;
import sortsApp.DemoData.DEMO_DATA_2;
import sortsApp.SortsTerms.SORT_TYPE;
//import cassis.match.*;


/**
 *
 * @author  cumming
 */
public class GraphLayoutTopFrame extends JFrame {
    
    protected Vector glPanels = new Vector(); //vector to hold GraphLayoutPages - each in separate tab
    //protected JTabbedPane tabbedPane = new JTabbedPane();
    private int panelCount = 0;
    //protected TGUIManager tgUIManager = new TGUIManager();
    SortsTerms terms = new SortsTerms();
    BusinessLogic logic = new BusinessLogic();
    SortDrawerBuilder builder = new SortDrawerBuilder();
    /**places to hold sorts for comparison. String descriptios shown in text fields */
    Sort firstComparisonSort = null;
    Sort secondComparisonSort = null;
    
    /** Creates new form GraphLayoutTopFrame */
    public GraphLayoutTopFrame() {
        //System.out.println("GLTopFrame constructor: line 37");
        log_("entering init components");
        //setDefaultLookAndFeelDecorated(true);
        setLookAndFeel();
        initComponents();
        
        /**@since 10.May 2005. add menu item listing all graphs in DemoGraphsData class */
        addDemoGraphs();
        //NOTE: Use with DPM: addPanel(false) 17 March 2004
        log_("entering add panel");
        //addPanel(true); //initial panel, with demo graph
        addPanel(false); //initial panel, with no demo graph
        setTitle("Sorts Demonstration App: 2005");
        pack();
        setPositionRight(this.getWidth(), this.getHeight(), this);
        show();
        System.out.println("This application is running from directory: " + terms.NEWLINE +
                System.getProperty("user.dir", "."));
    }
    
    public void setLookAndFeel() {
        // Get the currently installed look and feel
        LookAndFeel lf = UIManager.getLookAndFeel();
        //System.out.println("Look and feel before:" + lf.toString());
        
        // Install a different look and feel
        try {
            //MetalLookAndFeel laf = new MetalLookAndFeel();
            //laf.setCurrentTheme(new OceanTheme());
            //this works
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            
            //UIManager.setLookAndFeel(laf);          
            //javax.swing.plaf.metal.OceanTheme
            //XX javax.swing.plaf.synth.SynthLookAndFeel //needs to be implemented by developer
            
            // turn off bold fonts
            //UIManager.put("swing.boldMetal", Boolean.FALSE);
            
            // re-install the Metal Look and Feel
            //UIManager.setLookAndFeel(new MetalLookAndFeel());
            
            // only needed to update existing widgets
            //SwingUtilities.updateComponentTreeUI(rootComponent);
            
        } catch (Exception e) { }
//        ClassNotFoundException e) {
//        } catch (UnsupportedLookAndFeelException e) {
//        } catch (IllegalAccessException e) {
//        }
    }
    
    
    /**@since 10.May 2005 */
    public void addDemoGraphs() {
        JMenu demoMenu;
        /**Each demo graph gets a separate sub-menu */
        //-----------------------------------------
        demoMenu = new JMenu(DEMO_GRAPH_1.getMenuName());
        demoMenu.setFont(new java.awt.Font("Arial", 0, 12));
        /**Manually add additional graphs here */
        demoGraphsMenu.add(demoMenu);
        
        for(DEMO_GRAPH_1 g : DEMO_GRAPH_1.values()) {
            addMenuItem(demoMenu, g.getDemoName(), g.getMethodName(), true);
        }
        //-----------------------------------------
        demoMenu = new JMenu(DEMO_GRAPH_2.getMenuName());
        demoMenu.setFont(new java.awt.Font("Arial", 0, 12));
        /**Manually add additional graphs here */
        demoGraphsMenu.add(demoMenu);
        
        for(DEMO_GRAPH_2 g : DEMO_GRAPH_2.values()) {
            addMenuItem(demoMenu, g.getDemoName(), g.getMethodName(), true);
        }
        //-----------------------------------------
        /**Each demo graph gets a separate sub-menu */
        demoMenu = new JMenu(DEMO_DATA_2.getMenuName());
        demoMenu.setFont(new java.awt.Font("Arial", 0, 12));
        /**Manually add additional graphs here */
        demoDataMenu.add(demoMenu);
        
        for(DEMO_DATA_2 g : DEMO_DATA_2.values()) {
            addMenuItem(demoMenu, g.getDemoName(), g.getMethodName(), false);
        }
        //-----------------------------------------
        /**Each demo graph gets a separate sub-menu */
        demoMenu = new JMenu(DEMO_DATA_1.getMenuName());
        demoMenu.setFont(new java.awt.Font("Arial", 0, 12));
        /**Manually add additional graphs here */
        demoDataMenu.add(demoMenu);
        
        for(DEMO_DATA_1 g : DEMO_DATA_1.values()) {
            addMenuItem(demoMenu, g.getDemoName(), g.getMethodName(), false);
        }
    }
    
    /**@since 10.May 2005 */
    public void addMenuItem(JMenu parentMenu, String demoName, final String methodName, final boolean clearScreen) {
        final JMenuItem newItem = getNewMenuItem(demoName);
        parentMenu.add(newItem);
        
        /**Add action listener that invokes the method name specified in the demo graph */
        newItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /**First, clear tg panel */
                TGPanel editPanel = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
                if(clearScreen) {
                    clearPanel(editPanel);
                }
                DemoData demoData = new DemoData(editPanel);
                //System.out.println("Method name: " + methodName);
                demoData.invokeDemoMethod(methodName);
            }
        });
    }
    
    public JMenuItem getNewMenuItem(String name) {
        JMenuItem menuItem = new javax.swing.JMenuItem(name);
        menuItem.setFont(new java.awt.Font("Arial", 0, 12));
        return menuItem;
    }
    
    //UNUSED, doesn't work...
    public void addDG(String[] demoNames) {
        System.out.println("entering addDG()");
        try {
            //System.out.println("DemoNames length: " + demoNames.length);
            for (int i = 0; i < demoNames.length; i++) {
                System.out.println("Demo name: " + demoNames[i]);
                Class c = Class.forName(demoNames[i]);
                System.out.println("Class to string: " + c.toString());
            }
//                Object[] enums = c.getEnumConstants();
//                if(enums == null) {
//                   System.out.println("Enum is null");
//                }
//                for (int j = 0; j < enums.length; j++) {
//                    System.out.println("Enum constant " + j + " " + enums[j]);
//                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    //JMenu demoMenu = new JMenu(c.getDemoName());
    
//        } catch (ClassNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
    //}
    //catch
    //Enum e = DemoData.getClass(n);
    //for (int i = 0; i < enums.length; i++) {
    //Enum curEnum = enums[i];
    //Enum enum_ = (sortsApp.DemoData.DEMO_GRAPH_1);
    //Enum final static enum_ = sortsApp.DemoData.DEMO_GRAPH_1;
//        System.out.println("ENUM-Declaring class: " + DEMO_GRAPH_1.getDeclaringClass());
//        System.out.println("ENUM-Name: " + DEMO_GRAPH_1.name());
//        System.out.println("ENUM-To string: " + DEMO_GRAPH_1.toString());
    
    //System.out.println("Demo graph name: " + curEnum.toString());
    //JMenu demoMenu = new JMenu(curEnum.getDemoName());
    //}
    //}
    
    /**Prints a message to indicate progress during startup */
    public void log_(String s) {
        System.out.println(s);
    }
    
    //first java 1.5 method! 15.March.2005
    void testJava1_5(Set <String>  c) { //generics
        for (String s : c) { //enhanced for loop
            System.out.print(s);
        }
    }
    
    /** Sets position */
    public void setPosition(int width, int height, JFrame frame) {
        Dimension frameSize = frame.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
    }
    
    public void setPositionRight(int width, int height, JFrame frame) {
        Dimension frameSize = frame.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - width), 0); //(screenSize.height - height));
    }
    
    public void showErrorDialog(String message, Component component) {
        JOptionPane.showMessageDialog(component, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showInfoDialog(String message, Component component) {
        JOptionPane.showMessageDialog(component, message, "For your information", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void showMessageDialog(String message, Component component) {
        JOptionPane.showMessageDialog(component, message, "Message to user", JOptionPane.PLAIN_MESSAGE);
    }
    
    /** adds a tabbed panel - each with with two panels */
    public void addPanel(boolean withDemoGraph) {
        //System.out.println("GLTopFrame.addPanel(): line 47");
        panelCount++;
        GraphLayoutPage glp;
        String pageName = "Page_" + String.valueOf(panelCount);
        if(withDemoGraph == true) { //either a full graph e.g. sortsGraph3, or a single node
            if(panelCount==1) { //at initial page
                log_("entering new GLP w/ panel count==1");
                glp = new GraphLayoutPage(pageName, true, this); //(middle param) true => withDemoGraph
            } else { //panelCount > 1
                log_("entering new GLP w/ panel count>1");
                glp = new GraphLayoutPage(pageName, false, this); //(middle param) false => with NO DemoGraph
                try {
                    Node s1 = glp.getEditPanel().getTGPanel().addNodeSorts(SORT_TYPE.SUM, "top");
                } catch (TGException e) {
                    System.out.println(e);
                }
            }
        } else { //no demo graph
            glp = new GraphLayoutPage(pageName, false, this);
        }
        glPanels.addElement(glp); //Vector containing all glPages. Not yet used...
        //NOTE: changed 17 March 2004 for use with DPM: //glp.constructSorts(newName);
        glp.constructSorts(pageName);
        jTabbedPane.addTab(pageName, glp);
        jTabbedPane.setSelectedComponent(glp); //select the newly added tab
        glp.setVisible(true);
        this.pack();
        this.show();
    }
    
    //    public void switchTGUIManager(GLPanel selectedGLP) {
    //        this.tgUIManager = selectedGLP.getTgUIManager();
    //    }
    
    /**Gets the current EDIT TGPanel (one at top) */
    //should fix the tabbed pane problems 01.10.2003 MC
    public TGPanel getCurrentTGPanel() {
        GraphLayoutPage currentPage = (GraphLayoutPage)jTabbedPane.getSelectedComponent();
        return currentPage.getEditPanel().getTGPanel();
    }
    
    /**Gets the current EDIT GLPanel (one at top) */
    public GLPanel getCurrentGLPanel() {
        GraphLayoutPage currentPage = (GraphLayoutPage)jTabbedPane.getSelectedComponent();
        return currentPage.getEditPanel();
    }
    
    /**Get the current GraphLayoutPage (which contains both 'edit' and 'view' GLPanels) */
    public GraphLayoutPage getCurrentGraphLayoutPage() {
        return (GraphLayoutPage)jTabbedPane.getSelectedComponent();
    }
    
    public Node getSelectedNodeFromEditPanel() {
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        return tgpEdit.getSelect();
    }
    
    //added 16.10.2003 MC. A little hack to get buttons all set to the same value
    public void setAllButtons(AbstractAction action) {
        for (int n = 0; n < jTabbedPane.getTabCount(); n++) {
            GraphLayoutPage currPage = (GraphLayoutPage)jTabbedPane.getComponentAt(n);
            TGPanel currTGP = currPage.getEditPanel().getTGPanel();
            action.setEnabled(true);
        }
    }
    
    public void saveGraph(GraphLayoutPage glp) {
        //ImmutableGraphEltSet ges = glp.getEditPanel().getTGPanel().getGES();//CompleteEltSet();
        GraphEltSet ges = glp.getEditPanel().getTGPanel().getCompleteEltSet();
        String filePath = new String();
        final JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        File file = chooser.getSelectedFile();
        
        if(file != null && returnVal == JFileChooser.APPROVE_OPTION) {
            //JOptionPane.showMessageDialog(null, file.getPath());
            filePath = file.getPath();
        } else if(returnVal == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "Canceled");
        }
        
        if(filePath != null && ges != null) {
            try {
                //FileOutputStream fos = new FileOutputStream("P:/cumming/research/java/sorts.latest/examples.serialized/testG"); //save to a file
                FileOutputStream fos = new FileOutputStream(filePath); //save to a file
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream out = new ObjectOutputStream(gzos);  //save objects
                out.writeObject(ges); //write the glpage out
                out.flush();
                out.close();
                fos.close();
                //JOptionPane.showMessageDialog(null, file.getPath() + " Saved");
                printMessage("Graph saved to: " + file.getPath());
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
    
    public void loadGraph() {
        String filePath = new String();
        final JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        File file = chooser.getSelectedFile();
        
        if(file != null && returnVal == JFileChooser.APPROVE_OPTION) {
            filePath = file.getPath();
            //printMessage("PATH = " + filePath);
        } else if(returnVal == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "Canceled");
        }
        
        if(filePath != null) {
            try {
                //FileInputStream fis = new FileInputStream("P:/cumming/testA"); //read from a file
                FileInputStream fis = new FileInputStream(filePath);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream in = new ObjectInputStream(gzis);  //read objects
                GraphEltSet ges = (GraphEltSet)in.readObject(); //read the glpage in
                in.close();
                //ges.printAllNodes();
                //JOptionPane.showMessageDialog(null, file.getPath() + " Loaded");
                addPanel(false); //false means there is no demo graph
                //printMessage("PATH is " + filePath);
                TGPanel tgp = getCurrentTGPanel();
                //tgp.setCompleteEltSet(ges);
                tgp.setGraphEltSet(ges);
                tgp.setVisibleLocality(new VisibleLocality(ges));
                tgp.setAllVisible(ges); //makes all nodes and edges visible
                tgp.updateLocalityFromVisibility();
                //
                printMessage(file.getPath() + " loaded");
                printMessage("Num edges = " + String.valueOf(tgp.getEdgeCount()));
                printMessage("Num nodes = " + String.valueOf(tgp.getNodeCount()));
                printMessage("Visible node count = " + String.valueOf(tgp.visibleNodeCount()));
                //tgp.updateGraphSize();
                
                //tgp.updateDrawPositions();
                //tgp.setGraphEltSet(ges); //doesn't work
                tgp.repaint();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    
    /**@since Feb 10, 2005 */
    public Form getFormFromSdlFile(String dbName) {
        /**@since Feb 9, 2005 */
        String userName = "michael";
        User profile = User.find(userName);
        if (profile == null) {
            profile = new User(userName);
        }
        final JFileChooser chooser =
                new JFileChooser(new File("P:\\cumming\\research\\java\\sorts.latest\\src\\applets\\sdl"));
        int returnVal = chooser.showOpenDialog(this);
        File file = chooser.getSelectedFile();
        Form data = null;
        
        if(file != null && returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = file.getPath();
            System.out.println("Filepath: " + filePath);
            
            try {
                BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String input = dis.readLine();
                if (!input.startsWith("#SDL V1.0a")) {
                    System.out.println("File not recognized as valid 'SDL V1.0a' file");
                    return null;
                }
                profile.parse(dis);
            } catch (IOException e) {
                System.out.println(e);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            data = (Form)profile.retrieve(dbName);
        }
        return data;
    }
    
    /**@since 24.May 2005
     * See: DemoApplication_29_3_2005.java */
    public void retreiveAndPrintExistingForm(TGPanel tgPanel, String formName) {
        Sorts sorts = tgPanel.getSorts();
        Element data = tgPanel.getProfile().retrieve(formName);
        if(data != null) {
            SdlVisitor sdlVisitor = new SdlVisitor();
            data.ofSort().accept(sdlVisitor);
            sdlVisitor.defineVariable(formName, data);
            System.out.println(sdlVisitor.functionsToString());
            System.out.println(sdlVisitor.toString());
        } else {
            System.out.println("Error: data is null when attempting to retrieve Form: " + formName);
        }
    }
    
    /**@since Feb 17, 2005 */
    public SortsMC defineAndGetSorts(sNode selNode) {
        if(selNode == null) {
            return null;
        }
        /**clear nodes from view panel */
        clearPanel(getCurrentGraphLayoutPage().getViewPanel().getTGPanel());
        /**cleanup to avoid defining the same sort more than once */
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        tgpEdit.getSorts().cleanup();
        try {
            /**then define sorts on the edit panel -- all sorts are stored in the tgpEdit panel */
            builder.defineSortTreePostOrder(tgpEdit, selNode, 0, null); //start counter at zero
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return tgpEdit.getSorts();
    }
    
    /**@since Feb 17, 2005 */
    public Sort getSelectedTopSort() {
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selNode = (sNode)tgpEdit.getSelect();
        SortsMC sorts = tgpEdit.getSorts();
        
        if(sorts == null) {
            try {
                sorts = defineAndGetSorts(selNode);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sorts.topSort();
    }
    
    public void clearPanel(TGPanel panel) {
        panel.clearAll();
        panel.clearSelect(); //selected node got left behind...
        panel.repaint();
    }
    
    /**@since 27.May 2005 */
    public String getNewAnonLabel(TGPanel tgPanel) {
        int count = 0;
        for(sNode n : tgPanel.getNodesCollection()) {
            if(n.getSortType().equals(SORT_TYPE.ATTRIBUTE_ANON))
                count++;
        }
        return "ANON_" + count;
    }
    
    public void printMessage(String s) {
        /**messageArea is the scrolling text area for messages on a GraphLayoutPage */
        GraphLayoutPage pageSelect = (GraphLayoutPage)jTabbedPane.getSelectedComponent();
        pageSelect.getMessageArea().append(s + "\n");
        /**Also print out to stdOut @since 25.May 2005 */
        System.out.println(s);
    }
    
//        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
//        Node selectNode = tgpEdit.getSelect();
//
//        if(selectNode instanceof sNode) {
//            sNode selectSortNode = (sNode)selectNode;
//            SORT_TYPE type = selectSortNode.getSortType();
//            //String suf = selectSortNode.getSortType().getSortSuffix();
//            //see NewSortChooserPanel for Label suffixes: [Label], [Date], [Numeric], +, ^
//            if (type.equals(SORT_TYPE.PRIMITIVE_LABEL)) { // "[Label]"
//                SortInputFrame inputFrame = new SortInputFrame(selectNode, tgpEdit);
//                SortLabelInputPanel labelPanel = new SortLabelInputPanel(inputFrame);
//                inputFrame.addComponents(labelPanel);
//            } else if (type.equals(SORT_TYPE.ATTRIBUTE)) { // " ^ "
//                //SortAttributeFrame attForm = new SortAttributeFrame(n, tgPanel);
//                //attForm.show();
//            }
//        }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        jSeparator6 = new javax.swing.JSeparator();
        jTabbedPane = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addNewPanelMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        saveGraphMenuItem = new javax.swing.JMenuItem();
        closeCurrentGraphMenuItem = new javax.swing.JMenuItem();
        openExistingGraphMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        loadSdlFileMenuItem = new javax.swing.JMenuItem();
        drawSdlFileMenuItem = new javax.swing.JMenuItem();
        jSeparator = new javax.swing.JSeparator();
        exitAppMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        clearEditPanelMenuItem = new javax.swing.JMenuItem();
        clearViewPanelMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        clearMessagesMenuItem = new javax.swing.JMenuItem();
        selectedNodeMenu = new javax.swing.JMenu();
        editNodeMenuItem = new javax.swing.JMenuItem();
        deleteNodeMenuItem = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        addDataMenuItem = new javax.swing.JMenuItem();
        clearDataMenuItem = new javax.swing.JMenuItem();
        printDataStrings = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        makeAttributeTableMenuItem = new javax.swing.JMenuItem();
        printDataPairsMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        visualizeSortsGraphMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        printSortDefinitionMenuItem = new javax.swing.JMenuItem();
        printSortDefStringMenuItem = new javax.swing.JMenuItem();
        printFormDefStringMenuItem = new javax.swing.JMenuItem();
        printSortTreeMenuItem = new javax.swing.JMenuItem();
        printChildrenOfSelectedMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        setSelectedAsFirstSortMenuItem = new javax.swing.JMenuItem();
        setSelectedAsSecondSort = new javax.swing.JMenuItem();
        compareNodeMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        expandNodeMenuItem = new javax.swing.JMenuItem();
        collapseNodeMenuItem = new javax.swing.JMenuItem();
        hideNodeMenuItem = new javax.swing.JMenuItem();
        sortsMenu = new javax.swing.JMenu();
        PrintAllSortsInProfileMenuItem = new javax.swing.JMenuItem();
        resetSortsMenuItem = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        CompareFirstAndSecondSortsMenuItem = new javax.swing.JMenuItem();
        demoGraphsMenu = new javax.swing.JMenu();
        demoDataMenu = new javax.swing.JMenu();
        retrieveFormMenu = new javax.swing.JMenu();
        retrieveFormMenuItem = new javax.swing.JMenuItem();

        getContentPane().setLayout(new java.awt.FlowLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        getContentPane().add(jSeparator6);

        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

        getContentPane().add(jTabbedPane);

        menuBar.setFont(new java.awt.Font("Arial", 1, 12));
        fileMenu.setText("File");
        fileMenu.setFont(new java.awt.Font("Arial", 0, 12));
        addNewPanelMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        addNewPanelMenuItem.setText("New Graph");
        addNewPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewPanelMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(addNewPanelMenuItem);

        fileMenu.add(jSeparator2);

        saveGraphMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        saveGraphMenuItem.setText("Save Current Graph");
        saveGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGraphMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveGraphMenuItem);

        closeCurrentGraphMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        closeCurrentGraphMenuItem.setText("Close Current Graph");
        closeCurrentGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeCurrentGraphMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(closeCurrentGraphMenuItem);

        openExistingGraphMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        openExistingGraphMenuItem.setLabel("Load Graph from Serialized Graph File");
        openExistingGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openExistingGraphMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(openExistingGraphMenuItem);

        fileMenu.add(jSeparator3);

        loadSdlFileMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        loadSdlFileMenuItem.setLabel("Load Graph from SDL File");
        loadSdlFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSdlFileMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(loadSdlFileMenuItem);

        drawSdlFileMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        drawSdlFileMenuItem.setLabel("Draw SDL File");
        drawSdlFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawSdlFileMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(drawSdlFileMenuItem);

        fileMenu.add(jSeparator);

        exitAppMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        exitAppMenuItem.setText("Exit");
        exitAppMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitAppMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitAppMenuItem);

        menuBar.add(fileMenu);

        viewMenu.setText("View");
        viewMenu.setFont(new java.awt.Font("Arial", 0, 12));
        clearEditPanelMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        clearEditPanelMenuItem.setText("Clear Edit Panel");
        clearEditPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEditPanelMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(clearEditPanelMenuItem);

        clearViewPanelMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        clearViewPanelMenuItem.setText("Clear View Panel");
        clearViewPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearViewPanelMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(clearViewPanelMenuItem);

        viewMenu.add(jSeparator4);

        clearMessagesMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        clearMessagesMenuItem.setText("Clear Messages");
        clearMessagesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMessagesMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(clearMessagesMenuItem);

        menuBar.add(viewMenu);

        selectedNodeMenu.setText("Selected_Node");
        selectedNodeMenu.setFont(new java.awt.Font("Arial", 0, 12));
        editNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        editNodeMenuItem.setText("Edit");
        editNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(editNodeMenuItem);

        deleteNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        deleteNodeMenuItem.setText("Delete");
        deleteNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(deleteNodeMenuItem);

        selectedNodeMenu.add(jSeparator13);

        addDataMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        addDataMenuItem.setText("Add data to node");
        addDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(addDataMenuItem);

        clearDataMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        clearDataMenuItem.setText("Clear data contained in node");
        clearDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearDataMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(clearDataMenuItem);

        printDataStrings.setFont(new java.awt.Font("Arial", 0, 12));
        printDataStrings.setText("Print node's data");
        printDataStrings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printDataStringsActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printDataStrings);

        selectedNodeMenu.add(jSeparator7);

        makeAttributeTableMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        makeAttributeTableMenuItem.setText("Show Attribute Data Table");
        makeAttributeTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeAttributeTableMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(makeAttributeTableMenuItem);

        printDataPairsMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printDataPairsMenuItem.setText("Print out data pairs (attribute nodes only)");
        printDataPairsMenuItem.setActionCommand("Print out data pairs (Attribute nodes only)");
        printDataPairsMenuItem.setEnabled(false);
        printDataPairsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printDataPairsMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printDataPairsMenuItem);

        selectedNodeMenu.add(jSeparator5);

        visualizeSortsGraphMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        visualizeSortsGraphMenuItem.setText("Visualize Sorts Graph");
        visualizeSortsGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualizeSortsGraphMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(visualizeSortsGraphMenuItem);

        selectedNodeMenu.add(jSeparator1);

        printSortDefinitionMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printSortDefinitionMenuItem.setText("Print Sort Definition of Selected Node");
        printSortDefinitionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSortDefinitionMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printSortDefinitionMenuItem);

        printSortDefStringMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printSortDefStringMenuItem.setText("Print Sort Def string");
        printSortDefStringMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSortDefStringMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printSortDefStringMenuItem);

        printFormDefStringMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printFormDefStringMenuItem.setText("Print Form Def string");
        printFormDefStringMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printFormDefStringMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printFormDefStringMenuItem);

        printSortTreeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printSortTreeMenuItem.setText("Print Definition of Sort Tree");
        printSortTreeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSortTreeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printSortTreeMenuItem);

        printChildrenOfSelectedMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        printChildrenOfSelectedMenuItem.setText("Print Children");
        printChildrenOfSelectedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printChildrenOfSelectedMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(printChildrenOfSelectedMenuItem);

        selectedNodeMenu.add(jSeparator11);

        setSelectedAsFirstSortMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        setSelectedAsFirstSortMenuItem.setLabel("Set Selected as First Sort for Comparison");
        setSelectedAsFirstSortMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSelectedAsFirstSortMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(setSelectedAsFirstSortMenuItem);

        setSelectedAsSecondSort.setFont(new java.awt.Font("Arial", 0, 12));
        setSelectedAsSecondSort.setLabel("Set Selected as Second Sort for Comparison");
        setSelectedAsSecondSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSelectedAsSecondSortActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(setSelectedAsSecondSort);

        compareNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        compareNodeMenuItem.setText("Compare Selected Node");
        compareNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compareNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(compareNodeMenuItem);

        selectedNodeMenu.add(jSeparator9);

        expandNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        expandNodeMenuItem.setText("Expand Node");
        expandNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(expandNodeMenuItem);

        collapseNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        collapseNodeMenuItem.setText("Collapse Node");
        collapseNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collapseNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(collapseNodeMenuItem);

        hideNodeMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        hideNodeMenuItem.setText("Hide Node");
        hideNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideNodeMenuItemActionPerformed(evt);
            }
        });

        selectedNodeMenu.add(hideNodeMenuItem);

        menuBar.add(selectedNodeMenu);

        sortsMenu.setText("Sorts");
        sortsMenu.setFont(new java.awt.Font("Arial", 0, 12));
        PrintAllSortsInProfileMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        PrintAllSortsInProfileMenuItem.setText("Print All Sorts in Profile");
        PrintAllSortsInProfileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintAllSortsInProfileMenuItemActionPerformed(evt);
            }
        });

        sortsMenu.add(PrintAllSortsInProfileMenuItem);

        resetSortsMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        resetSortsMenuItem.setText("Reset Sorts in Profile");
        resetSortsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSortsMenuItemActionPerformed(evt);
            }
        });

        sortsMenu.add(resetSortsMenuItem);

        sortsMenu.add(jSeparator12);

        CompareFirstAndSecondSortsMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        CompareFirstAndSecondSortsMenuItem.setText("Compare First and Second Sorts");
        CompareFirstAndSecondSortsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompareFirstAndSecondSortsMenuItemActionPerformed(evt);
            }
        });

        sortsMenu.add(CompareFirstAndSecondSortsMenuItem);

        menuBar.add(sortsMenu);

        demoGraphsMenu.setText("Demo_Graphs");
        demoGraphsMenu.setFont(new java.awt.Font("Arial", 0, 12));
        menuBar.add(demoGraphsMenu);

        demoDataMenu.setText("Demo_Data");
        demoDataMenu.setFont(new java.awt.Font("Arial", 0, 12));
        menuBar.add(demoDataMenu);

        retrieveFormMenu.setText("Retrieve_form");
        retrieveFormMenu.setFont(new java.awt.Font("Arial", 0, 12));
        retrieveFormMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
        retrieveFormMenuItem.setText("Retrieve form data");
        retrieveFormMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retrieveFormMenuItemActionPerformed(evt);
            }
        });

        retrieveFormMenu.add(retrieveFormMenuItem);

        menuBar.add(retrieveFormMenu);

        setJMenuBar(menuBar);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    private void makeAttributeTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeAttributeTableMenuItemActionPerformed
// TODO add your handling code here:
        /**@since 12.July 2005 */
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        User profile = tgpEdit.getProfile();
        sNode selNode = (sNode)tgpEdit.getSelect();
        //FIX this.
        Form form = null;
        selNode.makeAttDataTable(selNode, profile, form);
    }//GEN-LAST:event_makeAttributeTableMenuItemActionPerformed
    
    private void printFormDefStringMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printFormDefStringMenuItemActionPerformed
        // TODO add your handling code here:
        /**@since 24.May 2005 */
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selNode = (sNode)tgpEdit.getSelect();
        //System.out.println(selNode.getFormDefString());
        printMessage(selNode.getFormDefString());
        
    }//GEN-LAST:event_printFormDefStringMenuItemActionPerformed
    
    private void printSortDefStringMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSortDefStringMenuItemActionPerformed
        // TODO add your handling code here:
        /**@since 24.May 2005 */
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selNode = (sNode)tgpEdit.getSelect();
        //System.out.println(selNode.getSortDefString());
        printMessage(selNode.getSortDefString());
        
    }//GEN-LAST:event_printSortDefStringMenuItemActionPerformed
    
    private void retrieveFormMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retrieveFormMenuItemActionPerformed
        // TODO add your handling code here:
        /**TEST code 24.May 2005
         * Assumes that data has been added to node already...*/
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selNode = (sNode)tgpEdit.getSelect();
        String nodeLabel = selNode.getLabel();
        SortsMC allSorts = defineAndGetSorts(selNode);
        retreiveAndPrintExistingForm(tgpEdit, nodeLabel); //should formName be same as node label?
        
    }//GEN-LAST:event_retrieveFormMenuItemActionPerformed
    
    private void printDataPairsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printDataPairsMenuItemActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selectNode = (sNode)tgpEdit.getSelect();
        SORT_TYPE type = selectNode.getSortType();
        
        if(type.equals(SORT_TYPE.ATTRIBUTE) || type.equals(SORT_TYPE.ATTRIBUTE_ANON)) {
            selectNode.printDataPairs();
        } else {
            showErrorDialog("Data pairs only apply to attribute nodes", this);
            return;
        }
    }//GEN-LAST:event_printDataPairsMenuItemActionPerformed
    
    private void clearDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDataMenuItemActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selectNode = (sNode)tgpEdit.getSelect();
        selectNode.clearData();
    }//GEN-LAST:event_clearDataMenuItemActionPerformed
    
    private void printDataStringsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printDataStringsActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selectNode = (sNode)tgpEdit.getSelect();
        selectNode.printDataStrings();
    }//GEN-LAST:event_printDataStringsActionPerformed
    
    private void CompareFirstAndSecondSortsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompareFirstAndSecondSortsMenuItemActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Sort s1 = firstComparisonSort;
        Sort s2 = secondComparisonSort;
        
        if(s1 == null || s2 == null) {
            showErrorDialog("Please set both comparison sorts first. \nSee 'Sorts' Menu.", this);
            return;
        }
        printMessage("Comparison: '" + s1.toString() + "' compared to '" + s2.toString() + "' :");
        /**see Rudi's compareApplet */
        Match match = s1.match(s2);
        showMessageDialog("Comparison: '" + s1.toString() + "' compared to '" + s2.toString() + "' :\n" +
                match.toString(), this);
        
        
        //        tgpEdit.getSorts().cleanup();
        //        builder.defineSortTreePostOrder(tgpEdit, n1, 0, null); // root node last
        //        SortsMC sorts = tgpEdit.getSorts();
        //
        //        if (sorts != null) {
        //            Sort s1 = sorts.sortOf(n1.getLabel());
        //            Sort s2 = sorts.sortOf(n2.getLabel());
        //            if (s1 != null && s2 != null) {
        //                printMessage(s1.relate(s2).toString());
        //            }
        //        }
    }//GEN-LAST:event_CompareFirstAndSecondSortsMenuItemActionPerformed
    
    private void setSelectedAsFirstSortMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSelectedAsFirstSortMenuItemActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        TGPanel tgpView = getCurrentGraphLayoutPage().getViewPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        if(selectNode instanceof sNode) {
            sNode selectSortNode = (sNode)selectNode;
            setFirstComparisonSort(getSelectedTopSort());
            getCurrentGraphLayoutPage().getFirstSortTextField().setText(selectSortNode.getSortDef());
        }
    }//GEN-LAST:event_setSelectedAsFirstSortMenuItemActionPerformed
    
    private void setSelectedAsSecondSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSelectedAsSecondSortActionPerformed
        // TODO add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        TGPanel tgpView = getCurrentGraphLayoutPage().getViewPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        if(selectNode instanceof sNode) {
            sNode selectSortNode = (sNode)selectNode;
            setSecondComparisonSort(getSelectedTopSort());
            getCurrentGraphLayoutPage().getSecondSortTextField().setText(selectSortNode.getSortDef());
        }
    }//GEN-LAST:event_setSelectedAsSecondSortActionPerformed
    
    private void drawSdlFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawSdlFileMenuItemActionPerformed
        /**@since Feb 10, 2005 */
        //NOTE: same as below?
        Form data = getFormFromSdlFile("db");
        data.maximalize();
        SdlContext sdl = new SdlContext();
        /**Builds an SDL description of this element within the specified context */
        //data.print(sdl);
        System.out.println(sdl.toString());
        //LineDrawingFrame frame = new LineDrawingFrame(data);
    }//GEN-LAST:event_drawSdlFileMenuItemActionPerformed
    
    private void loadSdlFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSdlFileMenuItemActionPerformed
        /**@since Feb 10, 2005 */
        Form form = getFormFromSdlFile("db");
        form.maximalize();
        SdlContext sdl = new SdlContext();
        /**Builds an SDL description of this element within the specified context */
        //form.print(sdl);
        System.out.println(sdl.toString());
        
        //        if(form instanceof SinglyForm) {
        //            SinglyForm sf = (SinglyForm)form;
        //            System.out.println("Size: " + sf.size());
        //        }
        //        else
        //        if(form instanceof MultiplyForm) {
        //            MultiplyForm mf = (MultiplyForm)form;
        //
        //            System.out.println("MultiplyForm size: " + mf.size());
        //            //System.out.println("MultiplyForm associate: " + mf.associate().toString());
        //            //System.out.println("MultiplyForm individual: " + mf.individual());
        //        }
        
        //System.out.println("Size: " + form.size());
        //form.toString();
        //        SdlContext sdl = new SdlContext();
        //        data.print(sdl);
        //        System.out.println(sdl.toString());
    }//GEN-LAST:event_loadSdlFileMenuItemActionPerformed
    
    private void expandNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandNodeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        
        if(selectNode != null) {
            tgpEdit.expandNode(selectNode);
        }
    }//GEN-LAST:event_expandNodeMenuItemActionPerformed
    
    private void collapseNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collapseNodeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        
        if(selectNode != null) {
            tgpEdit.collapseNode(selectNode);
        }
    }//GEN-LAST:event_collapseNodeMenuItemActionPerformed
    
    private void hideNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideNodeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        
        if(selectNode != null) {
            tgpEdit.hideNode(selectNode);
        }
    }//GEN-LAST:event_hideNodeMenuItemActionPerformed
    
    private void resetSortsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSortsMenuItemActionPerformed
        // Add your handling code here:
        getCurrentGraphLayoutPage().getEditPanel().getTGPanel().resetUserAndSorts();
        
//        if (tgpEdit.getSorts() != null) {
//            tgpEdit.getSorts().cleanup();
//        }
    }//GEN-LAST:event_resetSortsMenuItemActionPerformed
    
    private void compareNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compareNodeMenuItemActionPerformed
        // Add your handling code here:
        /**NOTE! select node is selected node
         * popupNode is node the mouse is under: see GLEditUI, line 193
         * How to have mouse over node, and also select command from menu at same time??*/
        //NOTE this code does not work yet
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        
        Node n1 = getSelectedNodeFromEditPanel();
        Node n2 = tgpEdit.getMouseOverN();
        //
        if (n2 != null) {
            tgpEdit.setSelect2(n2);
            //printMessage("'" + n2.getLabel() + "'" + " set as select2 node");
            printMessage("Comparison: '" + n1.getLabel() + "' compared to '" + n2.getLabel() + "' :");
            
            tgpEdit.getSorts().cleanup();
            //builder.defineSortTreePostOrder(tgpEdit, n1, 0, null); // root node last
            SortsMC sorts = tgpEdit.getSorts();
            
            if (sorts != null) {
                Sort s1 = sorts.sortOf(n1.getLabel());
                Sort s2 = sorts.sortOf(n2.getLabel());
                if (s1 != null && s2 != null) {
                    printMessage(s1.match(s2).toString());
                }
            }
        }
    }//GEN-LAST:event_compareNodeMenuItemActionPerformed
    
    private void printSortTreeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSortTreeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selectNode = (sNode)tgpEdit.getSelect();
        
        if(selectNode != null) {
            //tgpEdit.unmarkAllEdges();
            //printMessage("Full sort definition [tree]:");
            printMessage("Full sort definition of tree. Selected root node: " + selectNode.getLabel());
            printMessage(selectNode.getSortDefTreePostOrder()); //this doesn't work with property edges...
        }
    }//GEN-LAST:event_printSortTreeMenuItemActionPerformed
    
    private void addDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selectNode = (sNode)tgpEdit.getSelect();
        
        if(selectNode instanceof sNode) {
            SORT_TYPE type = selectNode.getSortType();
            //String suf = selectSortNode.getSortType().getSortSuffix();
            //see NewSortChooserPanel for Label suffixes: [Label], [Date], [Numeric], +, ^
            if(selectNode.getNumInds() < type.getMaxInds()) {
                DataInputFrame inputFrame = new DataInputFrame(selectNode, tgpEdit);
                SortLabelInputPanel labelPanel = new SortLabelInputPanel(inputFrame);
                inputFrame.addComponents(labelPanel);
                inputFrame.show();
            }
        }
    }//GEN-LAST:event_addDataMenuItemActionPerformed
    
    private void editNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editNodeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        
        if(selectNode != null) {
            JFrame editFrame = new EditNodeFrame(tgpEdit, selectNode);
        }
    }//GEN-LAST:event_editNodeMenuItemActionPerformed
    
    private void deleteNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNodeMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node selectNode = tgpEdit.getSelect();
        
        if(selectNode != null) {
            tgpEdit.deleteNode(selectNode);
            tgpEdit.repaint();
        }
    }//GEN-LAST:event_deleteNodeMenuItemActionPerformed
    
    private void PrintAllSortsInProfileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintAllSortsInProfileMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        
        if (tgpEdit.getSorts() != null)
            tgpEdit.getSorts().printAllSorts();
        else
            printMessage("Profile is empty of sorts - Visualize sorts first from a selected node");
    }//GEN-LAST:event_PrintAllSortsInProfileMenuItemActionPerformed
    
    private void printChildrenOfSelectedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printChildrenOfSelectedMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node popupNodeSel = tgpEdit.getSelect();
        
        if (popupNodeSel != null) {
            Collection <Node> children = popupNodeSel.getChildrenCollection();
            printMessage("All children of selected node: " + popupNodeSel.getLabel());
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                Node current = (Node)i.next();
                printMessage(current.getLabel());
            }
        }
    }//GEN-LAST:event_printChildrenOfSelectedMenuItemActionPerformed
    
    private void printSortDefinitionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSortDefinitionMenuItemActionPerformed
        // Add your handling code here:
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        Node popupNodeSel = tgpEdit.getSelect();
        
        if(popupNodeSel != null && popupNodeSel instanceof sNode) {
            sNode selectSortNode = (sNode)popupNodeSel;
            printMessage("Sort definition of selected node: " + selectSortNode.getLabel());
            printMessage(selectSortNode.getSortDef());
        }
    }//GEN-LAST:event_printSortDefinitionMenuItemActionPerformed
    
    private void visualizeSortsGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualizeSortsGraphMenuItemActionPerformed
        /**Add your handling code here: */
        TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        sNode selNode = (sNode)tgpEdit.getSelect();
        tgpEdit.resetUserAndSorts();
        /**Defines the sorts */
        SortsMC allSorts = defineAndGetSorts(selNode);
        Sort topSort = tgpEdit.getSorts().topSort();       
        TGPanel tgpView = getCurrentGraphLayoutPage().getViewPanel().getTGPanel();
        
        if (topSort != null) {
            printMessage("Top sort: " + topSort.toString());
            /**Draws a graph using the sorts in the profile */
            builder.drawSortGraphPreOrder(topSort, tgpView, null, ""); //includes aspects sorts
            allSorts.printAllAspects();
            builder.drawAllAspects(allSorts, tgpView); //just draws edges, not new nodes
            tgpView.repaintAfterMove();
        }
        /**@since 10.June 2005 */
        //String topFormName = selNode.getFormName();
        //builder.printExistingForm(topFormName, tgpEdit.getProfile(), tgpEdit);     
    }//GEN-LAST:event_visualizeSortsGraphMenuItemActionPerformed
    
    private void clearMessagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMessagesMenuItemActionPerformed
        // Add your handling code here:
        getCurrentGraphLayoutPage().getMessageArea().setText("");
    }//GEN-LAST:event_clearMessagesMenuItemActionPerformed
    
    private void clearViewPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearViewPanelMenuItemActionPerformed
        // Add your handling code here:
        //TGPanel tgpView = getCurrentGraphLayoutPage().getViewPanel().getTGPanel();
        clearPanel(getCurrentGraphLayoutPage().getViewPanel().getTGPanel());
//        tgpView.clearAll();
//        tgpView.clearSelect(); //selected node got left behind...
//        tgpView.repaint();
    }//GEN-LAST:event_clearViewPanelMenuItemActionPerformed
    
    private void clearEditPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearEditPanelMenuItemActionPerformed
        // Add your handling code here:
        //TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
        clearPanel(getCurrentGraphLayoutPage().getEditPanel().getTGPanel());
//        tgpEdit.clearAll();
//        tgpEdit.clearSelect(); //selected node got left behind...
//        tgpEdit.repaint();
    }//GEN-LAST:event_clearEditPanelMenuItemActionPerformed
    
    private void closeCurrentGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeCurrentGraphMenuItemActionPerformed
        // Add your handling code here:
        int n = jTabbedPane.getSelectedIndex();
        jTabbedPane.remove(n);
    }//GEN-LAST:event_closeCurrentGraphMenuItemActionPerformed
    
    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        // Add your handling code here:
        //printMessage("tabbed pane changed");
        //TGPanel curTGP = getCurrentTGPanel();
        //tgUIManager.activate("Edit"); //deactivating all others is done automatically
        //tgUIManager.setAllTgPanels(getCurrentTGPanel());
        
    }//GEN-LAST:event_jTabbedPaneStateChanged
    
    private void saveGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGraphMenuItemActionPerformed
        // Add your handling code here:
        GraphLayoutPage curPage = (GraphLayoutPage)jTabbedPane.getSelectedComponent();
        saveGraph(curPage);
    }//GEN-LAST:event_saveGraphMenuItemActionPerformed
    
    private void exitAppMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitAppMenuItemActionPerformed
        // Add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitAppMenuItemActionPerformed
    
    private void addNewPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewPanelMenuItemActionPerformed
        // Add your handling code here:
        addPanel(true); //adds a panel with a single node to start
    }//GEN-LAST:event_addNewPanelMenuItemActionPerformed
    
    private void openExistingGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openExistingGraphMenuItemActionPerformed
        // Add your handling code here:
        loadGraph();
    }//GEN-LAST:event_openExistingGraphMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //System.out.println("GLTopFrame main(): line 349");
        new GraphLayoutTopFrame();
    }
    
    /**
     * Getter for property firstComparisonSort.
     * @return Value of property firstComparisonSort.
     */
    public cassis.sort.Sort getFirstComparisonSort() {
        return firstComparisonSort;
    }
    
    /**
     * Setter for property firstComparisonSort.
     * @param firstComparisonSort New value of property firstComparisonSort.
     */
    public void setFirstComparisonSort(cassis.sort.Sort firstComparisonSort) {
        this.firstComparisonSort = firstComparisonSort;
    }
    
    /**
     * Getter for property secondComparisonSort.
     * @return Value of property secondComparisonSort.
     */
    public cassis.sort.Sort getSecondComparisonSort() {
        return secondComparisonSort;
    }
    
    /**
     * Setter for property secondComparisonSort.
     * @param secondComparisonSort New value of property secondComparisonSort.
     */
    public void setSecondComparisonSort(cassis.sort.Sort secondComparisonSort) {
        this.secondComparisonSort = secondComparisonSort;
    }
    
    /** Getter for property tgUIManager.
     * @return Value of property tgUIManager.
     *
     */
    //    public TGUIManager getTgUIManager() {
    //        return tgUIManager;
    //    }
    
    /** Setter for property tgUIManager.
     * @param tgUIManager New value of property tgUIManager.
     *
     */
    //    public void setTgUIManager(TGUIManager tgUIManager) {
    //        this.tgUIManager = tgUIManager;
    //    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem CompareFirstAndSecondSortsMenuItem;
    private javax.swing.JMenuItem PrintAllSortsInProfileMenuItem;
    private javax.swing.JMenuItem addDataMenuItem;
    private javax.swing.JMenuItem addNewPanelMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem clearDataMenuItem;
    private javax.swing.JMenuItem clearEditPanelMenuItem;
    private javax.swing.JMenuItem clearMessagesMenuItem;
    private javax.swing.JMenuItem clearViewPanelMenuItem;
    private javax.swing.JMenuItem closeCurrentGraphMenuItem;
    private javax.swing.JMenuItem collapseNodeMenuItem;
    private javax.swing.JMenuItem compareNodeMenuItem;
    private javax.swing.JMenuItem deleteNodeMenuItem;
    private javax.swing.JMenu demoDataMenu;
    private javax.swing.JMenu demoGraphsMenu;
    private javax.swing.JMenuItem drawSdlFileMenuItem;
    private javax.swing.JMenuItem editNodeMenuItem;
    private javax.swing.JMenuItem exitAppMenuItem;
    private javax.swing.JMenuItem expandNodeMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem hideNodeMenuItem;
    private javax.swing.JSeparator jSeparator;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JMenuItem loadSdlFileMenuItem;
    private javax.swing.JMenuItem makeAttributeTableMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openExistingGraphMenuItem;
    private javax.swing.JMenuItem printChildrenOfSelectedMenuItem;
    private javax.swing.JMenuItem printDataPairsMenuItem;
    private javax.swing.JMenuItem printDataStrings;
    private javax.swing.JMenuItem printFormDefStringMenuItem;
    private javax.swing.JMenuItem printSortDefStringMenuItem;
    private javax.swing.JMenuItem printSortDefinitionMenuItem;
    private javax.swing.JMenuItem printSortTreeMenuItem;
    private javax.swing.JMenuItem resetSortsMenuItem;
    private javax.swing.JMenu retrieveFormMenu;
    private javax.swing.JMenuItem retrieveFormMenuItem;
    private javax.swing.JMenuItem saveGraphMenuItem;
    private javax.swing.JMenu selectedNodeMenu;
    private javax.swing.JMenuItem setSelectedAsFirstSortMenuItem;
    private javax.swing.JMenuItem setSelectedAsSecondSort;
    private javax.swing.JMenu sortsMenu;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenuItem visualizeSortsGraphMenuItem;
    // End of variables declaration//GEN-END:variables
    
}
