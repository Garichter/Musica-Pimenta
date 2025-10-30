package music;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class InterfaceGrafica extends JFrame {

    private MusicPlayer tocar;
    private boolean isPlaying = false;
    private File file;
    private JTextArea areaTexto;
    private File arquivoDigitado;
    private int instrumentoAtual = 0; // 0 = piano, 24 = guitarra, 25 = violão

    public InterfaceGrafica() {
        super("Mauri music");
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JButton EscolherFile = new JButton("Escolher Arquivo");
        JButton NovoTexto = new JButton("Novo Texto");
        JButton LerDoTeclado = new JButton("Ler Texto do Teclado");
        JButton PlayandPause = new JButton("Start / Pause");
        JButton restart = new JButton("Restart");
        JButton EscolherInstrumento = new JButton("Escolher Instrumento");

        JButton[] botoes = {EscolherFile, NovoTexto, LerDoTeclado, PlayandPause, restart, EscolherInstrumento};
        for (JButton botao : botoes) {
            botao.setBackground(Color.DARK_GRAY);
            botao.setForeground(Color.WHITE);
            botao.setFont(new Font("Arial", Font.BOLD, 14));
            botao.setMargin(new Insets(10, 20, 10, 20));
        }

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(Color.BLACK);
        painelBotoes.setLayout(new GridLayout(0, 5, 10, 10));
        for (JButton b : botoes) painelBotoes.add(b);

        add(painelBotoes, BorderLayout.NORTH);

        EscolherFile.addActionListener(e -> {
            JFileChooser escolher = new JFileChooser();
            int result = escolher.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = escolher.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Arquivo selecionado: " + file.getName());
                prepararLeitura(file);
            }
        });

        NovoTexto.addActionListener(e -> abrirJanelaTexto());

        LerDoTeclado.addActionListener(e -> {
            if (arquivoDigitado != null && arquivoDigitado.exists()) {
                prepararLeitura(arquivoDigitado);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum texto digitado encontrado!");
            }
        });

        PlayandPause.addActionListener(e -> {
            if (tocar != null) {
                if (isPlaying) {
                    tocar.pause();
                    isPlaying = false;
                } else {
                    tocar.play();
                    isPlaying = true;
                }
            }
        });

        restart.addActionListener(e -> {
            if (tocar != null) {
                tocar.restart();
                isPlaying = true;
            }
        });

        EscolherInstrumento.addActionListener(e -> {
            String[] opcoes = {"Piano", "Violão", "Guitarra"};
            String escolha = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha o instrumento:",
                    "Instrumento MIDI",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha != null) {
                switch (escolha) {
                    case "Piano" -> instrumentoAtual = 0;
                    case "Violão" -> instrumentoAtual = 24;
                    case "Guitarra" -> instrumentoAtual = 26;
                }
                if (tocar != null) {
                    tocar.setInstrument(instrumentoAtual); // supondo que exista esse método
                }
                JOptionPane.showMessageDialog(this, "Instrumento selecionado: " + escolha);
            }
        });

        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void abrirJanelaTexto() {
        JFrame janelaTexto = new JFrame("Editor de Texto");
        janelaTexto.setSize(600, 400);
        janelaTexto.setLocationRelativeTo(this);

        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 14));

        JButton salvar = new JButton("Salvar Texto");
        salvar.addActionListener(e -> {
            try {
                arquivoDigitado = new File("texto_digitado.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoDigitado))) {
                    writer.write(areaTexto.getText());
                }
                JOptionPane.showMessageDialog(janelaTexto, "Texto salvo em: " + arquivoDigitado.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(janelaTexto, "Erro ao salvar: " + ex.getMessage());
            }
        });

        janelaTexto.add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        janelaTexto.add(salvar, BorderLayout.SOUTH);
        janelaTexto.setVisible(true);
    }

    private void prepararLeitura(File arquivo) {
        try {
            if (isPlaying) {
                tocar.pause();
                isPlaying = false;
            }
            tocar = new MusicPlayer();

            Track[] tracks = tocar.getSequence().getTracks();
            for (Track track : tracks) tocar.deleteTrack(track);

            SoundTrack soundTrack = tocar.createTrack();
            tocar.setInstrument(instrumentoAtual);
            MusicPlayer.readCharacterByCharacter(arquivo, soundTrack);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPlayer(MusicPlayer player) {
        this.tocar = player;
    }

    public void run() {
        SwingUtilities.invokeLater(InterfaceGrafica::new);
    }
}
