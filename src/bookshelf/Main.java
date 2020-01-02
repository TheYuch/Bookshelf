package bookshelf;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Main {

	static GameState state = new GameState();
	static final int WIN_WIDTH = 1000; //must be multiple of 100 for grids to work
	static final int WIN_HEIGHT = 600;
	static final int FONT_SIZE = 50;
	static int begin = 3;
	static boolean dead = false;

	public static void main(String args[]) throws InterruptedException {
		JFrame frame = new JFrame("Bookshelf");
		frame.addMouseMotionListener(state);

		GamePanel game = new GamePanel();
		frame.add(game);

		frame.setPreferredSize(new Dimension(WIN_WIDTH + 6, WIN_HEIGHT + 29));
		//+29 to compensate for top bar so it can draw full game window fully
		frame.pack();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.setVisible(true);

		Thread.sleep(1000);
		begin = 2;
		frame.repaint();
		Thread.sleep(1000);
		begin = 1;
		frame.repaint();
		Thread.sleep(1000);
		begin = 0;
		frame.repaint();
		while (true) {
			long startTime = System.currentTimeMillis();
			
			Main.state.update();
			frame.repaint();
			
			if (dead)
				break;

			long elapsedTime = System.currentTimeMillis() - startTime;

			Thread.sleep(Math.max(0, 1000 / 30 - elapsedTime));
		}
	}
}