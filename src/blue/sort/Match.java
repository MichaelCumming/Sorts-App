/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Match.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 27.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import java.util.Vector;
import java.util.Hashtable;
import blue.Thing;
import blue.struct.Coord;
import blue.struct.Rational;
import blue.struct.List;

public class Match implements Thing, Matching {
    // constants
    final static Match none = new Match(incongruousLevel, null, null);
    // representation
    private int level, grade;
    MatchStats stats;
    int excess, omits, skips, augs, dims, jumps, jumpsize;
    private Sort lhs, rhs;
    private Match parts[], alternative;

    // constructors
    Match(int level, Sort lhs, Sort rhs) {
        // if ((lhs != null) && (lhs.context() != rhs.context()))
        //    throw new IllegalArgumentException("Match: illegal arguments");
        this.level = level;
        this.grade = concordantGrade;
        this.stats = new MatchStats();
        this.excess = this.omits = 0;
        this.skips = this.jumps = this.jumpsize = 0;
        this.augs = this.dims = 0;
        this.lhs = lhs;
        this.rhs = rhs;
        this.parts = null;
        this.alternative = this;
    }

    private Match(int level, Sort lhs, Sort rhs, Match instance) {
        this(level, lhs, rhs);
        this.parts = new Match[1];
        this.parts[0] = instance;
        this.grade = instance.grade;
        this.stats.add(instance.stats);
    }

    Match(int level, Sort lhs, Sort rhs, Match first, Match second) {
        this(level, lhs, rhs);
        this.parts = new Match[2];
        this.parts[0] = first;
        this.parts[1] = second;
        this.grade = second.grade;
        this.stats.add(first.stats);
        this.stats.add(second.stats);
    }

    private Match(int level, Sort lhs, Sort rhs, int size) {
        this(level, lhs, rhs);
        this.parts = new Match[size];
    }

    // access methods
    public int level() { return this.level; }

    public Sort lhs() { return this.lhs; }

    public Sort rhs() { return this.rhs; }

    public boolean isIdentical() {
        return ((this.level == identicalLevel) && this.stats.clean());
    }

    public boolean isEquivalent() { return (this.level == equivalentLevel); }

    public boolean isStronglySimilar() { return (this.level <= stronglySimLevel); }

    public boolean isWeaklySimilar() { return (this.level <= similarLevel); }

    public boolean isSimilar() { return (this.level <= similarLevel); }

    public boolean isConvertible() { return (this.level <= convertibleLevel); }

    public boolean isIncomplete() { return (this.level <= incompleteLevel); }

    public boolean isIncongruous() { return (this.level == incongruousLevel); }

    public boolean isConcordant() { return (this.grade == concordantGrade); }

    public boolean isPartOf() { return (this.grade == partOfGrade); }

    public boolean isSubsumptive() { return (this.grade == subsumptiveGrade); }

    public boolean isPartial() { return (this.grade <= partialGrade); }

    public boolean isLocallyConvertible() {
        return (this.skips == 0) && (this.augs == 0) && (this.dims == 0) && (this.excess == 0) && (this.omits == 0);
    }

    public boolean isLocallySimilar() {
        return this.isLocallyConvertible() && (this.jumps == 0);
    }

    public boolean hasAlternative() { return (this.alternative != this); }

    int grade() { return this.grade; }

    Match alternative() { return this.alternative; }

    // Thing interface methods
    public int compare(Thing other) {
        if (!(other instanceof Match)) return FAILED;
        if (this.isIncongruous() && ((Match)other).isIncongruous())
            return EQUAL;
        int c = this.level - ((Match)other).level;
        c = (c < 0) ? LESS : ((c == 0) ? EQUAL : GREATER);
        if (c != EQUAL) return c;
        // c = this.stats.rational().compare(((Match) other).stats.rational());
        c = this.decimalLevel().compare(((Match)other).decimalLevel());
        if (c != EQUAL) return c;
        c = this.grade - ((Match)other).grade;
        return (c < 0) ? LESS : ((c == 0) ? EQUAL : GREATER);
    }

