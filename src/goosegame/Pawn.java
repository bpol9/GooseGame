package goosegame;

import java.awt.Color;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.*;


class Pawn extends JLabel{

    private Color color;
    private Square square;
    private final String PAWN_ICONS_DIRECTORY = "src/goosegame/images/";
    Pawn(Color color, Square square) {

	this.color = color;
	this.square = square;
	String wdir = System.getProperty("user.dir");
	String delim;
	if (wdir.contains("/")) {
		delim = "/";
	}
	else {
		delim = "\\\\";
	}
	String[] dentries = wdir.split(delim);
	String leaf = dentries[dentries.length-1];
   System.out.println("Working Directory = " + System.getProperty("user.dir"));
	String filepath;
	if (leaf.equals("src")) {
		filepath = "goosegame" + delim + "images" + delim;
	}
	else {
		filepath = "src" + delim + "goosegame" + delim + "images" + delim;
	}
	//String filepath = PAWN_ICONS_DIRECTORY + Board.getColorName(color) + "_pawn.png";
	filepath = filepath + Board.getColorName(color) + "_pawn.png";
	BufferedImage img = null;
	if (filepath != null) {
		try {
 		   img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
    		   e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        	setIcon(new ImageIcon(dimg));
	}
    }

    public int getCurrentPosition() {
	    return square.getNumber();
    }

    public int getSquareNumber() {
	    return square.getNumber();
    }

    public void removeFromCurrentSquare() {
	    square.removePawn(this);
	    square = null;
    }

    public void setCurrentSquare(Square s) {
	    square = s;
    }

    public Color getColor() {
	    return color;
    }
}
