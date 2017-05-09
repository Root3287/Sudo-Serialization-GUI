package site.root3287.serializationgui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import site.root3287.sudo.serialization.container.SerializationArray;
import site.root3287.sudo.serialization.container.SerializationDatabase;
import site.root3287.sudo.serialization.container.SerializationField;
import site.root3287.sudo.serialization.container.SerializationFieldType;
import site.root3287.sudo.serialization.container.SerializationObject;
import site.root3287.sudo.serialization.container.SerializationString;

public class SerializationGUI {

	private JFrame frame;
	private JTree tree = null;
	private JScrollPane scrollPane;
	private SerializationDatabase db = null;
	private JTextField txtOldvalue;
	private JTextField oldValue;
	private JTextField txtNewvalue;
	
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
		frame.setBounds(100, 100, 900, 900/16*9);
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
				if(choose.getSelectedFile() == null)
					return;
				SerializationPharser p = new SerializationPharser(choose.getSelectedFile());
				db = p.database;
				tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(db.getName())));
				
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
				
				for(SerializationObject o : p.database.objects){
					DefaultMutableTreeNode object = new DefaultMutableTreeNode(o);
					for(SerializationString s : o.strings){
						object.add(new DefaultMutableTreeNode(s));
					}
					for(SerializationField f : o.fields){
						object.add(new DefaultMutableTreeNode(f));
					}
					for(SerializationArray a : o.arrays){
						object.add(new DefaultMutableTreeNode(a));
					}
					root.add(object);	
				}
				tree.validate();
			}
		});
		
		JMenu mnNew = new JMenu("New");
		mnFile.add(mnNew);
		
		JMenuItem mntmSerializationFile = new JMenuItem("Serialization File");
		mnNew.add(mntmSerializationFile);
		
		JMenu mnSerializationItems = new JMenu("Serialization Items");
		mnNew.add(mnSerializationItems);
		
		JMenuItem addObject = new JMenuItem("Add Object");
		mnSerializationItems.add(addObject);
		
		JMenuItem mntmAddField = new JMenuItem("Add Field");
		mntmAddField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String r = (String) JOptionPane.showInputDialog(frame, "What kind of field?", "Confirmation", JOptionPane.QUESTION_MESSAGE, null, new Object[]{"byte", "short", "char", "int", "long", "double", "float", "boolean"}, "byte");
				System.out.println(r);
				if(r == null)
					return;
				if(r.equalsIgnoreCase("byte")){
				//	String r2 = JOptionPane.showInputDialog(frame, "Value: ", "Value", JOptionPane.QUESTION_MESSAGE);
				}else if(r.equalsIgnoreCase("short")){
					
				}else if(r.equalsIgnoreCase("char")){
					
				}else if(r.equalsIgnoreCase("int")){
					
				}else if(r.equalsIgnoreCase("long")){
					
				}else if(r.equalsIgnoreCase("double")){
					
				}else if(r.equalsIgnoreCase("float")){
					
				}else if(r.equalsIgnoreCase("boolean")){
					
				}
			}
		});
		mnSerializationItems.add(mntmAddField);
		
		JMenuItem mntmAddArray = new JMenuItem("Add Array");
		mnSerializationItems.add(mntmAddArray);
		mnFile.add(mntmOpen);
		
		JMenuItem save = new JMenuItem("Save File (CTRL+S)");
		mnFile.add(save);
		
		JMenuItem saveAs = new JMenuItem("Save File As (CTRL+SHIFT+S)");
		saveAs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showSaveDialog(SerializationGUI.this.frame);
			}
		});
		mnFile.add(saveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		
		JMenuItem mntmSaveKey = new JMenuItem("Save Key");
		mnFile.add(mntmSaveKey);
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
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		tree = new JTree(new DefaultMutableTreeNode("null"));
		tree.setEditable(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(current == null) // nothing
					return;

			    if (current.isLeaf()) { // EOL
			    	if(current.getUserObject() instanceof SerializationField){
			    		SerializationField f = (SerializationField) current.getUserObject();
			    		Object result = null;
			    		switch (f.type) {
			    		case SerializationFieldType.BYTE:
			    			result = f.getByte();
			    			break;
			    		case SerializationFieldType.SHORT:
			    			result = f.getShort();
			    			break;
			    		case SerializationFieldType.CHAR:
			    			result = f.getChar();
			    			break;
			    		case SerializationFieldType.INTEGER:
			    			result = f.getInt();
			    			break;
			    		case SerializationFieldType.LONG:
			    			result = f.getLong();
			    			break;
			    		case SerializationFieldType.DOUBLE:
			    			result = f.getDouble();
			    			break;
			    		case SerializationFieldType.FLOAT:
			    			result = f.getFloat();
			    			break;
			    		case SerializationFieldType.BOOLEAN:
			    			result = f.getBoolean();
			    			break;
						default:
							break;
						}
			    		oldValue.setText(result.toString());
			    		txtNewvalue.setText(result.toString());
			    	}else if(current.getUserObject() instanceof SerializationString){
			    		SerializationString s = (SerializationString) current.getUserObject();
			    		oldValue.setText(s.getString());
			    		txtNewvalue.setText(s.getString());
			    	}else if (current.getUserObject() instanceof SerializationArray) {
						
					}
			    } else {
			      
			    }
			}
		});
		scrollPane.setViewportView(tree);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblValues = new JLabel("Values");
		lblValues.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblValues, BorderLayout.NORTH);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.5);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel.add(splitPane_1, BorderLayout.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_1);
		
		JLabel lblOldValues_1 = new JLabel("Old Value");
		scrollPane_1.setColumnHeaderView(lblOldValues_1);
		
		oldValue = new JTextField();
		oldValue.setEnabled(false);
		oldValue.setText("value");
		scrollPane_1.setViewportView(oldValue);
		oldValue.setColumns(10);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_2);
		
		JLabel lblnValue = new JLabel("New Value");
		scrollPane_2.setColumnHeaderView(lblnValue);
		
		txtNewvalue = new JTextField();
		txtNewvalue.setText("Value");
		scrollPane_2.setViewportView(txtNewvalue);
		txtNewvalue.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		
		JLabel lblOldValues = new JLabel("Old Values");
		panel_1.add(lblOldValues);
		
		txtOldvalue = new JTextField();
		txtOldvalue.setText("oldValue");
		panel_1.add(txtOldvalue);
		txtOldvalue.setColumns(10);
	}
}
