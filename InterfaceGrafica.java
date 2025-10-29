package music;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import music.*; 


public class InterfaceGrafica extends JFrame {

    private MusicPlayer tocar;
    private boolean isPlaying = false;
    private File File;

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
                File = escolher.getSelectedFile();
                JOptionPane.showMessageDialog(this,
                        "Arquivo selecionado: " + File.getName());

                Note[] notes = {
                        new Note(0, 4, 100, 0, 400), // dó
                        new Note(0, 4, 100, 2, 400), // ré
                        new Note(0, 4, 100, 4, 400)  // mi
                };

                try {
                    tocar = new MusicPlayer();
                    tocar.loadMusic(notes);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceGrafica::new);
    }
}
