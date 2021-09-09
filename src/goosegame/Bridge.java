package goosegame;

import java.awt.Color;

public class Bridge extends Square {
	private final int DESTINATION = 12;
	public Bridge(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		NOTIFICATION = "%s, you landed on a Bridge. Go to square " + DESTINATION;
	}

	@Override
	public void action(Player p, Square source) {
		//super.action(p, source);
		game.showMessage(String.format(NOTIFICATION, p.getName()));
		Square newSquare = p.moveTo(DESTINATION);
		newSquare.action(p, source);
	}
}
