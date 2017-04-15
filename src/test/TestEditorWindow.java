package test;

import javax.swing.SwingUtilities;

import org.apache.log4j.BasicConfigurator;

import com.raviteja.editor.EditorWindow;

public class TestEditorWindow {

	public static void main(String[] args) {

		BasicConfigurator.configure();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new EditorWindow();
			}
		});
	}

}
