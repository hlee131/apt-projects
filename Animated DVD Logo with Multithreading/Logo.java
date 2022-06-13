import java.awt.*; 
import java.awt.geom.Rectangle2D; 
import java.awt.font.TextLayout; 

// represents the DVD logo 
class Logo extends Sprite {
	
	// state 
	private int size;
	private Color c = Color.WHITE; 
	private int updateX = 1, updateY = 1;

	// constructs a new logo 
	public Logo(Graphics2D g, int s, int startX, int startY) {
		super(startX, startY, g); 
		this.size = s;
	}

	// draws a logo by calling helper methods 
	// using the bounding box of the dvd text is easier to 
	// align the two elements 
	public void draw() {
		// get bounding box of dvd text 
		Rectangle2D bounds = drawDVD(getX(), getY(), (size / 7) * 3); 
		int width = (int) bounds.getWidth(); 
		// draw the lower disc using the bounding box of the dvd text 
		drawDisc((int) bounds.getX(), (int) (bounds.getY() + bounds.getHeight() + 10), width, (width / 7));
		bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() + 10 + (width/7));
		setBound(bounds);
	}

	public void update(DrawingPanel p) {
		// check collision
		// compare greater than rather than equal because iterating too fast, i.e.
		// 500.35234 != 500.00
		if (getBound().getX() < 0 || getBound().getMaxX() > p.getWidth()) {
			updateX *= -1;
			this.setRandomColor();
		}
		if (getBound().getY() < 0 || getBound().getMaxY() > p.getHeight()) {
			updateY *= -1;
			this.setRandomColor();
		}

		// update to next position for next frame
		setX(getX() + updateX);
		setY(getY() + updateY);
	}

	// sets the color of the logo to a random color 
	public void setRandomColor() {
		c = Color.getHSBColor((float) (Math.random() * 360), (float) 1.0, (float) 1.0);
	}

	// draws the lower disc of the logo 
	// lock is already engaged by the thread 
	private void drawDisc(int x, int y, int width, int height) {
		Graphics2D g = getGraphics(); 
		// draw outer oval
		g.setColor(c);
		g.fillOval(x, y, width, height);

		int innerWidth = (int) (width / 4.6);
		int innerHeight = (int) (height / 3);
		
		// draw inner oval 
		g.setColor(Color.BLACK);
		g.fillOval(x + (width / 2) - (innerWidth / 2), y + (height / 2) - (innerHeight / 2), 
			innerWidth, innerHeight);
	}

	// draws the dvd text 
	// lock is already engaged by thread 
	private Rectangle2D drawDVD(int x, int y, int fontSize) {
		getGraphics().setColor(c);
		Font f = new Font("SansSerif", Font.BOLD, fontSize); 
		TextLayout layout = new TextLayout("DVD", f, getGraphics().getFontRenderContext());
		layout.draw(getGraphics(), (float) x, (float) y);
		Rectangle2D bounds = layout.getBounds();
		bounds.setRect(bounds.getX()+x,
			bounds.getY()+y,
			bounds.getWidth(),
			bounds.getHeight());

		// returns the bounding box of the text
		return bounds; 
	}
}