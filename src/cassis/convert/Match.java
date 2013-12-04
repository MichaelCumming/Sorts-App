/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Match.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 24.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.convert;

import java.util.Vector;
import java.util.Hashtable;

import cassis.Thing;
import cassis.struct.Coord;
import cassis.struct.Rational;
import cassis.struct.List;
import cassis.sort.*;

/**
 * A <b>Match</b> specifies the degree of similarity between two sorts.
 */
public class Match implements Thing, Matching {

    // constants
    /**
     * A constant specifying an <b>identical</b> match, that is, both sorts are
     * identical.
     */
    final static Match identical = new Match(null, null, identicalLevel);
    /**
     * A constant specifying an incongruous match, that is, <b>no</b> match.
     */
    final static Match none = new Match(null, null, incongruousLevel);

    // representation

    private Sort lhs, rhs;
    private int level, grade, operation, front, back, ops[];
    private Match parts[];
    /**
     *
     */
    Match alternative;

    // constructors

    /**
     * Creates a <b>match</b> for two sorts, given a corresponding match level.
     */
    Match(Sort lhs, Sort rhs, int level) {
	this.lhs = lhs;
	this.rhs = rhs;
    	this.level = level;
	this.grade = concordantGrade;
        this.operation = noOp;
        this.front = this.back = 0;
	this.ops = new int[opsRange];
	this.parts = null;
	this.alternative = null;
    }
    private Match(Sort lhs, Sort rhs, int level, Match instance) {
	this(lhs, rhs, level);
        if (this.level < instance.level) this.level = instance.level;
	this.grade = instance.grade;
        for (int i = 0; i < opsRange; i++) this.ops[i] = instance.ops[i];
	this.parts = new Match[1];
	this.parts[0] = instance;
    }
    private Match(Sort lhs, Sort rhs, int level, Match[] instances) {
	this(lhs, rhs, level);
        for (int n = 0; n < instances.length; n++) {
            if (this.level < instances[n].level) this.level = instances[n].level;
            this.grade |= instances[n].grade;
            for (int i = 0; i < opsRange; i++) this.ops[i] += instances[n].ops[i];
        }
	this.parts = instances;
    }

    // access methods

    /**
     * Returns the <b>lhs</b> sort (or source) of a match.
     * @return a {@link cassis.sort.Sort} object
     */
    public Sort lhs() { return this.lhs; }
    /**
     * Returns the <b>rhs</b> sort (or target) of a match.
     * @return a {@link cassis.sort.Sort} object
     */
    public Sort rhs() { return this.rhs; }
    /**
     * Returns the <b>level</b> of a match. The level of a match is a measure
     * of the similarity of its sorts, with respect to their component sorts
     * and to the attribute operator on sorts.
     * @return an integer
     */
    public int level() { return this.level; }

    /**
     * Returns the <b>grade</b> of a match. The level of a match is a measure
     * of the similarity of its sorts, with respect to the disjunctive operator
     * on sorts. The disjunctive operator specifies a subsumption relationship
     * on sorts.
     * @return an integer
     */
    int grade() { return this.grade; }
    /**
     * Returns the matching <b>operation</b> that specifies this match in terms
     * of other matches on smaller compositions of sorts.
     * @return an integer
     */
    public int operation() { return this.operation; }
    /**
     * Returns the number of components that are directly <b>affected</b> by
     * the matching operation.
     * @return an integer
     */
    public int affectCount() { return this.back; }
    /**
     * Returns the number of components that are indirectly <b>concerned</b> by
     * the matching operation.
     * @return an integer
     */
    public int concernCount() { return this.front; }