    /**
     * Tests whether this thing is strictly less than a specified other thing.
     * Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }

    /**
     * Tests whether this thing is strictly greater than a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }

    /**
     * Tests whether this thing is less than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }

    /**
     * Tests whether this thing is greater than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        this.toString(result, "");
        return result.toString();
    }

    // methods
    // convert this match TO a STRING in the specified buffer with padding
    void toString(StringBuffer buffer, String padding) {
        buffer.append(padding).append(this.lhs.context().matchToString(this)).append('\n');
        if (this.parts != null)
            for (int n = 0; n < this.parts.length; n++)
                this.parts[n].toString(buffer, padding + "  ");
    }

    // REVERSE this match
    Match reverse() {
        if (this.isIncongruous()) return this;
        Match result = lookup(this.rhs, this.lhs);
        if (result != null) return result;
        if (this.parts == null) {
            result = new Match(this.level, this.rhs, this.lhs);
            result.stats.reverse(this.stats);
            return result;
        }
        result = new Match(this.level, this.rhs, this.lhs, this.parts.length);
        for (int n = 0; n < this.parts.length; n++) {
            result.parts[n] = this.parts[n].reverse();
            if (result.level < this.parts[n].level)
                result.level = this.parts[n].level;
            if ((result.grade != result.parts[n].grade) && (result.grade != partialGrade) &&
                (this.parts[n].grade != concordantGrade)) {
                    if (result.grade == concordantGrade)
                        result.grade = result.parts[n].grade;
                    else
                        result.grade = partialGrade;
            }
            result.stats.add(result.parts[n].stats);
        }
        return result;
    }

    private Rational scale() {
        Rational result = UNIT.scale(new Coord(this.lhs.simples() + this.lhs.names()));
        if (this.rhs != null) {
            Rational alt = UNIT.scale(new Coord(this.rhs.simples() + this.rhs.names()));
            if (alt.compare(result) == GREATER) return alt;
        }
        return result;
    }

    public Rational decimalLevel() {
        return this.stats.rational().divide(this.scale());
    }

    // create an IDENTICAL match
    static Match identical(Sort sort) {
        return new Match(identicalLevel, sort, sort);
    }

    // create an EQUIVALENT match
    static Match equivalent(Sort lhs, Sort rhs) {
        Match result = new Match(equivalentLevel, lhs, rhs);
        result.stats.byName();
        return result;
    }

    // create a SIMILAR match
    static Match similar(Sort lhs, Sort rhs) {
        Match result = new Match(similarLevel, lhs, rhs);
        result.stats.byDef();
        return result;
    }

    // create a CONVERTIBLE match
    static Match convertible(Sort lhs, Sort rhs) {
        Match result = new Match(convertibleLevel, lhs, rhs);
        result.stats.byArg();
        return result;
    }

    // create an INCOMPLETE match
    static Match incomplete(Sort lhs, Sort rhs, Match instance) {
        if (instance.isIncongruous()) return instance;
        if (!instance.isConvertible())
            return instance.assign(lhs, rhs);
        Match result;
        if (instance.isIdentical() && instance.isConcordant())
            result = new Match(incompleteLevel, lhs, rhs);
        else
            result = new Match(incompleteLevel, lhs, rhs, instance);
        System.out.println("> " + lhs.toString() + ", " + rhs.toString());
        if ((lhs.isNamed() && !(lhs instanceof PrimitiveSort)) || (rhs.isNamed() && !(rhs instanceof PrimitiveSort)))
            result.stats.byName();
        return result;
    }

    // update this match into a CONVERSION
    Match toStronglySimilar() {
        if (this.isIncongruous()) return this;
        if (this.level < stronglySimLevel)
            this.level = stronglySimLevel;
        return this;
    }

    // update this match into a CONVERSION
    Match conversion() {
        if (this.isIncongruous()) return this;
        if (this.level < convertibleLevel)
            this.level = convertibleLevel;
        return this;
    }

    // update this match into a REARRANGEMENT
    Match rearrangement(int jump, int size) {
        if (this.isIncongruous()) return this;
        if (this.level < convertibleLevel)
            this.level = convertibleLevel;
        this.stats.swaps++;
        this.jumps = jump;
        this.jumpsize = size;
        return this;
    }

    // update this match into a TRUNCATION
    Match truncation(int skips) {
        if (this.isIncongruous()) return this;
        this.level = incompleteLevel;
        this.stats.skips += skips;
        this.skips += skips;
        return this;
    }

    // update this match into an AUGMENTATION
    Match augmentation(int augs) {
        if (this.isIncongruous()) return this;
        this.level = incompleteLevel;
        this.stats.augs += augs;
        this.augs += augs;
        return this;
    }

    // update this match into a DIMINUTION
    Match diminution(int dims) {
        if (this.isIncongruous()) return this;
        this.level = incompleteLevel;
        this.stats.dims += dims;
        this.dims += dims;
        return this;
    }

    // update this match into a SUBSUMPTIVE match
    Match subsumptive() { return this.subsumptive(0); }

    Match subsumptive(int excess) {
        if (this.isIncongruous()) return this;
        this.grade |= subsumptiveGrade;
        this.stats.excess += excess;
        this.excess += excess;
        return this;
    }

    // update this match into a PART OF match
    Match partOf() { return this.partOf(0); }

    Match partOf(int omits) {
        if (this.isIncongruous()) return this;
        this.grade |= partOfGrade;
        this.stats.omits += omits;
        this.omits += omits;
        return this;
    }

    private static String keyname(Sort lhs, Sort rhs) {
        return lhs.context().profile().name() + "'" + lhs.toString() + '$' +
            rhs.context().profile().name() + "'" + rhs.toString();
    }

    /**
     * Approves and stores this match for later referral.
     * @return this match
     * @see Sorts#putMatch
     */
    Match approve() {
        String key = keyname(this.lhs, this.rhs);
        Match match = this.lhs.context().getMatch(key);
        if (match != null) {
            this.alternative = match; // .alternative;
            // match.alternative = this;
        } // else
        this.lhs.context().putMatch(key, this);
        // System.out.println("*approved*\n" + this.printString());
        return this;
    }

