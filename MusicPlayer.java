package music;

import javax.sound.midi.*;

public class MusicPlayer {
    private Sequencer controlador;
    private Sequence sequencia;

    public MusicPlayer() throws Exception {
        controlador = MidiSystem.getSequencer();
        controlador.open();
        sequencia = new Sequence(sequencia.PPQ, 4);
    }

    // Carrega um array de notas como trilha MIDI
    public void loadMusic(Note[] notes) {
        try {
            Track track = sequencia.createTrack();
            SoundTrack soundTrack = new SoundTrack(track, 0, notes); // canal 0
            soundTrack.createTrack(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            controlador.setSequence(sequencia);
            controlador.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (controlador.isRunning()) {
            controlador.stop();
        }
    }

    public void restart() {
        controlador.setTickPosition(0);
        controlador.start();
    }

    public void close() {
        controlador.close();
    }
}
