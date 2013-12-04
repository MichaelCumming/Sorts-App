package applets;

import cassis.parse.*;
import java.io.*;
import cassis.sort.Sort;
import cassis.sort.User;
import cassis.form.Form;
import cassis.ind.*;
import cassis.io.SdlContext;
//import cassis.io.ParseException;

public final class AbstractionsApplet extends UserApplet {
    private Form data;

    // initializer
    public void init() {
        super.init();
        data = data();
        viewSdl();
    }

    public void viewSdl() {
        SdlContext sdl = new SdlContext();
        //data.print(sdl);
        sendSdl(sdl.toString());
    }

    public Form data() {
        // this.parseUrl("../../classes/analysis.sdl");
        this.parseUrl("analysis.sdl");
        User profile = User.find("bige");
        Form data = (Form)profile.retrieve("documents");
        data.maximalize();
        return data;
    }

    public static void main(String[] argv) {
        User profile = new User("bige");
        // String name = "d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\analysis.sdl";
        String name = "p:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\inputFiles\\analysisMC.sdl";
        try {
            File file = new File(name);
            BufferedReader dis = new BufferedReader(new FileReader(file));
            String input = dis.readLine();
            if (!input.startsWith("#SDL V1.0a")) {
                System.out.println("File not recognized as valid 'SDL V1.0a' file");
                return;
            }
            int n = input.indexOf('[');
            if (n > 0) {
                name = input.substring(n + 1, input.indexOf(']')); // user name is found between '[' and ']' on first line
                profile = User.find(name);
                if (profile == null) profile = new User(name);
            }
            profile.parse(dis); // parse the rest and add it to Bige's profile (profile:User)
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }
        Form data = (Form)profile.retrieve("documents");
        data.maximalize();
        SdlContext sdl = new SdlContext();
        //data.print(sdl);
        System.out.println(sdl.toString());

        /*
	try {
	    File file = new File("d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\out.txt");
	    BufferedWriter dos = new BufferedWriter(new FileWriter(file));
            dos.write(sdl.toString());
            dos.flush();
            dos.close();
	} catch (IOException e) {
	    System.out.println(e);
        }
         */

        cassis.io.vrml.IccWorld gc = new cassis.io.vrml.IccWorld("");
        //data.visualize(gc);
        String[] vis = gc.toStringArray();
        try {
            // File file = new File("d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\out.wrl");
            File file = new File("p:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\00_outMC.wrl");
            BufferedWriter dos = new BufferedWriter(new FileWriter(file));
            for (int n = 0; n < vis.length; n++)
                dos.write(vis[n]);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
