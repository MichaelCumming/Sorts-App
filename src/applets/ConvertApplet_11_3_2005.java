package applets;


import applets.*;
import cassis.convert.*;
import cassis.io.*;
import cassis.parse.*;
import java.io.*;
import cassis.sort.*;
//import cassis.match.Match;
import cassis.form.Form;
import cassis.io.SdlContext;
//import cassis.io.ParseException;


public final class ConvertApplet_11_3_2005 extends UserApplet {

    private Form data;

    // initializer

    public void init() {
	super.init();
    }
    
    public static void main(String argv[]) {
        User profile = new User("me");
        String userName;
        //String name = "D:\\Documenten\\stouffs\\projects\\NWO D3r\\sorts\\classes\\convert.sdl";
        //String name = "C:\\Documents and Settings\\stouffs\\My Documents\\work\\sorts\\classes\\convert.sdl";
        String fileName = "P:\\cumming\\research\\java\\sorts.latest\\src\\applets\\sdl\\convert_11_3_2005.sdl";
        
	try {
	    File file = new File(fileName);
	    BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String input = dis.readLine();
	    if (!input.startsWith("#SDL V1.0a")) {
		System.out.println("File not recognized as valid 'SDL V1.0a' file");
		return;
	    }
	    int n = input.indexOf('[');
	    if (n > 0) {
		userName = input.substring(n + 1, input.indexOf(']'));
		profile = User.find(userName);
		if (profile == null) profile = new User(userName);
	    }
	    profile.parse(dis);
	} catch (IOException e) {
	    System.out.println(e);
	} catch (ParseException e) {
	    System.out.println(e.getMessage());
            return;
	}
        
        Sorts sorts = profile.sorts();
        //top level form:
        Sort one = sorts.sortOf("all");
        //target is identical to 'all' above
        Sort two = sorts.sortOf("target");
        Match match = one.match(two);
        System.out.println("MC comment: Match: 'all' vs. 'target'----");
        System.out.println(match.toString());
	Form data = (Form) profile.retrieve("db");
	data.maximalize();
	SdlContext sdl = new SdlContext();
	//data.print(sdl);
	System.out.println(sdl.toString());
        
        //get the 'target' form that calculates the total cost for all elements squashed together
	data = (Form) profile.retrieve("db2");
	data.maximalize();
	sdl = new SdlContext();
	//data.print(sdl);
        System.out.println("MC comment: new target print out----");
	System.out.println(sdl.toString());
        /**What this applet does: 
         defines 'reads in 'all' and 'target' forms, where 'all' calculates total cost for each type, 
         while 'target' calculates total costs for all types all at once. 
         That is: 'all': types ^ totalcosts... / 'target': totalcosts ^ types... */
    }
    
    
}
