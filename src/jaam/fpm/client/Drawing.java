package jaam.fpm.client;

import com.sun.istack.internal.logging.Logger;
import jaam.fpm.client.KeyConfig;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.logging.Level;

public class Drawing extends Image {

    public static final int DRAWING_WIDTH = 256;
    public static final int DRAWING_HEIGHT = 256;

    public static final int MINIMUM_BRUSH_SIZE = 5;
    public static final int MAXIMUM_BRUSH_SIZE = 9;

    private final Image comparison = new Image(DRAWING_WIDTH, DRAWING_HEIGHT);

    private Image prototype = new Image(DRAWING_WIDTH, DRAWING_HEIGHT);

    public static final Color[] COLORS = {
            Color.black,
            Color.red,
            Color.green,
            Color.blue,
            Color.yellow,
            Color.magenta,
            Color.cyan,
            Color.white
    };

    private int brushSize = MINIMUM_BRUSH_SIZE;

    public Drawing() throws SlickException {
        super(DRAWING_WIDTH, DRAWING_HEIGHT);
        isActive = true;
        currentColorIndex = 0;

        setFilter(FILTER_NEAREST);
        Graphics g = comparison.getGraphics();
        g.setColor(new Color(130, 76, 35, 255));
        g.fillOval(0, 0, DRAWING_WIDTH, DRAWING_HEIGHT);
        g.flush();
    }

    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    int currentColorIndex;
    boolean mouseButtonDown = false;
    Image undoBuffer;

    public void update(GameContainer gc, int i) throws SlickException {
        if (!isActive) {
        	return;
		}

        Graphics g = getGraphics();
        Input input = gc.getInput();
        g.setColor(COLORS[currentColorIndex]);
        Graphics pg = prototype.getGraphics();

        if (!input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && mouseButtonDown){
            mouseButtonDown = false;
            undoBuffer = this.getScaledCopy(1.f);
        }


        //boolean paintedAnywhere = false;
        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            mouseButtonDown = true;
			Point pt = getMouseLocationInImage(gc);
			
			if (isPointWithinDrawing(gc, pt)) {


				g.fillRect(pt.x - brushSize / 2, pt.y - brushSize / 2, brushSize, brushSize);
				pg.setColor(COLORS[currentColorIndex]);
				pg.fillRect(pt.x - brushSize / 2, pt.y - brushSize / 2, brushSize, brushSize);

				pg.flush();
			}
        }

        if (input.isKeyPressed(KeyConfig.NEXT_COLOR)) {
            if (++currentColorIndex >= COLORS.length) currentColorIndex = 0;
            System.out.println("Switched to next color (" + COLORS[currentColorIndex].toString() + ").");
        } else if (input.isKeyPressed(KeyConfig.PREV_COLOR)) {
            if (--currentColorIndex < 0) currentColorIndex = COLORS.length - 1;
            System.out.println("Switched to previous color (" + COLORS[currentColorIndex].toString() + ").");
        } else if (input.isKeyPressed(KeyConfig.BIGGER_BRUSH)) {
            if (++brushSize > MAXIMUM_BRUSH_SIZE) brushSize = MAXIMUM_BRUSH_SIZE;
        } else if (input.isKeyPressed(KeyConfig.SMALLER_BRUSH)) {
            if (--brushSize < MINIMUM_BRUSH_SIZE) brushSize = MINIMUM_BRUSH_SIZE;
        } else if (input.isKeyPressed(KeyConfig.UNDO)){
        	if (undoBuffer != null) {
				g.clear();
				g.drawImage(undoBuffer, 0, 0); // Not drawing <----
			}
        }

        g.flush();
    }

    public void render(final GameContainer gc, final Graphics g, final int translateX, final int translateY) {
		float wscale = gc.getWidth() / (float)getWidth();
		float hscale = gc.getHeight() / (float)getHeight();
		float scale = Math.min(wscale, hscale);

		int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
		int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

		comparison.draw(xpos + translateX, ypos + translateY, scale);
		draw(xpos + translateX, ypos + translateY, scale);

		g.drawString("Brush size: " + brushSize + " (X and C to change)", 10 + translateX, 30 + translateY);
		g.setColor(COLORS[currentColorIndex]);
		g.drawString("Color: " + COLORS[currentColorIndex].toString() + " (Q and E to change)", 10 + translateX, 50 + translateY);
		g.setColor(Color.white);
		g.drawString("Press R to finish drawing", 10 + translateX, 70 + translateY);
		g.drawString("Z to undo", 10 + translateX, 90 + translateY);
	}

    public boolean isPointWithinDrawing(GameContainer gc, Point pt) throws SlickException {
    	if (pt.x < 0 || pt.x >= comparison.getWidth()) return false;
    	if (pt.y < 0 || pt.y >= comparison.getHeight()) return false;
    	Color c = comparison.getGraphics().getPixel(pt.x, pt.y);

    	int alpha = c.getAlphaByte();

		comparison.getGraphics().flush();

		return alpha != 0;
    }

    public Point getMouseLocationInImage(GameContainer gc) throws IllegalArgumentException {
        Input input = gc.getInput();

        float wscale = gc.getWidth() / (float)getWidth();
        float hscale = gc.getHeight() / (float)getHeight();
        float scale = Math.min(wscale, hscale);

        int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
        int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

        int mouseX = (int)((input.getMouseX() - xpos) / scale);
        int mouseY = (int)((input.getMouseY() - ypos) / scale);

        return new Point(mouseX, mouseY);
    }

    public Image getImage() {
    	return prototype.getScaledCopy(Player.SIZE, Player.SIZE);
	}
}
