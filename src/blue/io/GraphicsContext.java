/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `GraphicsContext.java'                                    *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io;

import blue.struct.Vector;

public interface GraphicsContext {
    void clear();

    void register(String reference);

    void beginGroup(Class characteristic, int count);

    void endGroup();

    void point(Vector position);

    void lineSegment(Vector tail, Vector head);

    void label(String s);

    void jump(String reference);

    void satellite(String s);

    void link(String url, String label, String image);
}
