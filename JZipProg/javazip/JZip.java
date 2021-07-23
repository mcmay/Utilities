/**
 * A simple GUI program that compresses files and/or directories
 * and decompresses zip files.
 * @author Michael May
 * @version 1.3 11-05-2020
 */
package javazip;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.zip.*;
import java.util.ArrayList;

public class JZip {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			public void run () {
				new JZipFrame();
			}
		});
	}
}

class JZipFrame extends JFrame {
	private final String NEW_LINE = "\n";
	private final String SEPARATOR = System.getProperty("file.separator");
	private JTextArea displayArea;
	private JButton listButton;
	private JButton decompressButton;
	private File[] dChosenItems;
	private ArrayList<Integer> userOptions = new ArrayList<>();

	public JZipFrame () {
		setTitle("JZip");
		Toolkit tk = Toolkit.getDefaultToolkit();
    	Dimension screenSize = tk.getScreenSize();
    	int screenHeight = screenSize.height;
    	int screenWidth = screenSize.width;
    	setSize(screenWidth / 2, screenHeight / 2);
    	setLocation(screenWidth / 4, screenHeight / 4);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);

    	JMenuBar menuBar = createMenuBar();
    	JScrollPane displayPanel = createDisplayPanel();
    	add(displayPanel, BorderLayout.CENTER);
    	JPanel decompressPanel = createDecompressPanel();
		add(decompressPanel, BorderLayout.SOUTH);
    	setJMenuBar(menuBar);

