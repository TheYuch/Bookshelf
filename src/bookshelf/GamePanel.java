package bookshelf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GamePanel extends JPanel implements ActionListener {

	private JButton singlebutton;
	private JButton multibutton;
	private JButton hostbutton;
	private JButton clientbutton;
	private JLabel title;
	private JLabel scorelabel;
	private JLabel opponentscore;
	private JLabel waitingforclient;
	private JLabel typeip;
	private JTextField ipinput;
	
	public static boolean start = false; //start of game
	
	public static int startnum = -1;
	
	public GamePanel()
	{
		this.setLayout(null);
		this.setFocusable(true);
    	this.requestFocusInWindow();
		
		singlebutton = new JButton("Single Player");
		singlebutton.setBounds(Main.WIN_WIDTH / 2 - 125, Main.WIN_HEIGHT / 2 - 80, 250, 60);
		singlebutton.setFont(new Font("Serif", Font.PLAIN, 40));
		singlebutton.addActionListener(this);
		singlebutton.setVisible(true);
		singlebutton.setBackground(Color.LIGHT_GRAY);
		this.add(singlebutton);
		
		multibutton = new JButton("Multi Player");
		multibutton.setBounds(Main.WIN_WIDTH / 2 - 125, Main.WIN_HEIGHT / 2 + 20, 250, 60);
		multibutton.setFont(new Font("Serif", Font.PLAIN, 40));
		multibutton.addActionListener(this);
		multibutton.setVisible(true);
		multibutton.setBackground(Color.LIGHT_GRAY);
		this.add(multibutton);
		
		//multiplayer buttons
		hostbutton = new JButton("Host Server");
		hostbutton.setBounds(Main.WIN_WIDTH / 2 - 125, Main.WIN_HEIGHT / 2 - 80, 250, 60);
		hostbutton.setFont(new Font("Serif", Font.PLAIN, 40));
		hostbutton.addActionListener(this);
		hostbutton.setVisible(false);
		hostbutton.setBackground(Color.LIGHT_GRAY);
		this.add(hostbutton);
		clientbutton = new JButton("Join Server");
		clientbutton.setBounds(Main.WIN_WIDTH / 2 - 125, Main.WIN_HEIGHT / 2 + 20, 250, 60);
		clientbutton.setFont(new Font("Serif", Font.PLAIN, 40));
		clientbutton.addActionListener(this);
		clientbutton.setVisible(true);
		clientbutton.setBackground(Color.LIGHT_GRAY);
		this.add(clientbutton);
		
		title = new JLabel("Bookshelf");
    	title.setFont(new Font("Berlin Sans FB", Font.BOLD, 75));
    	title.setForeground(Color.BLACK);
    	title.setBounds(Main.WIN_WIDTH / 2 - 200, 70, 400, 100);
    	title.setHorizontalAlignment(SwingConstants.CENTER);
    	title.setVerticalAlignment(SwingConstants.CENTER);
    	title.setVisible(true);
    	this.add(title);
    	
    	scorelabel = new JLabel("Score: " + GameState.score);
    	scorelabel.setFont(new Font("Berlin Sans FB", Font.PLAIN, 40));
    	scorelabel.setForeground(Color.BLACK);
    	scorelabel.setBounds(0, Main.WIN_HEIGHT - 150, Main.WIN_WIDTH, 60);
    	scorelabel.setHorizontalAlignment(SwingConstants.CENTER);
    	scorelabel.setVerticalAlignment(SwingConstants.CENTER);
    	scorelabel.setVisible(true);
    	this.add(scorelabel);
    	
    	opponentscore = new JLabel("Opponent's Score: " + GameState.oscore);
    	opponentscore.setFont(new Font("Berlin Sans FB", Font.PLAIN, 40));
    	opponentscore.setForeground(Color.RED);
    	opponentscore.setBounds(0, 0, Main.WIN_WIDTH, 60);
    	opponentscore.setHorizontalAlignment(SwingConstants.RIGHT);
    	opponentscore.setVerticalAlignment(SwingConstants.CENTER);
    	opponentscore.setVisible(false);
    	this.add(opponentscore);
    	
    	waitingforclient = new JLabel("Waiting for client...");
    	waitingforclient.setFont(new Font("Impact", Font.ITALIC, 70));
    	waitingforclient.setForeground(Color.BLUE);
    	waitingforclient.setBounds(0, Main.WIN_HEIGHT / 2 - 40, Main.WIN_WIDTH, 80);
    	waitingforclient.setHorizontalAlignment(SwingConstants.CENTER);
    	waitingforclient.setVerticalAlignment(SwingConstants.CENTER);
    	waitingforclient.setVisible(false);
    	this.add(waitingforclient);
    	
    	typeip = new JLabel("Type in IP of host below:");
    	typeip.setFont(new Font("Berlin Sans FB", Font.PLAIN, 50));
    	typeip.setForeground(Color.BLUE);
    	typeip.setBounds(0, Main.WIN_HEIGHT / 2 - 90, Main.WIN_WIDTH, 60);
    	typeip.setHorizontalAlignment(SwingConstants.CENTER);
    	typeip.setVerticalAlignment(SwingConstants.CENTER);
    	typeip.setVisible(false);
    	this.add(typeip);
    	
    	ipinput = new JTextField();
    	ipinput.setFont(new Font("Calibri", Font.PLAIN, 50));
    	ipinput.setForeground(Color.BLACK);
    	ipinput.setBackground(Color.WHITE);
    	ipinput.setBounds(Main.WIN_WIDTH / 2 - 175, Main.WIN_HEIGHT / 2 - 35, 350, 70);
    	ipinput.addActionListener(this);
    	ipinput.setEditable(true);
    	ipinput.setVisible(false);
    	this.add(ipinput);
	}
	
	private void reset()
	{
		GameState.multiplayer = false;
		start = false; 
		singlebutton.setVisible(true);
		multibutton.setVisible(true);
		startnum = -1;
		Main.dead = false;
		Main.state.reset();
		title.setVisible(true);
		opponentscore.setVisible(false);
		scorelabel.setText("Score: " + GameState.score);
		scorelabel.setLocation(0, Main.WIN_HEIGHT - 150);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		if (GameState.connected && System.currentTimeMillis() > GameState.multiplayertime)
		{
			reset();
			if (GameState.score > GameState.oscore)
				scorelabel.setText("You won! Score: " + GameState.score + " Opponent: " + GameState.oscore);
			else if (GameState.score < GameState.oscore)
				scorelabel.setText("Opponent won. Score: " + GameState.score + " Opponent: " + GameState.oscore);
			else
				scorelabel.setText("Tie! Score: " + GameState.score);
		}
		if (Main.dead) 
		{ 
			if (GameState.connected)
			{
				if (System.currentTimeMillis() <= GameState.multiplayertime)
				{
					Main.dead = false;
					GameState.score --;
					Main.state.resetcharacter();
				}
			}
			else
				reset();
		}
		if (startnum > 0)
		{
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Serif", Font.PLAIN, 300));
			g.drawString(Integer.toString(startnum), (Main.WIN_WIDTH / 2) - 75, Main.WIN_HEIGHT / 2 + 100);
		}
		if (startnum == 0)
		{
			/*
			 * Draw the grid
			 */
			g.setColor(Color.LIGHT_GRAY);
			for (int i = 1; i < GameState.GRIDS_X; i++)
			{
				g.fillRect((i * GameState.constantx) - (GameState.GRID_THICKNESS / 2), 0, GameState.GRID_THICKNESS, Main.WIN_HEIGHT); 
				//rectangles act as thicker lines
			}
			for (int i = 1; i < GameState.GRIDS_Y; i ++)
			{
				g.fillRect(0, (i * GameState.constanty) - (GameState.GRID_THICKNESS / 2), Main.WIN_WIDTH, GameState.GRID_THICKNESS);
			}
			
			/*
			 * draw obstacles
			 */
			for (int i = 0; i < GameState.GRIDS_X; i ++)
			{
				for (int j = 0; j < GameState.GRIDS_Y; j ++)
				{
					if (GameState.obstacles[i][j] == 1)
					{
						g.setColor(Color.GRAY);
						g.fillRect(i * GameState.constantx, j * GameState.constanty, GameState.constantx, GameState.constanty);
					}
				}
			}
			
			/*
			 * Draw the player(s)
			 */
			
			if (GameState.connected) 
			{
				Point2D.Double opponent = GameState.getOpponent();
				g.setColor(Color.RED);
				g.fillOval((int)opponent.x, (int)opponent.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
			}
			
			g.setColor(Color.BLUE);
			Point2D.Double player = GameState.getPlayer();
			g.fillOval((int)player.x, (int)player.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
			
			/*
			 * Draw end
			 */
			g.setColor(Color.YELLOW);
			g.fillRect(GameState.end.x * GameState.constantx, GameState.end.y * GameState.constanty, GameState.constantx, GameState.constanty);
		
			//draw score
			scorelabel.setText(Integer.toString(GameState.score));
			if (GameState.connected)
			{
				opponentscore.setText("Opponent: " + GameState.oscore);
			}
		}
	}
	
	private void start()
	{
		GameState.score = 0;
		scorelabel.setText("0");
		start = true;
		singlebutton.setVisible(false);
		multibutton.setVisible(false);
		title.setVisible(false);
		startnum = 3;
		scorelabel.setLocation(0, 0); //will be in middle b/c of alignment
		hostbutton.setVisible(false);
		clientbutton.setVisible(false);
	}
	
	public void multiplayerstart()
	{
		waitingforclient.setVisible(false);
		GameState.score = 0;
		GameState.oscore = 0;
		startnum = 3;
		title.setVisible(false);
		start = true;
		scorelabel.setLocation(-(Main.WIN_WIDTH / 2) + 75, 0); //will be in middle b/c of alignment
		opponentscore.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) { //only abuse main, not AWT thread. no thread.sleep
		//or calling initserver/poll.
		if (e.getSource() == singlebutton)
		{
			start();
		}
		else if (e.getSource() == multibutton)
		{
			GameState.multiplayer = true;
			singlebutton.setVisible(false);
			multibutton.setVisible(false);
			//buttons to choose host or client, then start. Will give player their IP address
			//and will allow player to type in IP to connect to.
			hostbutton.setVisible(true);
			clientbutton.setVisible(true);
		}
		else if (e.getSource() == hostbutton)
		{
			hostbutton.setVisible(false);
			clientbutton.setVisible(false);
			title.setVisible(false);
			waitingforclient.setVisible(true);
			Main.init = 1;
			GameState.score = 0;
			GameState.oscore = 0;
		}
		else if (e.getSource() == clientbutton)
		{
			hostbutton.setVisible(false);
			clientbutton.setVisible(false);
			ipinput.setVisible(true);
			typeip.setVisible(true);
		}
		String text = ipinput.getText().trim();
		if (text.length() > 1)
		{
			ipinput.setVisible(false);
			typeip.setVisible(false);
			Main.state.hostnum = text;
			Main.init = 2;
			GameState.score = 0;
			GameState.oscore = 0;
		}
	}
}