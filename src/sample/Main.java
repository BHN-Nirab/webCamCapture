package sample;

import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class Main {

    static final int SIZE = 4;
    static int trackRedX[] = new int[SIZE];
    static int trackRedY[] = new int[SIZE];
    public static void main(String[] args) throws IOException {

//        final int SIZE = 4;
        /*nt trackRedX[] = new int[SIZE];
        int trackRedY[] = new int[SIZE];*/

        for (int i = 0; i < SIZE; i++) trackRedX[i] = -1;
        for (int i = 0; i < SIZE; i++) trackRedY[i] = -1;

        Webcam webcam = Webcam.getDefault();

        webcam.setViewSize(new Dimension(176, 144));

        System.out.println("-----------------------Started------------------------");

        webcam.open();


        int i = 0, skip = 0;
        int tmp = 1;

        while (tmp != 0) {
            ImageIO.write(webcam.getImage(), "JPG", new File("frame1.jpg"));

            Tracker track = new Tracker();
            track.generateTracker("frame1.jpg", 315, 360, 60, 60);
            int pos[] = track.getPos();

            if (pos[0] != -1 && pos[1] != -1 && skip <= 5) {
                trackRedX[i] = pos[0];
                trackRedY[i] = pos[1];
                i++;
            } else {
                skip++;
            }

            if (i == SIZE) {
                i = 0;

                boolean xIncr = true;
                boolean xDecr = true;

                boolean yIncr = true;
                boolean yDecr = true;

                // Check x axis

                if (xIncr) {
                    for (int j = 0; j < SIZE - 1; j++) {
                        if ((trackRedX[j] > trackRedX[j + 1]) && trackRedX[j] != -1) {
                            xIncr = false;
                            break;
                        }
                    }
                }

                if (xDecr) {
                    for (int j = 0; j < SIZE - 1; j++) {
                        if ((trackRedX[j] < trackRedX[j + 1]) && trackRedX[j] != -1) {
                            xDecr = false;
                            break;
                        }
                    }
                }

                if (xIncr && (Math.abs(trackRedX[0]-trackRedX[SIZE-1]))>=35) {
                    System.out.println("---Moved Left---" + "---> " + trackRedX[0] + " , " + trackRedX[SIZE-1]);
                    xIncr = true;
                } else if (xDecr && (Math.abs(trackRedX[0]-trackRedX[SIZE-1]))>=35) {
                    System.out.println("---Moved Right---" + "---> " + trackRedX[0] + " , " + trackRedX[SIZE-1]);
                    xDecr = true;
                }

                //check y axis

                if (yIncr) {
                    for (int j = 0; j < SIZE - 1; j++) {
                        if ((trackRedY[j] > trackRedY[j + 1]) && trackRedY[j] != -1) {
                            yIncr = false;
                            break;
                        }
                    }
                }

                if (yDecr) {
                    for (int j = 0; j < SIZE - 1; j++) {
                        if ((trackRedY[j] < trackRedY[j + 1]) && trackRedY[j] != -1) {
                            yDecr = false;
                            break;
                        }
                    }
                }

                if (yIncr && (Math.abs(trackRedY[0]-trackRedY[SIZE-1]))>=20) {
                    System.out.println("---Moved Down---" + "---> " + trackRedY[0] + " , " + trackRedY[SIZE-1]);
                    yIncr = true;
                } else if (yDecr && (Math.abs(trackRedY[0]-trackRedY[SIZE-1]))>=20) {
                    System.out.println("---Moved UP---" + "---> " + trackRedY[0] + " , " + trackRedY[SIZE-1]);
                    yDecr = true;
                }


                for (int j = 0; j < SIZE; j++) trackRedX[j] = -1;
                for (int j = 0; j < SIZE; j++) trackRedY[j] = -1;
            }

            if (skip == 5) skip = 0;

        }

        webcam.close();
    }
}
