/**
 * A program that lets user choose files
 * to be copied or moved to a default destination 
 * directory and edit the index.html file in the
 * directory immediately above the default directory.
 * Started on: 22-05-2020
 * @version 1.2 22/07/2020 
 * @author Michael May
 */
package uploader;

import java.io.*;
import java.nio.file.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.border.TitledBorder;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.select.Elements;

public class Uploader {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			public void run () {
				new UploaderFrame();
			}
		});
	}
}

class UploaderFrame extends JFrame {
	private final String NEW_LINE = System.lineSeparator();
	private final String SEPARATOR = System.getProperty("file.separator");
	private final String USER_HOME = System.getProperty("user.home");
	private JTextArea display;
	private JButton copyButton;
	private JButton moveButton;
	private JButton clearButton;
	private File[] files;
	private JFileChooser chooser;
	private String uploadFolder;
	private ArrayList<File> addedFiles;
	private ArrayList<File> deletedFiles;

	public UploaderFrame () {
		addedFiles = new ArrayList<>(50);
		setTitle("Loader");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		setLocation(screenWidth / 4, screenHeight / 4);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		JPanel displayPanel = createDisplayPanel();
		add(displayPanel, BorderLayout.CENTER);
		JPanel buttonPanel = createButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private JMenuBar createMenuBar () {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem openItem = createOpenItem();
		JMenuItem deleteItem = createDeleteItem();
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				System.exit(0);
			}
		}); 

		menu.add(openItem);
		menu.add(deleteItem);
		menu.add(exitItem);
		menuBar.add(menu);

		return menuBar;
	}

	private JMenuItem createOpenItem () {
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int option = fileChooserOption(JFileChooser.FILES_ONLY);
				if (option == JFileChooser.APPROVE_OPTION) {
					files = chooser.getSelectedFiles();
					for (File file : files) {
						display.append(file.getParent() + SEPARATOR + file.getName() + NEW_LINE);
						addedFiles.add(file);
					}
					copyButton.setEnabled(true);
					moveButton.setEnabled(true);
					clearButton.setEnabled(true);
				}
			}
		});
		return openItem;
	}
	private JMenuItem createDeleteItem () {
		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				try {
					deletedFiles = new ArrayList<File>();
					int option = fileChooserOption(JFileChooser.FILES_ONLY);
					if (option == JFileChooser.APPROVE_OPTION) {
						files = chooser.getSelectedFiles();
						for (File file : files) {
							deletedFiles.add(file);
							Files.delete(file.toPath());
						}
					}
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		});
		return deleteItem;
	}

	private JPanel createDisplayPanel () {
		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(BorderFactory.createTitledBorder("Files"));
		display = new JTextArea(20, 30);
		display.setEditable(false);
		JScrollPane scrlPane = new JScrollPane(display);
		displayPanel.add(scrlPane);

		return displayPanel;
	}

	private JPanel createButtonPanel () {
		JPanel buttonPanel = new JPanel();
		copyButton = createCopyButton();
		moveButton = createMoveButton();
		clearButton = createClearButton();

		buttonPanel.add(copyButton);
		buttonPanel.add(moveButton);
		buttonPanel.add(clearButton);

		return buttonPanel;
	}

	private JButton createCopyButton () {
		JButton copyButton = new JButton("Copy");
		if (files == null)
			copyButton.setEnabled(false);
		copyButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				if (uploadFolder == null)
					uploadFolder = getUploadPath();
				
				copyOrMove("copy");
			}
		});
		return copyButton;
	}

	private JButton createMoveButton () {
		JButton moveButton = new JButton("Move");
		if (files == null)
			moveButton.setEnabled(false);
		moveButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				if (uploadFolder == null)
					uploadFolder = getUploadPath();
				
				copyOrMove("move");
			}
		});
		return moveButton;
	}
	private JButton createClearButton () {
		JButton clearButton = new JButton("Clear");
		if (files == null)
			clearButton.setEnabled(false);
		clearButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				files = null;
				copyButton.setEnabled(false);
				moveButton.setEnabled(false);
				clearButton.setEnabled(false);
				display.setText("");
			}
		});
		return clearButton;
	}

	private void copyOrMove (String cm) {
		Path targetPath = null;
		for (File file : addedFiles) 
			try  {
				targetPath = Paths.get(uploadFolder + SEPARATOR + file.getName());
				if (cm.equals("copy"))
					Files.copy(Paths.get(file.getPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
				else if (cm.equals("move"))
					Files.move(Paths.get(file.getPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
			/*catch (FileAlreadyExistsException ex) {
				JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} */
			catch (IOException ioEx) {
				JOptionPane.showMessageDialog(this, ioEx.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		if (cm.equals("copy"))
			JOptionPane.showMessageDialog(this, "All files have been successfully copied.", "Information", JOptionPane.INFORMATION_MESSAGE);	
		else if (cm.equals("copy"))
			JOptionPane.showMessageDialog(this, "All files have been successfully moved.", "Information", JOptionPane.INFORMATION_MESSAGE);	

		Boolean written = addRecordsToHTML();
		if (written) {
			addedFiles = new ArrayList<>(50);
			JOptionPane.showMessageDialog(this, "All records have been successfully added to HTML file.", "Information", JOptionPane.INFORMATION_MESSAGE);	
		}
		else
			JOptionPane.showMessageDialog(UploaderFrame.this, "Error adding records to HTML file.", "Error", JOptionPane.ERROR_MESSAGE);
	}

	private String getUploadPath () {
		File uploadPathFile = new File("uploadPath");
		String uploadPathString = null;

		try (InputStream input = new FileInputStream(uploadPathFile)) {
			Scanner in = new Scanner(input);
			uploadPathString = in.nextLine();	
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "File not found.\nCreating a new htdocs path file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		int option = -1;
		File chosen = null;
		if (uploadPathString  == null) {
			option = fileChooserOption(JFileChooser.DIRECTORIES_ONLY);
			if (option == JFileChooser.APPROVE_OPTION) {
				chosen = chooser.getSelectedFile();
				uploadPathString  = chosen.getParent();
				createDefaultHtdocsPathFile(uploadPathString);
			}
		}
		return uploadPathString;
	}

	private void createDefaultHtdocsPathFile (String path) {
		int option = JOptionPane.showConfirmDialog(this, "Make this folder your default upload folder?",
									"Set Default Upload Folder", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option == JOptionPane.NO_OPTION) 
			return;
		if (option == JOptionPane.YES_OPTION)
			try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File("uploadPath")))) {
				osw.write(path);	
			} catch(IOException ioEx) {
				JOptionPane.showMessageDialog(this, "Cannot open file for write.", "Error", JOptionPane.ERROR_MESSAGE);
			}
	}

	private int fileChooserOption (int operation) {
		chooser = new JFileChooser();
		int option = -1;

		if (operation == JFileChooser.FILES_ONLY) {
			chooser.setCurrentDirectory(new File(USER_HOME + SEPARATOR + "Downloads" + SEPARATOR));
			chooser.setMultiSelectionEnabled(true);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		 	option = chooser.showOpenDialog(this);
		}
		else if (operation == JFileChooser.DIRECTORIES_ONLY) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			option = chooser.showSaveDialog(this);
		}
		return option;
	}
	private Boolean addRecordsToHTML () {
		Path uploadPath = Paths.get(uploadFolder); // /uploader
		Path sub = uploadPath.subpath(0, uploadPath.getNameCount() - 1); // /uploader where index.html exists
		Path root = uploadPath.getRoot();
		Path base = Paths.get(root.toString() + sub.toString());
		String htmlFilePathString = base.toString() + SEPARATOR + "index.html";
		Path htmlFilePath = Paths.get(htmlFilePathString);

		File input = new File(htmlFilePathString);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://192.168.1.108:8888/");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			return false;
		}
		Elements unorderedList = doc.getElementsByTag("ul");
		Element ul = null;
		if (unorderedList.size() > 0)
			ul = unorderedList.first();
		
		String prefix = "<a href=\"./";
		String midFix = "\">";
		String suffix = "</a>";
		Path subTargetPath;
		String relative;

		Element listItem = null;
		Element li = null;
		Path targetPath = null;
		for (File file : addedFiles) {
			targetPath = Paths.get(uploadFolder + SEPARATOR + file.getName()); // /uploader/Files/filename.xx
			subTargetPath = targetPath.subpath(0, targetPath.getNameCount()); // /uploader/Files
			relative = sub.relativize(subTargetPath).toString(); // Files/filename.xx
			listItem = ul.prependElement("li");
			li = listItem.html(prefix + relative + midFix + file.getName() + suffix);
		}
		
		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(htmlFilePath))) {
			pw.print(doc.html());
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			return false;
		}
		return true;
	}
	private Boolean deleteRecordToHTML () {
		// TODO

		return true;
	}
}