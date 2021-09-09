package goosegame;

import java.awt.Color;

public class Inn extends Square {
	private final int TURNS_TO_LOSE = 2;

	public Inn(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		NOTIFICATION = "%s, you landed on an Inn. You lose your next %d rolls.";
	}
	/*
	 * The player that lands to this square,
	 * loses his next TURNS_TO_LOSE turns.
	 */
	public void action(Player p, Square source) {
		//super.action(p, source);
		String msg = String.format(NOTIFICATION, p.getName(), TURNS_TO_LOSE);
		game.showMessage(msg);
		p.setInnCounter(TURNS_TO_LOSE);
	}
}