    /**
     * Returns whether this match <b>is identical</b>.
     * A match is identical if both sorts are identical.
     * @return a boolean
     */
    public boolean isIdentical() { return (this.level == identicalLevel); }
    /**
     * Returns whether this match <b>is equivalent</b>.
     * A match is equivalent if both sorts are derived from the same sort only
     * through naming operations on this sort.
     * @return a boolean
     */
    public boolean isEquivalent() { return (this.level <= equivalentLevel); }
    /**
     * Returns whether this match <b>is strongly similar</b>.
     * A match is strongly similar if all respective attribute component sorts
     * are similarly composed (in the same order) from equivalent component
     * sorts.
     * @return a boolean
     */
    public boolean isStronglySimilar() { return (this.level <= similarLevel) && (this.ops[redefinitionOp] == 0); }
    /**
     * Returns whether this match <b>is similar</b>.
     * A match is similar if all respective simple component sorts have
     * the same characteristic individual and if all respective attribute
     * component sorts are similarly composed (in the same order) from similar
     * component sorts.
     * @return a boolean
     */
    public boolean isSimilar() { return (this.level <= similarLevel); }
    /**
     * Returns whether this match <b>is convertible</b>.
     * A match is convertible if all respective attribute component sorts are
     * composed (not necessarily in the same order) from similar component
     * sorts.
     * @return a boolean
     */
    public boolean isConvertible() { return (this.level <= convertibleLevel); }
    /**
     * Returns whether this match <b>is incomplete</b>.
     * A match is incomplete if some component sorts are similar or convertible.
     * @return a boolean
     */
    public boolean isIncomplete() { return (this.level <= incompleteLevel); }
    /**
     * Returns whether this match <b>is incongruous</b>.
     * A match is incongruous if no component sorts are similar or convertible.
     * @return a boolean
     */
    public boolean isIncongruous() { return (this.level == incongruousLevel); }

    /**
     * Returns whether this match <b>is concordant</b>.
     * A match is concordant if neither lhs nor rhs is a disjunctive sort or,
     * otherwise, if a one-on-one matching relationship exists between
     * the disjunctive component sorts from the lhs and the disjunctive
     * component sorts from the rhs.
     * @return a boolean
     */
    public boolean isConcordant() { return (this.grade == concordantGrade); }
    /**
     * Returns whether this match <b>is a part-of</b> match.
     * A match is a part-of match if each disjunctive component sort from
     * the lhs matches a distinct disjunctive component sort from the rhs.
     * Each of these matches must be at least incomplete. If the lhs sort is
     * not a disjunctive sort, the entire sort must match a disjunctive
     * component sort from the rhs.
     * @return a boolean
     */
    public boolean isPartOf() { return (this.grade == partOfGrade); }
    /**
     * Returns whether this match <b>is a subsumptive</b> match.
     * A match is subsumptive if each disjunctive component sort from
     * the rhs matches a distinct disjunctive component sort from the lhs.
     * Each of these matches must be at least incomplete. If the rhs sort is
     * not a disjunctive sort, the entire sort must match a disjunctive
     * component sort from the lhs.
     * @return a boolean
     */
    public boolean isSubsumptive() { return (this.grade == subsumptiveGrade); }
    /**
     * Returns whether this match <b>is a partial</b> match.
     * A match is partial if at least a disjunctive component sort from
     * the lhs matches a disjunctive component sort from the rhs.
     * @return a boolean
     */
    public boolean isPartial() { return (this.grade <= partialGrade); }
    
    //public boolean hasAlternative() { return (this.alternative != null); }

    //Match alternative() { return this.alternative; }

    public Match getPart(int index) {
        if ((this.parts == null) || (index < 0) || (index > this.parts.length))
            return null;
        return this.parts[index];
    }
/*    public Match getPart(Sort sort) {
        if (this.parts != null)
            for (int n = 0; n < this.parts.length; n++)
                if (this.parts[n].lhs.base().equals(sort))
                    return this.parts[n];
        return null;
    } */

    // Thing interface methods

