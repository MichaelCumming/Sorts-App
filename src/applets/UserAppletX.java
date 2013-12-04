/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `UserApplet.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package sortsApp;

import cassis.parse.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.awt.Font;
import java.awt.Color;
import java.net.*;

//import netscape.javascript.JSObject;

import cassis.Element;
import cassis.sort.Sort;
import cassis.sort.User;
//import cassis.io.ParseException;
import cassis.form.Form;

public class UserAppletX extends Applet {

    // constants
    private final static Font font = new Font("Helvetica", Font.BOLD, 14);

    // representation

    User self;
    AppletContext context;
    String key;
    //JSObject win;
    String arg[], args[];
    URL base;

    // initializer

    public void init() {
	String name = getParameter("user");
	if (name == null) name = "my";
	init(name);

	this.setBackground(Color.black);
	this.setForeground(Color.white);
	this.setFont(font);
	this.setVisible(true);

	name = getParameter("url");
	if (name != null) parseUrl(name);

	//win = JSObject.getWindow(this);
	//win.eval("idleStatus()");
    }

    public void init(String name) {
	self = new User(name);
	context = getAppletContext();
	base = getDocumentBase();
	arg = new String[1];
	args = new String[2];
    }

    public void destroy() {
	self.cleanup();
    }

    // methods

    public String[] allSorts() {
	return self.sorts().allSorts();
    }

    public Sort sort(String definition) {
	return self.sorts().define(definition);
    }

    public Sort sortOf(String name) {
	return self.sorts().sortOf(name);
    }

    public Form form(Sort sort) {
	return sort.newForm();
    }
/*
    void javaScript(String function, String argument) {
	arg[0] = argument;
	win.call(function, arg);
    }

    public void sendVrml(String vrml) {
	javaScript("openDisplayVrml", null);
	javaScript("writeDisplayVrml", vrml);
	javaScript("closeDisplayVrml", null);
	win.eval("idleStatus()");
    }

    public void sendVrml(String[] vrml) {
	javaScript("openDisplayVrml", null);
        for (int n = 0; n < vrml.length; n++)
	    javaScript("writeDisplayVrml", vrml[n]);
	javaScript("closeDisplayVrml", null);
	win.eval("idleStatus()");
    }

    public void toVrml(String var) {
	try {
	    Element data = self.retrieve(var);
	    new Visualize(this, Visualize.Vrml, data).start();
	} catch (IllegalArgumentException e) {
	    System.out.println(e.getMessage());
	}
    }

    public void sendHtml(String html) {
	javaScript("displayHtml", html);
    }

    public void openHtml(String url) {
	javaScript("openHtml", url);
    }

    public void sendSdl(String sdl) {
	javaScript("displaySdl", sdl);
	win.eval("idleStatus()");
    }

    public void openSdl(String url) {
	javaScript("openSdl", url);
    }
*/
    public void sendStatus(String status) {
	context.showStatus(status);
    }
    
    static User parseInputStream(User profile, InputStream is) {
	try {
	    BufferedReader dis = new BufferedReader(new InputStreamReader(is));
	    String input = dis.readLine();
	    if (!input.startsWith("#SDL V1.0a")) {
		System.out.println("File not recognized as valid 'SDL V1.0a' file");
		return null;
	    }
	    int n = input.indexOf('[');
	    if (n > 0) {
		String name = input.substring(n + 1, input.indexOf(']'));
		profile = User.find(name);
		if (profile == null) profile = new User(name);
	    }
	    profile.parse(dis);
	} catch (IOException e) {
	    System.out.println(e);
	} catch (ParseException e) {
	    System.out.println(e.getMessage());
	    return null;
	}
	return profile;
    }

    public boolean parseUrl(String name) {
        boolean result = true;
        try {
	    URL url = new URL(base, name);
	    URLConnection con = url.openConnection();
            result = (parseInputStream(self, con.getInputStream()) != null);
	} catch (IOException e) {
	    System.out.println(e);
	}
	return result;
    }
}
