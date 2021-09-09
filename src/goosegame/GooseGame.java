package goosegame;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.Timer;


public class GooseGame implements Runnable {
	private JSplitPane splitPane;
	private JSplitPane sidebar;
	private MessageArea messageArea;
	private Board board;
	private DiceRollPanel diceRollPanel;
	private Splash splash;
	private ArrayList<Player> players;
	private int currPlayer;
	private int lastRoll;
	private int numberOfRounds;
	private InitSplash initSplash;
        private static final String BOARD_DESCRIPTION_FILE = "src/goosegame/SquareLabels.txt";

	public GooseGame(String boardDesc) {
		initSplash = new InitSplash();
		players = initSplash.getPlayers();
		initSplash.hideFrame();
		initSplash = null;

		ArrayList<Color> colors = new ArrayList<Color>(players.size());
		for (Player p: players) {
			colors.add(p.getColor());
		}
		board = new Board(boardDesc, colors, this);
		
		for (Player p: players) {
			p.setSquare(board.getSquare(0));
			p.setBoard(board);
		}
		currPlayer = 0;
		numberOfRounds = 0;

		messageArea = new MessageArea();
		diceRollPanel = new DiceRollPanel(this, board, messageArea);

		sidebar = new JSplitPane(JSplitPane.VERTICAL_SPLIT, diceRollPanel, messageArea);
		sidebar.setDividerLocation(0.33);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board, sidebar);
		splitPane.setDividerLocation(0.75);

		splash = new Splash(board);
	}

	public Player getPlayerByPawn(Pawn pawn) {
		Color c = pawn.getColor();
		for (Player p: players) {
			if (p.getColor().equals(c)) {
				return p;
			}
		}

		return null;
	}

	public void showMessage(String msg) {
		System.out.println("[GooseGame:showMessage()]");
		splash.show(msg);
	}

	private Player getCurrentPlayer() {
		return players.get(currPlayer);
	}

	public void repeatRoll() {
		//dicesRolled(lastRoll);
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	private void setNextPlayer() {
		while (true) {
			currPlayer = (currPlayer + 1) % players.size();
			if (currPlayer == 0) {
				++numberOfRounds;
			}
			if (!getCurrentPlayer().isTrapped()) {
				break;
			}
			else {
				showMessage(String.format("%s, you lose your turn.", getCurrentPlayer().getName()));
			}
		}
	}

	public void dicesRolled(int dice_1, int dice_2) {
		int newPos, roll;
		Player player = getCurrentPlayer();
		Square oldSquare = player.getSquare();
		roll = dice_1 + dice_2;
		player.setLastRoll(roll);
		Square newSquare = player.move(roll);

		// check here if player got 6+3 or 5+4 on his first roll
		if (((dice_1 == 6 && dice_2 == 3) || (dice_1 == 3 && dice_2 == 6)) && (numberOfRounds == 0)) {
			showMessage(String.format("%s, your first roll is 5+4, so you are sent to square 53", player.getName()));
			newSquare = player.move(26-9);
		}
		else if (((dice_1 == 5 && dice_2 == 4) || (dice_1 == 4 && dice_2 == 5)) && (numberOfRounds == 0)) {
			showMessage(String.format("%s, your first roll is 5+4, so you are sent to square 53", player.getName()));
			newSquare = player.move(53-9);
		}

		newSquare.action(player, oldSquare);
		setNextPlayer();
	}

	private ArrayList<Color> getPlayerColors()  {
		ArrayList<Color> colors = new ArrayList<Color>(players.size());
		for (Player p: players) {
			colors.add(p.getColor());
		}

		return colors;
	}

	private void initPlayers() {
		for (Player p: players) {
			p.setSquare(board.getSquare(0));
			p.setBoard(board);
		}
	}

	public void run() {
		players = initSplash.getPlayers();
		initSplash.hideFrame();
		initSplash = null;

		initPlayers();
		ArrayList<Color> colors = getPlayerColors();
		SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
		      board.reset(colors);
                   }
                });

		currPlayer = 0;
		numberOfRounds = 0;
		messageArea.reset();
	}

	private void startNewGame() {
		initSplash = new InitSplash();
		Thread thread = new Thread(this);
		thread.start();
	}

	private boolean playAgain() {
		String message = "Start new game?";
		return JOptionPane.showConfirmDialog(board, message, null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private void announceWinner(Player winner) {
		String message = String.format("%s congratulations, you won!", winner.getName());
		showMessage(message);
	}

	public void ended(Player winner) {
		announceWinner(winner);
		if (playAgain()) {
			startNewGame();
		}
		else {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
                String boardDescriptionFile = args.length > 0 ? args[0] : BOARD_DESCRIPTION_FILE;
		GooseGame game = new GooseGame(boardDescriptionFile);

		//Create and set up the window.
	        JFrame frame = new JFrame("GooseGame");
        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	frame.getContentPane().add(game.getSplitPane());

        	//Display the window.
       	 	frame.pack();
        	frame.setVisible(true);
	}
}

class Splash {
	private JDialog dialog;
	private JOptionPane optionPane;
	private Board board;
	private Timer timer;
	private int duration = 3000; // milliseconds

	public Splash(Board b) {
		board = b;
		optionPane = new JOptionPane("Message", JOptionPane.INFORMATION_MESSAGE);
                dialog = optionPane.createDialog(board, "Title");
		dialog.setPreferredSize(new Dimension(400, 170));
                dialog.setModal(true);
                dialog.setVisible(false);
		dialog.pack();

		timer = new Timer(duration, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
			//dialog.setModal(false);
                        //dialog.setVisible(false);
                    }
                });
	}

	public void setDuration(int d) {
		duration = d;
		timer = new Timer(duration, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
			//dialog.setModal(false);
                        dialog.setVisible(false);
                    }
                });

		// old timer object will be garbage collected
	}

	public void show(String msg) {
		System.out.println("[Spash:show()]");
		dialog.setLocationRelativeTo(board);
		optionPane.setMessage(String.format("<html><body style=\"text-align: left;  text-justify: inter-word;\">%s</body></html>",msg));
		timer.setRepeats(false);
		timer.start();
		dialog.setVisible(true);
	}

	public void hide() {
		dialog.setModal(false);
		dialog.setVisible(false);
	}
}

