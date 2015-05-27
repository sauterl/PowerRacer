/**
 * @author Simeon
 */
package client.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * List of sounds: 0 = MainSong 1 = HonkStart 2 = HonkMiddle 3 = HonkEnd 5 =
 * BeepLow 6 = BeepHigh 7 = Florians 8-bit Song 8 = JazzTrack 9 = IceTrack
 * Strangely not all wave files can be played What is gonna work for sure is
 * exporting songs from itunes with the following settings: 8000khz 8-bit Stereo
 */
public class SoundManager {

	/*
	 * Clip Array that holds all the wav-files
	 */
	Clip[] sounds = new Clip[11];
	SourceDataLine sl;
	static boolean PLAY_SOUND;

	public SoundManager(boolean sound) {
		PLAY_SOUND = sound;
		if (PLAY_SOUND) {
			loadSounds();
		}
	}

	/**
	 * Every Clip holds an AudioInputStream, the actual content of the Audio
	 */
	private void loadSounds() {
		try {
			for (int i = 0; i < 10; i++) {
				AudioInputStream ais = urlToAudioStream(stringToURL(i + ".wav"));
				AudioFormat format = ais.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				sounds[i] = (Clip) AudioSystem.getLine(info);
				sounds[i].open(ais);
			}
		} catch (IOException e) {
			// System.out.println("Input Error");
			PLAY_SOUND = false;
		} catch (LineUnavailableException e) {
			// System.out.println("Line unavailable Error");
			PLAY_SOUND = false;
		} catch (NullPointerException e) {
			// System.out.println("File not found Error");
			PLAY_SOUND = false;
		} catch (UnsupportedAudioFileException e) {
			// System.out.println("Unsupported Audio File Error");
			PLAY_SOUND = false;
		} catch (IllegalArgumentException e) {
			// System.out.println("Illegal Argument Error");
			PLAY_SOUND = false;
		}
	}

	public void playSound(int i) {
		if (PLAY_SOUND) {
			sounds[i].setFramePosition(0);
			sounds[i].start();
		}
	}

	public void loopSound(int i) {
		if (PLAY_SOUND) {
			sounds[i].loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void stopSound(int i) {
		if (PLAY_SOUND) {
			sounds[i].stop();
		}
	}

	private AudioInputStream urlToAudioStream(URL u)
			throws UnsupportedAudioFileException, IOException {

		AudioInputStream audioStream = AudioSystem.getAudioInputStream(u);
		return audioStream;
	}

	/**
	 * Creates an URL out of a String
	 */
	private URL stringToURL(String s) {
		URL u = getClass().getClassLoader().getResource(
				"client/gui/sounds/" + s);
		return u;
	}

	public static void main(String args[]) {
		SoundManager sm = new SoundManager(true);
		sm.playSound(9);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Sleep interupted");
			PLAY_SOUND = false;
		}
	}

	public void toggleMusic(int track) {
		if (PLAY_SOUND) {
			PLAY_SOUND = false;
			for (Clip c : sounds) {
				if (c != null) {
					c.stop();
				}
			}
		} else {
			PLAY_SOUND = true;
			loopSound(track);
		}
	}
}
