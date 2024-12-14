package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import Tetris.Piezas;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

//musica de fondo
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
//


public class Tablero extends JFrame {
    private static final int FILAS = 20;
    private static final int COLUMNAS = 10;
    private final JPanel[][] celdas;
    private final boolean[][] board;
    private int piezaX = 4, piezaY = 0; // Posición inicial de la pieza
    private Piezas piezaActual;
    private Timer timer;
    private int score = 0; // Puntaje del jugador
    private JTextField puntaje;
    private Clip fondo;
    private static final String[] canciones = {"19.wav"}; //para agregar mas canciones
    
//Reproduccion de cancion    
private Clip reproducirAudio(String nombreArchivo) {
    try {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(
            getClass().getResource("/Media/" + nombreArchivo)
        );
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir el audio en bucle
        clip.start();
        return clip;
    } catch (Exception e) {
        return null;
    }
}

    public Tablero() {
        setTitle("Tablero de Tetris");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String cancionA = canciones[new Random().nextInt(canciones.length)];
        fondo = reproducirAudio(cancionA);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        add(panelPrincipal);
        
        //panelPrincipal.setBackground(Color.BLACK);

        JPanel panelTablero = new JPanel(new GridLayout(FILAS, COLUMNAS));
        celdas = new JPanel[FILAS][COLUMNAS];
        board = new boolean[FILAS][COLUMNAS];

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                celdas[fila][col] = new JPanel();
                celdas[fila][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panelTablero.add(celdas[fila][col]);
            }
        }

        panelPrincipal.add(panelTablero, BorderLayout.CENTER);
        
        //puntaje y posicion
        JPanel panelPuntaje = new JPanel();
        panelPuntaje.setLayout(new GridLayout(2,1));
        panelPuntaje.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel pt = new JLabel("Puntaje");
        pt.setHorizontalAlignment(JLabel.CENTER);
        puntaje = new JTextField(String.valueOf(score));
        puntaje.setEditable(false); // No editable para que solo muestre el puntaje
        puntaje.setHorizontalAlignment(JTextField.CENTER);
        
        panelPuntaje.add(pt);
        panelPuntaje.add(puntaje);
        panelPrincipal.add(panelPuntaje, BorderLayout.EAST);
        
        //boton salir menu principal
        JButton salirb = new JButton("Menu principal");
        salirb.addActionListener((ActionEvent e) -> {
            
            if(fondo != null && fondo.isRunning()){
                fondo.stop();
            }
            
            Menu menu = new Menu(); //redirige al menu
            menu.setVisible(true); //hace visible la interfaz
            menu.setLocationRelativeTo(null); //coloca en el centro
            dispose(); //la ventana de Tablero se cierra y solo deja la de Menu 
        });
        panelPrincipal.add(salirb, BorderLayout.SOUTH);

