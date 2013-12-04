package applets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import cassis.sort.Sort;
//import cassis.io.ParseReader;
//import cassis.io.ParseException;

public final class SortsApplet extends UserApplet {
    public void init() {
        super.init();
    }

    public String[] define(String definition) {
        Sort def = this.sort(definition);
        if ((def != null) && def.isNamed())
            return def.context().newSorts();
        return null;
    }

    public String[] load() {
        this.destroy();
        this.parseUrl("definition.sdl");
        // this.parseUrl("../../classes/definition.sdl");
        return this.allSorts();
    }

//    public Match match(Sort left, Sort right) {
//        return left.relate(right);
//    }

/*
    public boolean action(Event evt, Object arg) {
	String sort;
	if (evt.target == this.viewOne) {
	    writeSort("icons/one_yellow.gif", this.fieldOne.getText());
	    try {
		sort = this.fieldOne.getText();
		sort = sort.substring(0, sort.indexOf(' '));
		Sort s;
		if (sort.charAt(0) == '(')
		    s = this.self.sorts().retrieve(new ParseReader(new StringReader(sort)));
		else s = this.self.sorts().sortOf(sort);
		new Visualize(this, Visualize.Vrml, s).start();
	    } catch (ParseException e) {
		System.out.println(e.getMessage());
	    }
	    return true;
	}
	return false;
    }
*/

    public static void main(String argv[]) {
        cassis.sort.User self = new cassis.sort.User("my");
        // System.out.println(self.sorts().define("types : [Label]"));
        try {
            // File file = new File("./definition.sdl");
            //File file = new File("D:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\definition.sdl");
            File file = new File("g:\\cumming\\together5.5\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\definition.sdl");
            FileInputStream fis = new FileInputStream(file);
            cassis.sort.User profile = parseInputStream(self, fis);
            cassis.sort.Sorts context = profile.sorts();
            String[] sorts = context.allSorts();
            for (int n = 0; n < sorts.length; n++) {
                Sort current = context.sortOf(sorts[n]);
                if (current != null)
                    System.out.println(sorts[n] + " : " + current.definition());
                else
                    System.out.println(sorts[n]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(".");
    }
}
