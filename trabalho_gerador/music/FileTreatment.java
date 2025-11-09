package music;

import javax.sound.midi.Track;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileTreatment {
    private File file;
    private SoundTrack soundTrack;

    @FunctionalInterface
    private interface SoundTrackAction {
        void execute(SoundTrack sound, char character, char previousCharacter, int previousNote);
    }

    private static final Map<Character, SoundTrackAction> ACTION_MAP;


    public FileTreatment(File arquivo, SoundTrack soundTrack){
        this.file = arquivo;
        this.soundTrack = soundTrack;
    }

    public void readCharacterByCharacter() {
        String filePath = file.getAbsolutePath();

        try (FileReader fileReader = new FileReader(filePath)) {
            int characterInt;
            char previousCharacter = '\0';
            int previousNote = Note.DO;

            while ((characterInt = fileReader.read()) != -1) {

                char character = (char) characterInt;

                processCharacter(character, previousCharacter, previousNote);
                previousCharacter = character;

                if (Note.isNote(character)) {
                    previousNote = Note.charToNote(character);
                }
            }
        } catch (IOException e) {

            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    static {
        ACTION_MAP = new HashMap<>();

        // Letras que geram nova nota
        for (char c : "ABCDEFGHabcdefgh".toCharArray()) {
            ACTION_MAP.put(c, SoundTrack::handleNewNote);
        }

        // Ações únicas
        ACTION_MAP.put(' ', SoundTrack::doubleVolume);
        ACTION_MAP.put('+', SoundTrack::increaseOctave);
        ACTION_MAP.put('-', SoundTrack::decreaseOctave);
        ACTION_MAP.put('?', SoundTrack::randomNote);
        ACTION_MAP.put('\n', SoundTrack::newInstrument);
        ACTION_MAP.put(';', SoundTrack::pause);

        // Letras que repetem nota
        for (char c : "OoIiUu".toCharArray()) {
            ACTION_MAP.put(c, SoundTrack::repeatNote);
        }
    }

    private String previousFourCharacters = " ".repeat(4);

    private String updatePreviousCharacters(String previous, char character) {
        return previous.substring(1) + character;
    }

    private SoundTrackAction lastAction = null;
    private char lastCharacter;
    private char lastpreviousCharacther;
    int lastpreviousNote;

    public void processCharacter(char character, char previousCharacter, int previousNote) {
        previousFourCharacters = updatePreviousCharacters(previousFourCharacters, character);
        SoundTrackAction action = ACTION_MAP.get(character);

        if (previousFourCharacters.equals("BPM+")) {
            soundTrack.increaseBPM(80.0F);
        }
        else if (action != null) {
            action.execute(soundTrack, character, previousCharacter, previousNote);
        }
        else if (lastAction != null) {
            lastAction.execute(soundTrack, lastCharacter, lastpreviousCharacther, lastpreviousNote);
            return;
        }
        lastAction = action;
        lastCharacter = character;
        lastpreviousCharacther = previousCharacter;
        lastpreviousNote = previousNote;
    }
}
