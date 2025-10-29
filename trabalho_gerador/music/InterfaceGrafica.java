package music;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;


public class InterfaceGrafica extends JFrame {

    private MusicPlayer tocar;
    private boolean isPlaying = false;
    private File file;

    public static void readCharacterByCharacter(File file, SoundTrack soundTrack) {
        String filePath = file.getAbsolutePath();

        try (FileReader fileReader = new FileReader(filePath)) {
            int characterInt;
            char previousCharacter = '\0';
            int previousNote = Note.DO;

            while ((characterInt = fileReader.read()) != -1) {

                char character = (char) characterInt;

                soundTrack.processCharacter(character, previousCharacter, previousNote);
                previousCharacter = character;

                if (soundTrack.isNote(character)) {
                    previousNote = Note.charToNote(character);
                }
            }
        } catch (IOException e) {

            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    public InterfaceGrafica() {
        super("Mauri music");
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JButton EscolherFile = new JButton("Escolher Arquivo");
        JButton PlayandPause = new JButton("Start / Pause");
        JButton restart = new JButton("Restart");

        JButton[] botoes = {EscolherFile, PlayandPause, restart};
        for (JButton botao : botoes) {
            botao.setBackground(Color.DARK_GRAY);
            botao.setForeground(Color.WHITE);     
            botao.setFont(new Font("Arial", Font.BOLD, 14));
            botao.setMargin(new Insets(10, 20, 10, 20)); 
        }

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(Color.BLACK); 
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); 
        painelBotoes.add(EscolherFile);
        painelBotoes.add(PlayandPause);
        painelBotoes.add(restart);

        add(painelBotoes, BorderLayout.CENTER);

        // Botão escolher arquivo
        EscolherFile.addActionListener(e -> {
            JFileChooser escolher = new JFileChooser();
            int result = escolher.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = escolher.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Arquivo selecionado: " + file.getName());
                if (isPlaying) { //caso botão seja clicado durante o play -> pausar
                    tocar.pause();
                    isPlaying = false; 
                }

                try {
                    tocar = new MusicPlayer();

                    Track[] tracks = tocar.getSequence().getTracks();
                    int tamanho = tracks.length;

                    for (int i = 0; i < tamanho; i++) {
                        Track track = tracks[i];
                        tocar.deleteTrack(track);
                    }

                    SoundTrack soundTrack = tocar.createTrack();

                    readCharacterByCharacter(file, soundTrack);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Botão Play/Pause
        PlayandPause.addActionListener(e -> {
            if (tocar != null) {
                if (isPlaying) { //caso botão seja clicado durante o play -> pausar
                    tocar.pause();
                    isPlaying = false; 
                } else {
                    tocar.play();
                    isPlaying = true;
                }
            }
        });

        // Botão Restart
        restart.addActionListener(e -> {
            if (tocar != null) {
                tocar.restart();
                isPlaying = true;
            }
        });

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setPlayer(MusicPlayer player) {
        this.tocar = player;
    }

    public void run() {
        SwingUtilities.invokeLater(InterfaceGrafica::new);        
    }
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceGrafica::new);
    }*/
}