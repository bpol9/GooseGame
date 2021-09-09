package goosegame;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.awt.Color;


class PlayerOptions {
	public String name;
	public String color;

	public PlayerOptions(String n, String c) {
		name = n;
		color = c;
	}
}

public class InitSplash extends JFrame {	
    private PlayerOptions[] optionsArray;
    private JPanel mainPanel;
    private JComboBox<String> numberCombo;
    private JPanel playersNumberPanel;
    private JPanel playerOptionsPanel;
    private JLabel playerPrompt;
    private JLabel welcomeMessage;
    private JTextField playerName;
    //private JTextField playerColor;
    private JComboBox<String> playerColor;
    private JButton next;
    private JButton previous;
    private int numberOfPlayers;
    private int currentStage;
    private int previousStage;
    //private GooseGame game
    private CountDownLatch latch;

    public InitSplash() {
	    //this.game = game;
	    currentStage = 0;
	    previousStage = -1;
	    numberOfPlayers = -1;

	    mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setOpaque(true);

	    welcomeMessage = new JLabel("Welcome to the GooseGame!");
	    welcomeMessage.setFont(new Font("Serif", Font.BOLD, 16));
	    welcomeMessage.setHorizontalAlignment(JLabel.CENTER);
	    welcomeMessage.setBorder(new EmptyBorder(10,0,10,10));
	    //panel.add(welcomeMessage, BorderLayout.PAGE_START);

	    playersNumberPanel = new JPanel(new FlowLayout());
	    playersNumberPanel.setBorder(new EmptyBorder(0,0,10,0));
	    JLabel prompt = new JLabel("Select number of players: ");
	    String[] playersNumber = { "2", "3", "4", "5", "6" };
	    numberCombo = new JComboBox<String>(playersNumber);
	    numberCombo.setSelectedIndex(4);
	    playersNumberPanel.add(prompt);
	    playersNumberPanel.add(numberCombo);
	    //panel.add(numberInput, BorderLayout.CENTER);

	    JPanel buttonBox = new JPanel();
	    previous = new JButton("Previous");
	    previous.setEnabled(false);
	    previous.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			    previousHandler();
		    }
	    });
	    next = new JButton("  Next  ");
	    next.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			    nextHandler();
		    }
	    });
	    buttonBox.add(previous);
	    buttonBox.add(next);
	    buttonBox.setBorder(new EmptyBorder(10,0,15,0));
	    //panel.add(buttonBox, BorderLayout.PAGE_END);

	    //p.setOpaque(true);

	    playerPrompt = new JLabel("Player 1, pick:");
	    //playerPrompt.setBorder(BorderFactory.createLineBorder(Color.black));
	    playerPrompt.setHorizontalAlignment(JLabel.CENTER);
	    playerPrompt.setBorder(new EmptyBorder(10,0,0,0));

            //String[] labels = {"Name: ", "Color: ", "Email: ", "Address: "};
            //int numPairs = labels.length;

            playerOptionsPanel = new JPanel(new SpringLayout());
	    JLabel l = new JLabel("Name: ", JLabel.TRAILING);
	    playerName = new JTextField(10);
	    l.setLabelFor(playerName);
	    playerOptionsPanel.add(l);
	    playerOptionsPanel.add(playerName);
	    l = new JLabel("Color: ", JLabel.TRAILING);
	    //playerColor = new JTextField(10);
            String[] colors = {"red", "blue", "black", "green", "pink", "yellow"};
            playerColor = new JComboBox<String>(colors);
            playerColor.setSelectedIndex(0);
	    l.setLabelFor(playerColor);
	    playerOptionsPanel.add(l);
	    playerOptionsPanel.add(playerColor);

        SpringUtilities.makeCompactGrid(playerOptionsPanel,
                                    2, 2, 	     //rows, cols
                                    6, 6,        //initX, initY
                                    6, 6);       //xPad, yPad

	    mainPanel.add(welcomeMessage, BorderLayout.PAGE_START);
	    mainPanel.add(playersNumberPanel, BorderLayout.CENTER);
	    mainPanel.add(buttonBox, BorderLayout.PAGE_END);

	    setPreferredSize(new Dimension(430, 180));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setContentPane(mainPanel);
    	    pack();
	    setVisible(true);	    
    }

    public void hideFrame() {
	    setVisible(false);
    }

    public ArrayList<Player> getPlayers() {
	    if (latch == null) {
		    latch = new CountDownLatch(1);
	    }
	    try {
	    	latch.await();
	    }
	    catch (InterruptedException e) {
	        e.printStackTrace();
		return null;
	    }

	    ArrayList<Player> players = new ArrayList<Player>(numberOfPlayers);
	    Color color;
	    String name;
	    for (int i=0; i<numberOfPlayers; i++) {
		    name = optionsArray[i].name;
		    color = getColorFromString(optionsArray[i].color);
		    players.add(new Player(name, color));
	    }

	    return players;
    }

    private void nextHandler() {
	    if (currentStage == 0) { //number of players decided
		numberOfPlayers = Integer.parseInt((String)numberCombo.getSelectedItem());
		optionsArray = new PlayerOptions[numberOfPlayers];
		previous.setEnabled(true);
	    }
	    else {
	    	PlayerOptions po = new PlayerOptions(playerName.getText(), (String)playerColor.getSelectedItem());
                playerColor.removeItemAt(playerColor.getSelectedIndex());
                //playerColor.setSelectedIndex(0);
	    	optionsArray[currentStage-1] = po;
		if (currentStage == numberOfPlayers) { // this was the last player
		    latch.countDown();
		    return;
		    //notify ready..
		}
	    }

	    previousStage = currentStage;
	    currentStage++;
	    //previousStage = currentStage++;
	    askForPlayerOptions();
	    if (currentStage == numberOfPlayers) {
		    next.setText(" Ready  ");
		    next.repaint();
	    }
    }

    private void previousHandler() {
	    previousStage = currentStage;
	    currentStage--;

            if (previousStage >= 2) {
                playerColor.addItem(optionsArray[currentStage-1].color);
            }
	    if (currentStage == 0) {
		    askForNumberOfPlayers();
	    }
	    else {
		    askForPlayerOptions();
	    }

	    if (previousStage == numberOfPlayers) {
		    next.setText("  Next  ");
		    next.repaint();
	    }
    }

    private void askForNumberOfPlayers() {
	    System.out.println("Entered askForNumberOfPlayers");
	    previous.setEnabled(false);
	    mainPanel.remove(playerPrompt);
	    mainPanel.remove(playerOptionsPanel);
	    mainPanel.add(welcomeMessage, BorderLayout.PAGE_START);
	    mainPanel.add(playersNumberPanel, BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint();

	    pack();
	    setPreferredSize(new Dimension(430, 180));
    }

    private void askForPlayerOptions() {
	    String prompt = "Player " + currentStage + ", pick:";
	    playerPrompt.setText(prompt);
	    playerName.setText("");
	    playerColor.setSelectedIndex(0);
	    playerPrompt.repaint();
	    playerName.repaint();
	    playerColor.repaint();

	    if (currentStage == 1 && previousStage == 0) {
		    mainPanel.remove(welcomeMessage);
		    mainPanel.remove(playersNumberPanel);
		    mainPanel.add(playerPrompt, BorderLayout.PAGE_START);
		    mainPanel.add(playerOptionsPanel, BorderLayout.CENTER);
		    mainPanel.revalidate();
		    mainPanel.repaint();

		    setPreferredSize(new Dimension(430, 180));
		    pack();
	    }
    }

    public static Color getColorFromString(String color) {
	Color c;
	try {
		Field field = Class.forName("java.awt.Color").getField(color);
		c = (Color)field.get(null);
	} catch (Exception e) {
    		c = null; // Not defined
	}

	return c;
    }

    public static String getColorName(Color c) {
    	for (Field f : Color.class.getFields()) {
        	try {
        	    if (f.getType() == Color.class && f.get(null).equals(c)) {
        	        return f.getName();
        	    }
        	} catch (java.lang.IllegalAccessException e) {
        	    // it should never get to here
        	} 
    	}
   	return "unknown";
    }
}