class MessageArea extends JSplitPane {
	private JTextArea textArea;
	private JScrollPane messagePane;
	private JScrollPane logoPane;
	private final static String newline = "\n";

	public MessageArea() {
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		textArea = new JTextArea("Hello to Goose Game" + newline);
		textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		messagePane = new JScrollPane(textArea);
		messagePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		messagePane.setPreferredSize(new Dimension(350, 250));

		JLabel logo = new JLabel();
		logo.setHorizontalAlignment(JLabel.CENTER);
		ImageIcon icon = createImageIcon("images/logo.png");
        	logo.setIcon(icon);
        	if  (icon != null) {
            		logo.setText(null);
        	} else {
            		logo.setText("Image not found");
        	}
		logoPane = new JScrollPane(logo);

		setLeftComponent(messagePane);
		setBottomComponent(logoPane);
		setDividerLocation(0.5);
	}

	protected static ImageIcon createImageIcon(String path) {
        	java.net.URL imgURL = MessageArea.class.getResource(path);
        	if (imgURL != null) {
            		return new ImageIcon(imgURL);
        	} else {
            		System.err.println("Couldn't find file: " + path);
            		return null;
        	}
    	}

	public void appendMessage(String message) {
		textArea.append(message + newline);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public void reset() {
		textArea.setText("");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}


class DiceRollPanel extends JPanel {
	private JLabel prompt;
	private JLabel firstDice;
	private JLabel secondDice;
	private JPanel dicesBox;
	private JPanel buttonsBox;
	private JButton rollButton;
	private ImageIcon[] diceIcons;
	private int NUMBER_OF_ROLLS = 4;
	private int MILLISECONDS_BETWEEN_ROLLS = 1000;
	private Board board;
	private MessageArea messageArea;
	private GooseGame game;
	private static final String DICE_ICONS_DIRECTORY = "src/goosegame/images/";

	public DiceRollPanel(GooseGame g, Board b, MessageArea m) {
		//panel = new JPanel();
		//panel.setLayout(new GridLayout(3, 1));
		game = g;
		board = b;
		messageArea = m;

		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(350, 150));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		prompt = new JLabel();
		prompt.setText("Hello");
		prompt.setHorizontalAlignment(JLabel.CENTER);
		add(prompt, c);

		diceIcons = new ImageIcon[6];
		for (int i=0; i<6; i++) {
			diceIcons[i] = getDiceImageIcon(i+1);
		}

		firstDice = new JLabel();
		secondDice = new JLabel();
		firstDice.setIcon(diceIcons[2]);
		secondDice.setIcon(diceIcons[5]);
		c.insets = new Insets(10,0,0,4);  //top and right padding
		c.gridwidth=1;
		c.gridx = 0;
		c.gridy = 1;
		add(firstDice, c);
		c.insets = new Insets(10,4,0,0); //top and left padding
		c.gridx = 1;
		add(secondDice, c);

		
		rollButton = new JButton("Roll");
		rollButton.addActionListener(new ActionListener() {
   			@Override
			public void actionPerformed(ActionEvent e) {
       				rollDices();
   			}
		});
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=2;
		c.insets = new Insets(10, 0, 0, 0);
		add(rollButton, c);
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	private static ImageIcon getDiceImageIcon(int number) {
		String filepath = DICE_ICONS_DIRECTORY + "dice_" + number + ".png";
		//System.out.println("Filepath: " + filepath);
		BufferedImage img = null;
		try {
 			img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
    			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        	return new ImageIcon(dimg);

	}

	private int getRandomDiceRoll() {
		// returns int in [0,5] range
		return ThreadLocalRandom.current().nextInt(0, 6);
	}

	public void rollDices() {
		SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
                      rollButton.setEnabled(false);
                   }
                });

		int dice_1=0, dice_2=0;
        	for (int i = 1; i <= NUMBER_OF_ROLLS; i++) {
		    dice_1 = getRandomDiceRoll();
		    dice_2 = getRandomDiceRoll();
		    firstDice.setIcon(diceIcons[dice_1]);
		    secondDice.setIcon(diceIcons[dice_2]);
		    firstDice.paintImmediately(0, 0, 5000, 5000);
		    secondDice.paintImmediately(0, 0, 5000, 5000);
		    if (i == NUMBER_OF_ROLLS) {
			prompt.setText("You got " + (dice_1 + dice_2 + 2));
			prompt.paintImmediately(0, 0, 5000, 5000);
		    }
	            try {
        	       TimeUnit.MILLISECONDS.sleep(MILLISECONDS_BETWEEN_ROLLS);
            	    } catch (InterruptedException e) {
                       throw new RuntimeException(e);
            	    }
        	}
		
	        try {
        	   TimeUnit.MILLISECONDS.sleep(MILLISECONDS_BETWEEN_ROLLS);
            	} catch (InterruptedException e) {
                   throw new RuntimeException(e);
            	}

		game.dicesRolled(dice_1+1, dice_2+1);
                //game.dicesRolled(15, 16);

		SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
                      rollButton.setEnabled(true);
                   }
                });
	}
}
