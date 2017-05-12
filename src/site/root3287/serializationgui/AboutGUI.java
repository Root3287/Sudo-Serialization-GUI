package site.root3287.serializationgui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextPane;

public class AboutGUI extends JFrame{
	public AboutGUI() {
		setAlwaysOnTop(true);
		setTitle("About");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtpnAbout = new JTextPane();
		txtpnAbout.setText("About \nCreated by: Timothy Gibbons \nMade for the SUDO Engine, and Cherno's Serialization Program.");
		scrollPane.setViewportView(txtpnAbout);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
