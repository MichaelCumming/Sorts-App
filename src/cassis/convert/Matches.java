/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Matches.java'                                            *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.convert;

import java.util.Hashtable;
import java.util.Vector;

import cassis.sort.*;
import cassis.struct.Rational;
import cassis.struct.List;

public class Matches implements Matching {
    
    // constants

    /**
     * A constant to represent the use of <b>english</b> for reporting on matches.
     */
    public static final int ENGLISH = 0;
    /**
     * A constant to represent the use of <b>german</b> for reporting on matches.
     */
    public static final int DEUTSCH = 1;
    /**
     * A constant to represent the use of <b>dutch</b> for reporting on matches.
     */
    public static final int NEDERLANDS = 1;

    // representation

    private static Hashtable matches = new Hashtable();

    private static int language = ENGLISH;
    private static String levelTerms[] = new String[6];
    private static String gradeTerms[] = new String[4];
    private static String operationTerms[] = new String[opsRange];

    // public access methods

    /**
     * <b>Cleans up</b> this context by clearing all matches,
     */
    public static void cleanup() {
	matches.clear();
    }

    // matching methods

    private static String keyname(Sort lhs, Sort rhs) {
	return lhs.context().profile().name() + "'" + lhs.toString() + '$' + rhs.context().profile().name() + "'" + rhs.toString();
    }
    /**
     * <b>Looks up</b> and retrieves the match corresponding to the specified
     * sorts. If no match is found, but a reverse match exists, then a match
     * is created from this reverse match and stored.
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @return a {@link Match} object
     */
    public static Match lookup(Sort lhs, Sort rhs) {
	Match result = (Match) matches.get(keyname(lhs, rhs));
        if (result != null) return result;
        result = (Match) matches.get(keyname(rhs, lhs));
        if (result != null) result.reverse();
        return result;
    }
    /**
     * <b>Stores</b> the specified match based on its lhs and rhs sorts.
     * If either lhs or rhs sort is null, then the match isn't stored.
     * If a match is already stored for the same sorts, then that match is
     * assigned as an alternative to the specified match.
     * @param match a {@link Match} object
     */
    static void store(Match match) {
        if ((match.lhs() == null) || (match.rhs() == null)) return;
	String description = keyname(match.lhs(), match.rhs());
	Match alt = (Match) matches.get(description);
        if (alt == match) return;
	if (alt != null) match.alternative = alt;
	matches.put(description, match);
    }
    /**
     * <b>Removes</b> the match corresponding to the specified sorts.
     * @param lhs a first sort
     * @param rhs a second sort
     */
    static void remove(Sort lhs, Sort rhs) {
	String description = keyname(lhs, rhs);
	matches.remove(description);
    }