        //inicia la pieza y escoge la pieza
        generarPiezaAleatoria();
        dibujarPieza();
        
        
        //teclas de movimiento
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> moverPieza(-1, 0);
                    case KeyEvent.VK_D -> moverPieza(1, 0);
                    case KeyEvent.VK_S -> moverPieza(0, 1);
                    case KeyEvent.VK_SPACE -> rotarPieza();
                }
            }
        });
        setFocusable(true);
        
        //movimiento automatico de piezas
        timer = new Timer(500, (ActionEvent e) -> {
            if (!moverPieza(0, 1)) {
                fijarPieza();
                int linesCleared = lineaL();
                Puntaje(linesCleared); // Sumar puntaje por lineas completadas
                velocidad();      // Actualizar la velocidad del juego
                if (Fin()) {
                    timer.stop();
                    System.out.println("Juego terminado");
                } else {
                    generarPiezaAleatoria();
                }
            }
        });
        timer.start();
    }

    private void generarPiezaAleatoria() {
        Piezas.TipoPieza[] tipos = Piezas.TipoPieza.values();
        Random random = new Random();
        Piezas.TipoPieza tipoAleatorio = tipos[random.nextInt(tipos.length)];
        piezaActual = new Piezas(tipoAleatorio);
        piezaX = 4;
        piezaY = 0;
    }

    private boolean moverPieza(int dx, int dy) {
        if (!Colision(piezaX + dx, piezaY + dy, piezaActual.getForma())) {
            limpiarPieza();
            piezaX += dx;
            piezaY += dy;
            dibujarPieza();
            return true;
        } else {
            return false;
        }
    }

    private void rotarPieza() {
        limpiarPieza();
        piezaActual.rotar();
        if (Colision(piezaX, piezaY, piezaActual.getForma())) {
            piezaActual.rotar();
        }
        dibujarPieza();
    }

    //verificacion de colision 
    private boolean Colision(int nx, int ny, int[][] forma) {
        //recorre la forma de la pieza
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                
                if (forma[i][j] != 0) {
                    int x = nx + j; //calcula la posicion x en el tablero
                    int y = ny + i; //calcula la posicion y en el tablero
                    
                    //comprobacion si la poscion esta fuera del tablero
                    if (x < 0 || x >= COLUMNAS || y >= FILAS) {
                        return true;
                    }
                    //comprobacion si hay una pieza en la posicion
                    if (y >= 0 && board[y][x]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

     //fija la pieza actual en el tablero
    private void fijarPieza() {
        int[][] forma = piezaActual.getForma(); //optiene la forma de la pieza actual
        Color color = piezaActual.getColor();  // Obtener el color de la pieza
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] != 0) {
                    int x = piezaX + j; //posicion x en el tablero
                    int y = piezaY + i; //posicion y en el tablero
                    
                    //fija el bloque en el tablero
                    if (y >= 0 && x >= 0 && x < COLUMNAS && y < FILAS) {
                        board[y][x] = true;
                        celdas[y][x].setBackground(color); // Asignar el color a la celda
                    }
                }
            }
        }
    }
    
    //elimina las lineas completas del tablero
    private int lineaL() {
        int linesCleared = 0;
        for (int y = 0; y < FILAS; y++) {
            boolean fullLine = true;
            for (int x = 0; x < COLUMNAS; x++) {
                if (!board[y][x]) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                linesCleared++;
                for (int j = y; j > 0; j--) {
                    for (int i = 0; i < COLUMNAS; i++) {
                        board[j][i] = board[j - 1][i];
                        celdas[j][i].setBackground(celdas[j - 1][i].getBackground());
                    }
                }
                for (int i = 0; i < COLUMNAS; i++) {
                    board[0][i] = false;
                    celdas[0][i].setBackground(null);
                }
            }
        }
        return linesCleared;
    }
       
    //verificacion si el juego a terminado
    private boolean Fin() {
        //recorre la primera fila del tablero
        for (int x = 0; x < COLUMNAS; x++) {
            if (board[0][x]) {
                timer.stop();
                 JOptionPane.showMessageDialog(this, "Juego terminado\n Puntaje: " + score, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        return false;
    }
    
    //actualiza el puntaje y lo muestra en la interfaz
    private void Puntaje(int lines) {
        score += lines * 100; //incrementa el puntaje dependiendo la linea
        puntaje.setText(String.valueOf(score));
        //System.out.println("Puntaje: " + score);
    }
    
    // actualiza la velocidad dependiendo del puntaje
    private void velocidad() {
        if (score > 500) {
            timer.setDelay(400); // Aumenta la velocidad si el puntaje es mayor a 500
        }
        if (score > 1000) {
            timer.setDelay(300); // Aumenta aún más la velocidad
        }
        if (score > 2000) {
            timer.setDelay(200); // Más velocidad
        }
    }

    private void dibujarPieza() {
        int[][] forma = piezaActual.getForma();
        Color color = piezaActual.getColor();  // Obtener el color de la pieza
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] == 1) {
                    int x = piezaX + j;
                    int y = piezaY + i;
                    if (x >= 0 && x < COLUMNAS && y >= 0 && y < FILAS) {
                        celdas[y][x].setBackground(color);  // Usar el color de la pieza
                    }
                }
            }
        }
    }
    
    private void limpiarPieza() {
        int[][] forma = piezaActual.getForma();
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] == 1) {
                    int x = piezaX + j;
                    int y = piezaY + i;
                    if (x >= 0 && x < COLUMNAS && y >= 0 && y < FILAS) {
                        celdas[y][x].setBackground(null);
                    }
                }
            }
        }
    }
}
