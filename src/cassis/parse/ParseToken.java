/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ParseToken.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.parse;

import java.io.IOException;

class ParseToken implements Parsing {

    // representation

    private ParseReader reader;
    private char value;
    private int line, index, endex;
    private String text;

    // constructor

    ParseToken(ParseReader reader) {
	this.reader = reader;
	this.value = 0;
	this.line = this.index = this.endex = 0;
	this.text = null;
    }

    // access methods

    char value() { return this.value; }
    public String toString() {
        // String result = this.text.substring(this.index - 1, this.endex);
        // System.out.println(result);
        // return result;
	return this.text.substring(this.index - 1, this.endex);
    }

    // methods

    int parse() {
	this.value = 0;
	this.text = null;
	this.index = this.endex = 0;
	if ((this.reader.index == this.reader.string.length()) &&
	    (this.reader.readLine() == null)) return this.reader.index;
	if (this.advance())
	    this.endex = this.span(this.index);
	//System.out.println(this.index + "-" + this.endex + ":" + this.text.substring(this.index - 1, this.endex));
	return this.endex;
    }

    private boolean advance() {
	this.text = this.reader.string;
	this.index = this.reader.index;
	this.line = this.reader.line;
	this.value = this.text.charAt(this.index++);

	while (((this.value == ' ') || (this.value == '\t')) &&
	       (this.index < this.text.length()))
	    this.value = this.text.charAt(this.index++);
	if ((this.value == ' ') || (this.value == '\t')) {
	    if (this.reader.readLine() == null) return false;
	    return this.advance();
	}
	if (this.index >= this.text.length()) return true;

	if (((this.value >= 'a') && (this.value <= 'z')) ||
	    ((this.value >= 'A') && (this.value <= 'Z')) ||
	    (this.value == '_'))
	    this.value = IDENTIFIER;
	else if ((this.value >= '0') && (this.value <= '9'))
	    this.value = NUMBER;
	else if (this.value == '"')
	    this.value = STRING;

	char next_value = this.text.charAt(this.index);
	if (next_value == this.value) {
	    if (this.value == '/') {
		if (this.reader.readLine() == null) return false;
		return this.advance();
	    }
	    if (this.value == '=')
		this.value = EQUALS;
	    else if (this.value == '&')
		this.value = LOGICALAND;
	    else if (this.value == '|')
		this.value = LOGICALOR;
	    else return true;
	    this.index++;
	    return true;
	}
	if (next_value == '=') {
	    if (this.value == '<')
		this.value = LESSQ;
	    else if (this.value == '>')
		this.value = GREATERQ;
	    else if (this.value == '!')
		this.value = NOTEQUAL;
	    else return true;
	    this.index++;
	}
	return true;
    }

    private int span(int lead) {
	char current;
	switch (this.value) {
	case STRING:
	    do {
		lead = this.text.indexOf('"', lead) + 1;
	    } while ((lead > 0) &&
		     (this.text.charAt(lead - 2) == '\\'));
	    if (lead < 1) {
		lead = this.text.length();
		if (this.reader.readLine() == null) return lead;
		this.text += '\n' + this.reader.string;
		return this.span(lead);
	    }
	    return lead;
	case IDENTIFIER:
            current = this.text.charAt(lead++);
            while (((current >= 'a') && (current <= 'z')) ||
		   ((current >= 'A') && (current <= 'Z')) ||
		   ((current >= '0') && (current <= '9')) ||
		   (current == '_')) {
		if (lead == this.text.length()) return lead;
                current = this.text.charAt(lead++);
            }
	    return lead - 1;
	case NUMBER:
            current = this.text.charAt(lead++);
	    while ((current >= '0') && (current <= '9')) {
                if (lead == this.text.length()) return lead;
                current = this.text.charAt(lead++);
            }
	    if (current == '.') {
                current = this.text.charAt(lead++);
                while ((current >= '0') && (current <= '9')) {
                    if (lead == this.text.length()) return lead;
                    current = this.text.charAt(lead++);
                }
		if ((current == 'e') || (current == 'E')) {
		    current = this.text.charAt(lead++);
		    if ((current == '+') || (current == '-'))
                        current = this.text.charAt(lead++);
                    while ((current >= '0') && (current <= '9')) {
                        if (lead == this.text.length()) return lead;
                        current = this.text.charAt(lead++);
                    }
		}
	    }
	    return lead - 1;
	case LOGICALAND: case LOGICALOR: case LESSQ: case GREATERQ: case NOTEQUAL: case EQUALS:
	    return lead + 1;
	}
	return lead;
    }
	    
    String toExceptionString(String s) {
	StringBuffer result = new StringBuffer("Parse exception at line ");
	result.append(this.line).append(": ").append(s).append('\n');
        if (this.text == null) return result.toString();
	int last = this.text.indexOf('\n');
	if (last < 0) last = this.text.length();
	result.append(this.text.substring(0, last)).append('\n');
	for (int n = 1; n < this.index; n++)
	    result.append(' ');
	return result.append("^\n").toString();
    }
}
