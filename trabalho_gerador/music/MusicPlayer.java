package music;

import javax.sound.midi.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MusicPlayer {
    private Sequencer controller;
    private Sequence sequence;

    public MusicPlayer() throws Exception {
        controller = MidiSystem.getSequencer();
        controller.open();
        sequence = new Sequence(sequence.PPQ, 4);
    }

    public Sequence getSequence() {
        return this.sequence;
    }
    public Sequencer getController() {return this.controller;}

    public SoundTrack createTrack(int currentInstrument, int volumeAtual) {
        Track track = this.sequence.createTrack();
        return new SoundTrack(track,currentInstrument,volumeAtual);

    }

    public boolean deleteTrack(Track track) {
        return this.sequence.deleteTrack(track);
    }

    public void play() {
        try {
            controller.setSequence(sequence);
            controller.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (controller.isRunning()) {
            controller.stop();
        }
    }

    public void restart() {
        controller.setTickPosition(SoundTrack.TIME_BEGIN);
        controller.start();
    }


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

    public boolean isRunning() {
        return this.controller.isRunning();
    }

    public void close() {
        controller.close();
    }
}