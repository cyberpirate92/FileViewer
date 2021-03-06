package com.raviteja.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.ozten.font.JFontChooser;

public class EditorWindow extends JFrame {

	private static final long serialVersionUID = -4760621689475380711L;
	private JPanel centerPanel, bottomPanel;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, helpMenu;
	private JTextArea editor;
	private JLabel statusLabel;
	private File currentFile;
	private final static Logger logger = Logger.getLogger(EditorWindow.class);
	private boolean isSessionSaved;
	private JScrollPane scrollPane;

	public EditorWindow() {
		super("LF-Edit v0.1");

		currentFile = null;
		isSessionSaved = true;

		setupPanels();
		setupMenuBar();
		setupEventListeners();

		this.setJMenuBar(menuBar);

		this.setSize(new Dimension(500, 500));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void setupEventListeners() {
		this.editor.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				isSessionSaved = false;
				if (!EditorWindow.this.getTitle().endsWith("*")) {
					EditorWindow.this.setTitle(EditorWindow.this.getTitle()
							+ "*");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
					try {
						statusLabel.setText("Line " + (editor.getLineOfOffset(editor.getCaretPosition())+1));
					} catch (BadLocationException ble) {
						logger.error("Unable to get line number: " + ble);
						statusLabel.setText("");
					}
				}
			}
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	if(!isSessionSaved) {
		    		int choice = JOptionPane.showConfirmDialog(null, "You have unsaved changes,\nDo you want to save your work before exiting?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			        if (choice == JOptionPane.YES_OPTION) {
			            saveSessionToFile();
			        }
			        else if(choice == JOptionPane.NO_OPTION) {
			        	EditorWindow.this.dispose();
			        	System.exit(0);
			        }
		    	}
		    	else {
		    		EditorWindow.this.dispose();
		    		System.exit(0);
		    	}
		    }
		});
	}

	private void setupPanels() {

		editor = new JTextArea();
		scrollPane = new JScrollPane(editor);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		bottomPanel = new JPanel();

		statusLabel = new JLabel();
		bottomPanel.add(statusLabel);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void setupMenuBar() {

		menuBar = new JMenuBar();

		setupFileMenu();
		setupEditMenu();
		setupHelpMenu();

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
	}
	
	private void setupHelpMenu() {
		helpMenu = new JMenu("Help");
		
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessageDialog("LF-Edit version 0.1\n A fast editor especially for large files");
			}
		});
		
		helpMenu.add(aboutMenuItem);
	}

	private void setupFileMenu() {

		fileMenu = new JMenu("File");

		JMenuItem newFileMenuItem, openFileMenuItem, saveFileMenuItem, exitMenuItem;

		newFileMenuItem = new JMenuItem("New File");
		newFileMenuItem.setMnemonic(KeyEvent.VK_N);
		newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.ALT_MASK));
		newFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initNewSession();
			}
		});
		fileMenu.add(newFileMenuItem);

		openFileMenuItem = new JMenuItem("Open File");
		openFileMenuItem.setMnemonic(KeyEvent.VK_O);
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.ALT_MASK));
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isSessionSaved) {
					if(showSaveConfirmationDialog("Before Opening a new file, would you like to save changes to the current one ?") == SessionState.CANCELLED) {
						return;
					}
				}
				final JFileChooser fileChooser = new JFileChooser();
				int val = fileChooser.showOpenDialog(EditorWindow.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					loadFileIntoEditor();
					editor.requestFocus();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("No file chosen");
					}
				}
			}
		});
		fileMenu.add(openFileMenuItem);

		fileMenu.addSeparator();

		saveFileMenuItem = new JMenuItem("Save File");
		saveFileMenuItem.setMnemonic(KeyEvent.VK_S);
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.ALT_MASK));
		saveFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isSessionSaved) {
					saveSessionToFile();
				}
			}
		});
		fileMenu.add(saveFileMenuItem);

		fileMenu.addSeparator();

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.ALT_MASK));
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isSessionSaved) {
					if(showSaveConfirmationDialog("You have unsaved changes, do you want to save them before exiting?") == SessionState.CANCELLED) {
						return;
					}
				}
				EditorWindow.this.dispose();
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
	}

	private boolean saveSessionToFile() {
		if (currentFile == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No file specified for saving, getting now");
			}
			final JFileChooser fileChooser = new JFileChooser();
			int val = fileChooser.showSaveDialog(EditorWindow.this);
			if (val == JFileChooser.APPROVE_OPTION) {
				currentFile = fileChooser.getSelectedFile();
				if (logger.isDebugEnabled()) {
					logger.debug("File chosen for saving: "
							+ currentFile.getAbsolutePath());
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No file chosen to save file");
					return false;
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to save file to "
					+ currentFile.getAbsolutePath());
		}
		if (this.currentFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						currentFile, false));
				writer.write(editor.getText());
				writer.flush();
				writer.close();
				isSessionSaved = true;
				statusLabel.setText("File Saved");
				this.setTitle(this.currentFile.getAbsolutePath());
				if (logger.isDebugEnabled()) {
					logger.debug("File saved to "
							+ currentFile.getAbsolutePath());
				}
				return true;
			} catch (Exception e) {
				logger.error("File cannot be saved due to exception: " + e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private void initNewSession() {
		initNewSession("untitled");
	}
	
	private SessionState showSaveConfirmationDialog(String customMessage) {
		int option = JOptionPane.showConfirmDialog(null, customMessage, "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
		if(option == JOptionPane.YES_OPTION) {
			this.saveSessionToFile();
			return SessionState.SAVE_SUCCESSFUL;
		}
		else if(option == JOptionPane.NO_OPTION) {
			return SessionState.SAVE_ABORTED;
		}
		else {
			return SessionState.CANCELLED;
		}
	}
	
	private SessionState showSaveConfirmationDialog() {
		return showSaveConfirmationDialog("Your changes have not been saved,\nDo you want to save them?");
	}

	private void initNewSession(String filename) {

		if (!isSessionSaved) {
			SessionState saveState = showSaveConfirmationDialog();
			if(saveState == SessionState.CANCELLED) {
				return;
			}
		}

		this.setTitle(filename);
		this.editor.setText("");
		this.statusLabel.setText("");
		this.currentFile = null;
	}

	private void setupEditMenu() {

		editMenu = new JMenu("Edit");

		JMenuItem findMenuItem, fontMenuItem, tabSpaceSubMenu;

		findMenuItem = new JMenuItem("Find");
		findMenuItem.setMnemonic(KeyEvent.VK_F);
		findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				ActionEvent.ALT_MASK));
		editMenu.add(findMenuItem);
		
		fontMenuItem = new JMenuItem("Font");
		fontMenuItem.setMnemonic(KeyEvent.VK_T);
		fontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		fontMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Font selectedFont = JFontChooser.showDialog(EditorWindow.this);
				if(selectedFont != null) {
					editor.setFont(selectedFont);
				}
			}
		});
		editMenu.add(fontMenuItem);
		
		tabSpaceSubMenu = new JMenu("Tab Length");
		editMenu.add(tabSpaceSubMenu);
		
		ButtonGroup tabSpaceButtonGroup = new ButtonGroup();
		JMenuItem fourSpacedTabMenuItem, eightSpacedTabMenuItem;
		
		fourSpacedTabMenuItem = new JRadioButtonMenuItem("4 spaces");
		fourSpacedTabMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.setTabSize(4);
				if(logger.isDebugEnabled()) {
					logger.debug("Tab space set to 4 spaces");
				}
			}
		});
		tabSpaceButtonGroup.add(fourSpacedTabMenuItem);
		tabSpaceSubMenu.add(fourSpacedTabMenuItem);
		
		eightSpacedTabMenuItem = new JRadioButtonMenuItem("8 spaces");
		eightSpacedTabMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.setTabSize(8);
				if(logger.isDebugEnabled()) {
					logger.debug("Tab space set to 8 spaces");
				}
			}
		});
		tabSpaceButtonGroup.add(eightSpacedTabMenuItem);
		tabSpaceSubMenu.add(eightSpacedTabMenuItem);
		
		if(editor.getTabSize() == 4)
			fourSpacedTabMenuItem.setSelected(true);
		if(editor.getTabSize() == 8) {
			eightSpacedTabMenuItem.setSelected(true);
		}
	}

	private void loadFileIntoEditor() {
		editor.setText("");
		if (logger.isDebugEnabled()) {
			logger.debug("Loading file " + currentFile.getAbsolutePath()
					+ " into editor");
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					currentFile));
			String line;

			while ((line = reader.readLine()) != null) {
				editor.append(line + "\n");
			}

			reader.close();
			this.setTitle(currentFile.getAbsolutePath());
		} catch (FileNotFoundException fnfe) {
			showMessageDialog("The file doesn't exist, please open a new file");
			currentFile = null;
			logger.error(fnfe);
		} catch (IOException ioe) {
			showMessageDialog("An unhandled exception has occured, Please restart");
			logger.error(ioe);
		} catch (Exception e) {
			showMessageDialog("An unhandled exception has occured, Please restart");
			logger.error(e);
		}
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
}