    /**
     * Sets the language for reporting on matches within this context.
     * @param lang an integer representing the selected language
     * @see #ENGLISH
     * @see #DEUTSCH
     * @see #NEDERLANDS
     */
    public static void setLanguage(int lang) {
	language = lang;

	if (language == NEDERLANDS) {
	    levelTerms[identicalLevel] = "identiek";
	    levelTerms[equivalentLevel] = " is gelijkwaardig met ";
	    levelTerms[similarLevel] = " is gelijkaardig aan ";
	    levelTerms[convertibleLevel] = " is omvormbaar tot ";
	    levelTerms[incompleteLevel] = " is gedeeltelijk omvormbaar tot ";
	    levelTerms[incongruousLevel] = "zonder overeenkomst";
 
	    gradeTerms[concordantGrade] = " stemt overeen met ";
	    gradeTerms[partOfGrade] = " maakt deel uit van ";
	    gradeTerms[subsumptiveGrade] = " omvat ";
	    gradeTerms[partialGrade] = " komt gedeeltelijk overeen met ";

	    operationTerms[0] = " (door naamgeving)";
	    operationTerms[1] = " (door een herdefinitie)";
	    operationTerms[2] = " (door beperking)";
	    operationTerms[3] = " (door herschikking)";
	    operationTerms[4] = " (door weglating)";
	    operationTerms[5] = " (door toevoeging)";
	    operationTerms[6] = " (door overslaan)";
	    operationTerms[7] = " (door omvatting)";
	    operationTerms[8] = " (door deel-van)";
	} else if (language == DEUTSCH) {
	    levelTerms[identicalLevel] = "identisch";
	    levelTerms[equivalentLevel] = " ist gleichwertig mit ";
	    levelTerms[similarLevel] = " ist aehnlich mit ";
	    levelTerms[convertibleLevel] = " ist verwandelbar in ";
	    levelTerms[incompleteLevel] = " ist teilweise verwandelbar in ";
	    levelTerms[incongruousLevel] = "ohne Entsprechung";
 
	    gradeTerms[concordantGrade] = " stimmt ueberein mit ";
	    gradeTerms[partOfGrade] = " ist enthalten in ";
	    gradeTerms[subsumptiveGrade] = " enthalt ";
	    gradeTerms[partialGrade] = " teilweise entsprecht ";

	    operationTerms[0] = " (durch Namengebung)";
	    operationTerms[1] = " (durch ein Neudefinition)";
	    operationTerms[2] = " (durch Beschraenkung)";
	    operationTerms[3] = " (durch Umordnung)";
	    operationTerms[4] = " (durch Auslassung)";
	    operationTerms[5] = " (durch Hinzufuegung)";
	    operationTerms[6] = " (durch Ueberschlaegung)";
	    operationTerms[7] = " (durch Enthaltung)";
	    operationTerms[8] = " (durch Teil-von)";
	} else {
	    levelTerms[identicalLevel] = "identical";
	    levelTerms[equivalentLevel] = " is equivalent to ";
	    levelTerms[similarLevel] = " is similar to ";
	    levelTerms[convertibleLevel] = " is convertible to ";
	    levelTerms[incompleteLevel] = " is partially convertible to ";
	    levelTerms[incongruousLevel] = "no match";

	    gradeTerms[concordantGrade] = " is concordant to ";
	    gradeTerms[partOfGrade] = " is subsumed by ";
	    gradeTerms[subsumptiveGrade] = " subsumes ";
	    gradeTerms[partialGrade] = " partially matches ";

	    operationTerms[0] = " (through naming)";
	    operationTerms[1] = " (through a redefinition)";
	    operationTerms[2] = " (through a constraint)";
	    operationTerms[3] = " (through a rearrangement)";
	    operationTerms[4] = " (through omission)";
	    operationTerms[5] = " (through addition)";
	    operationTerms[6] = " (through skipping)";
	    operationTerms[7] = " (through subsumption)";
	    operationTerms[8] = " (through part-of)";
	}
    }
    
    /**
     *
     */
    static String levelTerm(int level) {
        if (levelTerms[0] == null) setLanguage(ENGLISH);
        return levelTerms[level];
    }
    static String gradeTerm(int grade) {
        if (gradeTerms[0] == null) setLanguage(ENGLISH);
        return gradeTerms[grade];
    }
    static String operationTerm(int operation) {
        if (operationTerms[0] == null) setLanguage(ENGLISH);
        if (operation < 0) return "";
        return operationTerms[operation];
    }