    /**
     * Approves and stores this match for later referral.
     * @param left a first sort
     * @param right a second sort
     * @see Sorts#removeMatch
     */
    static void disapprove(Sort left, Sort right) {
        String key = keyname(left, right);
        left.context().removeMatch(key);
    }

    // LOOKUP a match in the table of known matches
    static Match lookup(Sort lhs, Sort rhs) {
        return lhs.context().getMatch(keyname(lhs, rhs));
    }

    // return the MINIMUM of two matches
    Match minimum(Match other) {
        if (this.isIncongruous()) return other;
        if (this.level < other.level) return this;
        if (other.level < this.level) return other;
        // c = this.stats.rational().compare(((Match) other).stats.rational());
        int c = this.decimalLevel().compare(((Match)other).decimalLevel());
        if (c < 0) return this;
        if (c > 0) return other;
        if (this.grade <= other.grade) return this;
        return other;
    }

    // COMBINE two matches into one,
    // the first one corresponds to match of primitive sorts (or aspects)
    Match combine(Match other, Sort lhs, Sort rhs) {
        if (this.parts != null)
            throw new IllegalArgumentException("Illegal match for Match.combine()");
        int level;
        if (this.isEquivalent() && other.isStronglySimilar())
            level = stronglySimLevel;
        else if (this.isSimilar() && other.isSimilar())
            level = weaklySimLevel;
        else if (this.isConvertible() && other.isConvertible())
            level = convertibleLevel;
        else if (this.isIncomplete() || other.isIncomplete())
            level = incompleteLevel;
        else
            return none;
        Match result;

/*	if (this.isIdentical() || this.isIncongruous()) {
	    if (other.isIncongruous() ||
		  (other.isIdentical() && other.isConcordant()))
		result = new Match(level, lhs, rhs);
	    else result = new Match(level, lhs, rhs, other);
	} else {
	    if (other.isIncongruous() ||
		  (other.isIdentical() && other.isConcordant()))
		result = new Match(level, lhs, rhs, this);
	    else result = new Match(level, lhs, rhs, this, other);
	}
*/

        if (this.isIncongruous()) {
            if (other.isIncongruous())
                result = new Match(level, lhs, rhs);
            else
                result = new Match(level, lhs, rhs, other);
        } else {
            if (other.isIncongruous())
                result = new Match(level, lhs, rhs, this);
            else
                result = new Match(level, lhs, rhs, this, other);
        }
        result.grade = other.grade;
        result.stats.add(this.stats);
        result.stats.add(other.stats);
        if (this.isIncongruous()) result.truncation(1);
        if (other.isIncongruous()) {
            int skips = lhs.simples();
            if (rhs.simples() > skips) skips = rhs.simples();
            int less = this.lhs().simples();
            if (this.rhs().simples() > less) less = this.rhs().simples();
            result.truncation(skips - less);
        }
        result.stats.byName();
        return result;
    }

