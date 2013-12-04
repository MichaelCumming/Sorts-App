/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SdlContext.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io;

import java.util.Stack;
import java.io.PrintStream;
import blue.sort.Sort;

public class SdlContext {
    // constants
    public static final int DEFAULT = 0;
    public static final int COMPACT = 1;
    public static final int VERBOSE = 2;
    public static final int ITEM = 0;
    public static final int GROUP = 1;
    public static final int COMMA = 2;
    public static final int DEFINE = 3;
    public static final int SORT = 0;
    public static final int IND = 1;
    public static final int FORM = 2;
    private static final String defines[] = {"sort", "individual", "form"};
    // representation
    private int style, status;
    private PrintStream ps;
    private String tab, result;
    private Sort sort;
    private Stack tabs;

    // constructors
    public SdlContext(PrintStream ps, int style) {
        this.style = DEFAULT;
        if ((style > DEFAULT) && (style <= VERBOSE)) this.style = style;
        this.status = DEFINE;
        this.ps = ps;
        this.tab = this.result = "";
        this.sort = null;
        this.tabs = new Stack();
    }

    public SdlContext(PrintStream ps) {
        this(ps, DEFAULT);
    }

    public SdlContext(int style) {
        this(null, style);
    }

    public SdlContext() {
        this(null, DEFAULT);
    }

    // access methods
    public String toString() {
        return this.result;
    }

    // methods
    private void print(String s) {
        if (this.ps == null)
            this.result += s;
        else
            this.ps.print(s);
    }

    public static String parenthesize(String s) {
        if (s.indexOf(' ') < 0)
            return s;
        return '(' + s + ')';
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public void header() {
        this.print("#SDL V1.0a\n\n");
    }

    public void header(String name) {
        this.print("#SDL V1.0a [" + name + "]\n\n");
    }

    public void define(int type, String name) {
        if ((type < SORT) || (type > FORM))
            throw new IllegalArgumentException("unrecognized definition type");
        String temp;
        if (type == SORT) {
            temp = defines[type] + ' ' + name + " :";
            this.status = ITEM;
        } else {
            temp = '\n' + defines[type] + " $" + name + " =";
            this.status = DEFINE;
        }
        this.print(temp);
    }

    public void write(String s) {
        String temp;
        switch (this.style) {
            case COMPACT:
                temp = ' ' + s;
                break;
            case VERBOSE:
                if (this.status == ITEM)
                    temp = ' ' + s;
                else
                    temp = '\n' + this.tab + this.sort.base().toString() + ": " + s;
                break;
            default: // case DEFAULT:
                if (this.status == DEFINE)
                    temp = this.sort.base().toString() + ": " + s;
                else if (this.status == COMMA)
                    temp = this.tab + s;
                else
                    temp = ' ' + s;
                break;
        }
        this.print(temp);
        this.status = ITEM;
    }

    public void beginGroup() {
        this.tabs.push(this.tab);
        String temp;
        switch (this.style) {
            case COMPACT:
                if (this.status != ITEM)
                    temp = ' ' + parenthesize(this.sort.toString()) + ": {";
                else
                    temp = " {";
                break;
            case VERBOSE:
                if (this.status == ITEM)
                    this.tab += "  ";
                temp = '\n' + this.tab + parenthesize(this.sort.toString()) + ": {";
                this.tab += "  ";
                break;
            default: // case DEFAULT:
                if (this.status == COMMA)
                    temp = this.tab + parenthesize(this.sort.toString()) + ":\n";
                else if (this.status != ITEM)
                    temp = ' ' + parenthesize(this.sort.toString()) + ":\n";
                else
                    temp = "\n";
                if (this.status != DEFINE) {
                    this.tab += "  ";
                    temp += this.tab;
                }
                this.tab += "  ";
                temp += '{';
                break;
        }
        this.print(temp);
        this.status = GROUP;
    }

    public void endGroup() {
        this.tab = (String)this.tabs.pop();
        String temp;
        if (this.style == VERBOSE) {
            if (this.status == COMMA)
                temp = "\n  " + this.tab + '}';
            else
                temp = '\n' + this.tab + '}';
        } else
            temp = " }";
        this.print(temp);
        this.status = COMMA;
    }

    public void separator() {
        String temp;
        if (this.style == COMPACT)
            temp = "";
        else if (this.style == DEFAULT)
            temp = ",\n";
        else
            temp = "";
        this.print(temp);
        this.status = COMMA;
    }

    public void endStatement() {
        this.print(";\n");
    }
}
