import java.awt.*; 
import java.awt.geom.Rectangle2D; 

// superclass that can easily be animated with an AnimateThread
class Sprite {

	// required state 
	private int x, y; 
	private Rectangle2D bounds;
	private Graphics2D g; 

	// make a new sprite
	public Sprite(int startX, int startY, Graphics2D g) {
		this.x = startX;
		this.y = startY;
		this.g = g; 
	}
	
	// draw method returns the bounds of the drawn object
	public void draw() {};

	// modify x and y for next frame 
	public void update(DrawingPanel p) {};

	// getters
	// these are protected to only allow the subclass, i.e. the 
	// implemented Sprite, to see its own position 
	protected int getX() { return this.x; }
	protected int getY() { return this.y; }
	// these are public to allow the AnimateThread to clear the 
	// previous frame 
	public Rectangle2D getBound() { return this.bounds; }
	public Graphics2D getGraphics() { return this.g; }

	// setters
	// protected to only allow the subclass to set its own x 
	// and y position. also only allows the subclass 
	// to modify its own box 
	protected void setX(int x) { this.x = x; }
	protected void setY(int y) { this.y = y; }
	protected void setBound(Rectangle2D b) { this.bounds = b; }

}