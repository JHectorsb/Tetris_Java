package Tetris;

import java.awt.Color;
import java.util.Random;
import color.Colores;

public class Piezas {

    private int[][] forma;
    private Color color; // Atributo para el color de la pieza

    private static final Color[] colores = {
        //Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.YELLOW
        Colores.Rojo_o, Colores.Azul_os, Colores.Rosa_o, Colores.Verde_o,
        Colores.Azul_o, Colores.Naranja, Colores.morado    
    };

    private static Random random = new Random();

    public enum TipoPieza {
        I, O, T, L, J, S, Z
    }

    // Constructor de la pieza
    public Piezas(TipoPieza tipo) {
        switch (tipo) {
            case I -> forma = new int[][] {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
                };
            
            case O -> forma = new int[][] {
                    {1, 1},
                    {1, 1}
                };
            
            case T -> forma = new int[][] {
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
                };
            case L -> forma = new int[][] {
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
                };
            case J -> forma = new int[][] {
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
                };
            case S -> forma = new int[][] {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0}
                };
            case Z -> forma = new int[][] {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0}
                };
        }
        // Asignar un color aleatorio a la pieza
        this.color = obtenerColorAleatorio();
    }

    // Método para obtener un color aleatorio
    private Color obtenerColorAleatorio() {
        return colores[random.nextInt(colores.length)];
    }

    // Getter para la forma de la pieza
    public int[][] getForma() {
        return forma;
    }

    // Getter para el color de la pieza
    public Color getColor() {
        return color;
    }

    // Método para rotar la pieza
    public void rotar() {
        int n = forma.length;
        int[][] nuevaForma = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                nuevaForma[j][n - 1 - i] = forma[i][j];
            }
        }
        forma = nuevaForma;
    }
}
