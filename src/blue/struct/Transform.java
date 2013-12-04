/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Transform.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.struct;

public class Transform {
    // constants
    static final int SIZE = 4;
    // representation
    private Coord[] [] matrix;
    private Rational scalar;

    // constructor
    public Transform() {
        this.matrix = new Coord[SIZE - 1] [SIZE];
        for (int n = 0; n < SIZE - 1; n++) {
            for (int m = 0; m < SIZE; m++)
                if (m != n) this.matrix[n] [m] = Coord.ZERO;
            this.matrix[n] [n] = Coord.ONE;
        }
        this.scalar = new Rational(Coord.ONE);
    }

    // access methods
    Coord scalar(int n, int m) {
        if ((n < 0) || (n >= SIZE) || (m < 0) || (m >= SIZE))
            throw new IndexOutOfBoundsException("Transform.matrix(): index out of bound");
        return this.matrix[n] [m];
    }

    // methods
    public Vector transform(Vector v) {
        Coord t = v.x().multiply(this.matrix[0] [0]);
        t = t.add(v.y().multiply(this.matrix[0] [1]));
        t = t.add(v.z().multiply(this.matrix[0] [2]));
        Rational x = new Rational(this.matrix[0] [3]).add(v.w().scale(t));
        t = v.x().multiply(this.matrix[1] [0]);
        t = t.add(v.y().multiply(this.matrix[1] [1]));
        t = t.add(v.z().multiply(this.matrix[1] [2]));
        Rational y = new Rational(this.matrix[1] [3]).add(v.w().scale(t));
        t = v.x().multiply(this.matrix[2] [0]);
        t = t.add(v.y().multiply(this.matrix[2] [1]));
        t = t.add(v.z().multiply(this.matrix[2] [2]));
        Rational z = new Rational(this.matrix[2] [3]).add(v.w().scale(t));
        return new Vector(x, y, z, this.scalar);
    }
}