    /**
     * Compares two sorts and returns the resulting <b>match</b>.
     * @param lhs a {@link cassis.sort.Sort} object
     * @param rhs a {@link cassis.sort.Sort} object
     * @return a {@link Match} object
     * @throws IllegalArgumentException if the sorts cannot be compared, e.g.,
     * one of the sorts (not both) is an {@link cassis.sort.AspectsSort}
     */
    public static Match compare(Sort lhs, Sort rhs) throws IllegalArgumentException {
        //System.out.println("comparing " + lhs + " to " + rhs);
        // identical
	if (lhs.equals(rhs)) return Match.identical;
        // equivalent
        if (lhs.canonical().equals(rhs.canonical()))
            return Match.equivalent(lhs, rhs);
        // aspects-sort
        if (lhs instanceof AspectsSort) {
            if (rhs instanceof AspectsSort)
                return Match.none;
            throw new IllegalArgumentException("Aspects sort '" + lhs.canonical() + "'");
        } else if (rhs instanceof AspectsSort)
	    throw new IllegalArgumentException("Aspects sort '" + rhs.canonical() + "'");
        // lookup
        Match result = lookup(lhs, rhs);
        if (result != null) return result;

        if (lhs instanceof Aspect) {
            if (rhs instanceof Aspect)
                return match((Aspect) lhs, (Aspect) rhs);
            return compare(rhs, lhs).reverse();
        } else if (lhs instanceof PrimitiveSort) {
            if (rhs instanceof PrimitiveSort) {
                if (((PrimitiveSort) lhs).characteristic().equals(((PrimitiveSort) rhs).characteristic()))
                    return Match.similar((PrimitiveSort) lhs, (PrimitiveSort) rhs);
                // convertible if not the same arguments
                return Match.none;
            } else if (rhs instanceof Aspect) {
                //result = compare(lhs, ((Aspect) rhs).argument());
                //result = result.extend(lhs, rhs);
                //if (result.isSimilar()) result.conversion();
                return Match.none;
            }
           return compare(rhs, lhs).reverse();
        } else if (lhs instanceof AttributeSort) {
            if (rhs instanceof AttributeSort)
                return match((AttributeSort) lhs, (AttributeSort) rhs);
             else if (rhs instanceof SimpleSort) {
                result = compare(lhs.base(), rhs).omission((AttributeSort) lhs, rhs, ((AttributeSort) lhs).weight().simples());
                Match alt = compare(((AttributeSort) lhs).weight(), (Sort) rhs).omission((AttributeSort) lhs, rhs, 0);
                return result.minimum(alt).store();
             } else if ((rhs instanceof DisjunctiveSort) || (rhs instanceof RecursiveSort))
                return compare(rhs, lhs).reverse();
        } else if (lhs instanceof DisjunctiveSort) {
            if (rhs instanceof SimpleSort)
                return match((DisjunctiveSort) lhs, (SimpleSort) rhs);
            else if (rhs instanceof RecursiveSort)
                return compare(rhs, lhs).reverse();
            else if (rhs instanceof AttributeSort)
                return match((DisjunctiveSort) lhs, (AttributeSort) rhs);
            else if (rhs instanceof DisjunctiveSort)
                return match((DisjunctiveSort) lhs, (DisjunctiveSort) rhs);
        } else if (lhs instanceof RecursiveSort) {
            /*
            if (rhs instanceof AttributeSort) {
                result = compare(lhs, ((AttributeSort) rhs).weight()).omission(lhs, rhs);
                new Match(lhs, rhs, Match.incongruousLevel).store();
                Match alt = compare(((RecursiveSort) lhs).instance(), rhs).extend(lhs, rhs);
                remove(lhs, rhs);
                return result.minimum(alt).store();
            }
            new Match(lhs, rhs, Match.incongruousLevel).store();
            result = compare(((RecursiveSort) lhs).instance(), rhs);
            remove(lhs, rhs);
            return result.extend(lhs, rhs).store();
             */
        }
 	return Match.none;
    }

    /**
     * <b>Matches</b> an aspect to another aspect.
     * If their aspect-sort instances are not equivalent, then no match exists.
     * Otherwise, if the aspect's arguments are identical, both aspects
     * are also denoted equivalent.
     * Otherwise, both argument sorts are matched and the result assigned to
     * the aspects. This match is at most strongly similar.
     * @param lhs an <tt>Aspect</tt> object
     * @param rhs an <tt>Aspect</tt> object
     * @return a {@link Match} object
     * @see AspectsSort#compare(AspectsSort)
     */
    private static Match match(Aspect lhs, Aspect rhs) {
	Match compare = compare(lhs.instance(), rhs.instance());
        if (!compare.isEquivalent()) return Match.none;
	if (lhs.argument().equals(rhs.argument()))
            return Match.equivalent(lhs, rhs);
        Sort[] args = (Sort[]) lhs.arguments().value();
        compare = compare(args[0], args[1]);
        //if (!compare.isIncongruous())
        //    compare = compare.extend(lhs, rhs).toSimilar();
        return compare;
    }

