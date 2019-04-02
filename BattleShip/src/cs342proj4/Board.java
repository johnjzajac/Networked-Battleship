package cs342proj4;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class Board {
	final String[] images = new String[] {"1.jpg","2.jpg","3.jpg","4.jpg","5.jpg",
            "6.jpg","7.jpg","8.jpg","9.jpg","10.jpg",
            "11.jpg","12.jpg","13.jpg","14.jpg","15.jpg","16.jpg",""};
	
	
	final String[] shipImages = new String[] {"batt1.gif","batt2.gif","batt3.gif","batt4.gif"
			,"batt5.gif","batt6.gif","batt7.gif","batt8.gif","batt9.gif","batt10.gif"};
	final String[] hitImages = new String[] {"batt201.gif","batt202.gif","batt203.gif","batt204.gif","batt205.gif","batt206.gif"};
	final String[] waterImages = new String[] {"batt100.gif","batt101.gif","batt102.gif","batt103.gif"};
	private Icon waterIcon[];
	private Icon hitIcon[];
	private JButton clickedShip;
	private JButton pieceClicked;
	private int numPatrol=0,numSubmarine=0,numDestroyer=0,numBattleship=0,numAircraft=0;
	private Icon iconArr[];
	private JPanel shipChoices;
	private JTextField num;
	private boolean isHorizontal = false;
	private boolean isVertical = false;
	private int numBoat = 0;
	private JButton[] shipButtons = new JButton[5];
	private List<String> shipNames = Arrays.asList("Patrol Boat", "Submarine", "Destroyer", "Battleship","Aircraft Carrier");

	public final int boardside = 11;
	final List<String> numbers = Arrays.asList(" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
	final List<String> letters = Arrays.asList(" ","A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
	private Socket clientSocket;
	private ServerSocket battleshipServer;
	private Socket serverSocket;
	JButton [][] userButtons = new JButton [11][11];
	JButton [][] opponentButtons = new JButton [11][11];
	JFrame frame;
	JPanel userboard;
	JPanel oppboard;
	JPanel statusbar;
	JPanel shipbar;
	JMenuBar menubar;
	JMenu filemenu;
	JMenu helpmenu;
	JMenu connectmenu;
	JMenu statmenu;
	
	private boolean gameStarted = false;
	private double numShots = 0.0;
	private double numHit = 0.0;
	private double numMiss = 0.0;
	private BufferedReader bufferReceived;
	private DataOutputStream bufferSent;
	String clientString;
	String serverString;
	public Board() {
		// frame init
		frame = new JFrame("Battleship");
		frame.setLayout(new BorderLayout());
		
		statusbar = new JPanel();
		initStatusBar(statusbar);
		frame.add(statusbar, BorderLayout.NORTH);
		
		// oppboard init
		oppboard = new JPanel();
		oppboard.setLayout(new GridLayout(boardside, boardside));
		initBoard(oppboard, 40,statusbar);
		oppboard.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.add(oppboard, BorderLayout.EAST);
		
		
		// user board init
		userboard = new JPanel();
		userboard.setLayout(new GridLayout(boardside, boardside));
		initUserBoard(userboard, 40,userButtons,statusbar,opponentButtons);
		userboard.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.add(userboard, BorderLayout.CENTER);
	
		
		// menubar init
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		// file menu item init
		filemenu = new JMenu("File");
		menubar.add(filemenu);
		initFileMenu(filemenu);
		
		// help menu item init
		helpmenu = new JMenu("Help");
		menubar.add(helpmenu);
		initHelpMenu(helpmenu);
		
		// connection menu item init
		connectmenu = new JMenu("Connect");
		menubar.add(connectmenu);
		initConnectMenu(connectmenu);

		// statistics menu item init
		statmenu = new JMenu("Statistics");
		menubar.add(statmenu);
		initStatMenu(statmenu);
		
		
		shipbar = new JPanel();
		initShipBar(shipbar);
		frame.add(shipbar, BorderLayout.SOUTH);
		
		initFrame(frame);
		}
	
	
	public void initFrame (JFrame frameX)
	{
		frameX.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameX.setSize(1000,700);
		frameX.setVisible(true);
	}
	
	
	private int hitX =0;
	private int hitY = 0;
	public void initBoard (JPanel panelX, int buttonSize,JPanel statusbar) 
	{
		JRadioButton yourTurn = (JRadioButton) statusbar.getComponent(3);
		JRadioButton opponentTurn = (JRadioButton) statusbar.getComponent(4);	
		Color custom = new Color(100,149,237);
		int k = 0;
		char c = 64;
		for (int i = 0; i < boardside; i++)
		{
			for (int j = 0; j < boardside; j++)
			{
				JButton button = new JButton();
				button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				// sets the side row of numbers on each grid
				if (j == 0)
				{
					if (k != 0)
					button.setText(k + "");
					k++;
					button.setBackground(Color.white);
				}
				else if(j!=0){
					button.setBackground(custom);
				}
				
				// sets the top row of letters on each grid
				if (i == 0)
				{
					if (c != 64)
					button.setText(c + "");
					c++;
					button.setBackground(Color.white);
				}
				else if(i!=0 && j!=0){
					button.setBackground(custom);
				}
				int curI = i;
				int curJ = j;
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (yourTurn.isSelected() == true) {
						try {
							bufferSent.writeBytes((curI) + " " + curJ + "\n");
							bufferSent.flush();
							hitX = curI;
							hitY = curJ;
							numShots++;
							yourTurn.setSelected(false);
							opponentTurn.setSelected(true);
							
						} catch (IOException e1) {
						}
						
						}
						else {
							JOptionPane.showMessageDialog(frame.getComponent(0), "It's not your turn!");
						}
					}
					});
				button.setPreferredSize(new Dimension(buttonSize, buttonSize));
				opponentButtons[i][j] = button;
				panelX.add(opponentButtons[i][j]);
			}
		}
	}
	
	
	public void initUserBoard (JPanel panelX, int buttonSize,JButton[][] buttons,JPanel panelY,JButton[][] opponentButtons) 
	{
		JCheckBox placed =(JCheckBox) panelY.getComponent(2);
		JRadioButton yourTurn = (JRadioButton) panelY.getComponent(3);
		JRadioButton opponentTurn = (JRadioButton) panelY.getComponent(4);
		JCheckBox iAmServer = (JCheckBox) panelY.getComponent(0);
		JCheckBox iAmClient = (JCheckBox) panelY.getComponent(1);
		JCheckBox hitBox = (JCheckBox) panelY.getComponent(5);
		JCheckBox missBox = (JCheckBox) panelY.getComponent(6);
		JCheckBox gameOver = (JCheckBox) panelY.getComponent(7);
		JCheckBox waiting = (JCheckBox) panelY.getComponent(8);
		Color custom = new Color(100,149,237);
		placed.setSelected(true);
		int k = 0;
		char c = 64;
		for (int i = 0; i < boardside; i++)
		{
			for (int j = 0; j < boardside; j++)
			{
				JButton button = new JButton();
				button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				// sets the side row of numbers on each grid
				if (j == 0)
				{
					if (k != 0)
					button.setText(k + "");
					k++;
					button.setBackground(Color.white);
				}
				else if(j!=0){
					button.setBackground(custom);
				}
				
				// sets the top row of letters on each grid
				if (i == 0)
				{
					if (c != 64)
					button.setText(c + "");
					c++;
					button.setBackground(Color.white);
				}
				else if(i!=0 && j!=0){
					button.setBackground(custom);
				}
				
				button.setPreferredSize(new Dimension(buttonSize, buttonSize));
				
				iconArr = new Icon[shipImages.length];
				for(int m = 0;m<iconArr.length;m++) {
					iconArr[m] = new ImageIcon(shipImages[m]);
				}
				waterIcon = new Icon[waterImages.length];
				for(int m = 0;m<waterIcon.length;m++) {
					waterIcon[m] = new ImageIcon(waterImages[m]);
				}
				hitIcon = new Icon[hitImages.length];
				for(int m = 0;m<hitIcon.length;m++) {
					hitIcon[m] = new ImageIcon(hitImages[m]);
				}
				
				buttons[i][j] = button;
				button.setName("");
				int curI = i;
				int curJ = j;
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pieceClicked = (JButton)e.getSource();
						if(clickedShip != null && pieceClicked.getIcon() == null ) {
						if(clickedShip.getText() == "Patrol Boat" && numBoat < 5 && numPatrol != 1) {						
							setPatrol(buttons,iconArr,curI,curJ);
						}
						
						if(clickedShip.getText() == "Submarine" && numBoat < 5 &&numSubmarine != 1) {
							setSubmarine(buttons,iconArr,curI,curJ);
						}
						
						if(clickedShip.getText() == "Destroyer"&& numBoat < 5 && numDestroyer!=1) {
							setDestroyer(buttons,iconArr,curI,curJ);
						}
						
						if(clickedShip.getText() == "Battleship"&& numBoat < 5&&numBattleship !=1) {
							setBattleship(buttons,iconArr,curI,curJ);
						}
						
						if(clickedShip.getText() == "Aircraft Carrier"&& numBoat < 5&&numAircraft != 1) {
							setAircraft(buttons,iconArr,curI,curJ);
						}
						}
						if(numBoat == 5 && gameStarted ==false) {
							//JOptionPane.showMessageDialog(null, "All ships are placed onto the grid. Connecting..");
							gameStarted = true;
							placed.setSelected(false);
							yourTurn.setSelected(true);
							waiting.setSelected(true);
							try {
								/*--------------Client side of the battleship--------------*/
								clientSocket = new Socket("127.0.0.1",6789);
								JOptionPane.showMessageDialog(frame.getComponent(0),"Second to connect. I am the client !!");
								iAmClient.setSelected(true);
								iAmServer.setSelected(false);
								yourTurn.setSelected(false);
								opponentTurn.setSelected(true);
								bufferReceived = new BufferedReader(
										new InputStreamReader(clientSocket.getInputStream()));
								bufferSent = new DataOutputStream(clientSocket.getOutputStream());
								System.out.println("-----------Client Side----------");				
								Runnable r = new Runnable() {
									public void run() {
										while(true) {
											try {
												boolean hit = false;
												String input = bufferReceived.readLine();
												//System.out.println(input+"------------length of " + input.length());
												if(input.length() == 4 || input.length() == 5 || input.length() == 3) {
												String[] parsed = input.split(" ");
												if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalEnd") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[0]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalMid") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[1]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalFront") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[2]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalEnd") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[3]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalMid") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[4]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
													
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalFront") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[5]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[2]);
													
													hit = false;
													
												}
												
												yourTurn.setSelected(true);
												opponentTurn.setSelected(false);
												//input = bufferReceived.readLine();
												if(hit == true) {
													bufferSent.writeBytes("hitship" + "\n");
													bufferSent.flush();
													
												}
												else if(hit == false) {
													bufferSent.writeBytes("missship" + "\n");
													bufferSent.flush();
													
												}
												}
												else if (input.length() == 7){
												
													opponentButtons[hitX][hitY].setIcon(waterIcon[3]);
													yourTurn.setSelected(false);
													opponentTurn.setSelected(true);
													numHit = numHit + 1.0;
													hitBox.setSelected(true);
													missBox.setSelected(false);
													
													if(numHit == 17.0) {
														
														gameOver.setSelected(true);
														JOptionPane.showMessageDialog(frame.getComponent(0), "Game over!You won!!");
														hitBox.setSelected(false);
														missBox.setSelected(true);
														gameOver.setSelected(true);
													}
												}
												else if (input.length() == 8){
													opponentButtons[hitX][hitY].setIcon(waterIcon[2]);
													yourTurn.setSelected(false);
													opponentTurn.setSelected(true);
													numMiss = numMiss + 1.0;
													hitBox.setSelected(false);
													missBox.setSelected(true);
												}
											
												
											} catch (IOException e) {
												// TODO Auto-generated catch block
												//e.printStackTrace();
											}
											
										}
									}
								};
								new Thread(r).start();
							} catch (UnknownHostException e1) {
								System.out.println("unknown host");
								
								e1.printStackTrace();
							} catch (IOException e1) {
								/*--------------Server side of the battleship---------------*/
								JOptionPane.showMessageDialog(frame.getComponent(0),"First one to connect. I am the server !! Waiting for client to connect");
								iAmClient.setSelected(false);
								iAmServer.setSelected(true);
								yourTurn.setSelected(true);
								opponentTurn.setSelected(false);
								try {
									battleshipServer = new ServerSocket(6789);
									battleshipServer.setSoTimeout(10000);
									serverSocket = battleshipServer.accept();
									JOptionPane.showMessageDialog(frame.getComponent(0),"Client has connected");
									System.out.println("-----------Server Side----------");
									//----starting game----
									bufferReceived = new BufferedReader(
											new InputStreamReader(serverSocket.getInputStream()));
									bufferSent = new DataOutputStream(serverSocket.getOutputStream());		
									Runnable r = new Runnable() {
										public void run() {
											while(true) {
												try {boolean hit = false;
												String input = bufferReceived.readLine();
												System.out.println(input+"------------");
												if(input.length() == 4 || input.length() == 5 || input.length() == 3) {
												String[] parsed = input.split(" ");
												if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalEnd") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[0]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalMid") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[1]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "HorizontalFront") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[2]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalEnd") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[3]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalMid") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[4]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
													
												}
												else if(buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].getName() == "VerticalFront") {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(hitIcon[5]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													
													hit = true;
												}
												else {
													buttons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[3]);
													//opponentButtons[Integer.parseInt(parsed[0])][Integer.parseInt(parsed[1])].setIcon(waterIcon[2]);
													
													hit = false;
													
												}
												
												yourTurn.setSelected(true);
												opponentTurn.setSelected(false);
												//input = bufferReceived.readLine();
												if(hit == true) {
													bufferSent.writeBytes("hitship" + "\n");
													bufferSent.flush();
													
												}
												else if(hit == false) {
													bufferSent.writeBytes("missship" + "\n");
													bufferSent.flush();
													
												}
												}
												
													else if (input.length() == 7){
														//System.out.println("got here");
													
														opponentButtons[hitX][hitY].setIcon(waterIcon[3]);
														yourTurn.setSelected(false);
														opponentTurn.setSelected(true);
														numHit = numHit + 1.0;
														if(numHit == 17.0) {
															//opponentButtons[hitX][hitY].setIcon(waterIcon[3]);
															JOptionPane.showMessageDialog(frame.getComponent(0), "Game over!You won!!");
															hitBox.setSelected(false);
															missBox.setSelected(true);
															gameOver.setSelected(true);
														}
														hitBox.setSelected(true);
														missBox.setSelected(false);
													}
													else if (input.length() == 8){
														opponentButtons[hitX][hitY].setIcon(waterIcon[2]);
														yourTurn.setSelected(false);
														opponentTurn.setSelected(true);
														numMiss = numMiss + 1.0;
														hitBox.setSelected(false);
														missBox.setSelected(true);
													}
													
												
												} catch (IOException e) {
													// TODO Auto-generated catch block
													//e.printStackTrace();
												}
												
											}
										}
									};
									new Thread(r).start();

								} catch (IOException e2) {
									System.out.println("Other application have connected to this socket.");
									
								}
							}
						}
						else if (gameStarted == true){
							JOptionPane.showMessageDialog(frame.getComponent(0),"Game has started. You can't add more ships onto your board!");
						}
					}
				});
				panelX.add(buttons[i][j]);
			}
		}
	}
	public void setImage(JButton[][] buttons,Icon[] iconArr,int i,int j,int imageIndex) {
		buttons[i][j].setIcon(iconArr[imageIndex]);
	}
	
	public void setEmpty(JButton[][] buttons,int curI,int curJ) {
		try {
		buttons[curI][curJ].setIcon(null);
		}
		catch (Exception ArrayIndexOutOfBoundsException) {
			
		}
	}
	
	public void setAircraft(JButton[][] buttons, Icon[] iconArr, int curI,int curJ) {
		if(isHorizontal && curI > 0 && curJ >0 ) {
			try {
				if(horizontalOccupiedPositive(buttons,curI,curJ,5) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("HorizontalEnd");
				buttons[curI][curJ+1].setName("HorizontalMid");
				buttons[curI][curJ+2].setName("HorizontalMid");
				buttons[curI][curJ+3].setName("HorizontalMid");
				buttons[curI][curJ+4].setName("HorizontalFront");
				
				setImage(buttons,iconArr,curI,curJ,0);
				setImage(buttons,iconArr,curI,curJ+1,1);
				setImage(buttons,iconArr,curI,curJ+2,2);
				setImage(buttons,iconArr,curI,curJ+3,3);
				setImage(buttons,iconArr,curI,curJ+4,4);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(horizontalOccupiedNegative(buttons,curI,curJ,5) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI,curJ+1);
				setEmpty(buttons,curI,curJ+2);
				setEmpty(buttons,curI,curJ+3);
				setEmpty(buttons,curI,curJ+4);
				
				buttons[curI][curJ-4].setName("HorizontalEnd");
				buttons[curI][curJ-3].setName("HorizontalMid");
				buttons[curI][curJ-2].setName("HorizontalMid");
				buttons[curI][curJ-1].setName("HorizontalMid");
				buttons[curI][curJ].setName("HorizontalFront");
				
				setImage(buttons,iconArr,curI,curJ-4,0);
				setImage(buttons,iconArr,curI,curJ-3,1);
				setImage(buttons,iconArr,curI,curJ-2,2);
				setImage(buttons,iconArr,curI,curJ-1,3);
				setImage(buttons,iconArr,curI,curJ,4);	
			}
		}
		else if(isVertical && curI > 0 && curJ >0 ){
			try {
				if(verticalOccupiedPositive(buttons,curI,curJ,5) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("VerticalEnd");
				buttons[curI+1][curJ].setName("VerticalMid");
				buttons[curI+2][curJ].setName("VerticalMid");
				buttons[curI+3][curJ].setName("VerticalMid");
				buttons[curI+4][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI,curJ,5);
				setImage(buttons,iconArr,curI+1,curJ,6);
				setImage(buttons,iconArr,curI+2,curJ,7);
				setImage(buttons,iconArr,curI+3,curJ,8);
				setImage(buttons,iconArr,curI+4,curJ,9);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(verticalOccupiedNegative(buttons,curI,curJ,5) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI + 1,curJ);
				setEmpty(buttons,curI + 2,curJ);
				setEmpty(buttons,curI + 3,curJ);
				setEmpty(buttons,curI + 4,curJ);
				
				buttons[curI-4][curJ].setName("VerticalEnd");
				buttons[curI-3][curJ].setName("VerticalMid");
				buttons[curI-2][curJ].setName("VerticalMid");
				buttons[curI-1][curJ].setName("VerticalMid");
				buttons[curI][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI - 4,curJ,5);
				setImage(buttons,iconArr,curI - 3,curJ,6);
				setImage(buttons,iconArr,curI - 2,curJ,7);
				setImage(buttons,iconArr,curI - 1,curJ,8);
				setImage(buttons,iconArr,curI ,curJ,9);	
			}
			}
		
		numBoat++;
		numAircraft++;
		num.setText("Number of Boats: " + numBoat);
	}
	

	
	public void setBattleship(JButton[][] buttons, Icon[] iconArr, int curI,int curJ) {
		if (curI <= 0 && curJ <=0){
			JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place your ship there!");
			return;	
		}
			else if(isHorizontal && curI > 0 && curJ >0 ) {
				try {
					if(horizontalOccupiedPositive(buttons,curI,curJ,4) == false) {
						JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
						return;
					}
					buttons[curI][curJ].setName("HorizontalEnd");
					buttons[curI][curJ+1].setName("HorizontalMid");
					buttons[curI][curJ+2].setName("HorizontalMid");
					buttons[curI][curJ+3].setName("HorizontalFront");
					
					setImage(buttons,iconArr,curI,curJ,0);
					setImage(buttons,iconArr,curI,curJ+1,1);
					setImage(buttons,iconArr,curI,curJ+2,2);
					setImage(buttons,iconArr,curI,curJ+3,4);
				}
				catch (Exception ArrayIndexOutOfBoundsException) {
					if(horizontalOccupiedNegative(buttons,curI,curJ,4) == false) {
						JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
						return;
					}
					setEmpty(buttons,curI,curJ);
					setEmpty(buttons,curI,curJ+1);
					setEmpty(buttons,curI,curJ+2);
					setEmpty(buttons,curI,curJ+3);
					
					buttons[curI][curJ-3].setName("HorizontalEnd");
					buttons[curI][curJ-2].setName("HorizontalMid");
					buttons[curI][curJ-1].setName("HorizontalMid");
					buttons[curI][curJ].setName("HorizontalFront");
					
					setImage(buttons,iconArr,curI,curJ-3,0);
					setImage(buttons,iconArr,curI,curJ-2,1);
					setImage(buttons,iconArr,curI,curJ-1,2);
					setImage(buttons,iconArr,curI,curJ,4);	
				}
			}
			else if(isVertical && curI > 0 && curJ >0 ){
				try {
					if(verticalOccupiedPositive(buttons,curI,curJ,4) == false) {
						JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
						return;
					}
					buttons[curI][curJ].setName("VerticalEnd");
					buttons[curI+1][curJ].setName("VerticalMid");
					buttons[curI+2][curJ].setName("VerticalMid");
					buttons[curI+3][curJ].setName("VerticalFront");
					
					setImage(buttons,iconArr,curI,curJ,5);
					setImage(buttons,iconArr,curI+1,curJ,6);
					setImage(buttons,iconArr,curI+2,curJ,7);
					setImage(buttons,iconArr,curI+3,curJ,9);
				}
				catch (Exception ArrayIndexOutOfBoundsException) {
					if(verticalOccupiedNegative(buttons,curI,curJ,4) == false) {
						JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
						return;
					}
					setEmpty(buttons,curI,curJ);
					setEmpty(buttons,curI + 1,curJ);
					setEmpty(buttons,curI + 2,curJ);
					setEmpty(buttons,curI + 3,curJ);
					
					buttons[curI-3][curJ].setName("VerticalEnd");
					buttons[curI-2][curJ].setName("VerticalMid");
					buttons[curI-1][curJ].setName("VerticalMid");
					buttons[curI][curJ].setName("VerticalFront");
					
					setImage(buttons,iconArr,curI - 3,curJ,5);
					setImage(buttons,iconArr,curI - 2,curJ,6);
					setImage(buttons,iconArr,curI - 1,curJ,7);
					setImage(buttons,iconArr,curI ,curJ,9);	
				}
				}
			
			numBoat++;
			numBattleship++;
			num.setText("Number of Boats: " + numBoat);
		}
	
	public void setDestroyer(JButton[][] buttons,Icon[] iconArr, int curI,int curJ) {
		if(isHorizontal && curI > 0 && curJ >0 ) {
			try {
				if(horizontalOccupiedPositive(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("HorizontalEnd");
				buttons[curI][curJ+1].setName("HorizontalMid");
				buttons[curI][curJ+2].setName("HorizontalFront");
				setImage(buttons,iconArr,curI,curJ,0);
				setImage(buttons,iconArr,curI,curJ+1,1);
				setImage(buttons,iconArr,curI,curJ+2,4);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(horizontalOccupiedNegative(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI,curJ+1);
				setEmpty(buttons,curI,curJ+2);
				
				buttons[curI][curJ-2].setName("HorizontalEnd");
				buttons[curI][curJ-1].setName("HorizontalMid");
				buttons[curI][curJ].setName("HorizontalFront");
				
				setImage(buttons,iconArr,curI,curJ-2,0);
				setImage(buttons,iconArr,curI,curJ-1,1);
				setImage(buttons,iconArr,curI,curJ,4);	
			}
		}
		else if(isVertical && curI > 0 && curJ >0 ){
			try {
				if(verticalOccupiedPositive(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("VerticalEnd");
				buttons[curI+1][curJ].setName("VerticalMid");
				buttons[curI+2][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI,curJ,5);
				setImage(buttons,iconArr,curI+1,curJ,6);
				setImage(buttons,iconArr,curI+2,curJ,9);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(verticalOccupiedNegative(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI + 1,curJ);
				setEmpty(buttons,curI + 2,curJ);
				
				buttons[curI-2][curJ].setName("VerticalEnd");
				buttons[curI-1][curJ].setName("VerticalMid");
				buttons[curI][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI - 2,curJ,5);
				setImage(buttons,iconArr,curI - 1,curJ,6);
				setImage(buttons,iconArr,curI ,curJ,9);	
			}
			}
			numBoat++;
			numDestroyer++;
			num.setText("Number of Boats: " + numBoat);
		}
	
	
	public void setSubmarine(JButton[][] buttons,Icon[] iconArr,int curI,int curJ) {
		if(isHorizontal && curI > 0 && curJ >0 ) {
			try {
				if(horizontalOccupiedPositive(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("HorizontalEnd");
				buttons[curI][curJ+1].setName("HorizontalMid");
				buttons[curI][curJ+2].setName("HorizontalFront");
				setImage(buttons,iconArr,curI,curJ,0);
				setImage(buttons,iconArr,curI,curJ+1,1);
				setImage(buttons,iconArr,curI,curJ+2,4);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(horizontalOccupiedNegative(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI,curJ+1);
				setEmpty(buttons,curI,curJ+2);
				
				buttons[curI][curJ-2].setName("HorizontalEnd");
				buttons[curI][curJ-1].setName("HorizontalMid");
				buttons[curI][curJ].setName("HorizontalFront");
				
				setImage(buttons,iconArr,curI,curJ-2,0);
				setImage(buttons,iconArr,curI,curJ-1,1);
				setImage(buttons,iconArr,curI,curJ,4);	
			}
		}
		else if(isVertical && curI > 0 && curJ >0 ){
			try {
				if(verticalOccupiedPositive(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("VerticalEnd");
				buttons[curI+1][curJ].setName("VerticalMid");
				buttons[curI+2][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI,curJ,5);
				setImage(buttons,iconArr,curI+1,curJ,6);
				setImage(buttons,iconArr,curI+2,curJ,9);
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				if(verticalOccupiedNegative(buttons,curI,curJ,3) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				setEmpty(buttons,curI,curJ);
				setEmpty(buttons,curI + 1,curJ);
				setEmpty(buttons,curI + 2,curJ);
				
				buttons[curI-2][curJ].setName("VerticalEnd");
				buttons[curI-1][curJ].setName("VerticalMid");
				buttons[curI][curJ].setName("VerticalFront");
				
				setImage(buttons,iconArr,curI - 2,curJ,5);
				setImage(buttons,iconArr,curI - 1,curJ,6);
				setImage(buttons,iconArr,curI ,curJ,9);	
			}
			}
		
		numBoat++;
		numSubmarine++;
		num.setText("Number of Boats: " + numBoat);
	}
	
	public boolean horizontalOccupiedPositive(JButton[][] buttons,int curI,int curJ,int length) {
		int cur = 0;
		for(int i = 0;i<length;i++) {
			cur = curJ+i;
			//System.out.println("Current name :" + buttons[curI][cur].getName());
			if (buttons[curI][cur].getName() != "") {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean horizontalOccupiedNegative(JButton[][] buttons,int curI,int curJ,int length) {
		int cur = 0;
		for(int i = 0;i<length;i++) {
			cur = curJ-i;
			//System.out.println("Current name :" + buttons[curI][cur].getName());
			if (buttons[curI][cur].getName() != "") {
				return false;
			}
		}
		return true;
	}
	
	public boolean verticalOccupiedPositive(JButton[][] buttons,int curI,int curJ,int length) {
		int cur = 0;
		for(int i = 0;i<length;i++) {
			cur = curI+i;
			//System.out.println("Current name :" + buttons[curI][cur].getName());
			if (buttons[cur][curJ].getName() != "") {
				return false;
			}
		}
		return true;
	}
	
	public boolean verticalOccupiedNegative(JButton[][] buttons,int curI,int curJ,int length) {
		int cur = 0;
		for(int i = 0;i<length;i++) {
			cur = curI-i;
			//System.out.println("Current name :" + buttons[curI][cur].getName());
			if (buttons[cur][curJ].getName() != "") {
				return false;
			}
		}
		return true;
	}
	
	public void setPatrol(JButton[][] buttons,Icon[] iconArr,int curI,int curJ) {
		if(isHorizontal && curI > 0 && curJ >0 ) {
			try {
				if(horizontalOccupiedPositive(buttons,curI,curJ,2) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("HorizontalEnd");
				buttons[curI][curJ+1].setName("HorizontalFront");
			setImage(buttons,iconArr,curI,curJ,0);
			setImage(buttons,iconArr,curI,curJ+1,4);
				}
		catch (Exception ArrayIndexOutOfBoundsException) {
			if(horizontalOccupiedNegative(buttons,curI,curJ,2) == false) {
				JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
				return;
			}
			buttons[curI][curJ-1].setName("HorizontalEnd");
			buttons[curI][curJ].setName("HorizontalFront");
			setImage(buttons,iconArr,curI,curJ - 1,0);
			setImage(buttons,iconArr,curI,curJ,4);
		}
		}
		if(isVertical && curI > 0 && curJ >0 ){
			try {
				if(verticalOccupiedPositive(buttons,curI,curJ,2) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI][curJ].setName("VerticalEnd");
				buttons[curI+1][curJ].setName("VerticalFront");
			setImage(buttons,iconArr,curI,curJ,5);
			setImage(buttons,iconArr,curI+1,curJ,9);
			}
			catch(Exception ArrayIndexOutOfBoundsException) {
				if(verticalOccupiedNegative(buttons,curI,curJ,2) == false) {
					JOptionPane.showMessageDialog(frame.getComponent(0), "You can't place a ship there!");
					return;
				}
				buttons[curI-1][curJ].setName("VerticalEnd");
				buttons[curI][curJ].setName("VerticalFront");
			setImage(buttons,iconArr,curI - 1,curJ,5);
			setImage(buttons,iconArr,curI,curJ,9);	
			}
		}
		numBoat++;
		numPatrol++;
		num.setText("Number of Boats: " + numBoat);
	}
	
	public boolean isEmpty(JButton[][] buttons,int curI,int curJ) {
		try {
		return (buttons[curI][curJ].getIcon() == null);
		}
		catch (Exception ArrayIndexOutOfBoundsException) {
			return false;
		}
	}
	
	
	public boolean indexValid(int i,int j) {
		System.out.print("\ni index:" + i + "j index: " + j + "boolean: " + ((i > 1 && i < 11) && (j > 1 && j < 11)));
		return (i > 1 && i < 11) && (j > 1 && j < 11);
	}
	public void initFileMenu (JMenu menuX)
	{
		JMenuItem aboutUs = new JMenuItem("About the creators");
		class aboutusaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				JOptionPane.showMessageDialog(frame.getComponent(0), 
						"Created by: \n"
						+ "Dat Nguyen: dnguye50\n"
						+ "Cang Le: cle8\n"
						+ "John Zajac: jzajac4");
			}
		}
		aboutUs.addActionListener(new aboutusaction());
		menuX.add(aboutUs);
		
		JMenuItem quitGame = new JMenuItem("Quit the game");
		class quitaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				System.exit(0);
			}
		}
		quitGame.addActionListener(new quitaction());
		menuX.add(quitGame);
	}
	
	public void initHelpMenu (JMenu menuX)
	{
		JMenuItem how2Connect = new JMenuItem("How to connect");
		class how2connectaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				JOptionPane.showMessageDialog(frame.getComponent(0), 
						"To connect, simply exit out of this window and go\n"
						+ "to the 'Connect' menu in the menu bar and select\n"
						+ "'Connect' under the tab. You will be automatically\n"
						+ "connected with another player.");
			}
		}
		how2Connect.addActionListener(new how2connectaction());
		menuX.add(how2Connect);
		
		JMenuItem how2Battleship = new JMenuItem("How to play Battleship");
		class how2playaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				JOptionPane.showMessageDialog(frame.getComponent(0), 
						"Battleship is a board game for two players who try to guess the \n"
						+ "location of the ships each player hides on a grid that \n"
						+ "can't be seen by his opponent. Players take turns calling out \n"
						+ "a row and column on the other player's grid in an attempt to \n"
						+ "name a square that contains an opponent's ship.\n" 
						+ "Each player receives a board with two grids, five ships, and \n"
						+ "hit and miss markers (white and red pegs). One of the \n"
						+ "grids contains a player's ships and the other is used to \n"
						+ "record shots fired and an opponent's ships and whether they \n"
						+ "hit or missed. The goal of the game is to sink all of the \n"
						+ "opponent's ships by correctly guessing their location.\n");
			}
		}
		how2Battleship.addActionListener(new how2playaction());
		menuX.add(how2Battleship);
	}
	
	public void initConnectMenu (JMenu menuX)
	{
		JCheckBoxMenuItem toggleConnect = new JCheckBoxMenuItem("Check to connect");
		class connectaction implements ActionListener {
			public void actionPerformed (ActionEvent e)
			{
				// have yet to write a connection method
			}
		}
		toggleConnect.addActionListener(new connectaction());
		menuX.add(toggleConnect);
	}
	
	public void initStatMenu (JMenu menuX)
	{
		JMenuItem statbox = new JMenuItem("Display Statistics");
		class stataction implements ActionListener {
			public void actionPerformed (ActionEvent e)
			{
				double percentHit = (numHit/numShots)*100.0;
				double percentMiss = 100.0 - percentHit;
				
				JOptionPane.showMessageDialog(frame.getComponent(0), 
						"Number of hits: " + (int)numHit 
						+ "\nNumber of misses: " + (int)numMiss
						+ "\nPercentage Hit: " + percentHit + "%"
						+ "\nPercentage Miss " + percentMiss + "%"
						+ "\nNumber of shots fired: " + (int)numShots);
			}
		}
		statbox.addActionListener(new stataction());
		menuX.add(statbox);
	}
	
	public void initStatusBar (JPanel barX)
	{	
		JCheckBox connect2server = new JCheckBox("I am the sever");
		connect2server.setSelected(false);
		connect2server.setEnabled(false);
		barX.add(connect2server);
		
		
		JCheckBox connecting2server = new JCheckBox("I am the client");
		connecting2server.setSelected(false);
		connecting2server.setEnabled(false);
		barX.add(connecting2server);
		
		JCheckBox placeships = new JCheckBox("Time to place ships onto grid");
		placeships.setSelected(false);
		placeships.setEnabled(false);
		barX.add(placeships);
		
		ButtonGroup group = new ButtonGroup();
		JRadioButton yourturn = new JRadioButton("your turn");
		JRadioButton oppturn = new JRadioButton("opponent's turn");
		group.add(yourturn);
		yourturn.setSelected(false);
		yourturn.setEnabled(false);
		oppturn.setEnabled(false);
		group.add(oppturn);
		barX.add(yourturn);
		barX.add(oppturn);
		
		JCheckBox hit = new JCheckBox("Hit");
		hit.setSelected(false);
		hit.setEnabled(false);
		barX.add(hit);
		
		JCheckBox miss = new JCheckBox("Missed");
		miss.setSelected(false);
		miss.setEnabled(false);
		barX.add(miss);
		
		JCheckBox isgameover = new JCheckBox("Game over?");
		isgameover.setSelected(false);
		isgameover.setEnabled(false);
		barX.add(isgameover);
		
		JCheckBox waiting = new JCheckBox("Waiting for client");
		waiting.setSelected(false);
		waiting.setEnabled(false);
		barX.add(waiting);
	}
	
	public void initShipBar (JPanel barX)
	{
		/*Add row of ship choices for user to interact with*/
		shipChoices = new JPanel(new GridLayout(1,5));
		num = new JTextField("Number of Boats: " + numBoat);
		barX.add(num);
		JCheckBox horizontal = new JCheckBox("Horizontal");
		horizontal.setSelected(true);
		isHorizontal = true;
		horizontal.setEnabled(true);
		
		JCheckBox vertical = new JCheckBox("Vertical");
		vertical.setSelected(false);
		vertical.setEnabled(true);
		
		horizontal.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            vertical.setSelected(false);
		            isVertical = false;
		        } else {//checkbox has been deselected
		        	isVertical = true;
		        	vertical.setSelected(true);
		        };
		    }
		});
		vertical.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            horizontal.setSelected(false);
		            isHorizontal = false;
		        } else {
		        	isHorizontal = true;
		        	horizontal.setSelected(true);
		        };
		    }
		});
		
		barX.add(horizontal);
		barX.add(vertical);
		
		
		for(int i = 0;i<5;i++) {
			shipButtons[i] = new JButton(shipNames.get(i));
			shipButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton push = (JButton)e.getSource();
					for(int i = 0;i<5;i++) {
						shipButtons[i].setBackground(null);
						if(shipButtons[i] == push) {
							clickedShip = push;
							shipButtons[i].setBackground(Color.MAGENTA);
						}
					}
				}
			});
			shipChoices.add(shipButtons[i]);
			//shipChoicesButtons.add(shipButtons[i]);
		}
		//JButton button = new JButton("This is where the horizontal display of all the ship choices will go, TO BE ADDED (soon)");
		barX.add(shipChoices);
	}
	
}
