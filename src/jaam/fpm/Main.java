package jaam.fpm;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame
{
	Drawing currentDrawing = null;

	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		currentDrawing = new Drawing();
		gc.setShowFPS(false);
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		if (currentDrawing != null) {
			currentDrawing.update(gc, i);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		if (currentDrawing != null) {
			currentDrawing.render(gc, g);
		}
		//g.drawString("Howdy!", 10, 10);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("Simple Slick Game"));
			appgc.setDisplayMode(640, 480, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
