package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Opens window displaying the game's credits.
 *
 * @author Florian
 */
public class CreditsWindow extends Thread {
	private final JFrame frame;
	private final JTextArea creditsArea;
	private final Queue<String> credits;
	private boolean close;
	private final JButton creditsButton;
	private final SoundManager sound;

	/**
	 * Constructor for CreditsWindow.
	 *
	 * @param button the button that opened this credits window, which will need to
	 *               be set enabled again after the window is closed.
	 */
	public CreditsWindow(JButton button) {
		creditsButton = button;

		frame = new JFrame("Credits");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				close();
			}
		});

		frame.setResizable(false);

		creditsArea = new JTextArea();
		creditsArea.setEditable(false);
		creditsArea.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(creditsArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(500, 500));

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(e -> close());

		// Add the credits to the queue in the order it should be displayed
		credits = new ArrayDeque<>(100);
		credits.offer("PowerRacer Credits");
		credits.offer("");
		credits.offer("Programming");
		credits.offer("");
		credits.offer("-Server Programming");
		credits.offer(" Lead Server Programmer - Marco Leu");
		credits.offer(" Executive Server Programmer - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Client Programming");
		credits.offer(" Lead Client Programmer - Florian Spiess");
		credits.offer(" Associate Client Programmer - Simeon Jackman");
		credits.offer("");
		credits.offer("-Gameplay Programming");
		credits.offer(" Visionary Gameplay Programmer - Florian Spiess");
		credits.offer(" Gameplay Programming Supervisor - Simeon Jackman");
		credits.offer("");
		credits.offer("-Communications Programming");
		credits.offer(" Chief Communications Programmer - Marco Leu");
		credits.offer(" Junior Communications Programmer - Florian Spiess");
		credits.offer(" Communications Programming Supervisor - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-GUI Programming");
		credits.offer(" Senior GUI Programmer - Florian Spiess");
		credits.offer(" Executive GUI Programmer - Simeon Jackman");
		credits.offer("");
		credits.offer("-Randomization Programming");
		credits.offer(" Lead Track Randomization Programmer - Simeon Jackman");
		credits.offer(" Chief Bogo Programmer - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Network Programming");
		credits.offer(" Head Network Programmer - Marco Leu");
		credits.offer(" Executive Network Programmer - Florian Spiess");
		credits.offer("");
		credits.offer("-Lobby System Programming");
		credits.offer(" Chief Structural Lobby Programmer - Marco Leu");
		credits.offer(" Executive Lobby Programmer - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Chat System Programming");
		credits.offer(" Head Chat Programer - Marco Leu");
		credits.offer(" Executive Chat Programmer - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Sound Programming");
		credits.offer(" Chief Sound Programmer - Simeon Jackman");
		credits.offer("");
		credits.offer("-Graphics Programming");
		credits.offer(" Lead Graphics Programmer - Florian Spiess");
		credits.offer("");
		credits.offer("-Test Programming");
		credits.offer(" Master of JUnit Testing - Benjamin Zumbrunn");
		credits.offer(" Associate Co-Tester - Florian Spiess");

		credits.offer("");
		credits.offer("\nDesign");
		credits.offer("");
		credits.offer("-Gameplay Design");
		credits.offer(" Lead Gameplay Designer - Florian Spiess");
		credits.offer(" Executive Gameplay Designer - Marco Leu");
		credits.offer(" Associate Gameplay Designer - Simeon Jackman");
		credits.offer(" Associate Gameplay Designer - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Track Design");
		credits.offer(" Chief Track Designer - Florian Spiess");
		credits.offer(" Executive Track Designer - Simeon Jackman");
		credits.offer("");
		credits.offer("-Sound Design");
		credits.offer(" Master of Sound - Simeon Jackman");
		credits.offer(" Co-Master of Sound and Golden Voice - Benjamin Zumbrunn");
		credits.offer(" Junior Sound Designer - Florian");
		credits.offer("");
		credits.offer("-Powerup Design");
		credits.offer(" General Powerup Design - Mario Kart Powerup Designers");
		credits.offer(" Senior Powerup Designer - Marco Leu");
		credits.offer(" Executive Powerup Designer - Florian Spiess");
		credits.offer("");
		credits.offer("-Graphical Design");
		credits.offer(" Head Graphical Designer - Florian Spiess");
		credits.offer(" Executive Graphical Designer - Simeon Jackman");
		credits.offer(" Associate Graphical Designer - Benjamin Zumbrunn");

		credits.offer("");
		credits.offer("\nQuality Assurance");
		credits.offer("");
		credits.offer("-Documentation");
		credits.offer(" Wikia Dokumentation Manager - Simeon Jackman");
		credits.offer(" Chief Protocol Manager and Minute Taker - Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("-Testers");
		credits.offer(" Chief Tester - Benjamin Zumbrunn");
		credits.offer(" Tester - Florian Spiess");
		credits.offer(" Tester - Marco Leu");
		credits.offer(" Tester - Simeon Jackman");
		credits.offer("");
		credits.offer("-Coordination");
		credits.offer(" Lead Coordination Coordinator - Benjamin Zumbrunn");

		credits.offer("");
		credits.offer("\nSoundtrack");
		credits.offer("");
		credits.offer("\"Power Racer\"");
		credits.offer(" Written and Performed by Simeon Jackman and Benjamin Zumbrunn");
		credits.offer("");
		credits.offer("\"8-Bit Roads\"");
		credits.offer(" A Spiess Retro Production");

		credits.offer("");
		credits.offer("\nSpecial Thanks");
		credits.offer(" Thanks to cookies for being delicious.");
		credits.offer("\n Thank you for playing our game!");

		credits.offer("\n\u00A92015 PitCrew");

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(scrollPane).addComponent(okButton));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane).addComponent(okButton));

		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		frame.getContentPane().add(panel);

		frame.pack();

		frame.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width - frame
						.getWidth()) / 2, (Toolkit.getDefaultToolkit()
						.getScreenSize().height - frame.getHeight()) / 2);
		frame.setVisible(true);

		sound = new SoundManager(true);
		start();
	}

	/**
	 * Adds the argument message to the JTextArea creditsArea and scrolls its
	 * Focus down to the new credit.
	 *
	 * @param message text to be added to credits
	 */
	public void display(String message) {
		creditsArea.append(message + "\n");
		creditsArea.setCaretPosition(creditsArea.getDocument().getLength());
	}

	/**
	 * Closes this CreditsWindow, enables the calling button, stops the playing
	 * sound and releases used resources
	 */
	protected void close() {
		close = true;
		interrupt();
		frame.dispose();
		sound.stopSound(0);
		creditsButton.setEnabled(true);
	}

	/**
	 * Modified run method printing credits to the creditsArea.
	 */
	@Override
	public void run() {
		sound.loopSound(0);
		try {
			// Print new Credit each second
			while (credits.peek() != null && !close) {
				display(credits.poll());
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {

		}
	}
}
