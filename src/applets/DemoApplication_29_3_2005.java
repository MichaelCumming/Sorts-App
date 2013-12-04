package applets;

import java.io.*;
import cassis.sort.*;
import cassis.convert.Match;
import cassis.Element;
import cassis.visit.SdlVisitor;
import cassis.parse.ParseException;


public final class DemoApplication_29_3_2005 {
    
    public static void main(String argv[]) {
        User profile = new User("me");
        //run this application by running this file only: Shift + F6
        String fileName = "P:\\cumming\\research\\java\\sorts.latest\\src\\applets\\sdl\\demo_29_3_2005.sdl";
        //this one >> String name = "D:\\rudi\\sorts\\classes\\demo.sdl";
        
        try {
            File file = new File(fileName);
            BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String input = dis.readLine();
            if (!input.startsWith(SdlVisitor.HEADER)) {
                System.out.println("File not recognized as valid '" + SdlVisitor.HEADER.substring(1) + "' file");
                return;
            }
            int n = input.indexOf('[');
            if (n > 0) {
                String userName = input.substring(n + 1, input.indexOf(']'));
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
//        Sort one = sorts.sortOf("all");
//        Sort two = sorts.sortOf("target");
//        Match match = one.match(two);
//        System.out.println(match.toString())
        
        SdlVisitor sdlVisitor = new SdlVisitor();
        
        Element data = profile.retrieve("step1");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step1", data);
        
        data = profile.retrieve("step2");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step2", data);
        
        data = profile.retrieve("step3");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step3", data);
        
        data = profile.retrieve("step4");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step4", data);
        
        data = profile.retrieve("step5");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step5", data);
        
        data = profile.retrieve("step6");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step6", data);
        
        data = profile.retrieve("step7");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step7", data);
        
        data = profile.retrieve("step8");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step8", data);
        
        data = profile.retrieve("step9");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step9", data);
        
        data = profile.retrieve("step10");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step10", data);
        
        data = profile.retrieve("step11");
        data.ofSort().accept(sdlVisitor);
        sdlVisitor.defineVariable("step11", data);
        
        System.out.println(sdlVisitor.functionsToString());
        System.out.println(sdlVisitor.toString());
    }
    
}
