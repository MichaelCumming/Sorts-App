/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ParseReader.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.parse;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

public class ParseReader extends BufferedReader implements Parsing {

    // constants

    private static final int CURRENT = 0;
    private static final int PREVIEW = 1;
    private static final int TRAILER = 2;
    private static final int TOKENS = 3;

    // representation

    private ParseToken tokens[];
    String string;
    int index, line;
    private boolean preview, trailer;

    // constructor

    public ParseReader(Reader reader) {
	super(reader);
	this.line = 0;
	this.string = this.readLine();
	this.preview = this.trailer = false;
	this.tokens = new ParseToken[TOKENS];
	this.tokens[CURRENT] = new ParseToken(this);
	this.index = this.tokens[CURRENT].parse();
	this.tokens[PREVIEW] = new ParseToken(this);
	this.tokens[TRAILER] = new ParseToken(this);
    }

    // access methods

    public char token() { return this.tokens[CURRENT].value(); }
    public String tokenString() { return this.tokens[CURRENT].toString(); }

    // methods

    public String readLine() {
	this.string = null;
	try {
	    this.string = super.readLine();
	    this.line++;
	    while ((this.string != null) && this.string.equals("")) {
		this.string = super.readLine();
		this.line++;
	    }
	} catch (IOException e) { }
	this.index = 0;
	return this.string;
    }

    public char newToken() {
	if (this.trailer) {
	    ParseToken token = this.tokens[CURRENT];
	    this.tokens[CURRENT] = this.tokens[PREVIEW];
	    this.tokens[PREVIEW] = this.tokens[TRAILER];
	    this.tokens[TRAILER] = token;
	    this.trailer = false;
	} else if (this.preview) {
	    ParseToken token = this.tokens[CURRENT];
	    this.tokens[CURRENT] = this.tokens[PREVIEW];
	    this.tokens[PREVIEW] = token;
	    this.preview = false;
	} else
	    this.index = this.tokens[CURRENT].parse();
	return this.tokens[CURRENT].value();
    }

    public char previewToken() {
	if (!this.preview) {
	    this.index = this.tokens[PREVIEW].parse();
	    this.preview = true;
	}
	return this.tokens[PREVIEW].value();
    }

    public String previewString() {
	if (!this.preview) {
	    this.index = this.tokens[PREVIEW].parse();
	    this.preview = true;
	}
	return this.tokens[PREVIEW].toString();
    }

    public char previewTrailer() {
	if (!this.preview) {
	    this.index = this.tokens[PREVIEW].parse();
	    this.preview = true;
	}
	if (!this.trailer) {
	    this.index = this.tokens[TRAILER].parse();
	    this.trailer = true;
	}
	return this.tokens[TRAILER].value();
    }

    public static boolean isIdentifier(String s) {
	if ((s == null) || s.equals("")) return false;
	char current = s.charAt(0);
	if (((current < 'a') || (current > 'z')) &&
	    ((current < 'A') || (current > 'Z')) && (current != '_'))
	    return false;
	for (int n = 1; n < s.length(); n++) {
	    current = s.charAt(0);
	    if (((current < 'a') || (current > 'z')) &&
		((current < 'A') || (current > 'Z')) && (current != '_') &&
		((current < '0') || (current > '9')))
		return false;
	}
	return true;
    }
	    
    public String toExceptionString(String s) {
	return this.tokens[CURRENT].toExceptionString(s);
    }
}
