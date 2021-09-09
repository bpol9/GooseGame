package goosegame;

import java.awt.Color;

public class Prison extends Square {
        private final String RELEASE_MESSAGE = "%s, %s releases you from the Prison";
        private final String PRISON_MESSAGE = "%s, you lose your turn until another player arrives on the Prison to release you";
	public Prison(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		//NOTIFICATION = "%s, you landed on the Prison and will stay here until another player arrives to release you.";
	}
	/*
	 * The player that landed on this square becomes prisoner
	 * if there was no other player on the square already. Else
	 * if there was a prisoner on this square he is freed
	 * and the new player is not prisoned.
	 */
	public void action(Player p, Square source) {
		// if there is already a prisoner on the square, he is released
		if (player != null && player.isPrisoned()) {
                        game.showMessage(String.format(RELEASE_MESSAGE, player.getName(), p.getName()));
			player.release();
		}
		else if (player == null) { // there is no other player in the prison, so new player is jailed
                        game.showMessage(String.format(PRISON_MESSAGE, p.getName()));
			p.prison();
		}
		super.action(p, source);
	}
}
