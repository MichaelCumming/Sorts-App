/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MatchStats.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.struct.Coord;
import blue.struct.Rational;

class MatchStats implements Matching {
    // representation
    int excess, omits;
    int skips, swaps;
    int augs, dims;
    int bynames, bydefs, byargs;

    // constructor
    MatchStats() {
        this.excess = this.omits = 0;
        this.skips = this.swaps = 0;
        this.augs = this.dims = 0;
        this.bynames = this.bydefs = this.byargs = 0;
    }

    // methods
    void byName() { this.bynames++; }

    void byDef() { this.bydefs++; }

    void byArg() { this.byargs++; }

    boolean clean() {
        return (this.bynames == 0) && (this.bydefs == 0) && (this.byargs == 0);
    }

    void add(MatchStats other) {
        this.excess += other.excess;
        this.omits += other.omits;
        this.skips += other.skips;
        this.swaps += other.swaps;
        this.augs += other.augs;
        this.dims += other.dims;
        this.bynames += other.bynames;
        this.bydefs += other.bydefs;
        this.byargs += other.byargs;
    }

    void reverse(MatchStats other) {
        this.excess = other.omits;
        this.omits = other.excess;
        this.skips = other.skips;
        this.swaps = other.swaps;
        this.augs = other.dims;
        this.dims = other.augs;
        this.bynames += other.bynames;
        this.bydefs += other.bydefs;
        this.byargs += other.byargs;
    }

    Rational rational() {
        Rational result = PRIM_UNIT.scale(new Coord(this.bydefs + this.byargs * 2));
        result = result.add(NAME_UNIT.scale(new Coord(this.bynames + this.bydefs + this.byargs)));
        result = result.add(SKIP_UNIT.scale(new Coord(this.skips + this.dims + this.augs)));
        return result.add(SWAP_UNIT.scale(new Coord(this.swaps)));
    }

    void toString(StringBuffer buffer) {
        buffer.append('{').append(this.bynames + this.bydefs + this.byargs).append(',');
        buffer.append(this.bydefs + this.byargs * 2).append(',').append(this.dims).append(',');
        buffer.append(this.augs).append(',').append(this.skips).append(',').append(this.swaps).append('}');
    }
}
