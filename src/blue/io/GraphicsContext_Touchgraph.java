/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `GraphicsContext_Touchgraph.java                          *
 * written by: Michael Cumming                               *
 * last modified: 14.03.2003                                 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io;

import blue.struct.Vector;

public interface GraphicsContext_Touchgraph {
    void clear();

    //void register(String reference);
    void beginGroup(Class characteristic, int count);

    void endGroup();

    //void point(Vector position);
    //void lineSegment(Vector tail, Vector head);
    void label(String s);

    void jump(String reference);

    void satellite(String s);

    void link(String url, String label, String image);
}
