package nir.model.map;

import nir.util.Mat;
import org.locationtech.jts.geom.Coordinate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LevelMap {
    private static int xSize = 1200;
    private static int ySize = 800;

    private int[][] levelMap = new int[xSize][ySize];

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void showLevels() {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                System.out.print(levelMap[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int[][] getLevelMap() {
        return levelMap;
    }

    public void fillMap(int[][] object, int[][] level) {
        levelMap = level;
    }

    public LevelMap() {
        loadMapFromImage("levelmap2.jpg");
    }
    public void loadMapFromImage(String fileName){
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            xSize = image.getWidth();
            ySize = image.getHeight();
            Color[][] colors = MapUtil.loadPixelsFromImage(image);
            levelMap = new int[xSize][ySize];
            for (int i = 0; i < xSize; i++) {
                for (int j = 0; j < ySize; j++) {
                    Color pix = colors[i][j];
                    float[] pixel = Color.RGBtoHSB(pix.getRed(), pix.getGreen(), pix.getBlue(), null);
                    float level = (240f - (pixel[0] * 240f));
                    levelMap[i][j] = (int) level;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLevelUping(int x1, int y1, int x2, int y2) {
        try {
            int v1 = levelMap[x1][y1];
            int v2 = levelMap[x2][y2];
            if (v2 > v1) return v2 - v1;
            else return 0;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 40;
        }

    }

    public int getLineLevelUpping(Coordinate s, Coordinate f) {
        List<Coordinate> list = Mat.line_s4((int) s.x, (int) s.y, (int) f.x, (int) f.y);
        int res = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            int x1 = (int) list.get(i).x;
            int y1 = (int) list.get(i).y;
            int x2 = (int) list.get(i + 1).x;
            int y2 = (int) list.get(i + 1).y;
            res += getLevelUping(x1, y1, x2, y2);
        }
        return res;
    }
}
