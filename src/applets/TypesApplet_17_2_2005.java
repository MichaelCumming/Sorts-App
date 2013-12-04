package applets;

import cassis.parse.*;
import java.io.*;
import cassis.sort.Sort;
import cassis.sort.User;
import cassis.form.Form;
import cassis.ind.*;
import cassis.io.SdlContext;
import cassis.io.vrml.IccWorld;
//import cassis.io.ParseException;

public final class TypesApplet_17_2_2005 extends UserApplet {
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

    public void viewVrml() {
        String base = this.getDocumentBase().toString();
        base = base.substring(0, base.lastIndexOf('/')) + "/../";
        IccWorld world = new IccWorld(base);
        //data.first().visualize(world);
        //data.visualize(world);
        sendVrml(world.toStringArray());
    }

    public Form data() {
        // this.parseUrl("../../classes/types.sdl");
        this.parseUrl("types.sdl");
        User profile = User.find("bige");
        Form data = (Form)profile.retrieve("types");
        data.maximalize();
        return data;
    }

    public static void main(String[] argv) {
        User profile = new User("michael");
        String name = "P:\\cumming\\research\\java\\sorts.latest\\src\\applets\\sdl\\sortsDemo21_Feb_2005.sdl";
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
        Form data = (Form)profile.retrieve("db");
        data.maximalize();
        SdlContext sdl = new SdlContext();
        //data.print(sdl);
        System.out.println(sdl.toString());
    }
}
