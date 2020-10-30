package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tracker {
    public void generateTracker(String path, int lowerBoundHue, int upperBoundHue, int lowerBoundSaturation, int lowerBoundValue) throws IOException {
        //Reading image

        File input = new File(path);
        BufferedImage image = ImageIO.read(input);

        int height, width;
        height = image.getHeight();
        width = image.getWidth();

        int row, col;

        //Segment image (Sample color is Shade of Blue)

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                int p = image.getRGB(col, row);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int r1, g1, b1;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);

                if ((hsb[0] * 360 >= lowerBoundHue && hsb[0] * 360 <= upperBoundHue) && (hsb[1] * 360 >= lowerBoundSaturation) && (hsb[2] * 360 >= lowerBoundValue)) {
                    r1 = g1 = b1 = 0;
                } else {
                    r1 = g1 = b1 = 255;
                }

                int p1 = (a << 24) | (r1 << 16) | (g1 << 8) | (b1);

                image.setRGB(col, row, p1);

            }
        }

        //Write segmented image (Binary format)

        File output = new File("output.jpg");
        ImageIO.write(image, "jpg", output);

        //Input a binary image and doing erosion

        input = new File("output.jpg");
        image = ImageIO.read(input);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                //Make a sample of 3*3 matrix

                int m, n;
                m = row + 2;
                n = col + 2;

                //If not match then make most upper left coordinate to white

                boolean fullyMatch = true;

                if (m < height && n < width) {
                    for (int k = row; k <= m; k++) {
                        for (int l = col; l <= n; l++) {
                            int p = image.getRGB(l, k);
                            int a = (p >> 24) & 0xff;
                            int r = (p >> 16) & 0xff;
                            int g = (p >> 8) & 0xff;
                            int b = p & 0xff;

                            if (r > 200 && g > 200 && b > 200) {
                                fullyMatch = false;
                                break;
                            }
                        }
                    }
                    if (!fullyMatch) {
                        int p = image.getRGB(col, row);
                        int a = (p >> 24) & 0xff;
                        int r, g, b;

                        r = g = b = 255;
                        int p1 = (a << 24) | (r << 16) | (g << 8) | (b);

                        image.setRGB(col, row, p1);
                        fullyMatch = true;
                    } else {
                        int p = image.getRGB(col, row);
                        int a = (p >> 24) & 0xff;
                        int r, g, b;

                        r = g = b = 0;
                        int p1 = (a << 24) | (r << 16) | (g << 8) | (b);

                        image.setRGB(col, row, p1);

                    }

                }

            }
        }

        //Write binary image after erosion

        output = new File("output2.jpg");
        ImageIO.write(image, "jpg", output);


        //Dilation

        input = new File("output2.jpg");
        image = ImageIO.read(input);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                //Make a sample of 3*3 matrix

                int m, n;
                m = row + 2;
                n = col + 2;

                //Make most uper left coordinate of inputed image to black
                //Dilation process filled any gap of an image and erosion is clear any noise

                boolean Match = false;

                if (m < height && n < width) {
                    for (int k = row; k <= m; k++) {
                        for (int l = col; l <= n; l++) {
                            int p = image.getRGB(l, k);
                            int a = (p >> 24) & 0xff;
                            int r = (p >> 16) & 0xff;
                            int g = (p >> 8) & 0xff;
                            int b = p & 0xff;

                            if (r < 50 && g < 50 && b < 50) {
                                Match = true;
                                break;
                            }
                        }
                    }
                    if (Match) {
                        int p = image.getRGB(col, row);
                        int a = (p >> 24) & 0xff;
                        int r, g, b;
                        r = g = b = 0;
                        int p1 = (a << 24) | (r << 16) | (g << 8) | (b);

                        image.setRGB(col, row, p1);
                        Match = false;
                    } else {
                        int p = image.getRGB(col, row);
                        int a = (p >> 24) & 0xff;
                        int r, g, b;
                        r = g = b = 255;
                        int p1 = (a << 24) | (r << 16) | (g << 8) | (b);

                        image.setRGB(col, row, p1);
                    }

                }

            }
        }

        output = new File("output3.jpg");
        ImageIO.write(image, "jpg", output);

        //Track image (Tracking mark is RED)
        //After opening process (Erosion and Dilation) now its time to track it.

        input = new File("output3.jpg");
        image = ImageIO.read(input);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                int m, n;
                m = row + 10;
                n = col + 10;

                boolean Match = true;

                if (m < height && n < width) {
                    for (int k = row; k <= m; k++) {
                        for (int l = col; l <= n; l++) {
                            int p = image.getRGB(l, k);
                            int a = (p >> 24) & 0xff;
                            int r = (p >> 16) & 0xff;
                            int g = (p >> 8) & 0xff;
                            int b = p & 0xff;

                            if (r > 200 && g > 200 && b > 200) {
                                Match = false;
                                break;
                            }
                        }
                    }
                    if (Match) {
                        for (int k = row; k <= m; k++) {
                            for (int l = col; l <= n; l++) {
                                int p = image.getRGB(l, k);
                                int a = (p >> 24) & 0xff;
                                int r = (p >> 16) & 0xff;
                                int g = (p >> 8) & 0xff;
                                int b = p & 0xff;

                                r = 255;
                                g = b = 0;
                                int p1 = (a << 24) | (r << 16) | (g << 8) | (b);

                                image.setRGB(l, k, p1);
                            }
                        }
                        Match = false;
                    }

                }

            }
        }

        output = new File("output4.jpg");
        ImageIO.write(image, "jpg", output);

    }

    public int[] getPos() throws IOException {
        int pos[] = new int[2];

        pos[0] = -1;
        pos[1] = -1;

        try {
            File input = new File("output4.jpg");
            BufferedImage image = ImageIO.read(input);

            int height, width;
            height = image.getHeight();
            width = image.getWidth();

            int row, col;

            //Segment image (Sample color is Shade of Blue)

            for (row = 0; row < height; row++) {
                for (col = 0; col < width; col++) {
                    int p = image.getRGB(col, row);
                    int a = (p >> 24) & 0xff;
                    int r = (p >> 16) & 0xff;
                    int g = (p >> 8) & 0xff;
                    int b = p & 0xff;


                    // System.out.println(r + " , " + g + " , " + b + " (" + col + " , " + row + ")");

                    if (r >= 240 && (g <= 10 && b <= 10)) {
                        pos[0] = col;
                        pos[1] = row;
                        // System.out.println(r + " , " + g + " , " + b + " (" + col + " , " + row + ")" + " ***");
                        return pos;
                    }

                }
            }

            //System.out.println("End of reading image...!");
        } catch (Exception e) {
            return pos;
        }

        return pos;
    }

}