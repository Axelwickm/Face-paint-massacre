package jaam.fpm.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Note {
    private Vector2f position;
    private Image image;

    public Note(byte[] image, Vector2f position) {
        //this.image = image;
        this.position = position;
    }

    public void update(final GameContainer gameContainer, final int dt) {

    }

    public void render(final Graphics g) {

    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
