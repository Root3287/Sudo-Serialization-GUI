package site.root3287.serializationgui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

public class SerializationGUI {

	private JFrame frame;
	private JTree tree;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SerializationGUI window = new SerializationGUI();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					window.frame.setVisible(true);
					window.frame.setTitle("File Editor");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SerializationGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser choose = new JFileChooser();
				choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				choose.showOpenDialog(SerializationGUI.this.frame);
				//SerializationPharser p = new SerializationPharser(choose.getSelectedFile());
				
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		
		JMenuItem save = new JMenuItem("Save (CTRL+S)");
		mnFile.add(save);
		
		JMenuItem saveAs = new JMenuItem("Save-As (CTRL+SHIFT+S)");
		saveAs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showSaveDialog(SerializationGUI.this.frame);
			}
		});
		mnFile.add(saveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mnEdit.add(mntmCut);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mnEdit.add(mntmPaste);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.setEditable(true);
		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setViewportView(tree);
	}

}
