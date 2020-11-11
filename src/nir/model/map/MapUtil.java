package nir.model.map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

public class MapUtil {
    public static Color[][] loadPixelsFromImage(BufferedImage image) throws IOException {
        Color[][] colors = new Color[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x][y] = new Color(image.getRGB(x, y));
            }
        }

        return colors;
    }

    public static void saveImage(byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream("image.jpg");
            try {
                fos.write(data);
            } finally {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}