    /**
     * <b>Matches</b> an attribute sort to another attribute sort.
     * Two attribute sorts are denoted equivalent if their
     * canonical definitions are identical. Two attribute sorts are denoted
     * similar if both their respective base and weight sorts are similar.
     * They are denoted convertible if ...
     * They are denoted incomplete if ...
     * Otherwise, no match exists.
     * @param lhs an {@link AttributeSort} object
     * @param rhs an {@link AttributeSort} object
     * @return a {@link Match} object
     */
    private static Match match(AttributeSort lhs, AttributeSort rhs) {
        //System.out.println("matching " + lhs + " to " + rhs);
        Match bases, weights, result;
        bases = compare(lhs.base(), rhs.base());
        weights = compare(lhs.weight(), rhs.weight());
        if (weights.isIncongruous()) {
            if (bases.isIncongruous()) {
                result = Match.none;
            } else {
                int skips = lhs.weight().simples();
                if (rhs.weight().simples() > skips) skips = rhs.weight().simples();
                result = bases.skipping(lhs, rhs, skips);
            }
        } else if (bases.isIncongruous())
            result = weights.skipping(lhs, rhs, 0);
        else {
            result = bases.combination(lhs, rhs, weights);
            if (result.isSimilar()) return result.store();
        }
        //System.out.println(">>> match: result");
        //System.out.println(result);

	// Convertible
	Sort head = lhs.base(), tail = lhs.weight(), temp;
        AttributeSort assist;
	Match alt, alt2, local;
	
	for (int n = 1; !(tail instanceof DisjunctiveSort); n++) {
            //System.out.println(n + " : " + tail.base());
	    local = compare(tail.base(), rhs.base());
	    if (!local.isIncongruous()) {
		if (tail instanceof AttributeSort)
		    temp = head.combine(((AttributeSort) tail).weight());
		else temp = head;
                assist = (AttributeSort) tail.base().combine(temp);
                alt = local.combination(assist, rhs, compare(temp, rhs.weight()));
		alt = alt.rearrangement(lhs, rhs, n, 1);
                //System.out.println(">>> AttributeMatch: alt");
                //System.out.println(alt);
		result = alt.minimum(result);
		if (tail instanceof AttributeSort) {
		    Sort ohead = rhs.base(), otail = rhs.weight();
		    Sort shead = tail.base(), stail = ((AttributeSort) tail).weight();
		    int direction = -1, dir;
		    for (int m = 2; otail instanceof AttributeSort; m++) {
			ohead = ohead.combine(otail.base());
			otail = ((AttributeSort) otail).weight();
			shead = shead.combine(stail.base());
			if (stail instanceof AttributeSort)
			    temp = head.combine(((AttributeSort) stail).weight());
			else temp = head;
                        assist = (AttributeSort) shead.combine(temp);
			alt2 = local.combination(assist, rhs, compare(assist.weight(), rhs.weight()));
                        alt2 = alt2.rearrangement(lhs, rhs, n, m);
                        //System.out.println(">>> AttributeMatch: alt2");
                        //System.out.println(alt2);
			result = alt2.minimum(result);
			dir = alt.compare(alt2);
			if ((direction > 0) && (dir > 0)) break;
			if (!(stail instanceof AttributeSort)) break;
			stail = ((AttributeSort) stail).weight();
			alt = alt2; direction = dir;
		    }
		}
	    }
	    if (tail instanceof SimpleSort) break;
	    head = head.combine(tail.base());
	    tail = ((AttributeSort) tail).weight();
	}
	if (result.isConvertible()) {
            //System.out.println(">>> match: result'");
            //System.out.println(result);
	    return result.store();
	}

	// Incomplete
	alt = compare(lhs, rhs.weight()).addition(lhs, rhs, 0);
        //System.out.println(">>> match: alt'");
        //System.out.println(alt);
	result = result.minimum(alt);
	alt = compare(lhs.weight(), rhs).omission(lhs, rhs, 0);
        //System.out.println(">>> match: alt''");
        //System.out.println(alt);
	result = result.minimum(alt);
	return result.store();
    }

    /**
     * <b>Matches</b> a disjunctive sort to a simple sort.
     * Each component sort is compared to the simple sort.
     * The minimum match of all these is stored and returned.
     * @param lhs an {@link DisjunctiveSort} object
     * @param rhs an {@link SimpleSort} object
     * @return a {@link Match} object
     */
    private static Match match(DisjunctiveSort lhs, SimpleSort rhs) {
	Match result = Match.none;
	lhs.toBegin();
	while(!lhs.beyond()) {
	    result = result.minimum(compare(lhs.current(), (Sort) rhs));
	    lhs.toNext();
	}
        if (result.isIncongruous()) return result;
        return result.subsumption(lhs, (Sort) rhs, lhs.components() - 1).store();
    }

