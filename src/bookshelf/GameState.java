package bookshelf;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class GameState extends MouseAdapter {

	static final int JUMP = 20;
	static final int BLOCKS_ALLOWED = 7;
	static final int GRIDS_X = 20;
	static final int GRIDS_Y = 12;
	static final int GRID_THICKNESS = 2;
	static final int PLAYER_SIZE = Main.WIN_WIDTH / GRIDS_X - 10;
	
	public int score = 0;
	
	final int constantx = Main.WIN_WIDTH / GRIDS_X;
	final int constanty = Main.WIN_HEIGHT / GRIDS_Y;

	private Point2D.Double player;
	private Point2D.Double prevplayer;
	private Point2D.Double velocity;
	private double ELASTICITY = 0.5;
	public boolean grounded = false;
	private ArrayList<Point> toremove;
	
	private boolean collidedx = false;
	private boolean collidedy = false;
	
	//public boolean[][] obstacles;
	public int[][] obstacles;
	
	//end point
	public Point end;
	
	private Point mousePos;
	
	Point relativepos; //for mouse position checking

	public GameState() {
		player = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), 0);
		prevplayer = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), 0);
		velocity = new Point2D.Double(0, 0);
		//obstacles = new boolean[GRIDS_X][GRIDS_Y];
		obstacles = new int[GRIDS_X][GRIDS_Y];
		//set remove_idx to length of current arraylist
		toremove = new ArrayList<Point>();
		Random rand = new Random();
		if (rand.nextInt(2) == 0)
			velocity.x = 5;
		else
			velocity.x = -5;
		end = new Point(rand.nextInt(GRIDS_X - 10) + 5, rand.nextInt(GRIDS_Y - 10) + 5);
		mousePos = new Point(0, 0);
		obstacles[end.x][end.y] = 2; 
		relativepos = new Point(0, 0);
	}

	private void addremove(int[][] obstacles, int x, int y)
	{
		if (x >= 0 && x < GRIDS_X && y >= 0 && y < GRIDS_Y && obstacles[x][y] == 0)
		{
			toremove.add(new Point(x, y));
			if (toremove.size() > BLOCKS_ALLOWED)
			{
				obstacles[toremove.get(0).x][toremove.get(0).y] = 0;
				toremove.remove(0); //always removing the first element
			}
			obstacles[x][y] = 1;
		}
	}
	
	public void update() {
		velocity.y += 1;
		player.y += velocity.y;
		player.x += velocity.x;
		
		//check if out of screen
		if (player.x > Main.WIN_WIDTH || player.x + PLAYER_SIZE < 0
				|| player.y > Main.WIN_HEIGHT || player.y + PLAYER_SIZE < 0)
		{
			Main.dead = true;
			return;
		}
		//collision detection
		grounded = false;
		collidedx = false;
		collidedy = false;
		for (int i = Math.max(0, (int)((player.x+velocity.x)/constantx)); i < Math.min((int)((player.x+PLAYER_SIZE+velocity.x)/constantx) + 1, GRIDS_X); i ++)
		{
			for (int j = Math.max(0, (int)((player.y+velocity.y)/constanty)); j < Math.min((int)((player.y+PLAYER_SIZE+velocity.y)/constanty) + 1, GRIDS_Y); j ++)
			{
				if (obstacles[i][j] == 1 || obstacles[i][j] == 2)
				{
					Rectangle r = new Rectangle(i * constantx, j * constanty, constantx, constanty);
					if (player.x <= r.x + r.width && player.x + PLAYER_SIZE >= r.x && player.y <= r.y + r.height && player.y + PLAYER_SIZE >= r.y) {
						if (obstacles[i][j] == 2)
						{
							score++;
							Random rand = new Random();
							obstacles[end.x][end.y] = 0;
							if (score <= 5)
							{
								end.x = rand.nextInt(GRIDS_X - 10) + 5;
								end.y = rand.nextInt(GRIDS_Y - 10) + 5;
							}
							else
							{
								end.x = rand.nextInt(GRIDS_X);
								end.y = rand.nextInt(GRIDS_Y);
							}
							obstacles[end.x][end.y] = 2;
						}
						if (prevplayer.y <= r.y - PLAYER_SIZE) //bottom collision
						{
							player.y = r.y - PLAYER_SIZE;
							if (!collidedy)
								velocity.y = (int)(-ELASTICITY * velocity.y); //bounce off ground
							grounded = true;
							collidedy = true;
						}
						else if (prevplayer.y >= r.y + r.height) //up collision
						{
							player.y = r.y + r.height;
							if (velocity.y < 0 && !collidedy)
								velocity.y = (int)(-ELASTICITY * velocity.y);
							collidedy = true;
						}
						else if (prevplayer.x <= r.x - PLAYER_SIZE) //left collision
						{
							player.x = r.x - PLAYER_SIZE;
							if (!collidedx)
								velocity.x = -velocity.x; //bounce off sides, switching x velocity
							collidedx = true;
						}
						else if (prevplayer.x >= r.x + r.width) //right collision
						{
							player.x = r.x + r.width;
							if (!collidedx)
								velocity.x = -velocity.x; //bounce off sides, switching x velocity
							collidedx = true;
						}
					}
				}
			}
		}
		
		//note that these check the middle positions of the player
		int playerxidx = (int)((player.x + PLAYER_SIZE / 2) / constantx);
		int playeryidx = (int)((player.y + PLAYER_SIZE) / constanty);
		if (playeryidx < GRIDS_Y - 1 && playeryidx >= 0 && playerxidx >= 0 && playerxidx < GRIDS_X)
		{
			//jump
			if (grounded && obstacles[playerxidx][playeryidx] == 1 && obstacles[playerxidx][playeryidx + 1] == 1) 
				velocity.y = -JUMP;
		}		
		
		//keep track of prev position of player for collision logic
		prevplayer.x = player.x;
		prevplayer.y = player.y;
		
		//check mouse position
		addremove(obstacles, (int)(mousePos.x / constantx), (int)(mousePos.y / constanty));
	}

	Point2D.Double getPlayer() {
		return player;
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mousePos.x = e.getX() - 6;
		mousePos.y = e.getY() - 29;
	}
}