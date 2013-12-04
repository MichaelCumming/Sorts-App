package applets;

import java.io.*;
import blue.sort.Sort;
import blue.sort.User;
import blue.form.Form;
import blue.ind.*;
import blue.io.SdlContext;
import blue.io.vrml.IccWorld;
import blue.io.ParseException;

public final class TypesApplet extends UserApplet {
    private Form data;

    // initializer
    public void init() {
        super.init();
        data = data();
        viewSdl();
    }

    public void viewSdl() {
        SdlContext sdl = new SdlContext();
        data.print(sdl);
        sendSdl(sdl.toString());
    }

    public void viewVrml() {
        String base = this.getDocumentBase().toString();
        base = base.substring(0, base.lastIndexOf('/')) + "/../";
        IccWorld world = new IccWorld(base);
        data.first().visualize(world);
        data.visualize(world);
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
        User profile = new User("bige");
        // String name = "d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\types.sdl";
        String name = "g:\\cumming\\together5.5\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\types.sdl";
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
        Form data = (Form)profile.retrieve("types");
        data.maximalize();
        SdlContext sdl = new SdlContext();
        data.print(sdl);
        System.out.println(sdl.toString());
    }
}
