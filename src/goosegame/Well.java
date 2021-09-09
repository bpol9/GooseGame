package goosegame;

import java.awt.Color;

public class Well extends Square {
        private final String UNSTUCKED_MESSAGE = "%s, %s releases you from the Well";
        private final String STUCKED_MESSAGE = "%s, you lose your turn until another player arrives on the Well to release you";
	public Well(Color color, String text, int number, GooseGame game) {
		super(color, text, number, game);
		//NOTIFICATION = "%s, you landed on a Well and will stay here until another player arrives to take your place.";
	}
	/*
	 * The player that landed on this square becomes stucked in the Well.
	 * If there was a player before, he is released and moved
	 * to the square from which the new player came from.
	 */
	public void action(Player p, Square source) {
		// if there is already a player on the Well, he is unstucked
		if (player != null) {
                        game.showMessage(String.format(UNSTUCKED_MESSAGE, player.getName(), p.getName()));
			player.setStuckedInWell(false);
		}
		super.action(p, source);
                game.showMessage(String.format(STUCKED_MESSAGE, player.getName()));
		player.setStuckedInWell(true);
	}
}
