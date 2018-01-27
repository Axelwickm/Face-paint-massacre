package jaam.fpm.client;

import com.sun.istack.internal.logging.Logger;
import jaam.fpm.client.KeyConfig;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;
import java.util.logging.Level;

public class Drawing extends ImageBuffer {

    public static final int DRAWING_WIDTH = 32;
    public static final int DRAWING_HEIGHT = 32;

    public static final int MINIMUM_BRUSH_SIZE = 0;
    public static final int MAXIMUM_BRUSH_SIZE = 9;

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

    private boolean isErasing = false;

    public Drawing() throws SlickException {
        super(DRAWING_WIDTH, DRAWING_HEIGHT);
        isActive = true;
        currentColorIndex = 0;
    }

    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    int currentColorIndex;



    public void update(GameContainer gc, int i) {
        if (!isActive) return;

        Input input = gc.getInput();
        Color currentColor = COLORS[currentColorIndex];

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            if (isMouseWithinDrawing(gc)) {
                Point pt = getMouseLocationInImage(gc);


                for (int dx = -brushSize; dx <= brushSize; ++dx) {
                    if (pt.x + dx < 0) continue;
                    if (pt.x + dx >= getWidth()) break;
                    for (int dy = -brushSize; dy <= brushSize; ++dy) {
                        if (pt.y + dy < 0) continue;
                        if (pt.y + dy >= getHeight()) break;
                        if (isErasing) setRGBA(pt.x + dx, pt.y + dy
                                              , 0
                                              , 0
                                              , 0
                                              , 0);
                        else setRGBA(pt.x + dx, pt.y + dy
                                    , currentColor.getRedByte()
                                    , currentColor.getGreenByte()
                                    , currentColor.getBlueByte()
                                    , currentColor.getAlphaByte());
                    }
                }
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
        } else if (input.isKeyPressed(KeyConfig.TOGGLE_ERASE)) {
            isErasing = !isErasing;
        }
    }

    public void render(final GameContainer gc, final Graphics g, final int translateX, final int translateY) {
		float wscale = gc.getWidth() / (float)getWidth();
		float hscale = gc.getHeight() / (float)getHeight();
		float scale = Math.min(wscale, hscale);

		int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
		int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

		getImage(Image.FILTER_NEAREST).draw(xpos + translateX, ypos + translateY, scale);

		g.drawString("Brush size: " + brushSize + " (Z and X to change)", 10 + translateX, 30 + translateY);
		g.drawString("Color: " + COLORS[currentColorIndex].toString() + " (Q and E to change)", 10 + translateX, 50 + translateY);
		g.drawString("Eraser: " + (isErasing ? "Active" : "Inactive") + " (press W to toggle)", 10 + translateX, 70 + translateY);
		g.drawString("Press R to finish drawing", 10 + translateX, 90 + translateY);
	}

    public boolean isMouseWithinDrawing(GameContainer gc) {
        Input input = gc.getInput();

        float wscale = gc.getWidth() / (float)getWidth();
        float hscale = gc.getHeight() / (float)getHeight();
        float scale = Math.min(wscale, hscale);

        int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
        int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

        return input.getMouseX() - xpos >= 0
            && input.getMouseX() - xpos < getWidth() * scale
            && input.getMouseY() - ypos >= 0
            && input.getMouseY() - ypos < getHeight() * scale;
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
