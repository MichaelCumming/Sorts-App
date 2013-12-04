/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Visualize.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package applets;

import cassis.Element;
import cassis.sort.Sort;
import cassis.io.vrml.IccWorld;

public class Visualize extends Thread {
    // constants
    final static String Vrml = "VRML";
    final static String Html = "HTML";
    // final static String Java = "Java";
    // representation
    private String language;
    private Element data;
    private Sort sort;
    private UserApplet actor;

    Visualize(UserApplet actor, String language, Element data) {
        super(language);
        this.language = language;
        this.data = data;
        this.sort = null;
        this.actor = actor;
    }

    Visualize(UserApplet actor, String language, Sort sort) {
        super(language);
        this.language = language;
        this.sort = sort;
        this.data = null;
        this.actor = actor;
    }

    public void run() {
        if (this.language.equals(Vrml)) {
            this.actor.sendStatus("Please wait, VRML data is being generated");
            String base = this.actor.getDocumentBase().toString();
            int dir = base.lastIndexOf("/");
            base = base.substring(0, dir + 1);
            IccWorld world = new IccWorld(base);
            if (this.data != null)
                ;
                //this.data.visualize(world);
            else
                ;
                //this.sort.visualize(world);
            this.actor.sendVrml(world.toString());
        } else {
            this.actor.sendStatus("Please wait, VRML data is being generated");
            String base = this.actor.getDocumentBase().toString();
            int dir = base.lastIndexOf("/");
            base = base.substring(0, dir + 1);
            IccWorld world = new IccWorld(base);
            if (this.data != null)
                ;
                //this.data.visualize(world);
            else
                ;
                //this.sort.visualize(world);
            this.actor.sendHtml("<pre>\n" + world.toString() + "\n</pre>");
            // this.actor.sendHtml("<br><p><strong>Sorry,<br>HTML visualization is not yet supported</strong>");
        }
    }
}
