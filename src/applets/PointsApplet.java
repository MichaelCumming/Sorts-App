package applets;

import cassis.ind.function.Function;
import java.awt.Color;
import cassis.sort.Sort;
import cassis.form.Form;
import cassis.ind.*;
import cassis.io.SdlContext;
import cassis.io.vrml.IccWorld;
import cassis.proc.*;

public final class PointsApplet extends UserApplet {
    private static final int GRID_MAX = 10;
    private static final int LABL_MAX = 5;
    private static final double WGHT_MAX = 40.0;
    private static final int IND_MAX = 24;
    private Form data;
    private Traversal total;
    private Traversal gravity;
    private Function swap;

    // initializer
    public void init() {
        super.init();
        total = new TotalWeight("Total weight");
        gravity = new GravityCenter("Center of gravity");
        //swap = new SwapTop2("Swap two");
        data = data();
        viewSdl();
    }

    public void viewSdl() {
        SdlContext sdl = new SdlContext();
        //data.print(sdl);
        sendSdl(sdl.toString());
    }

    public void viewVrml() {
        IccWorld world = new IccWorld(this.getDocumentBase().toString());
        //data.first().visualize(world);
        sendVrml(world.toString());
    }

    public void totalWeight() {
        System.out.println("> total weight");
        total.initialize();
        //data.traverse(total);
        System.out.println("< total weight");
        javaScript("viewInfo", total.getTitle() + ": " + total.toString());
    }

    public void gravityCenter() {
        gravity.initialize();
        //data.traverse(gravity);
        javaScript("viewInfo", gravity.getTitle() + ": " + gravity.toString());
    }

    public Form data() {
        Sort labeledpoints = this.sort("labeledpoints : (points : [Point]) ^ (labels : [Label]) ^ (weights : [Weight](40.0))");
        Sort positionlabels = this.sort("positionlabels : labels ^ points ^ weights");
        Sort all = this.sort("all : labeledpoints + positionlabels + labels ^ weights + points ^ weights");
        Form data = this.form(all);
        // labels
        String label[] = {"Square", "Triangle", "Circle", "Diamond", "Star"};
        // labeled points
        Sort points = this.sortOf("points");
        Sort labels = this.sortOf("labels");
        Sort weights = this.sortOf("weights");
        Individual weight, position, object, el;
        double x, y, z;
        for (int n = 0; n < IND_MAX; n++) {
            weight = new Weight(weights, (double)((int)(Math.random() * WGHT_MAX) + 1));
            object = new Label(labels, label[(int)(Math.random() * LABL_MAX)]);
            object.addAttribute(weight);
            x = (double)((int)(Math.random() * GRID_MAX));
            y = (double)((int)(Math.random() * GRID_MAX));
            z = (double)((int)(Math.random() * GRID_MAX));
            position = new Point(points, x, y, z);
            position.addAttribute(object);
            data.add(position);
        }
        data.maximalize();
        return data;
    }
}
