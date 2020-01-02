package bookshelf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	/*
	 * Moved the update method into GameState.java
	 */

	@Override
	public void paintComponent(Graphics g) {
		/*
		 * Clear the screen
		 */
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, getWidth(), getHeight());

		GameState currState = Main.state;
		
		if (Main.dead)
		{
			g.setColor(Color.BLACK);
			g.setFont(new Font("Serif", Font.BOLD, 210));
			g.drawString("Score: " + currState.score, 0, Main.WIN_HEIGHT / 2 + 70);
		}
		else
		{
			/*
			 * Draw the grid
			 */
			g.setColor(Color.LIGHT_GRAY);
			for (int i = 1; i < currState.GRIDS_X; i++)
			{
				g.fillRect((i * currState.constantx) - (currState.GRID_THICKNESS / 2), 0, currState.GRID_THICKNESS, Main.WIN_HEIGHT); 
				//rectangles act as thicker lines
			}
			for (int i = 1; i < currState.GRIDS_Y; i ++)
			{
				g.fillRect(0, (i * currState.constanty) - (currState.GRID_THICKNESS / 2), Main.WIN_WIDTH, currState.GRID_THICKNESS);
			}
			
			/*
			 * draw obstacles
			 */
			for (int i = 0; i < currState.GRIDS_X; i ++)
			{
				for (int j = 0; j < currState.GRIDS_Y; j ++)
				{
					if (currState.obstacles[i][j] == 1)
					{
						g.setColor(Color.GRAY);
						g.fillRect(i * currState.constantx, j * currState.constanty, currState.constantx, currState.constanty);
					}
				}
			}
			
			/*
			 * Draw the player
			 */
			g.setColor(Color.BLUE);
			Point2D.Double player = currState.getPlayer();
			g.fillOval((int)player.x, (int)player.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
			
			/*
			 * Draw end
			 */
			g.setColor(Color.YELLOW);
			g.fillRect(currState.end.x * currState.constantx, currState.end.y * currState.constanty, currState.constantx, currState.constanty);
		
			g.setColor(Color.ORANGE);
			if (Main.begin == 0)
			{
				g.setFont(new Font("Serif", Font.ITALIC, Main.FONT_SIZE));
				g.drawString("Score: " + currState.score, 0, 35);
			}
			else
			{
				g.setFont(new Font("Serif", Font.PLAIN, 300));
				g.drawString(Integer.toString(Main.begin), (Main.WIN_WIDTH / 2) - 75, Main.WIN_HEIGHT / 2 + 100);
			}
		}
	}
}