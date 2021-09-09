package goosegame;

import java.awt.Color;

public class Death extends Square {
	private final int DESTINATION = 1;

	public Death(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		NOTIFICATION = "%s, you landed on Death square. Go back to square " + DESTINATION;
	}

	/*
	 * A Labyrinth square returns the player to square 30
	 */
	public void action(Player p, Square source) {
		String msg = String.format(NOTIFICATION, p.getName());
		game.showMessage(msg);
		p.moveTo(DESTINATION);
	}
}
