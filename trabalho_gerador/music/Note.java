package music;

public class Note {
    public static final int DO = 0;
    public static final int DO_ = 1;
    public static final int RE = 2;
    public static final int RE_ = 3;
    public static final int MI = 4;
    public static final int FA = 5;
    public static final int FA_ = 6;
    public static final int SOL = 7;
    public static final int SOL_ = 8;
    public static final int LA = 9;
    public static final int LA_ = 10;
    public static final int SI = 11;
    public static final int DEFAULT_DURATION = 4;
    public static final int MAX_SEMITONE = 127;

    private int semitone;
    private int duration;

    public Note(int semitone, int duration) {
        if (semitone < 0 || semitone > MAX_SEMITONE) {
            throw new IllegalArgumentException("Semitone inválido: " + semitone);
        }
        this.semitone = semitone;
        this.duration = duration;
    }

    public int getSemitone() {
        return semitone;
    }

    public int getDuration() {
        return duration;
    }

    public static int charToNote(char character) {
        switch (Character.toUpperCase(character)) {
            case 'A': return LA;
            case 'B': return SI;
            case 'C': return DO;
            case 'D': return RE;
            case 'E': return MI;
            case 'F': return FA;
            case 'G': return SOL;
            case 'H': return LA_;
            default: return -1;
        }
    }

    public static boolean isNote(char character) {
        return ('A' <= character && character<= 'H') ||
                ('a' <= character && character<= 'h');
    }
}
