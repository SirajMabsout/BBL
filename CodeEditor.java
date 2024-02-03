
// A simple code editor GUI that allows users to write and save code. The editor supports undo and redo functionality,
//  as well as a dark mode toggle. The editor also displays line numbers and allows users to save their code to a file.
//  The editor uses a custom text area to display alternating line colors.

package coProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.undo.*;

public class CodeEditor {

	// A class representing a custom text area with line numbers and undo/redo
	// functionality.
	// Contains a code area, line numbers, and an undo manager.
	// Also contains a string representing the geu and address.

	private static CustomTextArea codeArea;
	private static JTextArea lineNumbers;
	private static UndoManager undoManager;
	private String geu = "";

	// The main method that initializes the CodeEditor and BBLCompiler objects

	public static void main(String[] args) {

		CodeEditor b = new CodeEditor();
		b.createAndShowGUI();

		while (b.geu.isEmpty()) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void createAndShowGUI() {

		// Creates a new JFrame with a code editor.

		JFrame frame = new JFrame("Code Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		codeArea = new CustomTextArea(Color.WHITE, new Color(230, 230, 230));
		codeArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		codeArea.setTabSize(4);

		// Initializes the undo manager and line numbers for the code area.

		undoManager = new UndoManager();
		codeArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

		lineNumbers = new JTextArea("1");
		lineNumbers.setFont(new Font("monospaced", Font.PLAIN, 12));
		lineNumbers.setBackground(Color.LIGHT_GRAY);
		lineNumbers.setEditable(false);

		// Adds a document listener to the code area and updates the line numbers
		// displayed in the lineNumbers text area.
		// The line numbers are updated every time the document is changed.

		codeArea.getDocument().addDocumentListener(new DocumentListener() {
			private void updateLineNumbers() {
				StringBuilder sb = new StringBuilder();
				Element root = codeArea.getDocument().getDefaultRootElement();
				int count = root.getElementCount();
				for (int i = 1; i <= count; i++) {
					sb.append(i).append(System.lineSeparator());
				}
				lineNumbers.setText(sb.toString());
			}

			// Overrides the insertUpdate, removeUpdate, and changedUpdate methods of the
			// DocumentListener interface.
			// Calls the updateLineNumbers method whenever a change is made to the document.

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLineNumbers();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLineNumbers();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLineNumbers();
			}
		});

		// Creates a JScrollPane with a given JTextArea and JList as its row header
		// view.
		// The JScrollPane is then added to the center of a given container using
		// BorderLayout.

		JScrollPane scrollPane = new JScrollPane(codeArea);
		scrollPane.setRowHeaderView(lineNumbers);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// Creates a JPanel with a FlowLayout and adds a JButton to it.

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		JButton saveButton = new JButton("Save");

		// Adds an action listener to the save button. When the button is clicked, a
		// file chooser dialog is opened
		// and the user can select a file to save the code in. If a file is selected,
		// the code is written to the file
		// the file name and contents are stored in the address and geu variables,
		// respectively. If an error occurs while saving the file, an error message is
		// displayed.

		saveButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			int returnValue = fileChooser.showSaveDialog(null);

			// Displays a file chooser dialog and returns the selected file if the user
			// approves the selection.
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();

				// Saves the contents of the codeArea to a file selected by the user.

				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile + ".s"));

					BBLCompiler compiler = new BBLCompiler(codeArea.getText());
					String compiledCode = compiler.compile();
					writer.write(compiledCode);
					writer.close();

					// Displays a message dialog indicating whether the file was saved successfully
					// or not.

					JOptionPane.showMessageDialog(frame, "File saved successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					frame.dispose();
					System.exit(returnValue);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(frame, "An error occurred while saving the file.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Adds a save button to the bottom panel.

		bottomPanel.add(saveButton);

		// Creates a new JButton with the text "Undo" and adds an ActionListener to it.
		// When the button is clicked, it checks if the undoManager can undo and if so
		// it undoes the last action.

		JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(e -> {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		});

		// Adds a redo button to the bottom panel and sets an action listener to redo
		// the last action if possible.

		bottomPanel.add(undoButton);

		JButton redoButton = new JButton("Redo");
		redoButton.addActionListener(e -> {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		});

		// Adds a redo button to the bottom panel.

		bottomPanel.add(redoButton);

		JToggleButton darkModeSwitch = new JToggleButton("Dark Mode");
		darkModeSwitch.addActionListener(new ActionListener() {
			@Override

			// This method is called when the user interacts with the dark mode switch. If
			// the switch is selected, the code area and line numbers will be set to a dark
			// color scheme.

			public void actionPerformed(ActionEvent e) {
				if (darkModeSwitch.isSelected()) {
					codeArea.setBackground(Color.DARK_GRAY);
					codeArea.setForeground(Color.LIGHT_GRAY);
					lineNumbers.setBackground(Color.GRAY);
					lineNumbers.setForeground(Color.LIGHT_GRAY);
					codeArea.setLineColors(new Color(60, 60, 60), Color.DARK_GRAY);

					// Sets the background and foreground colors of the code area and line numbers
					// to white and black, respectively, and sets the line colors to white and a
					// light gray color.
					// This is executed when the condition in the if statement is false.

				} else {
					codeArea.setBackground(Color.WHITE);
					codeArea.setForeground(Color.BLACK);
					lineNumbers.setBackground(Color.LIGHT_GRAY);
					lineNumbers.setForeground(Color.BLACK);
					codeArea.setLineColors(Color.WHITE, new Color(230, 230, 230));
				}
			}
		});

		// Adds a dark mode switch to the bottom panel of the content pane and makes the
		// frame visible.
		bottomPanel.add(darkModeSwitch);

		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		frame.setVisible(true);
	}

	// A custom text area that has alternating line colors.

	static class CustomTextArea extends JTextArea {

		private static final long serialVersionUID = 1L;
		private Color evenLineColor;
		private Color oddLineColor;

		// Creates a custom text area with alternating line colors.
		public CustomTextArea(Color evenLineColor, Color oddLineColor) {
			this.evenLineColor = evenLineColor;
			this.oddLineColor = oddLineColor;
			setOpaque(false);
		}

		// Sets the colors of the even and odd lines in the component and repaints it.
		public void setLineColors(Color evenLineColor, Color oddLineColor) {
			this.evenLineColor = evenLineColor;
			this.oddLineColor = oddLineColor;
			repaint();
		}

		@Override
		// Overrides the paintComponent method to paint the background of the table with
		// alternating colors.
		protected void paintComponent(Graphics g) {
			int lineHeight = getRowHeight();
			int totalRows = getHeight() / lineHeight;

			// Draws a table with alternating row colors.
			for (int i = 0; i < totalRows; i++) {
				Color lineColor = (i % 2 == 0) ? evenLineColor : oddLineColor;
				g.setColor(lineColor);
				g.fillRect(0, i * lineHeight, getWidth(), lineHeight);
			}

			// Overrides the paintComponent method of the parent class to paint the
			// component with the given graphics object.
			super.paintComponent(g);
		}
	}
}
