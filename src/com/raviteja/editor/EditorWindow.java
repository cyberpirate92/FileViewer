package com.raviteja.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

public class EditorWindow extends JFrame {

	private static final long serialVersionUID = -4760621689475380711L;
	private JPanel centerPanel, bottomPanel;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JTextArea editor;
	private JLabel statusLabel;
	private File currentFile;
	private final static Logger logger = Logger.getLogger(EditorWindow.class);

	public EditorWindow() {
		super("LF-Edit v0.1");
		
		currentFile = null;
		
		setupPanels();
		setupMenuBar();
		
		this.setJMenuBar(menuBar);
		
		this.setSize(new Dimension(500,500));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void setupPanels() {

		centerPanel = new JPanel();

		editor = new JTextArea();
		centerPanel.add(editor);

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

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
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
				final JFileChooser fileChooser = new JFileChooser();
				int val = fileChooser.showOpenDialog(EditorWindow.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					loadFileIntoEditor();
				}
				else {
					
				}
			}
		});
		fileMenu.add(newFileMenuItem);

		openFileMenuItem = new JMenuItem("Open File");
		openFileMenuItem.setMnemonic(KeyEvent.VK_O);
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.ALT_MASK));
		fileMenu.add(openFileMenuItem);

		fileMenu.addSeparator();

		saveFileMenuItem = new JMenuItem("Save File");
		saveFileMenuItem.setMnemonic(KeyEvent.VK_S);
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.ALT_MASK));
		fileMenu.add(saveFileMenuItem);

		fileMenu.addSeparator();

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.ALT_MASK));
		fileMenu.add(exitMenuItem);
	}

	private void setupEditMenu() {

		editMenu = new JMenu("Edit");

		JMenuItem findMenuItem;

		findMenuItem = new JMenuItem("Find");
		findMenuItem.setMnemonic(KeyEvent.VK_F);
		findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				ActionEvent.ALT_MASK));
		editMenu.add(findMenuItem);
	}
	
	private void loadFileIntoEditor() {
		if(logger.isDebugEnabled()) {
			logger.debug("Loading file " + currentFile.getAbsolutePath() + " into editor");
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(currentFile));
			String line;
			
			while((line = reader.readLine()) != null) {
				editor.append(line);
			}
			
			reader.close();
		}
		catch(FileNotFoundException fnfe) {
			showErrorMessageDialog("The file doesn't exist, please open a new file");
			currentFile = null;
			logger.error(fnfe);
		}
		catch(IOException ioe) {
			showErrorMessageDialog("An unhandled exception has occured, Please restart");
			logger.error(ioe);
		}
		catch(Exception e) {
			showErrorMessageDialog("An unhandled exception has occured, Please restart");
			logger.error(e);
		}
	}
	
	private void showErrorMessageDialog(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
}
