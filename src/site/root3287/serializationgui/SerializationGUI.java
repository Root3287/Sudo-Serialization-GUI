package site.root3287.serializationgui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

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
	private JSplitPane valuesPane;
	private JTabbedPane varibleTabbedPane;
	private JTree arrayValueTree;
	private File currentFile = null;
	
	private List<SerializationObject> objects = new ArrayList<>();
	private HashMap<SerializationObject, List<SerializationField>> fields = new HashMap<>();
	private HashMap<SerializationObject, List<SerializationArray>> array = new HashMap<>();
	private HashMap<SerializationObject, List<SerializationString>> string = new HashMap<>();
	
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
				
				currentFile = choose.getSelectedFile();
				
				tree.setRootVisible(true);
				SerializationPharser p = new SerializationPharser(choose.getSelectedFile());
				db = p.database;
				tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(db.getName())));
				
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
				
				objects.clear();
				fields.clear();
				array.clear();
				string.clear();
				
				for(SerializationObject o : p.database.objects){
					DefaultMutableTreeNode object = new DefaultMutableTreeNode(o);
					List<SerializationField> fieldsArr = new ArrayList<>();
					List<SerializationArray> arrayArr = new ArrayList<>();
					List<SerializationString> stringsArr = new ArrayList<>();
					
					for(SerializationString s : o.strings){
						object.add(new DefaultMutableTreeNode(s));
						stringsArr.add(s);
					}
					for(SerializationField f : o.fields){
						object.add(new DefaultMutableTreeNode(f));
						fieldsArr.add(f);
					}
					for(SerializationArray a : o.arrays){
						DefaultMutableTreeNode arrays = new DefaultMutableTreeNode(a);
						object.add(arrays);
						arrayArr.add(a);
					}
					objects.add(o);
					fields.put(o, fieldsArr);
					array.put(o, arrayArr);
					string.put(o, stringsArr);
					root.add(object);	
				}
				db = null;
				tree.validate();
			}
		});
		
		JMenu mnNew = new JMenu("New");
		mnFile.add(mnNew);
		
		JMenuItem mntmSerializationFile = new JMenuItem("Serialization File");
		mntmSerializationFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tree.isRootVisible() || currentFile != null){
					int cSave = JOptionPane.showConfirmDialog(frame, "Do you want to save?");
					if(cSave == 0){ // yes
						saveToFile(currentFile, false);
					}else if(cSave == 1){ // no
						DefaultMutableTreeNode treeroot = (DefaultMutableTreeNode) tree.getModel().getRoot();
						treeroot.removeAllChildren();
					}else{ //cancel
						
					}
				}
				db = new SerializationDatabase("null");
				tree.setRootVisible(true);
			}
		});
		mnNew.add(mntmSerializationFile);
		
		JMenu mnSerializationItems = new JMenu("Serialization Items");
		mnNew.add(mnSerializationItems);
		
		JMenuItem addObject = new JMenuItem("Add Object");
		addObject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String res = JOptionPane.showInputDialog(frame, "name", "Name", JOptionPane.INFORMATION_MESSAGE);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				SerializationObject o = new SerializationObject(res);
				objects.add(o);
				
				//Start the fields
				fields.put(o, new ArrayList<SerializationField>());
				//Start the arrays
				array.put(o, new ArrayList<SerializationArray>());
				//Start the strings
				string.put(o, new ArrayList<SerializationString>());
				
				if(node != null){
					((DefaultTreeModel) tree.getModel()).insertNodeInto(new DefaultMutableTreeNode(o), (MutableTreeNode) tree.getModel().getRoot(), ((DefaultMutableTreeNode) tree.getModel().getRoot()).getChildCount());
				}else{
					((DefaultMutableTreeNode)tree.getModel().getRoot()).add(new DefaultMutableTreeNode(o));
				}
			}
		});
		mnSerializationItems.add(addObject);
		
		JMenuItem mntmAddField = new JMenuItem("Add Field");
		mntmAddField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tree.getSelectionPath() == null){
					JOptionPane.showMessageDialog(frame, "No object was selected!", "No object!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				Object current = selected.getUserObject();
				if(!(current instanceof SerializationObject)){
					JOptionPane.showMessageDialog(frame, "No Serialization Object has been selected!");
					return;
				}
				
				SerializationObject currentObject = (SerializationObject) current;
				String r = (String) JOptionPane.showInputDialog(frame, "What kind of field?", "Confirmation", JOptionPane.QUESTION_MESSAGE, null, new Object[]{"byte", "short", "char", "int", "long", "double", "float", "boolean"}, "byte");
				String name = JOptionPane.showInputDialog(frame, "Name: ", "Field Name", JOptionPane.QUESTION_MESSAGE);
				String value = JOptionPane.showInputDialog(frame, "Value:", "Value", JOptionPane.QUESTION_MESSAGE);
				
				if(r == null){
					System.out.println("no field selected!");
					return;
				}
				SerializationField c = null;
				if(r.equalsIgnoreCase("byte")){
					c = SerializationField.createByteField(name, Byte.parseByte(value));
				}else if(r.equalsIgnoreCase("short")){
					c = SerializationField.createShortField(name, Short.parseShort(value));
				}else if(r.equalsIgnoreCase("char")){
					c = SerializationField.createCharField(name, value.charAt(0));
				}else if(r.equalsIgnoreCase("int")){
					c = SerializationField.createIntegerField(name, Integer.parseInt(value));
				}else if(r.equalsIgnoreCase("long")){
					c = SerializationField.createLongField(name, new Long(value));
				}else if(r.equalsIgnoreCase("double")){
					c = SerializationField.createDoubleField(name, Double.parseDouble(value));
				}else if(r.equalsIgnoreCase("float")){
					c = SerializationField.createFloatField(name, Float.parseFloat(value));
				}else if(r.equalsIgnoreCase("boolean")){
					c = SerializationField.createBooleanField(name, Boolean.parseBoolean(value));
				}
				if(c != null){
					if(fields.containsKey(currentObject)){
						fields.get(currentObject).add(c);
					}
					((DefaultTreeModel) tree.getModel()).insertNodeInto(new DefaultMutableTreeNode(c), selected, selected.getChildCount());
				}
			}
		});
		mnSerializationItems.add(mntmAddField);
		
		JMenuItem mntmAddArray = new JMenuItem("Add Array");
		mnSerializationItems.add(mntmAddArray);
		mnFile.add(mntmOpen);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		JMenuItem save = new JMenuItem("Save File (CTRL+S)");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				saveToFile(currentFile, true);
			}
		});
		mnFile.add(save);
		
		JMenuItem saveAs = new JMenuItem("Save File As (CTRL+SHIFT+S)");
		saveAs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToFile(null, true);
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
		
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(SerializationObject o : objects){
					System.out.println(o);
					for(SerializationField f: fields.get(o)){
						System.out.println(f);
					}
				}
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmRefresh);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmRename = new JMenuItem("Rename");
		mntmRename.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if(current == null){
					JOptionPane.showMessageDialog(frame, "There is no item to rename!");
					return;
				}
				String newName = JOptionPane.showInputDialog(frame, "New Name", "Rename", JOptionPane.QUESTION_MESSAGE);
				if(current.getUserObject() instanceof SerializationObject){
					SerializationObject obj = (SerializationObject) current.getUserObject();
					obj.setName(newName);
				}else if(current.getUserObject() instanceof SerializationField){
					SerializationField f = (SerializationField) current.getUserObject();
					f.setName(newName);
				}else if(current.getUserObject() instanceof SerializationArray){
					SerializationArray a = (SerializationArray) current.getUserObject();
					a.setName(newName);
				}
			}
		});
		mnEdit.add(mntmRename);
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mnEdit.add(mntmCut);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mnEdit.add(mntmPaste);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHowTo = new JMenuItem("How to");
		mnHelp.add(mntmHowTo);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutGUI();
			}
		});
		mnHelp.add(mntmAbout);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		tree = new JTree(new DefaultMutableTreeNode("null"));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(current == null) // nothing
					return;

			    if (current.isLeaf()) { // EOL
			    	if(current.getUserObject() instanceof SerializationField){
			    		SerializationField f = (SerializationField) current.getUserObject();
			    		arrayValueTree.setRootVisible(false);
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
			    		varibleTabbedPane.setSelectedIndex(0);
			    	}else if(current.getUserObject() instanceof SerializationString){
			    		SerializationString s = (SerializationString) current.getUserObject();
			    		oldValue.setText(s.getString());
			    		txtNewvalue.setText(s.getString());
			    		arrayValueTree.setRootVisible(false);
			    		varibleTabbedPane.setSelectedIndex(0);
			    	}else if (current.getUserObject() instanceof SerializationArray) {
			    		arrayValueTree.setRootVisible(true);
			    		SerializationArray obj = (SerializationArray) current.getUserObject();
			    		// Switch to array
			    		varibleTabbedPane.setSelectedIndex(1);
			    		
			    		//All the values
			    		DefaultTreeModel avtNode = (DefaultTreeModel) arrayValueTree.getModel();
			    		avtNode.setRoot(new DefaultMutableTreeNode(obj.getName()));
			    		DefaultMutableTreeNode avtRoot = (DefaultMutableTreeNode) avtNode.getRoot();
			    		
			    		switch (obj.type) {
						case SerializationFieldType.BYTE:
							for(byte b : obj.getData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.CHAR:
							for(char b : obj.getCharData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.SHORT:
							for(short b : obj.getShortData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.INTEGER:
							for(int b : obj.getIntData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.LONG:
							for(long b : obj.getLongData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.FLOAT:
							for(float b : obj.getFloatData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.DOUBLE:
							for(double b : obj.getDoubleData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						case SerializationFieldType.BOOLEAN:
							for(boolean b : obj.getBooleanData()){
								avtRoot.add(new DefaultMutableTreeNode(b));
							}
							break;
						default:
							break;
						}
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
		
		varibleTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		varibleTabbedPane.setEnabled(false);
		panel.add(varibleTabbedPane, BorderLayout.CENTER);
		
		/**
		 * This is for a varible or string.
		 */
		valuesPane = new JSplitPane();
		valuesPane.setResizeWeight(0.5);
		valuesPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		valuesPane.setLeftComponent(scrollPane_1);
		
		JLabel lblOldValues_1 = new JLabel("Old Value");
		scrollPane_1.setColumnHeaderView(lblOldValues_1);
		
		oldValue = new JTextField();
		oldValue.setEnabled(false);
		oldValue.setText("value");
		scrollPane_1.setViewportView(oldValue);
		oldValue.setColumns(10);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		valuesPane.setRightComponent(scrollPane_2);
		
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
		
		varibleTabbedPane.add("Varible", valuesPane);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		varibleTabbedPane.addTab("Array", null, scrollPane_3, null);
		
		arrayValueTree = new JTree(new DefaultMutableTreeNode("null"));
		arrayValueTree.setShowsRootHandles(true);
		arrayValueTree.setRootVisible(false);
		arrayValueTree.setEditable(true);
		scrollPane_3.setViewportView(arrayValueTree);
	}
	
	public void saveToFile(File file, boolean toCurrentFile){
		if(file == null){
			JFileChooser chooser = new JFileChooser();
			chooser.showSaveDialog(frame);
			file = chooser.getSelectedFile();
			if(file.getPath().indexOf(".") == -1){
				if(JOptionPane.showConfirmDialog(frame, "Do you want to save with out an extension?") == 1){
					chooser.showSaveDialog(frame);
					file = chooser.getSelectedFile();
				}
			}
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(toCurrentFile){
			currentFile = file;
		}
		if(file != null){
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db = new SerializationDatabase(file.getName().substring(0, file.getName().indexOf(".")));
			List<SerializationObject> tempObject = new ArrayList<>();
			tempObject.addAll(objects);
			for(SerializationObject o : tempObject){
				for(SerializationField f : fields.get(o)){
					o.addField(f);
				}
				for(SerializationArray a : array.get(o)){
					o.addArray(a);
				}
				for(SerializationString s : string.get(o)){
					o.addString(s);
				}
				db.addObject(o);
			}
			tempObject.clear();
			db.serializeFile(file.getAbsolutePath());
		}
	}
}
