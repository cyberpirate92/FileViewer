package test;

import javax.swing.SwingUtilities;

import com.raviteja.editor.EditorWindow;

public class TestEditorWindow {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new EditorWindow();
			}
		});
	}

}
