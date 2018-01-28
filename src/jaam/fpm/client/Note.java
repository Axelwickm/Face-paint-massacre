package jaam.fpm.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.geom.Vector2f;

public class Note {
    private Vector2f position;
    private Image image;

    public Note(byte[] image, Vector2f position) {
        this.image = decodeImage(image, 256, 256);
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

    public Image decodeImage(byte[] data, int width, int height) {
        ImageBuffer ib = new ImageBuffer(width, height);
        for (int i = 0; i < data.length; i += 4) {
            int r = data[i + 0];
            int g = data[i + 1];
            int b = data[i + 2];
            int a = data[i + 3];

            System.out.println(r + " "+ g+ " " +b +" "+ a);

            ib.setRGBA((i / 4) % width, Math.floorDiv((i / 4), width), r, g, b, a);
        }
        
        return ib.getImage();
    }
}
