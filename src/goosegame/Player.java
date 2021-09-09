package goosegame;

import java.awt.Color;

public class Player {
	private String name;
	private Board board;
	private Square currentSquare;
	//private Pawn pawn;
	private Color color;
	private int lastRoll;
	private boolean lastMoveWasBackwards;

	// If true, the player loses his turn
	private boolean isPrisoner;
	private boolean isStuckedInWell;

	// how many turns the player has to still lose because of being in an Inn
	private int innCounter;

	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
		innCounter = 0;
		lastRoll = -1;
		lastMoveWasBackwards = false;
	}

	public void setLastRoll(int roll) {
		lastRoll = roll;
	}

	public int getLastRoll() {
		return lastRoll;
	}

	public boolean wasLastMoveBackwards() {
		return lastMoveWasBackwards;
	}

	public Square moveTo(int squareNumber) {
		Square oldSquare = currentSquare;
		int steps = squareNumber - getPosition();
		currentSquare = board.movePawn(color, steps);
		if (currentSquare.getNumber() - oldSquare.getNumber() < 0) {
			lastMoveWasBackwards = true;
		}
		else {
			lastMoveWasBackwards = false;
		}

		return currentSquare;
	}

	public Square move(int steps) {
		System.out.println("[Player:moveTo()] moving player " + steps + " steps starting from " + currentSquare.getNumber());
		Square oldSquare = currentSquare;
		currentSquare = board.movePawn(color, steps);
		if (currentSquare.getNumber() - oldSquare.getNumber() < 0) {
			lastMoveWasBackwards = true;
		}
		else {
			lastMoveWasBackwards = false;
		}

		return currentSquare;
	}

	public String getName() {
		return name;
	}

	public void setSquare(Square s) {
		currentSquare = s;
	}

	public void setBoard(Board b) {
		board = b;
	}

	public Square getSquare() {
		return currentSquare;
	}

	public int getPosition() {
		return currentSquare.getNumber();
	}

	public void setInnCounter(int c) {
		innCounter = c;
	}

	public void decreaseInnCounter() {
		innCounter--;
	}

	public int getInnCounter() {
		return innCounter;
	}

	private boolean isStuckedInWell() {
		return isStuckedInWell;
	}

	public boolean isPrisoned() {
		return isPrisoner;
	}

	private boolean isDrinkingInInn() {
		if (innCounter > 0) {
			--innCounter;
			return true;
		}
		else {
			return false;
		}

	}

	public void release() {
		isPrisoner = false;
	}

	public void prison() {
		isPrisoner = true;
	}

	public void setStuckedInWell(boolean stucked) {
		isStuckedInWell = stucked;
	}

	public Color getColor() {
		return color;
	}

	public boolean isTrapped() {
		return isDrinkingInInn() || isStuckedInWell() || isPrisoned();
	}
}
