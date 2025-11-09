import javax.sound.midi.*;
import java.io.*;
import music.*;
import music.Instrument;

public class Gerador {
	public static void main(String[] args) throws Exception {

        MusicPlayer player = new MusicPlayer();

        geraSom(player);

        InterfaceGrafica screen = new InterfaceGrafica();
        screen.setPlayer(player);
    }
    private static void geraSom(MusicPlayer player) throws Exception {
        SoundTrack st = player.createTrack(Instrument.PIANO, 0);
        SoundTrack st_lows = player.createTrack(Instrument.PIANO,0);


        int tick = beginning(st);


        int[] melody = {
                59, 59, 60, 62, 62, 60, 59, 57, 55, 55, 57, 59, 59, 57, 57,
                59, 59, 60, 62, 62, 60, 59, 57, 55, 55, 57, 59, 57, 55, 55,
                57, 57, 59, 55, 57, 59, 60, 59, 55, 57, 59, 60, 59, 57, 55, 57, 50,
                59, 59, 60, 62, 62, 60, 59, 57, 55, 55, 57, 59, 57, 55, 55
        };

        int[] durations = {
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 2, 8,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 2, 8,
                4, 4, 4, 4, 4, 2, 2, 4, 4, 4, 2, 2, 4, 4, 4, 4, 8,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 2, 12
        };

        int[] lows = {
                43, 0, 0, 0, 42, 0, 0, 0, 43, 0, 0, 0, 43, 0, 42,
                43, 0, 0, 0, 42, 0, 0, 0, 43, 0, 0, 0, 42, 0, 43,
                42, 0, 43, 0, 42, 0, 0, 43, 0, 42, 0, 0, 43, 0, 43, 0, 42,
                43, 0, 0, 0, 42, 0, 0, 0, 43, 0, 0, 0, 42, 0, 43,
        };

        int[] duration_l = {
                16, 0, 0, 0, 16, 0, 0, 0, 16, 0, 0, 0, 8, 0, 8,
                16, 0, 0, 0, 16, 0, 0, 0, 16, 0, 0, 0, 8, 0, 8,
                8, 0, 8, 0, 8, 0, 0, 8, 0, 8, 0, 0, 8, 0, 8, 0, 8,
                16, 0, 0, 0, 16, 0, 0, 0, 16, 0, 0, 0, 8, 0, 12,
        };

        int tamanho = melody.length;


        st.setChannel(0);
        st.changeInstrument(Instrument.PIANO);
        st.setVolume(100);
        st.setTick(tick);

        st_lows.setChannel(1);
        st_lows.changeInstrument(Instrument.FLAUTA);
        st_lows.setVolume(75);
        st_lows.setTick(tick);

        int fullNote;
        int octave;
        int semitone;
        int duration;
        Note note;

        for (int i = 0; i < tamanho; i++) {
            fullNote = melody[i];
            octave = fullNote / 12 - 1;
            semitone = fullNote % 12;
            duration = durations[i];

            note = new Note(semitone, duration);

            st.setOctave(octave);
            st.addNote(note);

            fullNote = lows[i];
            octave = fullNote / 12 - 1;
            semitone = fullNote % 12;
            duration = duration_l[i];

            note = new Note(semitone, duration);

            st_lows.setOctave(octave);
            st_lows.addNote(note);
        }
    }

        public static int beginning(SoundTrack track) throws Exception {
            int counterTick = SoundTrack.TIME_BEGIN;
            int beat = 2;

            track.setChannel(9);  // Channel 9 = drums;
            track.setOctave(2);
            track.setVolume(100);
            track.setTick(counterTick);

            for (int i = 0; i < 4; i++) {
                track.addNote(new Note(1, beat));

                counterTick = track.getCurrentTick();

                track.setTick(counterTick + beat);
            }

            return track.getCurrentTick();
        }
}
