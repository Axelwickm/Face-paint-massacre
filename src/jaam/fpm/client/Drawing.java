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

    public static final int MINIMUM_BRUSH_SIZE = 0;
    public static final int MAXIMUM_BRUSH_SIZE = 9;

    private final Image comparison = new Image(DRAWING_WIDTH, DRAWING_HEIGHT);

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

    private int brushSize = 0;

    public Drawing() throws SlickException {
        super(DRAWING_WIDTH, DRAWING_HEIGHT);
        isActive = true;
        currentColorIndex = 0;

        setFilter(FILTER_NEAREST);
        getGraphics().fillOval(0, 0, DRAWING_WIDTH, DRAWING_HEIGHT);
        comparison.getGraphics().fillOval(0, 0, DRAWING_WIDTH, DRAWING_HEIGHT);
    }

    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    int currentColorIndex;


    public void update(GameContainer gc, int i) throws SlickException {
        if (!isActive) return;

        Graphics g = getGraphics();
        Input input = gc.getInput();
        g.setColor(COLORS[currentColorIndex]);

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
        	Point pt = getMouseLocationInImage(gc);
        	for (int dx = -brushSize; dx <= brushSize; ++dx) {
				if (pt.x + dx < 0) continue;
				if (pt.x + dx >= getWidth()) break;
				for (int dy = -brushSize; dy <= brushSize; ++dy) {
					if (pt.y + dy < 0) continue;
					if (pt.y + dy >= getHeight()) break;
					if (!isPointWithinDrawing(gc, new Point(pt.x + dx, pt.y + dy))) continue;

					g.fillRect(pt.x + dx, pt.y + dy, 1, 1);
				}
			}

        }
        g.flush();

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
        }
    }

    public void render(final GameContainer gc, final Graphics g, final int translateX, final int translateY) {
		float wscale = gc.getWidth() / (float)getWidth();
		float hscale = gc.getHeight() / (float)getHeight();
		float scale = Math.min(wscale, hscale);

		int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
		int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

		draw(xpos + translateX, ypos + translateY, scale);

		g.drawString("Brush size: " + brushSize + " (Z and X to change)", 10 + translateX, 30 + translateY);
		g.drawString("Color: " + COLORS[currentColorIndex].toString() + " (Q and E to change)", 10 + translateX, 50 + translateY);
		g.drawString("Press R to finish drawing", 10 + translateX, 70 + translateY);
	}

    public boolean isPointWithinDrawing(GameContainer gc, Point pt) throws SlickException {
		return comparison.getGraphics().getPixel(pt.x, pt.y).getAlphaByte() != 0;
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
}
