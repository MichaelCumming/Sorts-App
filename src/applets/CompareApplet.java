package applets;

import applets.*;
import cassis.convert.*;
import cassis.parse.*;
import java.io.*;

import cassis.sort.Sort;
import cassis.sort.User;
import cassis.form.Form;
//import cassis.match.*;
import cassis.ind.*;
import cassis.io.SdlContext;
//import cassis.io.ParseException;

public final class CompareApplet extends UserApplet {

    // initializer

    public void init() {
	super.init();

    }

    public static void main(String argv[]) {
        User profile = new User("me");
        String name = "D:\\Documenten\\stouffs\\projects\\NWO D3r\\sorts\\classes\\compare.sdl";
	try {
	    File file = new File(name);
	    BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String input = dis.readLine();
	    if (!input.startsWith("#SDL V1.0a")) {
		System.out.println("File not recognized as valid 'SDL V1.0a' file");
		return;
	    }
	    int n = input.indexOf('[');
	    if (n > 0) {
		name = input.substring(n + 1, input.indexOf(']'));
		profile = User.find(name);
		if (profile == null) profile = new User(name);
	    }
	    profile.parse(dis);
	} catch (IOException e) {
	    System.out.println(e);
	} catch (ParseException e) {
	    System.out.println(e.getMessage());
            return;
	}
	//Form data = (Form) profile.retrieve("types");
	//Form data = (Form) profile.retrieve("lights");
	//data.maximalize();
	//SdlContext sdl = new SdlContext();
	//data.print(sdl);
	//System.out.println(sdl.toString());
        Sort lights = profile.sorts().sortOf("lights");
        Sort lights2 = profile.sorts().sortOf("lights2");
        Match match = lights.match(lights2);
        System.out.println(match.toString());
    }
}
