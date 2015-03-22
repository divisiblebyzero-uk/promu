package uk.divisiblebyzero.promu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;

/**
 * Created by: Matthew Smalley
 * Date: 19/03/2015
 */
public class Generator {
    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);

    public void go() {

        try {
            Sequence sequence = new Sequence(Sequence.PPQ, 24);

            Track track = sequence.createTrack();
//****  General MIDI sysex -- turn on General MIDI sound set  ****
            byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
            SysexMessage sm = new SysexMessage();
            sm.setMessage(b, 6);
            MidiEvent me = new MidiEvent(sm,(long)0);
            track.add(me);

//****  set tempo (meta event)  ****
            MetaMessage mt = new MetaMessage();
            byte[] bt = {0x02, (byte)0x00, 0x00};
            mt.setMessage(0x51 ,bt, 3);
            me = new MidiEvent(mt,(long)0);
            track.add(me);

//****  set track name (meta event)  ****
            mt = new MetaMessage();
            String TrackName = new String("midifile track");
            mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
            me = new MidiEvent(mt,(long)0);
            track.add(me);

//****  set omni on  ****
            ShortMessage mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7D,0x00);
            me = new MidiEvent(mm,(long)0);
            track.add(me);

//****  set poly on  ****
            mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7F,0x00);
            me = new MidiEvent(mm,(long)0);
            track.add(me);

//****  set instrument to Piano  ****
            mm = new ShortMessage();
            mm.setMessage(0xC0, 0x00, 0x00);
            me = new MidiEvent(mm,(long)0);
            track.add(me);


            //****  note on - middle C  ****
            mm = new ShortMessage();
            mm.setMessage(0x90,0x3C,0x60);
            me = new MidiEvent(mm,(long)1);
            track.add(me);

//****  note off - middle C - 120 ticks later  ****
            mm = new ShortMessage();
            mm.setMessage(0x80,0x3C,0x40);
            me = new MidiEvent(mm,(long)121);
            track.add(me);

//****  set end of track (meta event) 19 ticks later  ****
            mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F,bet,0);
            me = new MidiEvent(mt, (long)140);
            track.add(me);

//****  write the MIDI sequence to a MIDI file  ****
            File f = new File("midifile.mid");
            MidiSystem.write(sequence, 1, f);

        } catch (InvalidMidiDataException e) {
            LOG.error("Error writing midi file: {}", e);
        } catch (IOException e) {
            LOG.error("Error writing midi file: {}", e);
        }
    }

    public static void main(String[] args) {
        new Generator().go();
    }
}
