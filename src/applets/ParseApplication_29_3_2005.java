package applets;

import java.io.*;
import cassis.sort.*;
import cassis.convert.Match;
import cassis.form.Form;
import cassis.visit.SdlVisitor;
import cassis.parse.ParseException;

public final class ParseApplication_29_3_2005 {
    
    public static void main(String argv[]) {
        User profile = new User("me");
        //run this application by running this file only: Shift + F6
        String name = "P:\\cumming\\research\\java\\sorts.latest\\src\\applets\\sdl\\analysis_29_3_2005.sdl";
        //this one >> String name = "D:\\rudi\\sorts\\classes\\analysis.sdl";
        
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
        SdlVisitor sdl = new SdlVisitor();
        Form data = (Form) profile.retrieve("types");
        sdl.defineVariable("types", data);
        System.out.println(sdl.toString());
    }
    
}
