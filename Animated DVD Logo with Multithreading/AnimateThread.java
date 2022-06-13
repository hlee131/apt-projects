import java.lang.Thread; 
import java.awt.Graphics2D; 
import java.awt.geom.Rectangle2D;
import java.awt.Color;

// this class represents a single animation using a thread 
public class AnimateThread extends Thread {

	// state and animation settings
	private int refresh = 100;
	private DrawingPanel p;
	private Sprite s;
	private Color bg; 

	// constructor for creating a new animation thread 
	public AnimateThread(Sprite s, DrawingPanel p, Color background) {
		this.p = p;
		this.s = s;
		this.bg = background; 
	}

	/*
	This method overrides Thread's run method and will be called by the start method. 
	This method holds the animation loop for each animation. 
	*/
	public void run() {
		// first frame 
		s.draw();

		// animation loop 
		while (true) {
			// needs to be synchronized because Graphics is not thread safe so we 
			// need synchronize to lock the Graphics object. if we do not lock the 
			// Graphics object, a race condition will occur, meaning multiple 
			// threads will try to modify the same Graphics object 
			// this can lead to colors being messed up and flashing renders 
			Graphics2D g = s.getGraphics(); 
			Rectangle2D bounds = s.getBound(); 
			synchronized (g) {
				g.setColor(bg); 
				g.fillRect((int) bounds.getX(), (int) bounds.getY(), 
					(int) Math.ceil(bounds.getWidth()) + 3, (int) Math.ceil(bounds.getHeight()) + 3);
				s.draw();
			}
			
			s.update(p);

			try { Thread.sleep(refresh); } 
			catch (InterruptedException e) {
				System.out.println(e);
				System.exit(1);
			}
		}
	}
}