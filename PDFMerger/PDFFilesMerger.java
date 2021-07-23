/**
 * PDFMerger GUI
 * @version 0.11 2020-4-9
 * @author Michael May
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PDFFilesMerger {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			@Override
			public void run () {
				new PDFMergerFrame();
			}
		});
	}
}

class PDFMergerFrame extends JFrame {

	private JTextArea textArea;
	private JTextField textField;
	private JButton bindButton;
	private File[] files;
	private File mergedFile;

	public PDFMergerFrame () {
		setTitle("PDF Merger");
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JMenu menu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				openFiles();
				for (File file : files) 
					textArea.append(file.getParent() + "\\" + file.getName() + "\n");
				textArea.append("\n");
				bindButton.setEnabled(true);
			}
		});

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				System.exit(0);
			}
		});

		menu.add(openItem);
		//menu.add(saveItem);
		menu.add(exitItem);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		setJMenuBar(menuBar);

		textArea = new JTextArea(40, 50);
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);

		JPanel operationPanel = new JPanel();
		BoxLayout layout = new BoxLayout(operationPanel, BoxLayout.LINE_AXIS);
		operationPanel.setLayout(layout);

		JLabel label = new JLabel("Result: ");

		textField = new JTextField(30);
		textField.setEditable(false);

		bindButton = new JButton("Bind");
		bindButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				try {
					mergeFiles();	
					String destination = mergedFile.getParent() + "\\" + mergedFile.getName();
					textField.setText("Saved to " + destination);
				}
				catch (IOException ioEx) {
					ioEx.printStackTrace();
				}
				
				//saveItem.setEnabled(true);
			}
		});

		operationPanel.add(label);
		operationPanel.add(textField);
		operationPanel.add(bindButton);
		add(operationPanel, BorderLayout.NORTH);

		pack();
		setVisible(true);
	}


	public void openFiles () {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("C:\\Users\\Sharon Li\\Downloads\\temp"));
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
			}

			public String getDescription() {
				return "PDF files";
			}
		});
		chooser.setMultiSelectionEnabled(true);
		int option = chooser.showOpenDialog(this);
		
		if (option == JFileChooser.APPROVE_OPTION) 
			files = chooser.getSelectedFiles();
	}

	public void mergeFiles () throws IOException {
		
		saveFile();

		FileOutputStream out = new FileOutputStream(mergedFile);
		PDFMergerUtility merger = new PDFMergerUtility();

		for (File f : files)
			merger.addSource(f);

		merger.setDestinationFileName(mergedFile.getName());
		merger.setDestinationStream(out);
		merger.mergeDocuments(null);
	}

	public void saveFile () {
		File selected = null;

		JFileChooser save = new JFileChooser();
		save.setCurrentDirectory(new File("C:\\Users\\Sharon Li\\Downloads\\temp"));
		save.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
			}

			public String getDescription() {
				return "PDF files";
			}
		});
		int r = save.showSaveDialog(this);
		String dest = save.getSelectedFile().getParent() + "\\" + 
					save.getSelectedFile().getName() + ".pdf";
		if (r == JFileChooser.APPROVE_OPTION) {
			mergedFile = new File(dest); 
		}
	}
}