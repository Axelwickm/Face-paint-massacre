package com.fisherevans.procedural_generation.dungeons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

public class DungeonRenderer {
	public static final String FILE_PREFIX = "dungeon_";

	public static Color darkGray = new Color(38, 38, 38);
	public static Color gray = new Color(168, 168, 168);
	public static Color red = new Color(168, 85, 85);
    public static Color blue = new Color(85, 131, 168);
    public static Color green = new Color(85, 168, 131);

	public static void view() {
		//*
		for(File file:new File("gen").listFiles()) {
			if(file.getName().startsWith(FILE_PREFIX)) {
				file.delete();
			}
		}

		saveImage(0, drawFinalDungeon(),"Map");
	}
	
	public static BufferedImage drawFinalDungeon() {
		int size = (DungeonGenerator.mapSize+DungeonGenerator.maxSideLength)*4;
		int half = size/2;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();

		g.setColor(darkGray);
		g.fillRect(0,  0,  size, size);

		Room.expandAll(DungeonGenerator.rooms, -1);
		drawBoxes(DungeonGenerator.rooms, g, half, Color.green);
		drawBoxes(DungeonGenerator.halls, g, half, Color.green);

		Room.expandAll(DungeonGenerator.corridors, -1);
		drawBoxes(DungeonGenerator.corridors, g, half, Color.green);

        return img;
	}
	
	public static void drawFinalDungeon2(int count) {
		int size = (DungeonGenerator.mapSize+DungeonGenerator.maxSideLength)*10;
		int half = size/2;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		BufferedImage img2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img2.createGraphics();

		g.setColor(darkGray);
		g.fillRect(0,  0,  size, size);
		g2.setColor(darkGray);
		g2.fillRect(0,  0,  size, size);

		Room.expandAll(DungeonGenerator.rooms, -1);
		drawBoxes(DungeonGenerator.rooms, g, half, blue);
		drawBoxes(DungeonGenerator.halls, g, half, blue);
		

		drawBorders(DungeonGenerator.corridors, g, half, darkGray);
		Room.expandAll(DungeonGenerator.corridors, -1);
		drawBoxes(DungeonGenerator.corridors, g, half, red);

        g.setColor(green);
        for(Room room:DungeonGenerator.corridors)
            if(room == DungeonGenerator.start || room == DungeonGenerator.end)
                g.fillRect(room.getLeft() + half, room.getTop() + half - room.getHeight(), room.getWidth(), room.getHeight());
		
		replaceColor(img, darkGray, new Color(0, 0, 0, 0));
		

		Room.expandAll(DungeonGenerator.halls, -1);
		drawBoxes(DungeonGenerator.halls, g2, half, gray);
		
		g2.drawImage(img, 0,  0, img.getWidth(), img.getHeight(), null);


        saveImage(count, img2, "final");
	}
	
	public static void drawBoxes(List<Room> rooms, Graphics2D g, int half, Color color) {
		g.setColor(color);
        for(Room room:rooms)
            g.fillRect(room.getLeft() + half, room.getTop() + half - room.getHeight(), room.getWidth(), room.getHeight());
	}
	
	public static void drawBorders(List<Room> rooms, Graphics2D g, int half, Color color) {
		g.setColor(color);
        for(Room room:rooms)
            g.drawRect(room.getLeft()+half, room.getTop()+half-room.getHeight(), room.getWidth()-1, room.getHeight()-1);
	}
	
	public static void replaceColor(BufferedImage img, Color a, Color b) {
		for(int y = 0;y < img.getHeight();y++) {
			for(int x = 0;x < img.getWidth();x++) {
				if(img.getRGB(x, y) == a.getRGB())
					img.setRGB(x, y, b.getRGB());
			}
		}
	}
	
	public static void saveImage(int id, BufferedImage img, String prefix) {
		String filename = FILE_PREFIX + prefix + "_" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()))
				+ "_" + id + "_" + DungeonGenerator.lastSeed + ".png";
		System.out.println("\nSaving image to: " + filename);
		try {
            BufferedImage bigImg = new BufferedImage(img.getWidth()*1, img.getHeight()*1, BufferedImage.TYPE_INT_RGB);
            bigImg.createGraphics().drawImage(img, 0, 0, img.getWidth()*1, img.getHeight()*1, null);
			ImageIO.write(bigImg, "png", new File("gen/" + filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
