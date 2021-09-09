package goosegame;

import java.awt.Color;

public class Labyrinth extends Square {
	private final int DESTINATION = 30;

	public Labyrinth(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		NOTIFICATION = "%s, you landed on a Labyrinth. Go back to square " + DESTINATION;
	}

	/*
	 * A Labyrinth square returns the player to square 30
	 */
	public void action(Player p, Square source) {
		String msg = String.format(NOTIFICATION, p.getName());
		game.showMessage(msg);
		Square newSquare = p.moveTo(DESTINATION);
                newSquare.action(p, source);
	}
}
