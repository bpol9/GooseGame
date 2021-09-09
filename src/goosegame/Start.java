package goosegame;

import java.awt.Color;

public class Start extends Square {
	public Start(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
	}

	/*
	 * When players land on the Start square, nothing happens
	 */
	public void action(Player p, Square source) {
	}
}
