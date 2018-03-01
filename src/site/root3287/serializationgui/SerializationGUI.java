package site.root3287.serializationgui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private boolean preventClose = false;
	private boolean recentlySaved = true;
	
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
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(preventClose && !recentlySaved){
					int i = JOptionPane.showConfirmDialog(frame,
	                        "Do you want to save changes to the serialized file?", "Save Changes",
	                        JOptionPane.YES_NO_OPTION);
	                if (i == JOptionPane.NO_OPTION) {
	                    System.exit(0);
	                }else if(i == JOptionPane.YES_OPTION){
	                	saveToFile(currentFile, false);
	                	System.exit(0);
	                }
				}else{
					System.exit(0);
				}
			}
		});
		
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
					objects.add(new SerializationObject(o.getName()));
					root.add(object);
				}
				
				int i =0;
				for(SerializationObject co : objects){
					fields.put(objects.get(i), new ArrayList<SerializationField>());
					array.put(objects.get(i), new ArrayList<SerializationArray>());
					string.put(objects.get(i), new ArrayList<SerializationString>());
					
					DefaultMutableTreeNode object = (DefaultMutableTreeNode) root.getChildAt(i);
					for(SerializationString s : ((SerializationObject)(object.getUserObject())).strings){
						object.add(new DefaultMutableTreeNode(s));
						string.get(co).add(SerializationString.create(s.getName(), s.getString()));
					}
					for(SerializationField f : ((SerializationObject)(object.getUserObject())).fields){
						object.add(new DefaultMutableTreeNode(f));
						switch(f.type){
						case SerializationFieldType.BYTE:
							fields.get(co).add(SerializationField.createByteField(f.getName(), f.getByte()));
							break;
						case SerializationFieldType.SHORT:
							fields.get(co).add(SerializationField.createShortField(f.getName(), f.getShort()));
							break;
						case SerializationFieldType.CHAR:
							fields.get(co).add(SerializationField.createCharField(f.getName(), f.getChar()));
							break;
						case SerializationFieldType.INTEGER:
							fields.get(co).add(SerializationField.createIntegerField(f.getName(), f.getInt()));
							break;
						case SerializationFieldType.LONG:
							fields.get(co).add(SerializationField.createLongField(f.getName(), f.getLong()));
							break;
						case SerializationFieldType.DOUBLE:
							fields.get(co).add(SerializationField.createDoubleField(f.getName(), f.getDouble()));
							break;
						case SerializationFieldType.FLOAT:
							fields.get(co).add(SerializationField.createFloatField(f.getName(), f.getFloat()));
							break;
						case SerializationFieldType.BOOLEAN:
							fields.get(co).add(SerializationField.createBooleanField(f.getName(), f.getBoolean()));
							break;
						}
					}
					for(SerializationArray a : ((SerializationObject)(object.getUserObject())).arrays){
						DefaultMutableTreeNode arrays = new DefaultMutableTreeNode(a);
						object.add(arrays);
						switch(a.type){
						case SerializationFieldType.BYTE:
							array.get(co).add(SerializationArray.createByteArray(a.getName(), a.getData()));
							break;
						case SerializationFieldType.SHORT:
							array.get(co).add(SerializationArray.createShortArray(a.getName(), a.getShortData()));
							break;
						case SerializationFieldType.CHAR:
							array.get(co).add(SerializationArray.createCharArray(a.getName(), a.getCharData()));
							break;
						case SerializationFieldType.INTEGER:
							array.get(co).add(SerializationArray.createIntegerArray(a.getName(), a.getIntData()));
							break;
						case SerializationFieldType.LONG:
							array.get(co).add(SerializationArray.createLongArray(a.getName(), a.getLongData()));
							break;
						case SerializationFieldType.DOUBLE:
							array.get(co).add(SerializationArray.createDoubleArray(a.getName(), a.getDoubleData()));
							break;
						case SerializationFieldType.FLOAT:
							array.get(co).add(SerializationArray.createFloatArray(a.getName(), a.getFloatData()));
							break;
						case SerializationFieldType.BOOLEAN:
							array.get(co).add(SerializationArray.createBooleanArray(a.getName(), a.getBooleanData()));
							break;
						}
					}
					i++;
				}
				
				db = null;
				tree.validate();
				
				preventClose = true;
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
				
				preventClose = true;
				recentlySaved = false;
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
				
				recentlySaved = false;
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
				
				int index = 0;
				for(int i = 0; i < objects.size(); i++){
					if(objects.get(i).getName().equals( ((SerializationObject) current).getName())){
						index = i;
					}
				}
				
				SerializationObject currentObject = objects.get(index);
				
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
					}else{
						System.out.println("No key found!");
					}
					((DefaultTreeModel) tree.getModel()).insertNodeInto(new DefaultMutableTreeNode(c), selected, selected.getChildCount());
				}
				recentlySaved = false;
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
				if(preventClose && !recentlySaved){
					int i = JOptionPane.showConfirmDialog(frame,
	                        "Do you want to save changes to the serialized file?", "Save Changes",
	                        JOptionPane.YES_NO_OPTION);
	                if (i == JOptionPane.NO_OPTION) {
	                    System.exit(0);
	                }else if(i == JOptionPane.YES_OPTION){
	                	saveToFile(currentFile, false);
	                	System.exit(0);
	                }
				}else{
					System.exit(0);
				}
			}
		});
		
		JMenuItem mntmSaveKey = new JMenuItem("Save Key");
		mnFile.add(mntmSaveKey);
		
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				displaySerializationTemp();
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
		txtNewvalue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(txtOldvalue.getText().equalsIgnoreCase(txtNewvalue.getText())){ // We did nothing but pressed enter...
					return;
				}
				
				if(((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()) == null){
					return;
				}
				
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				if(!(current.getUserObject() instanceof SerializationField) && !(current.getUserObject() instanceof SerializationString)){
					return;
				}
				if(current.getUserObject() instanceof SerializationField){
					SerializationField f = (SerializationField) current.getUserObject();
					
					int index = 0;
					for(int i = 0; i < objects.size(); i++){
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) current.getParent();
						if(parent.getUserObject() instanceof SerializationObject){
							if(objects.get(i).getName().equalsIgnoreCase(((SerializationObject)parent.getUserObject()).getName())){
								index = i;
								break;
							}
						}
					}
					
					int fIndex = 0;
					for(int i = 0; i < fields.get(objects.get(index)).size(); i++){
						if(current.getUserObject() instanceof SerializationField){
							if(fields.get(objects.get(index)).get(i).getName().equalsIgnoreCase((((SerializationField) current.getUserObject()).getName()))){
								fIndex = i;
								break;
							}
						}
					}
					
					switch (f.type) {
					case SerializationFieldType.BYTE:
						try{
							byte b = Byte.parseByte(txtNewvalue.getText());
							SerializationField newField = SerializationField.createByteField(f.getName(), b);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						}catch (Exception error) {
							JOptionPane.showMessageDialog(frame, "The input value is invalid due to mismatched value type. This value require you to have a byte.");
						}
						break;
					case SerializationFieldType.CHAR:
						try{
							if(txtNewvalue.getText().length() > 1){
								JOptionPane.showMessageDialog(frame, "The input you have made is a string! This field is for a character. Taking the first character.");
							}
							char c = txtNewvalue.getText().charAt(0);
							
							SerializationField newField = SerializationField.createCharField(f.getName(), c);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						}catch(Exception error){
							
						}
						break;
					case SerializationFieldType.SHORT:
						try{
							short s = Short.parseShort(txtNewvalue.getText());
							
							SerializationField newField = SerializationField.createShortField(f.getName(), s);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						}catch (Exception e2) {
							JOptionPane.showMessageDialog(frame, "The input value is invalid due to mismatched value type. This value require you to have a short.");
						}
						break;
					case SerializationFieldType.INTEGER:
						try{
							int i = Integer.parseInt(txtNewvalue.getText());
							
							SerializationField newField = SerializationField.createIntegerField(f.getName(), i);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						}catch (Exception e2) {
							JOptionPane.showMessageDialog(frame, "The input value is invalid due to mismatched value type. This value require you to have a integer.");
						}
						break;
					case SerializationFieldType.FLOAT:
						try{
							float inputf = Float.parseFloat(txtNewvalue.getText());
							SerializationField newField = SerializationField.createFloatField(f.getName(), inputf);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						}catch (Exception e2) {
							JOptionPane.showMessageDialog(frame, "The input value is invalid due to mismatched value type. This value require you to have a float.");
						}
						break;
					case SerializationFieldType.DOUBLE:
						try {
							double d = Double.parseDouble(txtNewvalue.getText());
							
							SerializationField newField = SerializationField.createDoubleField(f.getName(), d);
							current.setUserObject(newField);
							
							fields.get(objects.get(index)).set(fIndex, newField);
						} catch (Exception e2) {
							// TODO: handle exception
						}
						break;
					case SerializationFieldType.BOOLEAN:
						boolean b = Boolean.parseBoolean(txtNewvalue.getText());
						SerializationField newField = SerializationField.createBooleanField(f.getName(), b);
						current.setUserObject(newField);
						fields.get(objects.get(index)).set(fIndex, newField);
					default:
						break;
					}
					recentlySaved = false;
					oldValue.setText(txtNewvalue.getText().trim());
				}
			}
		});
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
		displaySerializationTemp();
		if(file == null){
			JFileChooser chooser = new JFileChooser();
			chooser.showSaveDialog(frame);
			file = chooser.getSelectedFile();
			if(file == null){
				return;
			}else if(file.getPath().indexOf(".") == -1){
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
			SerializationDatabase tempDB = new SerializationDatabase(file.getName().substring(0, file.getName().indexOf('.')));
			List<SerializationObject> tempObj = new ArrayList<>(objects);
			for(SerializationObject o : tempObj){
				for(SerializationField f:fields.get(o)){
					o.addField(f);
				}
				tempDB.addObject(o);
			}
			tempObj.clear();
			tempDB.serializeFile(file.getAbsolutePath());
		}
		recentlySaved = true;
	}
	
	public void displaySerializationTemp(){
		for(SerializationObject o : objects){
			System.out.println(o);
			if(!fields.containsKey(o) || !array.containsKey(o) || !string.containsKey(o)){
				System.out.println("something does not contains this obj");
				return;
			}
			for(SerializationField field : fields.get(o)){
				System.out.println("\t"+field);
				switch (field.type){
				case SerializationFieldType.BYTE:
					System.out.println("\t\t"+field.getByte());
					break;
				case SerializationFieldType.SHORT:
					System.out.println("\t\t"+field.getShort());
					break;
				case SerializationFieldType.CHAR:
					System.out.println("\t\t"+field.getChar());
					break;
				case SerializationFieldType.INTEGER:
					System.out.println("\t\t"+field.getInt());
					break;
				case SerializationFieldType.LONG:
					System.out.println("\t\t"+field.getLong());
					break;
				case SerializationFieldType.DOUBLE:
					System.out.println("\t\t"+field.getDouble());
					break;
				case SerializationFieldType.FLOAT:
					System.out.println("\t\t"+field.getFloat());
					break;
				case SerializationFieldType.BOOLEAN:
					System.out.println("\t\t"+field.getBoolean());
					break;
				}
			}
			for(SerializationArray a : array.get(o)){
				System.out.println("\t"+a);
			}
			for(SerializationString s : string.get(o)){
				System.out.println("\t"+s);
			}
		}
	}
}
