package bookshelf;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class GameState extends MouseAdapter {

	static final int JUMP = 20;
	static final int BLOCKS_ALLOWED = 7;
	static final int GRIDS_X = 20;
	static final int GRIDS_Y = 12;
	static final int GRID_THICKNESS = 2;
	static final int PLAYER_SIZE = Main.WIN_WIDTH / GRIDS_X - 10;
	static final int MULTIPLAYERGAMETIME = 20000; //20 seconds
	
	public static int score = 0;
	public static int oscore = 0; //opponent's score
	
	public final static int constantx = Main.WIN_WIDTH / GRIDS_X;
	public final static int constanty = Main.WIN_HEIGHT / GRIDS_Y;

	private static Point2D.Double player;
	private Point2D.Double prevplayer;
	private Point2D.Double velocity;
	private double ELASTICITY = 0.5;
	public boolean grounded = false;
	private ArrayList<Point> toremove;
	
	private static Point2D.Double opponent;
	private ArrayList<Point> otoremove;
	
	private boolean collidedx = false;
	private boolean collidedy = false;
	
	//public boolean[][] obstacles;
	public static int[][] obstacles;
	
	//end point
	public static Point end;
	
	private Point mousePos;
	
	//multiplayer networking stuff
	public static boolean multiplayer = false;
	public static boolean connected = false;
	public static boolean ishost;
	public static long multiplayertime;
	private PrintWriter out;
	private BufferedReader in;
	private PushbackInputStream pin;
	
	private Socket server;
	private ServerSocket serverSocket;
	public String hostnum;

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
		
		opponent = new Point2D.Double();
		otoremove = new ArrayList<Point>();
	}
	
	public void reset()
	{
		if (connected)
		{
			try {
				if (ishost)
					serverSocket.close();
				else
					server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		player = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), 0);
		prevplayer = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), 0);
		velocity = new Point2D.Double(0, 0);
		obstacles = new int[GRIDS_X][GRIDS_Y];
		toremove = new ArrayList<Point>();
		Random rand = new Random();
		if (rand.nextInt(2) == 0)
			velocity.x = 5;
		else
			velocity.x = -5;
		end = new Point(rand.nextInt(GRIDS_X - 10) + 5, rand.nextInt(GRIDS_Y - 10) + 5);
		mousePos = new Point(0, 0);
		obstacles[end.x][end.y] = 2; 
		multiplayer = false;
		connected = false;
		
		otoremove.clear();
	}

	private void addremove(ArrayList<Point> removelist, int x, int y)
	{
		if (x >= 0 && x < GRIDS_X && y >= 0 && y < GRIDS_Y && obstacles[x][y] == 0)
		{
			removelist.add(new Point(x, y));
			if (removelist.size() > BLOCKS_ALLOWED)
			{
				obstacles[removelist.get(0).x][removelist.get(0).y] = 0;
				removelist.remove(0); //always removing the first element
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
		//to rid some bugs
		obstacles[end.x][end.y] = 2;
		
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
		addremove(toremove, (int)(mousePos.x / constantx), (int)(mousePos.y / constanty));
		if (connected)
			write(true);
	}

	public static Point2D.Double getPlayer() {
		return player;
	}

	public static Point2D.Double getOpponent() {
		return opponent;
	}
	
	private void write(boolean added)
	{
		if (added)
		{
			Point tmp;
			try
			{
				tmp = toremove.get(toremove.size() - 1);
			}
			catch (Exception e)
			{
				tmp = toremove.get(toremove.size() - 1); //just in case toremove was changed at this moment by another thread.
			}
			out.println(score + " " + player.x + " " + player.y + " " + tmp.x + " " + tmp.y);
		}
		else
			out.println(score + " " + player.x + " " + player.y + " -1");
	}
	
	private void read()
	{
		try {
			String[] inp = in.readLine().split(" ");
			oscore = Integer.parseInt(inp[0]);
			opponent.x = Double.parseDouble(inp[1]);
			opponent.y = Double.parseDouble(inp[2]);
			if (Integer.parseInt(inp[3]) != -1)
			{
				addremove(otoremove, Integer.parseInt(inp[3]), Integer.parseInt(inp[4]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initserver() throws IOException
	{
		ishost = true;
		serverSocket = new ServerSocket(Main.portnum); //put as class private variable
        Socket clientSocket = serverSocket.accept();//and then close when reset. 20398403984093840983
        connected = true;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        pin = new PushbackInputStream(clientSocket.getInputStream());
        multiplayertime = System.currentTimeMillis() + MULTIPLAYERGAMETIME;
        write(false);
	}
	
	public void initclient() throws IOException
	{
		ishost = false;
		//hostnum = "192.168.1.9"; //"192.168.1.1";
		server = new Socket(hostnum, Main.portnum);
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out = new PrintWriter(server.getOutputStream(), true);
        pin = new PushbackInputStream(server.getInputStream());
        connected = true;
        multiplayertime = System.currentTimeMillis() + MULTIPLAYERGAMETIME;
        write(false);
	}
	
	/*
	 * Note for polling: (CHECK IF PIN.AVAILABLE() AND SET CONNECTED = TRUE
	 * Will send the following in the following order: (each thing separated by space)
	 * 1. int - the score of the sender.
	 * 2. sender's position, x and y separated by space.
	 * 3. opponent's new block added. if -1, no change.
	 */
	
	public void poll()
	{
		try {
			if (pin.available() != 0)
			{
				read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mousePos.x = e.getX() - 6;
		mousePos.y = e.getY() - 29;
	}
	
	public void resetcharacter()
	{
		player.x = Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2);
		player.y = 0;
		prevplayer.x = Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2);
		prevplayer.y = 0;
		velocity.y = 0;
		Random rand = new Random();
		if (rand.nextInt(2) == 0)
			velocity.x = 5;
		else
			velocity.x = -5;
	}
}