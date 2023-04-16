package org.example;

/**
	\file SelectedResolution.java
	\brief Plik klasą definiującą rozdzielczość ekranu.
*/

/**
	\brief Klasa definiująca rozdzielczość ekranu 
	
	z enum z domyślnymi rozdzielczościami do wyboru w GUI.
*/
public class SelectedResolution {
	//! Domyślne rozdzielczości.
    enum ResolutionOption {
        RES_640x480(640, 480),
        RES_1280x720(1280, 720),
        RES_1920x1080(1920, 1080),
        RES_2312x1736(2312, 1736),
        RES_3840x2160(3840, 2160),
        RES_4624x3472(4624, 3472),
        RES_9152x6944(9152, 6944);

		//! Szerokość ekranu.
        private final int width;

		//! Wysokość ekranu.
        private final int height;

		//! Setter rozdzielczości ekranu.
        ResolutionOption(int width, int height) {
            this.width = width;
            this.height = height;
        }

		//! Getter szerokości ekranu.
        public int getWidth() {
            return width;
        }

		//! Getter wysokości ekranu.
        public int getHeight() {
            return height;
        }

		//! Przeciążenie funkcji toString klasy Object.
        @Override
        public String toString() {
            return width + "x" + height;
        }
    }


    enum ResolutionOptionVideo { // Unfortunately Video works at 1080p max
        RES_640x480(640, 480),
        RES_1280x720(1280, 720),
        RES_1920x1080(1920, 1080);

		//! Szerokość ekranu.
        private final int width;

		//! Wysokość ekranu.
        private final int height;

		//! Setter rozdzielczości ekranu.
        ResolutionOptionVideo(int width, int height) {
            this.width = width;
            this.height = height;
        }

		//! Getter szerokości ekranu.
        public int getWidth() {
            return width;
        }

		//! Getter wysokości ekranu.
        public int getHeight() {
            return height;
        }

		//! Przeciążenie funkcji toString klasy Object.
        @Override
        public String toString() {
            return width + "x" + height;
        }
    }
}