    	setVisible(true);
	} 

	private JMenuBar createMenuBar () {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Operations");
		JMenuItem compressItem = createCompressItem();
		JMenuItem decompressItem = createDecompressItem();
		JMenuItem quitItem = createQuitItem();

		menu.add(compressItem);
		menu.add(decompressItem);
		menu.add(quitItem);
		menuBar.add(menu);

		return menuBar;
	}

	private JScrollPane createDisplayPanel () {
		displayArea = new JTextArea(20, 40);
		displayArea.setMargin(new Insets(5, 5, 5, 5));
		displayArea.setEditable(false);
		JScrollPane scrlPanel = new JScrollPane(displayArea);

		return scrlPanel;
	}

	private JMenuItem createCompressItem () {
		JMenuItem cItem = new JMenuItem("Compression");
		cItem.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				File[] cChosenItems = selectFilesForCompression();
				compress(cChosenItems);
			}
		});

		return cItem;
	}

	private JMenuItem createDecompressItem () {
		JMenuItem dItem = new JMenuItem("Decompression");
		dItem.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				dChosenItems = selectFilesForDecompression();
				listButton.setEnabled(true);
				decompressButton.setEnabled(true);
			}
		});

		return dItem;
	}

	private JPanel createDecompressPanel () {
		JPanel dPanel = new JPanel();
		listButton = new JButton("List Entries");
		decompressButton = new JButton("Decompress");
		listButton.setEnabled(false);
		decompressButton.setEnabled(false);
		listButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				displayArea.setText("");
				for (File item : dChosenItems)
					listZipEntries(item);
			}
		});
		decompressButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				for (File f : dChosenItems)
					try {
						decompress(f);
					}
					catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(JZipFrame.this, ex.toString(), "Error#7", JOptionPane.ERROR_MESSAGE);
					}
				if (userOptions.contains(JOptionPane.CANCEL_OPTION)) {
					JOptionPane.showMessageDialog(JZipFrame.this, "One or more zip files have not been decompressed.", 
													"WARNING", JOptionPane.WARNING_MESSAGE);
					// refresh option list
					userOptions = new ArrayList<>();
				}
				else 	
					JOptionPane.showMessageDialog(JZipFrame.this, "All zip files have been successfully decompressed.", 
													"Information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		dPanel.add(listButton);
		dPanel.add(decompressButton);

		return dPanel;
	}

	private final File[] selectFilesForCompression () {
		final JFileChooser cChooser = new JFileChooser();
		File[] selectedItems = null;

		cChooser.setMultiSelectionEnabled(true);
		cChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int cRetVal = cChooser.showOpenDialog(JZipFrame.this);
		if (cRetVal == JFileChooser.APPROVE_OPTION) {
			selectedItems = cChooser.getSelectedFiles();
			for (File item : selectedItems)
				displayArea.append(item.getParent() + SEPARATOR 
										+ item.getName() + NEW_LINE);
		}
		return selectedItems;
	}

	private final File[] selectFilesForDecompression () {
		final JFileChooser dChooser = new JFileChooser();
		dChooser.setMultiSelectionEnabled(true);
		dChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dChooser.addChoosableFileFilter(new FileFilter() {
			public String getDescription () {
				return "ZIP Files (*.zip)";
			}
			public boolean accept (File f) {
				if (f.isDirectory())
					return true;
				else 
					return f.getName().toLowerCase().endsWith(".zip");
			}
		});
		int dRetVal = dChooser.showOpenDialog(JZipFrame.this);
		File [] selectedItems = null;
		if (dRetVal == JFileChooser.APPROVE_OPTION) {
			selectedItems = dChooser.getSelectedFiles();
			for (File item : selectedItems)
				displayArea.append(item.getParent() + SEPARATOR 
										+ item.getName() + NEW_LINE);
		}
		return selectedItems;
	}

	private JMenuItem createQuitItem () {
		JMenuItem qItem = new JMenuItem("Quit");
		qItem.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent event) {
				System.exit(0);
			}
		});
		return qItem;
	}

	private final void compress (File[] files) {
		FileOutputStream out = null;
		String zipName = null;

		if (files.length == 1) { // When only a single file or directory is to be compressed
			zipName = files[0].getName();
			if (files[0].isDirectory()) {
				if (zipName.endsWith("/") || zipName.endsWith("\\"))
					zipName = zipName.substring(0, zipName.length() - 1);
			}
			try {
				// set up output file name as the name of the directory or the root name of the single file
				String name = null;
				if (!files[0].isDirectory())
					name = zipName.substring(0, zipName.lastIndexOf("."));
				else
					name = zipName;
				out = new FileOutputStream(files[0].getParent() + SEPARATOR + name + ".zip");
			}
			catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else { // when multiple directories and/or files to be compressed
			try { // put files under the parent directory of the first file or directory to be compressed
				out = new FileOutputStream(files[0].getParent() + SEPARATOR + "compressed.zip");
			}
			catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "Error#1", JOptionPane.ERROR_MESSAGE);
			}
		}
		ZipOutputStream zipOut = new ZipOutputStream(out);
		zipOut.setMethod(ZipOutputStream.DEFLATED);
		for (File f : files) {
			try {
				zipFile(f, f.getName(), zipOut);	
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "Error#2", JOptionPane.ERROR_MESSAGE);
			}
			displayArea.append("Now processing: " + f.getName() + NEW_LINE);
		}
		JOptionPane.showMessageDialog(this, "Compression was successful!", "Message", JOptionPane.INFORMATION_MESSAGE);
		try {
			zipOut.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void zipFile (File file, String fileName, ZipOutputStream zipOut) throws IOException {

		if (file.isHidden()) {
			return;
		}
		if (file.isDirectory()) { // recursively process directories if any
			if (fileName.endsWith(SEPARATOR)) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			}
			else { // empty directory
				zipOut.putNextEntry(new ZipEntry(fileName + SEPARATOR));
				zipOut.closeEntry();
			}
			File[] children = file.listFiles();
			for (File childFile : children)
				zipFile(childFile, fileName + SEPARATOR + childFile.getName(), zipOut);

			return;
		}
		FileInputStream input = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = input.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length); // 0 is starting offset
		}
		try {
			input.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	} 

	private void listZipEntries (File file) {
		ZipInputStream zis = null;
		ZipEntry zipEntry = null;
		try {
			zis = new ZipInputStream(new FileInputStream(file));
		}
		catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(this, ex.toString(), "Error#3", JOptionPane.ERROR_MESSAGE);
		}
		try {
			zipEntry = zis.getNextEntry();
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.toString(), "Error#4", JOptionPane.ERROR_MESSAGE);
		}
		displayArea.append(NEW_LINE + file.getParent() + SEPARATOR + file.getName() + NEW_LINE);
		while (zipEntry != null) {
			displayArea.append(zipEntry.getName() + NEW_LINE);
			try {
				zipEntry = zis.getNextEntry();
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(this, ex.toString(), "Error#4", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private final void decompress (File file) throws IOException {
		File destDir = new File(file.getParent());
		byte[] buffer = new byte[1024];
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(file));
		}
		catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(this, ex.toString(), "Error#5", JOptionPane.ERROR_MESSAGE);
		}
		ZipEntry zipEntry = null;
		try {
			zipEntry = zis.getNextEntry();
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.toString(), "Error#6", JOptionPane.ERROR_MESSAGE);
		}
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			int option;
			if (zipEntry.isDirectory()) {
				if (newFile.exists()) {
					option = JOptionPane.showConfirmDialog(this, zipEntry.getName() + " already exists in current directory. Overwrite?",
											"Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					userOptions.add(option);
					if (option == JOptionPane.CANCEL_OPTION)
						break;
				}
				newFile.mkdir();
				zipEntry = zis.getNextEntry();
				continue;
			}
			else {
				if (newFile.exists()) {
					option = JOptionPane.showConfirmDialog(this, zipEntry.getName() + " already exists in current directory. Overwrite?",
											"Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					userOptions.add(option);
					if (option == JOptionPane.CANCEL_OPTION)
						break;
				}
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(newFile);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	// Check against Zip Slip
	private File newFile (File destDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destDir, zipEntry.getName());
		String destDirPath = destDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}
		return destFile;
	}
}