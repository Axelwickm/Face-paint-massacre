package jaam.fpm;

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

    public static final int DRAWING_WIDTH = 100;
    public static final int DRAWING_HEIGHT = 200;

    public static final Color[] COLORS = {
            Color.black,
            Color.red,
            Color.green,
            Color.blue,
            Color.yellow,
            Color.cyan
    };

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
                System.out.println("Drawing is happening");
                Point pt = getMouseLocationInImage(gc);

                this.setRGBA(pt.x, pt.y, currentColor.getRedByte(), currentColor.getGreenByte(), currentColor.getBlueByte(), currentColor.getAlphaByte());
            } else System.out.println("Mouse is not within image (probably)");
        } else {
            //System.out.println("Mose button is not down");
        }

        if (input.isKeyPressed(KeyConfig.KEYCODE_NEXT_COLOR)) {
            if (++currentColorIndex >= COLORS.length) currentColorIndex = 0;
            System.out.println("Switched to next color (" + COLORS[currentColorIndex].toString() + ").");
        } else if (input.isKeyPressed(KeyConfig.KEYCODE_PREV_COLOR)) {
            if (--currentColorIndex < 0) currentColorIndex = COLORS.length - 1;
            System.out.println("Switched to previous color (" + COLORS[currentColorIndex].toString() + ").");
        }
    }


    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        float wscale = gc.getWidth() / (float)getWidth();
        float hscale = gc.getHeight() / (float)getHeight();
        float scale = Math.min(wscale, hscale);

        int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
        int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

        // if (xpos != 0 && ypos != 0) Logger.getLogger(Drawing.class).log(Level.SEVERE, "Drawing might not have been properly scaled");

        getImage().draw(xpos, ypos, scale);
    }

    public boolean isMouseWithinDrawing(GameContainer gc) {
        Input input = gc.getInput();

        float wscale = gc.getWidth() / getWidth();
        float hscale = gc.getHeight() / getHeight();
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

        float wscale = gc.getWidth() / getWidth();
        float hscale = gc.getHeight() / getHeight();
        float scale = Math.min(wscale, hscale);

        int xpos = (gc.getWidth() - (int)(scale * getWidth())) / 2;
        int ypos = (gc.getHeight() - (int)(scale * getHeight())) / 2;

        int mouseX = (int)((input.getMouseX() - xpos) / scale);
        int mouseY = (int)((input.getMouseY() - ypos) / scale);

        return new Point(mouseX, mouseY);
    }
}
