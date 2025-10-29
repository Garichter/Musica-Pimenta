import javax.sound.midi.*;
import music.*;

/*
 * int DO = 0;
 * int DO_ = 1;
 * int RE = 2;
 * int RE_ = 3;
 * int MI = 4;
 * int FA = 5;
 * int FA_ = 6;
 * int SOL = 7;
 * int SOL_ = 8;
 * int LA = 9;
 * int LA_ = 10;
 * int SI = 11;
 * 
 * int nota_midi = 12 * (oitava + 1) + semitom;
 */

public class TesteSound {
    public static int noteToMidi(int oitava, int semitom)  {
        return 12 * (oitava + 1) + semitom;
    }
    
    public static void main(String[] args) throws Exception {

        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();

        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();
        Track track_lows = sequence.createTrack();

        ShortMessage sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
        track.add(new MidiEvent(sm, 0));
        ShortMessage sml = new ShortMessage();
        sml.setMessage(ShortMessage.PROGRAM_CHANGE, 1, 42, 0);
        track_lows.add(new MidiEvent(sml, 0));

        int[] melody = {
            59,59,60,62, 62,60,59,57, 55,55,57,59, 59,57,57,
            59,59,60,62, 62,60,59,57, 55,55,57,59, 57,55,55,
            57,57,59,55, 57,59,60,59,55, 57,59,60,59,57, 55,57,50,
            59,59,60,62, 62,60,59,57, 55,55,57,59, 57,55,55
        };

        int[] durations = {
            4,4,4,4, 4,4,4,4, 4,4,4,4, 6,2,8,
            4,4,4,4, 4,4,4,4, 4,4,4,4, 6,2,8,
            4,4,4,4, 4,2,2,4,4, 4,2,2,4,4, 4,4,8,
            4,4,4,4, 4,4,4,4, 4,4,4,4, 6,2,8
        };

        int[] lows = {
            43,0,0,0, 42,0,0,0, 43,0,0,0, 43,0,42,
            43,0,0,0, 42,0,0,0, 43,0,0,0, 42,0,43,
            42,0,43,0, 42,0,0,43,0, 42,0,0,43,0, 43,0,42,
            43,0,0,0, 42,0,0,0, 43,0,0,0, 42,0,43,
        };

        int[] duration_l = {
            16,0,0,0, 16,0,0,0, 16,0,0,0, 8,0,8,
            16,0,0,0, 16,0,0,0, 16,0,0,0, 8,0,8,
            8,0,8,0, 8,0,0,8,0, 8,0,0,8,0, 8,0,8,
            16,0,0,0, 16,0,0,0, 16,0,0,0, 8,0,8,
        };

        int tamanho = melody.length;
        int instrument = 0, volume = 100;
        int instrument_lows = 42, volume_lows = 75;
        Note[] allNotes = new Note[tamanho];
        Note[] notes_lows = new Note[tamanho];

        for (int i = 0; i < tamanho; i++) {
            int fullNote = melody[i];
            int octave = fullNote / 12 - 1;
            int semitone = fullNote % 12;
            int duration = durations[i];

            Note nota = new Note(instrument, octave, volume, semitone, duration);
            allNotes[i] = nota;
        }

        SoundTrack soundTrack = new SoundTrack(track, 0, allNotes);

        for (int i = 0; i < tamanho; i++) {
            int fullNote = lows[i];
            int octave = fullNote / 12 - 1;
            int semitone = fullNote % 12;
            int duration = duration_l[i];

            Note nota = new Note(instrument_lows, octave, volume_lows, semitone, duration);
            notes_lows[i] = nota;
        }

        SoundTrack soundTrack_lows = new SoundTrack(track_lows, 1, notes_lows);

        int counterTick = 24;
        int beat = 4;

        for (int i = 0; i < 4; i++) {
            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, 9, 37, 100); // Channel 9 = drums, 42 = hi-hat
            track.add(new MidiEvent(on, counterTick));

            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, 9, 37, 100);
            track.add(new MidiEvent(off, counterTick + beat/2)); // short sound

            counterTick += beat;
        }

        int tick = counterTick;
        //int note, d, low, dl;
        if (soundTrack.createTrack(tick)) track = soundTrack.getTrack();
        if (soundTrack_lows.createTrack(tick)) track_lows = soundTrack_lows.getTrack();

        /*
        for (int i = 0; i < melody.length; i++) {
            d = durations[i];
            note = melody[i];
            // Note on
            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, 0, note, 100);
            track.add(new MidiEvent(on, tick));

            // Note off
            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, 0, note, 100);
            track.add(new MidiEvent(off, tick + d));

            if (lows[i] != 0) {
                low = lows[i];
                dl = duration_l[i];

                ShortMessage onl = new ShortMessage();
                onl.setMessage(ShortMessage.NOTE_ON, 1, low, 75);
                track_lows.add(new MidiEvent(onl, tick));

                ShortMessage offl = new ShortMessage();
                offl.setMessage(ShortMessage.NOTE_OFF, 1, low, 75);
                track_lows.add(new MidiEvent(offl, tick + dl));
            }

            tick += d;  // move forward
        }
        */

        sequencer.setSequence(sequence);
        sequencer.start();
        while (sequencer.isRunning()) {
            Thread.sleep(100);
        }
        sequencer.close();
    }
}
