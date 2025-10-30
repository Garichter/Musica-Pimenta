package music;

import javax.sound.midi.*;
// MUDANÇA: Importação necessária para eventos de tempo (MetaMessage)
import javax.sound.midi.MetaMessage;
import java.util.HashMap;
import java.util.Map;

public class SoundTrack {
    public static final int TIME_BEGIN = 24;
    public static final int DEFAULT_VOLUME = 100;
    public static final int MAX_VOLUME = 127;
    public static final int DEFAULT_OCTAVE = -1;
    public static final int MAX_OCTAVE = 9;

    // MUDANÇA: Adicionada constante de BPM padrão
    public static final float DEFAULT_BPM = 120.0f;

    public static final int BANDONEON = 23;
    public static final int GAITA_DE_FOLES = 109;
    public static final int ONDAS_DO_MAR = 122;
    public static final int TUBULAR_BELLS = 14;
    public static final int AGOGO = 113;
    public static final int TELEFONE_TOCANDO = 124;

    private int volume;
    private int octave;
    private int instrument;

    private Track track;
    private int channel;
    private int tick;
    private Sequencer controller;

    // MUDANÇA: Adicionado rastreador de BPM atual
    private float currentBPM;

    public void setController(Sequencer controller) {this.controller = controller;}
    public int getVolume() {
        return this.volume;
    }
    public int getOctave() {
        return this.octave;
    }
    public int getInstrument(){return this.instrument;}
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

    public SoundTrack(Track track, int currentInstrument, int currentVolume) {
        this.octave = DEFAULT_OCTAVE;
        this.volume = currentVolume;
        this.instrument = currentInstrument;

        this.track = track;
        this.channel = 0;
        this.tick = TIME_BEGIN;

        this.currentBPM = DEFAULT_BPM;
        this.addTempoChange(this.currentBPM);

        this.changeInstrument(this.instrument);
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
            increaseBPM(80.0F);
        }
        else if (action != null) {
            action.execute(this, character, previousCharacter, previousNote);
        }
        else if (lastAction != null) {
            lastAction.execute(this, lastCharacter, lastpreviousCharacther, lastpreviousNote);
            return;
        }
        lastAction = action;
        lastCharacter = character;
        lastpreviousCharacther = previousCharacter;
        lastpreviousNote = previousNote;
    }

    public void increaseBPM(float bpm) {
        this.currentBPM += bpm;
        this.addTempoChange(this.currentBPM);
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

    // MUDANÇA: Adicionado método para inserir eventos de tempo (BPM) na trilha
    public boolean addTempoChange(float bpm) {
        if (bpm <= 0) return false;

        // Converte BPM para "microsegundos por semínima" (MPQ)
        long mpq = (long) (60000000.0 / bpm);

        // O MetaMessage de tempo armazena o MPQ como 3 bytes
        byte[] data = new byte[3];
        data[0] = (byte) ((mpq >> 16) & 0xFF);
        data[1] = (byte) ((mpq >> 8) & 0xFF);
        data[2] = (byte) (mpq & 0xFF);

        try {
            MetaMessage tempoMessage = new MetaMessage();
            // 0x51 é o código padrão MIDI para "Set Tempo"
            tempoMessage.setMessage(0x51, data, 3);

            // Adiciona o evento de tempo na track, no tick ATUAL
            this.track.add(new MidiEvent(tempoMessage, this.tick));
            return true;

        } catch (InvalidMidiDataException e) {
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
        sound.addNote(new Note(previousNote, Note.DEFAULT_DURATION));
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
            int instrumentoAtual = sound.getInstrument();
            sound.changeInstrument(TELEFONE_TOCANDO);
            sound.addNote(new Note(Note.DO, Note.DEFAULT_DURATION));
            sound.changeInstrument(instrumentoAtual);
        }
    }

    private static void newInstrument(SoundTrack sound, char character, char previousCharacter, int previousNote) {
        int newInstrument = (int)(Math.random()*127);
        sound.changeInstrument(newInstrument);
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