import java.awt.*;

// main class that creates animation threads 
class Main {

	public static int w = 500;
	public static int h = 400;

	public static void main(String[] args) {
		
		// create the drawing panel and get graphics object 
		DrawingPanel panel = new DrawingPanel(w, h);
		Graphics2D g = panel.getGraphics();
		panel.setBackground(Color.BLACK);

		// start animation threads 
		new AnimateThread(new Logo(g, 180, 100, 100), panel, Color.BLACK).start();
		new AnimateThread(new Logo(g, 150, 200, 300), panel, Color.BLACK).start();
		// new AnimateThread(new Logo(g, 50, 300, 200), panel, Color.BLACK).start();
	}
}