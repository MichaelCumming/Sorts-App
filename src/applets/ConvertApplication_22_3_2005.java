package applets;

import java.io.*;
import cassis.sort.*;
import cassis.convert.Match;
import cassis.form.Form;
import cassis.visit.SdlVisitor;
import cassis.parse.ParseException;

public final class ConvertApplication_22_3_2005 {

    public static void main(String argv[]) {
        User profile = new User("me");
        //String name = "D:\\Documenten\\stouffs\\projects\\NWO D3r\\sorts\\classes\\convert.sdl";
        String name = "C:\\Documents and Settings\\stouffs\\My Documents\\work\\sorts\\classes\\convert.sdl";
        
	try {
	    File file = new File(name);
	    BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String input = dis.readLine();
	    if (!input.startsWith(SdlVisitor.HEADER)) {
		System.out.println("File not recognized as valid '" + SdlVisitor.HEADER.substring(1) + "' file");
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
        Sorts sorts = profile.sorts();
        Sort one = sorts.sortOf("all");
        Sort two = sorts.sortOf("target");
        Match match = one.match(two);
        System.out.println(match.toString());
	SdlVisitor sdl = new SdlVisitor();
	Form data = (Form) profile.retrieve("db");
        sdl.defineVariable("db", data);
	data = (Form) profile.retrieve("db2");
        sdl.defineVariable("db2", data);
	System.out.println(sdl.toString());
    }
}
