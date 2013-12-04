/*
 * SortsMC.java
 *
 * Created on 30 maart 2005, 11:20
 */

package cassis.sort;

import com.touchgraph.graphlayout.*;
import java.util.*;
import sortsApp.*;


/**
 *
 * @author cumming
 */
public class SortsMC extends Sorts {
      /**added by MC for demo graphlayout */
    private TGPanel tgPanel;
    private GraphLayoutPage glPage;
    protected GraphLayoutTopFrame topFrame;
    
    /** Creates a new instance of SortsMC */
    public SortsMC(User profile) {
        super(profile);
    }
    
     /** added 26.09.2003 MC */
    public SortsMC(User profile, TGPanel tgPanel) {
        super(profile);
        //this.matches = new Hashtable();
        this.attributesorts = new Hashtable();
        this.aspects = new Hashtable();
        this.newbies = new Stack();
        this.hanging = new Vector();
        this.timed = new Vector();
        this.timestamp = 1;
        //this.setLanguage(ENGLISH);
        this.tgPanel = tgPanel;
        //this.glPage = tgPanel.getGlPage();
        
    }
    
     //since HashSet, eliminates duplicates
    public HashSet getAllAspects() {
        HashSet allA = new HashSet();
        for (int n = 0; n < this.timed.size(); n++) {
            Sort s = (Sort)this.timed.elementAt(n);
            if (s instanceof Aspect) {
                System.out.println("Aspect found: toS: " + s.toString() + "; def: " + s.definition());
                allA.add(s);
            }
        }
        return allA;
    }
    
    /**
     * Returns the last sort defined - assuming bottom-up definition.
     * @since 20.06.2003
     * @author Michael Cumming
     */
    public Sort topSort() { //get first non-aspect sort from end of vector
        Vector t = this.timed;
        if (t != null && t.size() > 0) {
            for (int i = t.size()-1; i >= 0; i--) {
                Sort curS = (Sort)t.elementAt(i);
                if (!(curS instanceof AspectsSort) && !(curS instanceof Aspect))
                    return curS;
            }
        }
        return null;
    }
    
    /**
     * @since 19.06.2003
     * @author Michael Cumming
     */
    public void printAllSorts() {
        if (this != null) {
            String[] sortArray = this.allSortsS();
            //printMessage("Contents of current profile:");
            printMessage("Contents of current profile:");
            for (int n = 0; n < sortArray.length; n++) {
                //System.out.println(sortArray[n]);
                printMessage(sortArray[n]);
            }
        }
        else
            System.out.println("Sorts are null in profile");
    }
    
    /**
     * returns an array of names + types of all sorts defined within this context.
     * The array is ordered according to the time of definition.
     * @since 05.11.2002
     * @author Michael Cumming
     */
    public String[] allSortsS() {
        String[] array = new String[this.timed.size()];
        for (int n = 0; n < this.timed.size(); n++) {
            Sort temp = (Sort)this.timed.elementAt(n);
            array[n] = temp.toString() + " : " + temp.definition();
        }
        return array;
    }
    
    public void printMessage(String message) {
        if (topFrame == null) {
            //this.topFrame = tgPanel.getTopFrame();
        }
        topFrame.printMessage(message);
    }
    
    //modified 26.09.2003 MC
    //moved from 'Node'
    public void printAllAspects()  { //Sorts allS) {
        HashSet allA = this.getAllAspects(); //this method eliminates duplicate aspects
        //System.out.println("Print all aspects---------");
        for (Iterator i = allA.iterator(); i.hasNext(); ) {
            Aspect curA = (Aspect)i.next();
            System.out.println("curA: " + curA.toString());
        }
    }
    
}
