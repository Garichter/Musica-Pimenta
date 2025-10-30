package music;

import javax.sound.midi.*;
import java.util.HashMap;
import java.util.Map;

public class SoundTrack {
    public static final int TIME_BEGIN = 24;
    public static final int DEFAULT_VOLUME = 100;
    public static final int MAX_VOLUME = 127;
    public static final int DEFAULT_OCTAVE = -1;
    public static final int MAX_OCTAVE = 9;

    public static final int BANDONEON = 23;
    public static final int GAITA_DE_FOLES = 109;
    public static final int ONDAS_DO_MAR = 122;
    public static final int TUBULAR_BELLS = 14;
    public static final int AGOGO = 113;

    private int volume;
    private int octave;
    private int instrument;

    private Track track;
    private int channel;
    private int tick;

    public int getVolume() {
        return this.volume;
    }
    public int getOctave() {
        return this.octave;
    }
    public int getInstrument() {
        return this.instrument;
    }
    public Track getTrack() {
        return this.track;
    }
    public int getChannel() {
        return this.channel;
    }
    public int getCurrentTick() {
        return this.tick;
    }
    public void setTick(int tick) {
        this.tick = tick;
    }
    public void setTrack(Track track) {
        this.track = track;
    }
    public void setInstrument(int instrument) {this.instrument = instrument;}
    public void setOctave(int octave) {
        this.octave = octave;
    }
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @FunctionalInterface
    private interface SoundTrackAction {
        void execute(SoundTrack sound, char character, char previousCharacter, int previousNote);
    }

    private static final Map<Character, SoundTrackAction> ACTION_MAP;

    public SoundTrack(Track track) {
        this.octave = DEFAULT_OCTAVE;
        this.volume = DEFAULT_VOLUME;
        this.instrument = 0;

        this.track = track;
        this.channel = 0;
        this.tick = TIME_BEGIN;
    }

    static {
        ACTION_MAP = new HashMap<>();

        for (char c = 'A'; c <= 'H'; c++) {
            ACTION_MAP.put(c, SoundTrack::handleNewNote);
        }
        for (char c = 'a'; c <= 'h'; c++) {
            ACTION_MAP.put(c, SoundTrack::handleNewNote);
        }

        ACTION_MAP.put(' ', SoundTrack::doubleVolume);

        ACTION_MAP.put('+', SoundTrack::increaseOctave);

        ACTION_MAP.put('-', SoundTrack::decreaseOctave);

        SoundTrackAction repeatAction = SoundTrack::repeatNote;
        ACTION_MAP.put('O', repeatAction);
        ACTION_MAP.put('o', repeatAction);
        ACTION_MAP.put('I', repeatAction);
        ACTION_MAP.put('i', repeatAction);
        ACTION_MAP.put('U', repeatAction);
        ACTION_MAP.put('u', repeatAction);

        ACTION_MAP.put('?', SoundTrack::randomNote);
        ACTION_MAP.put('\n', SoundTrack::newInstrument);

        ACTION_MAP.put(';', SoundTrack::pause);
    }

    private SoundTrackAction lastAction = null;

    public void processCharacter(char character, char previousCharacter, int previousNote) {
        SoundTrackAction action = ACTION_MAP.get(character);

        if (action != null) {
            action.execute(this, character, previousCharacter, previousNote);
            lastAction = action;
        } else if (lastAction != null){
            lastAction.execute(this, previousCharacter, previousCharacter, previousNote);
        }
    }



    public boolean changeInstrument(int instrument) {
        try {
            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, this.channel, instrument, 0);
            track.add(new MidiEvent(sm, this.tick));
            this.instrument = instrument;

            return true;
        }
        catch (InvalidMidiDataException e) {
            return false;
        }
    }

    private int getFullTone(int semitone) {
        int fullTone = 12 * (this.octave + 1) + semitone;
        if (fullTone > Note.MAX_SEMITONE) {
            return Note.MAX_SEMITONE;
        }
        return fullTone;
    }

    public static boolean isNote(char character) {
       return ('A' <= character && character<= 'H') ||
               ('a' <= character && character<= 'h');
    }

    public boolean addNote(Note note) {
        int semitone = note.getSemitone();
        int duration = note.getDuration();
        int fullTone = getFullTone(semitone);

        try{
            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, this.channel, fullTone, this.volume);
            this.track.add(new MidiEvent(on, this.tick));

            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, this.channel, fullTone, this.volume);
            this.track.add(new MidiEvent(off, this.tick + duration));

            this.tick += duration;
            return true;
        }
        catch (InvalidMidiDataException e) {
            return false;
        }
    }

    private static void handleNewNote(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        if(isNote(character)) {
            int newNote = Note.charToNote(character);
            sound.addNote(new Note(newNote, Note.DEFAULT_DURATION));
        }
    }

    private static void pause(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        int temp = sound.getVolume();
        sound.setVolume(0);
        sound.addNote(new Note(Note.DO, Note.DEFAULT_DURATION));
        sound.setVolume(temp);
    }

    private static void doubleVolume(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        int doubleVolume = sound.getVolume() * 2;
        sound.setVolume(Math.min(doubleVolume, SoundTrack.MAX_VOLUME));
    }

    private static void repeatNote(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        if (isNote(previousCharacter)) {
            sound.addNote(new Note(previousNote, Note.DEFAULT_DURATION));
        } else {
            sound.addNote(new Note(Note.DO, Note.DEFAULT_DURATION));
        }
    }

    private static void newInstrument(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        sound.changeInstrument(sound.getInstrument());
    }

    private static void increaseOctave(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        int newOctave = sound.getOctave() + 1;
        sound.setOctave((newOctave > SoundTrack.MAX_OCTAVE) ? SoundTrack.DEFAULT_OCTAVE : newOctave);
    }

    private static void decreaseOctave(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        int newOctave = sound.getOctave() - 1;
        sound.setOctave((newOctave < SoundTrack.DEFAULT_OCTAVE) ? SoundTrack.MAX_OCTAVE : newOctave);
    }

    private static void randomNote(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        char randomChar = (char) ('A' + (int)(Math.random() * 8));
        sound.addNote(new Note(randomChar, Note.DEFAULT_DURATION));
    }
}