    public int compare(Thing other) {
	if (!(other instanceof Match)) return FAILED;
	if (this.isIncongruous() && ((Match) other).isIncongruous())
	    return EQUAL;
	int c = this.level - ((Match) other).level;
	c = (c < 0) ? LESS : ((c == 0) ? EQUAL : GREATER);
	if (c != EQUAL) return c;
	// c = this.stats.rational().compare(((Match) other).stats.rational());
	c = this.decimalLevel().compare(((Match) other).decimalLevel());
	if (c != EQUAL) return c;
	c = this.grade - ((Match) other).grade;
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

    private void toString(StringBuffer buffer, String padding) {
	buffer.append(padding);
	if (this.isIncongruous() || this.isIdentical())
	    buffer.append(Matches.levelTerm(this.level));
        else if (!this.isConcordant() &&
	    !((this.lhs instanceof AttributeSort) &&
	      (this.rhs instanceof AttributeSort)))
	    buffer.append(this.lhs.toString()).append(Matches.gradeTerm(this.grade)).append(this.rhs.toString());
        else {
            buffer.append(this.lhs.toString()).append(Matches.levelTerm(this.level)).append(this.rhs.toString());
            buffer.append(Matches.operationTerm(this.operation));
            Rational decimal = this.decimalLevel();
            buffer.append(" [").append(this.level + decimal.doubleValue()).append('#').append(decimal.toString()).append("] ");
            buffer.append('{').append(this.ops[0]);
            for (int i = 1; i < opsRange; i++) buffer.append(',').append(this.ops[i]);
            buffer.append('}');
        }
        buffer.append('\n');
	if (this.parts != null)
	    for (int n = 0; n < this.parts.length; n++)
		this.parts[n].toString(buffer, padding + "  ");
    }
    /**
     * Returns a description reporting on this match.
     * @return a <tt>String</tt> object
     * @see Matches#setLanguage
     */
    public String toString() {
	StringBuffer result = new StringBuffer();
	this.toString(result, "");
	return result.toString();
    }

    // methods
    
    /**
     * <b>Stores</b> this match for later retrieval. This sort isn't stored if
     * it is an incongruous or identical match. Otherwise, if a match for these
     * sorts is already stored, it is assigned as an alternative to this match.
     * @return this match
     * @see Matches#store
     */
    Match store() {
        if (!this.isIncongruous() && !this.isIdentical())
            Matches.store(this);
	return this;
    }

    // return the MINIMUM of two matches
    Match minimum (Match other) {
	if (this.compare(other) <= 0) return this;
        return other;
    }

    /**
     * Extends this match with an <b>omission</b> operation to a new match
     * for the specified sorts. Either the base or the weight of the lhs
     * sort may be omitted. If the weight is omitted, the omission operation
     * also specifies the number of simple component sorts that are omitted.
     * If the base is omitted, this number must be specified as 0.
     * Additionally, the naming operation is considered for either attribute
     * sort that is named and that is not identical to this match's
     * corresponding sort.
     * @param lhs a {@link cassis.sort.AttributeSort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @param backs an integer
     * @return a match
     */
    Match omission(AttributeSort lhs, Sort rhs, int backs) {
	if (this.isIncongruous()) return this;
        Match result;
        if (this.isIdentical())
            result = new Match(lhs, rhs, incompleteLevel);
        else result = new Match(lhs, rhs, incompleteLevel, this);
        result.operation = omissionOp;
        result.back = backs;
	result.ops[omissionOp]++;
        if (lhs.isNamed() && !lhs.equals(this.lhs)) result.ops[namingOp]++;
        if (!this.isIdentical() && !rhs.equals(this.rhs)) result.ops[namingOp]++;
	return result;
    }

    /**
     * Extends this match with an <b>addition</b> operation to a new match
     * for the specified sorts. Either the base or the weight of the rhs
     * sort may be added. If the weight is added, the addition operation
     * also specifies the number of simple component sorts that are added.
     * If the base is added, this number must be specified as 0.
     * Additionally, the naming operation is considered for either attribute
     * sort that is named and that is not identical to this match's
     * corresponding sort.
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.AttributeSort} object
     * @param backs an integer
     * @return a match
     */
    Match addition(Sort lhs, AttributeSort rhs, int backs) {
	if (this.isIncongruous()) return this;
        Match result;
        if (this.isIdentical())
            result = new Match(lhs, rhs, incompleteLevel);
        else result = new Match(lhs, rhs, incompleteLevel, this);
        result.operation = additionOp;
        result.back = backs;
	result.ops[additionOp]++;
        if (!this.isIdentical() && !lhs.equals(this.lhs)) result.ops[namingOp]++;
        if (rhs.isNamed() && !rhs.equals(this.rhs)) result.ops[namingOp]++;
	return result;
    }

    /**
     * Extends this match with a <b>skipping</b> operation to a new match
     * for the specified attribute sorts. Either the base or the weight of both
     * sorts may be skipped. If the weights are skipped, the skipping operation
     * also specifies the number of simple component sorts that are skipped.
     * If the base is skipped, this number must be specified as 0.
     * Additionally, the naming operation is considered for either attribute
     * sort that is named and that is not identical to this match's
     * corresponding sort.
     * @param lhs a {@link cassis.sort.AttributeSort} object
     * @param rhs a {@link cassis.sort.AttributeSort} object
     * @param backs an integer
     * @return a match
     */
    Match skipping(AttributeSort lhs, AttributeSort rhs, int backs) {
	if (this.isIncongruous()) return this;
	Match result = new Match(lhs, rhs, incompleteLevel, this);
        result.operation = skippingOp;
        result.back = backs;
	result.ops[skippingOp]++;
        if (lhs.isNamed() && !lhs.equals(this.lhs)) result.ops[namingOp]++;
        if (rhs.isNamed() && !rhs.equals(this.rhs)) result.ops[namingOp]++;
	return result;
    }

    /**
     * Extends this match with a <b>rearrangement</b> operation to a new
     * match for the specified attribute sorts. The rearrangement operation
     * specifies the respective number of simple component sorts that are
     * moved forwards and back as part of this rearrangement. Additionally,
     * the naming operation is considered for either attribute sort that is
     * named and that is not identical to this match's corresponding sort.
     * @param lhs a {@link cassis.sort.AttributeSort} object
     * @param rhs a {@link cassis.sort.AttributeSort} object
     * @param forwards an integer
     * @param backs an integer
     * @return a match
     */
    Match rearrangement(AttributeSort lhs, AttributeSort rhs, int forwards, int backs) {
	if (this.isIncongruous()) return this;
        int level = convertibleLevel;
        if (this.level > level) level = this.level;
	Match result = new Match(lhs, rhs, level, this);
        
        result.operation = rearrangementOp;
        result.front = forwards;
        result.back = backs;
	result.ops[rearrangementOp] += forwards + backs - 1;
        if (lhs.isNamed()) result.ops[namingOp]++;
        if (rhs.isNamed()) result.ops[namingOp]++;
	return result;
    }

    /**
     * Combines two matches to a new match for the specified attribute sorts.
     * If either match is incongruous, then an incongruous match will be
     * returned. A skipping operation should then be used instead.
     * The resulting match is at least equivalent. The naming operation is
     * considered for either attribute sort that is named and that is not
     * identical to this match's corresponding sort.
     * @param lhs a {@link cassis.sort.AttributeSort} object
     * @param rhs a {@link cassis.sort.AttributeSort} object
     * @param other a match
     * @return a match
     */
    Match combination(AttributeSort lhs, AttributeSort rhs, Match other) {
	if (this.isIncongruous() || other.isIncongruous()) return none;
        int level = equivalentLevel;
        if (this.level > level) level = this.level;
        if (other.level > level) level = other.level;
        Match instances[] = {this, other};
	Match result = new Match(lhs, rhs, level, instances);
        
        if (lhs.isNamed()) result.ops[namingOp]++;
        if (rhs.isNamed()) result.ops[namingOp]++;
	return result;
    }

    /**
     * Extends this match with a <b>subsumption</b> operation to a new match
     * for the specified sorts. XXX
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @param parts an integer
     * @return a match
     */
    Match subsumption(Sort lhs, Sort rhs, int parts) {
	if (this.isIncongruous()) return this;
	Match result = new Match(lhs, rhs, this.level, this);
	result.grade |= subsumptiveGrade;
        result.operation = subsumptionOp;
	result.ops[subsumptionOp] += parts;
	return result;
    }

    /**
     * Extends this match with a <b>part-of</b> operation to a new match
     * for the specified sorts. XXX
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @param parts an integer
     * @return a match
     */
    Match partOf(Sort lhs, Sort rhs, int parts) {
	if (this.isIncongruous()) return this;
	Match result = new Match(lhs, rhs, this.level, this);
	result.grade |= partOfGrade;
        result.operation = partOfOp;
	result.ops[partOfOp] += parts;
	return result;
    }

    /**
     * Creates an <b>equivalent</b> match for two sorts, and stores the result.
     * This match is specified with one or two naming operations, depending on
     * whether the sorts are primitive sorts or not.
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @return a match
     * @see #store()
     */
    static Match equivalent(Sort lhs, Sort rhs) {
        Match result = new Match(lhs, rhs, equivalentLevel);
        result.operation = namingOp;
        result.ops[namingOp]++;
        if (!(lhs instanceof PrimitiveSort)) result.ops[namingOp]++;
        return result;
    }
    /**
     * Creates a <b>similar</b> match for two simple sorts, and stores the
     * resulting match. The match is specified with a redefinition operation.
     * @param lhs a {@link cassis.sort.SimpleSort} object
     * @param rhs a {@link cassis.sort.SimpleSort} object
     * @return a match
     * @see #store()
     */
    static Match similar(SimpleSort lhs, SimpleSort rhs) {
        Match result = new Match((Sort) lhs, (Sort) rhs, similarLevel);
        result.operation = redefinitionOp;
        result.ops[redefinitionOp]++;
        return result;
    }
    /**
     * Creates a <b>convertible</b> match for two simple sorts, and stores
     * the resulting match. The match is specified with a constraint operation.
     * @param lhs a {@link cassis.sort.SimpleSort} object
     * @param rhs a {@link cassis.sort.SimpleSort} object
     * @return a match
     * @see #store()
     */
    static Match convertible(Sort lhs, Sort rhs) {
        Match result = new Match((Sort) lhs, (Sort) rhs, convertibleLevel);
        result.operation = constraintOp;
        result.ops[constraintOp]++;
        return result;
    }

    /**
     * <b>Reverses</b> this match. Creates a new match from this one,
     * in which the lhs and rhs are switched.
     * @return a match
     * @see #store()
     */
    Match reverse() {
	if (this.isIncongruous() || this.isIdentical()) return this;
        if (this.operation == rearrangementOp) {
            Sort back = this.rhs.base(), remainder = ((AttributeSort) this.rhs).weight();
            for (int n = 1; n < this.back; n++) {
		back = back.combine(remainder.base());
                remainder = ((AttributeSort) remainder).weight();
            }
            Sort front = remainder.base();
            for (int n = 0; n < this.front; n++) {
                if (remainder instanceof AttributeSort)
                    remainder = ((AttributeSort) remainder).weight();
                else remainder = null;
		if (n + 1 < this.front) front = front.combine(remainder.base());
            }
            if (remainder != null) back = back.combine(remainder);
            AttributeSort assist = (AttributeSort) front.combine(back);
            Match result = Matches.compare(assist.base(), this.lhs.base());
            result = result.combination(assist, (AttributeSort) this.lhs, Matches.compare(assist.weight(), ((AttributeSort) this.lhs).weight()));
            result = result.rearrangement((AttributeSort) this.rhs, (AttributeSort) this.lhs, this.back, this.front);
            return result.store();
        }

	Match result = new Match(this.rhs, this.lhs, this.level);
        switch (this.grade) {
            case subsumptiveGrade: result.grade = partialGrade;
            case partialGrade: result.grade = subsumptiveGrade;
            default: result.grade = this.grade;
        }
        for (int i = 0; i < opsRange; i++) result.ops[i] = this.ops[i];
        result.ops[omissionOp] = this.ops[additionOp];
        result.ops[additionOp] = this.ops[omissionOp];
        result.ops[subsumptionOp] = this.ops[partOfOp];
        result.ops[partOfOp] = this.ops[subsumptionOp];
        switch (this.operation) {
            case omissionOp: result.operation = additionOp; break;
            case additionOp: result.operation = omissionOp; break;
            case subsumptionOp: result.operation = partOfOp; break;
            case partOfOp: result.operation = subsumptionOp; break;
            default: result.operation = this.operation;
        }
        if (this.parts != null) {
            result.parts = new Match[this.parts.length];
            for (int n = 0; n < this.parts.length; n++)
                result.parts[n] = this.parts[n].reverse();
	}
	return result.store();
    }

    private Rational scale() {
	Rational result = UNIT.scale(new Coord(this.lhs.simples() + this.lhs.names()));
	if (this.rhs != null) {
	    Rational alt = UNIT.scale(new Coord(this.rhs.simples() + this.rhs.names()));
	    if (alt.compare(result) == GREATER) return alt;
	}
	return result;
    }
    /**
     *
     */
    public Rational decimalLevel() {
	Rational result = NAME_UNIT.scale(new Coord(this.ops[namingOp]));
	result = result.add(PRIM_UNIT.scale(new Coord(this.ops[redefinitionOp] + this.ops[constraintOp])));
	result = result.add(SWAP_UNIT.scale(new Coord(this.ops[rearrangementOp])));
	result = result.add(SKIP_UNIT.scale(new Coord(this.ops[omissionOp] + this.ops[additionOp] + this.ops[skippingOp] * 2)));
	return result.divide(this.scale());
    }
}
