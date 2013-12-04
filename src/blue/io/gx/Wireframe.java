/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Wireframe.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.io.gx;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import blue.io.GraphicsContext;
import blue.struct.Vector;
import blue.ind.Geometry;

public class Wireframe implements GraphicsContext {
    private Canvas parent;
    private Graphics g;
    private boolean coordinates;
    private double scale;
    private int offx = 4, offy;

    public Wireframe(Canvas parent) {
        this.parent = parent;
        this.g = null;
        this.coordinates = false;
        this.scale = 1.0;
    }

    public Wireframe(Canvas parent, boolean coords) {
        this.parent = parent;
        this.g = null;
        this.coordinates = coords;
    }

    public void setCoordinates(boolean coords) {
        this.coordinates = coords;
    }

    public void coordinatesOn() {
        this.coordinates = true;
    }

    public void coordinatesOff() {
        this.coordinates = false;
    }

    public boolean coordinates() {
        return this.coordinates;
    }

    public void setColor(Color color) {
        if (this.g != null) this.g.setColor(color);
    }

    public Color getColor() {
        if (this.g == null) return null;
        return this.g.getColor();
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return this.scale;
    }

    public void setGraphics(Graphics g) {
        this.g = g;
        this.offy = this.parent.getSize().height - this.offx;
    }

/*
    public void draw(Graphics g, Geometry ind) {
	this.g = g;
	this.offy = this.parent.getSize().height - this.offx;
	if (ind != null) ind.draw(this);
    }

    public void draw(Geometry ind) {
	if (ind != null) ind.draw(this);
    }
*/

    private int getX(Vector position) {
        return this.offx + (int)(position.w().scale(position.x()).doubleValue() * this.scale);
    }

    private int getY(Vector position) {
        return this.offy - (int)(position.w().scale(position.y()).doubleValue() * this.scale);
    }

    public void point(Vector position) {
        int x = this.getX(position);
        int y = this.getY(position);
        if (g == null) return;
        g.fillOval(x - 2, y - 2, 5, 5);
        if (this.coordinates) {
            String coords = position.toString();
            x += 4;
            y -= 4;
            if (y - g.getFontMetrics().getAscent() < 0) y = 0;
            if (x + g.getFontMetrics().stringWidth(coords) > this.parent.getSize().width)
                x = this.parent.getSize().width - g.getFontMetrics().stringWidth(coords);
            g.drawString(coords, x, y);
        }
    }

    public void lineSegment(Vector tail, Vector head) {
        int x1 = this.getX(tail);
        int y1 = this.getY(tail);
        int x2 = this.getX(head);
        int y2 = this.getY(head);
        if (g == null) return;
        g.drawLine(x1, y1, x2, y2);
    }

    public void clear() { }

    public void register(String reference) { }

    public void beginGroup(Class characteristic, int count) { }

    public void endGroup() { }

    public void label(String s) { }

    public void jump(String reference) { }

    public void satellite(String s) { }

    public void link(String url, String label, String image) { }
}
