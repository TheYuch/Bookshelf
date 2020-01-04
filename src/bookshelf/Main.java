package bookshelf;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

public class Main {

	static GameState state = new GameState();
	static final int WIN_WIDTH = 1000; //must be multiple of 100 for grids to work
	static final int WIN_HEIGHT = 600;
	static final int FONT_SIZE = 50;
	public static final int portnum = 1024;
	public static int init = 0; //set by gamepanel to initserver/client. 0 = nothing, 1 = server, 2 = client
	
	public static boolean dead = false;

	public static void main(String args[]) throws InterruptedException, IOException {
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

		while (true) {
			long startTime = System.currentTimeMillis();
			
			if (GamePanel.start)
			{
				if (GameState.connected)
					state.poll();
				state.update();
			}
			if (GamePanel.startnum > 0)
			{
				frame.repaint();
				Thread.sleep(1000);
				GamePanel.startnum -= 1;
			}
			frame.repaint();
			
			if (init > 0)
			{
				if (init == 1)
					state.initserver();
				else
					state.initclient();
				game.multiplayerstart();
				init = 0;
			}

			long elapsedTime = System.currentTimeMillis() - startTime;

			Thread.sleep(Math.max(0, 1000 / 30 - elapsedTime));
		}
	}
}