    // COMPOSE a match for two composite sorts
    static Match compose(DisjunctiveSort lhs, DisjunctiveSort rhs) {
        Match result = new Match(stronglySimLevel, lhs, rhs);
        List matches = new List();
        Hashtable sorts = new Hashtable();
        Vector parts = new Vector();
        Match found;
        for (lhs.toBegin(); !lhs.beyond(); lhs.toNext()) {
            if (lhs.current().canonical.equals(rhs.canonical)) {
                if (lhs.current().toString().equals(rhs.toString()))
                    found = Match.identical(rhs);
                else
                    found = Match.equivalent(lhs.current(), rhs);
                return found.subsumptive(lhs.size() - 1);
            }
            for (rhs.toBegin(); !rhs.beyond(); rhs.toNext()) {
                if (lhs.canonical.equals(rhs.current().canonical)) {
                    if (lhs.toString().equals(rhs.current().toString()))
                        found = Match.identical(lhs);
                    else
                        found = Match.equivalent(lhs, rhs.current());
                    return found.partOf(rhs.size() - 1);
                }
                found = lhs.current().relate(rhs.current());
                if (!found.isIncongruous()) matches.append(found);
                Match temp = found.alternative();
                while (temp != found) {
                    matches.append(temp);
                    temp = temp.alternative();
                }
            }
        }
        matches.sort();
        matches.toBegin();
        while (!matches.beyond()) {
            Match current = (Match)matches.current();
            // if (!current.isStronglySimilar()) break;
            if ((sorts.get(current.lhs()) == null) && (sorts.get(current.rhs()) == null)) {
                if (!current.isIdentical())
                    parts.addElement(current);
                if (current.isConcordant() || current.isSubsumptive())
                    sorts.put(current.lhs(), current);
                if (current.isConcordant() || current.isPartOf())
                    sorts.put(current.rhs(), current);
                matches.delete();
            } else
                matches.toNext();
        }

/*	
	matches.toBegin();
	while (!matches.beyond()) {
	    Match current = (Match) matches.currentThing();
	    if (!current.isStronglySimilar()) break;
	    if ((sorts.get(current.lhs()) == null) ||
		(sorts.get(current.rhs()) == null)) {
		parts.addElement(current);
		if (current.isConcordant() || current.isSubsumptive())
		    sorts.put(current.lhs(), current);
		if (current.isConcordant() || current.isPartOf())
		    sorts.put(current.rhs(), current);
	    }
	    matches.delete();
	}
*/

        result.parts = new Match[parts.size()];
        for (int n = 0; n < parts.size(); n++) {
            result.parts[n] = (Match)parts.elementAt(n);
            if (result.level < result.parts[n].level)
                result.level = result.parts[n].level;
            if ((result.grade != result.parts[n].grade) && (result.grade != partialGrade) &&
                (result.parts[n].grade != concordantGrade)) {
                    if (result.grade == concordantGrade)
                        result.grade = result.parts[n].grade;
                    else
                        result.grade = partialGrade;
            }
            result.stats.add(result.parts[n].stats);
        }
        for (lhs.toBegin(); !lhs.beyond(); lhs.toNext())
            if (sorts.get(lhs.current()) == null)
                result.subsumptive(1);
        for (rhs.toBegin(); !rhs.beyond(); rhs.toNext())
            if (sorts.get(rhs.current()) == null)
                result.partOf(1);
        return result;
    }

    // ASSIGN this match to the specified sorts
    Match assign(Sort lhs, Sort rhs) {
        if (this.isIncongruous()) return this;
        Match result;
        // if (!this.isIdentical() || !this.isConcordant())
        result = new Match(this.level, lhs, rhs, this);
        // else result = new Match(this.level, lhs, rhs);
        result.grade = this.grade;
        result.stats.add(this.stats);
        if ((lhs.isNamed() && !(lhs instanceof PrimitiveSort)) || (rhs.isNamed() && !(rhs instanceof PrimitiveSort)))
            result.stats.byName();
        return result;
    }
}