    /**
     * <b>Matches</b> a disjunctive sort to an attribute sort.
     * Each component sort is compared to the attribute sort and
     * the disjunctive sort is also compared to a new disjunctive sort
     * composed of the base and weight components of the attribute sort.
     * The minimum match of all these is stored and returned.
     * @param lhs an {@link DisjunctiveSort} object
     * @param rhs an {@link AttributeSort} object
     * @return a {@link Match} object
     */
    private static Match match(DisjunctiveSort lhs, AttributeSort rhs) {
        /*
	Match result = Match.none;
	lhs.toBegin();
	while(!lhs.beyond()) {
	    result = result.minimum(compare(lhs.current(), rhs));
	    lhs.toNext();
	}
        if (!result.isIncongruous())
            result = result.subsumption(lhs, rhs, lhs.components() - 1);
	DisjunctiveSort sort = new DisjunctiveSort(rhs.context());
	sort.insert(rhs.base());
	sort.insert(rhs.weight());
	Match alt = new Match(sort, rhs, Match.incompleteLevel);
	// alt.rearrangement(2);
	alt = new Match(lhs, rhs, Match.incompleteLevel, alt, compare(lhs, sort));
	return result.minimum(alt).store();
         */
        return Match.none;
    }

    /**
     * <b>Matches</b> a disjunctive sort to another disjunctive sort.
     * Two disjunctive sorts are denoted equivalent if their
     * canonical definitions are identical. Otherwise, if no match has already
     * been stored, their respective component list are compared, and
     * the minimum match is stored.
     * @param lhs an {@link DisjunctiveSort} object
     * @param rhs an {@link DisjunctiveSort} object
     * @return a {@link Match} object
     * @see Match#compose
     */
    private static Match match(DisjunctiveSort lhs, DisjunctiveSort rhs) {
	Match result = new Match(lhs, rhs, similarLevel);
	List matches = new List();
	Hashtable sorts = new Hashtable();
	Vector parts = new Vector();
	Match found;
/*
	for (lhs.toBegin(); !lhs.beyond(); lhs.toNext()) {
	    if (lhs.current().canonical().equals(rhs.canonical())) {
		if (lhs.current().toString().equals(rhs.toString()))
		    found = Match.identical;
		else found = Match.equivalent(lhs.current(), rhs);
		return found.subsumptive(lhs.size() - 1);
	    }
	    for (rhs.toBegin(); !rhs.beyond(); rhs.toNext()) {
		if (lhs.canonical().equals(rhs.current().canonical())) {
		    if (lhs.toString().equals(rhs.current().toString()))
			found = Match.identical;
		    else found = Match.equivalent(lhs, rhs.current());
		    return found.partOf(rhs.size() - 1);
		}
		found = compare(lhs.current(), rhs.current());
		if (!found.isIncongruous()) matches.append(found);
		Match temp = found.alternative();
		while (temp != found) {
		    matches.append(temp);
		    temp = temp.alternative();
		}
	    }
	}
	matches.order();
*/
	matches.toBegin();
	while (!matches.beyond()) {
	    Match current = (Match) matches.current();
	    // if (!current.isStronglySimilar()) break;
	    if ((sorts.get(current.lhs()) == null) &&
		(sorts.get(current.rhs()) == null)) {
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
//	result.parts = new Match[parts.size()];
//	for (int n = 0; n < parts.size(); n++) {
//	    result.parts[n] = (Match) parts.elementAt(n);
/*
	    if (result.level < result.parts[n].level)
		result.level = result.parts[n].level;

	    if ((result.grade != result.parts[n].grade) &&
		(result.grade != partialGrade) &&
		(result.parts[n].grade != concordantGrade)) {
		if (result.grade == concordantGrade)
		    result.grade = result.parts[n].grade;
		else result.grade = partialGrade;
	    }
	    result.stats.add(result.parts[n].stats);
 */
//	}
	//for (lhs.toBegin(); !lhs.beyond(); lhs.toNext())
	//    if (sorts.get(lhs.current()) == null)
	//	result.subsumptive(1);
	//for (rhs.toBegin(); !rhs.beyond(); rhs.toNext())
	//    if (sorts.get(rhs.current()) == null)
	//	result.partOf(1);
	return result;
    }
}
