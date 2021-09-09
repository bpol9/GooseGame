package goosegame;

import javax.swing.JLabel;
import java.util.ArrayList;
import java.awt.Color;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import java.util.concurrent.TimeUnit;

class Square extends JLabel {

    protected int number;
    //protected ArrayList<Player> players;
    protected Board board;
    protected GooseGame game;
    //private JPanel pawnsBox;
    private Box pawnsBox;
    //private ArrayList<Pawn> pawns = new ArrayList<Pawn>();

    // the player that is currently on this square. By the rules of the game, only one player can be on one square at a time
    protected Player player;
    private String DEFAULT_NOTIFICATION;
    protected String NOTIFICATION;

    private static final int WIDTH = 130;
    private static final int HEIGHT = 75;

    public Square(Color color, String text, int number, GooseGame game) {
	this.number = number;
	this.game = game;
	DEFAULT_NOTIFICATION = "%s, a new player arrived and you are sent back to square %d";
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
	setLayout(new GridLayout(2, 1));
        setOpaque(true);
        setBackground(color);
	setBorder(BorderFactory.createLineBorder(Color.black));
	JLabel textLabel = new JLabel();
	if (text != null) {
		textLabel.setText(String.format("<html><body style=\"text-align: left;  text-justify: inter-word;\">%s</body></html>",text));
		textLabel.setFont(new Font("Serif", Font.PLAIN, 15));
	}
	pawnsBox = new Box(BoxLayout.X_AXIS);
    add(textLabel);
   	add(pawnsBox);
    }



    public boolean addPawn(Pawn pawn) {
	pawnsBox.add(pawn);
	pawnsBox.paintImmediately(0, 0, 5000, 5000);
	//player = game.getPlayerByPawn(pawn);
	return true;
    }
    
    public boolean addPassingByPawn(Pawn pawn) {
	pawnsBox.add(pawn);
	pawnsBox.paintImmediately(0, 0, 5000, 5000);
	return true;
    }

    public boolean addPawnWithRevalidate(Pawn pawn) {
	    pawnsBox.add(pawn);
	    pawnsBox.revalidate();
	    pawnsBox.paintImmediately(0, 0, 5000, 5000);
	    //player = game.getPlayerByPawn(pawn);
	    return true;
    }

    public boolean addPawnShort(Pawn pawn) {
	    addPassingByPawn(pawn);
	    try {
	    	TimeUnit.MILLISECONDS.sleep(500);
	    } catch (InterruptedException e) {
		    // do nothing.
	    }
	    removePassingByPawn(pawn);
	    return true;
    }

    public boolean removePassingByPawn(Pawn pawn) {
	pawnsBox.remove(pawn);
	pawnsBox.paintImmediately(0, 0, 5000, 5000);
	return true;
    }
    
    public boolean removePawn(Pawn pawn) {
	pawnsBox.remove(pawn);
	pawnsBox.paintImmediately(0, 0, 5000, 5000);
	player = null;
	return true;
    }

    public int getNumber() {
	    return number;
    }

    public void setPlayer(Player p) {
	    player = p;
    }

    public void reset() {
	    player = null;
	    pawnsBox.removeAll();
	    pawnsBox.revalidate();
	    pawnsBox.repaint();
    }

    /*
     * Player p arrived on this square coming from source square.
     * If there is already a player on this square, he is sent to source.
     */
    public void action(Player p, Square source) {
	if (player != null) { // Exchange players between this and source squares
                Player oldPlayer = player; // keep a local copy, because Player.move will set this.player to null
		player.move(source.getNumber() - getNumber());
		source.action(oldPlayer, this);
	}
	player = p;
    }
}
