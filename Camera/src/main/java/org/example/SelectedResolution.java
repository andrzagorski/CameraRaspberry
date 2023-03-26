package org.example;

public class SelectedResolution {

    enum ResolutionOption {
        RES_640x480(640, 480),
        RES_1280x720(1280, 720),
        RES_1920x1080(1920, 1080),
        RES_2312x1736(2312, 1736),
        RES_3840x2160(3840, 2160),
        RES_4624x3472(4624, 3472),
        RES_9152x6944(9152, 6944);

        private final int width;
        private final int height;

        ResolutionOption(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
        @Override
        public String toString() {
            return width + "x" + height;
        }
    }
}
