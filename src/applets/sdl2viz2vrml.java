package applets;

//reads in an .sdl file and outputs a .wrl (VRML) file
import cassis.parse.*;
import java.io.*;
import cassis.sort.*;
//import cassis.sort.Sort;
//import cassis.sort.User;
import cassis.form.Form;
import cassis.ind.*;
import cassis.io.SdlContext;
//import cassis.io.ParseException;
import applets.UserApplet;
import cassis.io.LogFile;

public final class sdl2viz2vrml {
    private Form data;
    private static String exampleName = "latest"; // name of input and output files

    public static void main(String[] argv) {
        User profile = new User("michael");
        String name = "g:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\inputFiles\\"
            + exampleName + ".sdl";
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
                // user name is found between '[' and ']' on first line
                name = input.substring(n + 1, input.indexOf(']'));
                profile = User.find(name);
                if (profile == null) profile = new User(name);
            }
            // where rest of the .sdl file is parsed
            profile.parse(dis); // parse the rest and add it to the profile (profile:User)
        } catch (IOException e) {
            System.out.println(e);
            LogFile.logErr(e.toString(), e.getMessage());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            LogFile.logErr(e.toString(), e.getMessage());
            return;
        }
        // profile.retrieve returns an Element; Form = collection of elements
        Form data = (Form)profile.retrieve("top");
        // .sorts() = definitions, just like they're written in the .sdl file
        // string array of all sorts from the Users profile
        // profile:User. sorts():Sorts. allSorts():String[]
        // all.SortsS() prints out the sort name and also the definition e.g. label:[Label]
        SortsMC sortsMC = (SortsMC)profile.sorts();
        String[] sortArray = sortsMC.allSortsS();
        //Sorts newSorts = profile.sorts();
        //SdlContext sdlx = new SdlContext();
        //newSorts.print(sdlx);
        SdlContext sdl = new SdlContext();
        //data.print(sdl);
        System.out.println(sdl.toString()); // prints to screen the same thing that is saved to file below...
        //  @author Michael Cumming : file writer
        try {
            //File file = new File("d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\out.txt");
            File file = new File("g:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\outputFiles\\" +
                exampleName + "AllSorts.txt");
            BufferedWriter aOut = new BufferedWriter(new FileWriter(file));
            for (int n = 0; n < sortArray.length; n++) {
                aOut.write(sortArray[n]);
                aOut.write("\n");
            }
            //aOut.write(sdl.toString());
            aOut.flush();
            aOut.close();
        } catch (IOException e) {
            System.out.println(e);
            LogFile.logErr(e.toString(), e.getMessage());
        }

       /* // all 'content' established by this point
        cassis.io.vrml.IccWorld gc = new cassis.io.vrml.IccWorld("");
        // data:Form comes from above
        //Sort vix = new Sort(data_array[0]);
        data.visualize(gc); // visualize 'data' within the new gc world
        //vix.visualize(gc);
        //contents.visualize(gc);
        String[] vis = gc.toStringArray(); // chops up the gc world into an array of strings
        try {
            // File file = new File("d:\\MSNT4W\\Profiles\\stouffs\\projects\\sorts\\classes\\out.wrl");
            File file = new File("g:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\outputFiles\\" + exampleName + ".wrl");
            BufferedWriter dos = new BufferedWriter(new FileWriter(file));
            for (int n = 0; n < vis.length; n++)
               dos.write(vis[n]);
            dos.flush();
            dos.close();
            //
        } catch (IOException e) {
            System.out.println(e);
            LogFile.logErr(e.toString(),e.getMessage());
        }*/
    }
}
