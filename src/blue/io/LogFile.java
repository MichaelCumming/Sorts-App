// Generated by Together
// copied from: http://www.artima.com/legacy/design/cleanup/messages/70.html
// 7 Oct.2002

package blue.io;

import java.io.*;
import java.util.*;

public class LogFile {
    private FileOutputStream log;
    private static LogFile logfile;

    private LogFile(String s1, String s2) {
        try {
            //PrintStream ps = new PrintStream((new FileOutputStream(s, true));
            File file = new File("g:\\cumming\\together6.0\\TogetherProjects\\out\\classes\\sorts.18.01.2002\\errorLog.log");
            BufferedWriter aOut = new BufferedWriter(new FileWriter(file));
            aOut.write("Exception : " + s1);
            aOut.write("Description : " + s2);
            aOut.write("Time : " + (new Date().toString().substring(11, 19)));
            aOut.write("\n");
            aOut.write("------------------------------------");
            aOut.flush();
            aOut.close();
        } catch (Exception e) {
            System.out.println(e);
            //logErr(e.toString(), e.getMessage()); // call in your programs like this
        }
    }

    public static void logErr(String s1, String s2) {
        try {
            logfile = new LogFile(s1, s2);
        } catch (Exception e) {
            System.out.println(e);
            // logErr(e.toString(), e.getMessage());
        }
    }
}
