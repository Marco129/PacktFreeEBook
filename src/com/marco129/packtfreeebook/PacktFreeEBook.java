package com.marco129.packtfreeebook;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.validator.routines.EmailValidator;

import com.marco129.packtfreeebook.Processor.ProcessorListener;

public class PacktFreeEBook {

	private static JFrame gui;
	private static JTextField emailField;
	private static JPasswordField passwordField;
	private static JButton submitButton;

	private static ProcessorListener listener;

	public static void main(String[] args) {
		listener = new ProcessorListener() {

			@Override
			public void onSuccess(String bookTitle) {
				String message = "eBook (" + bookTitle + ") added to your account, enjoy!";
				if (isCLIMode(args)) {
					System.out.println(message);
				} else {
					JOptionPane.showMessageDialog(gui,
						"eBook (" + bookTitle + ") added to your account, enjoy!", "Error",
						JOptionPane.INFORMATION_MESSAGE);
					submitButton.setEnabled(true);
				}
			}

			@Override
			public void onFailure() {
				String message = "Something goes wrong! Please try again";
				if (isCLIMode(args)) {
					System.out.println(message);
				} else {
					JOptionPane.showMessageDialog(gui, message, "Error",
						JOptionPane.ERROR_MESSAGE);
					submitButton.setEnabled(true);
				}
			}
		};

		if (isCLIMode(args)) { // CLI mode
			if (args.length < 2) {
				System.out.println("Missing required arguments");
				return;
			}
			if (!EmailValidator.getInstance().isValid(args[0])) {
				System.out.println("Please enter a valid email address");
				return;
			}
			Processor processor = new Processor(args[0], args[1], listener);
			processor.execute();
			while (!processor.isDone());
			return;
		}

		// Use system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		gui = new JFrame();
		gui.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(PacktFreeEBook.class.getResource("/com/marco129/packtfreeebook/res/ebook.png")));
		gui.setSize(new Dimension(500, 230)); // Set window size
		gui.setTitle("Packt Free E-Book");
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.getContentPane().setLayout(new BoxLayout(gui.getContentPane(), BoxLayout.X_AXIS));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		gui.getContentPane().add(leftPanel);

		JLabel logo = new JLabel((String) null);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		logo.setIcon(new ImageIcon(PacktFreeEBook.class.getResource("/com/marco129/packtfreeebook/res/ebook.png")));
		leftPanel.add(logo);

		JPanel copyright = new JPanel();
		copyright.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		leftPanel.add(copyright);

		JLabel builtwithlove = new JLabel("Built with love by Marco129");
		builtwithlove.setFont(new Font("Arial", Font.PLAIN, 11));
		copyright.add(builtwithlove);

		JButton about = new JButton((String) null);
		about.setFocusPainted(false);
		about.setBorder(null);
		about.setToolTipText("About");
		about.setContentAreaFilled(false);
		about.setBorderPainted(false);
		about.setIcon(new ImageIcon(PacktFreeEBook.class.getResource("/com/marco129/packtfreeebook/res/faq.png")));
		about.setFont(new Font("Arial", Font.PLAIN, 11));
		about.addMouseListener(new AboutMouseListener());
		copyright.add(about);

		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		rightPanel.setLayout(new GridLayout(0, 1, 0, 0));
		gui.getContentPane().add(rightPanel);

		JTextPane title = new JTextPane();
		title.setFocusable(false);
		title.setEditable(false);
		title.setFont(new Font("Arial", Font.BOLD, 16));
		title.setBackground(SystemColor.menu);
		title.setText("Packt Free eBook");
		rightPanel.add(title);

		JTextPane description = new JTextPane();
		description.setFocusable(false);
		description.setEditable(false);
		description.setText("Get a free eBook everyday automatically!");
		description.setFont(new Font("Arial", Font.PLAIN, 14));
		description.setBackground(SystemColor.menu);
		rightPanel.add(description);

		JLabel emailLabel = new JLabel("E-mail address:");
		emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		rightPanel.add(emailLabel);

		emailField = new JTextField();
		emailField.setFont(new Font("Arial", Font.PLAIN, 12));
		rightPanel.add(emailField);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		rightPanel.add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
		rightPanel.add(passwordField);

		JLabel emptyLabel = new JLabel((String) null);
		rightPanel.add(emptyLabel);

		submitButton = new JButton("Get it now!");
		submitButton.setFont(new Font("Arial", Font.PLAIN, 12));
		submitButton.addMouseListener(new SubmitMouseListener());
		rightPanel.add(submitButton);

		// Display GUI in center of the screen
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
	}

	private static boolean isCLIMode(String[] args) {
		return args.length > 0;
	}

	private static class AboutMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(gui,
					"Fork me on GitHub.\nIf you'd like to contribute, feel free to submit a pull request!", "About",
					JOptionPane.INFORMATION_MESSAGE);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	private static class SubmitMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!submitButton.isEnabled()) return;
			String email = emailField.getText().trim();
			String password = String.valueOf(passwordField.getPassword()).trim();
			if (email.equals("") || password.equals("")) {
				JOptionPane.showMessageDialog(gui, "Please enter email address and password", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!EmailValidator.getInstance().isValid(email)) {
				JOptionPane.showMessageDialog(gui, "Please enter a valid email address", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			submitButton.setEnabled(false);
			Processor processor = new Processor(email, password, listener);
			processor.execute();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

}
