import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class PDFMergerStackOverflow implements ActionListener {

    private JFrame frame;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;
    private JTextArea textArea;
    private JTextField textField;
    private JButton bindButton;
    private File[] files;
    private File mergedFile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PDFMergerStackOverflow();
            }
        });
    }

    public PDFMergerStackOverflow() {
        frame = new JFrame("PDF Merger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar menuBar = createJMenuBar();
        frame.setJMenuBar(menuBar);

        JPanel operationPanel = createOperationPanel();
        frame.add(operationPanel, BorderLayout.NORTH);
        JPanel textPanel = createTextPanel();
        frame.add(textPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JPanel createOperationPanel() {
        JPanel operationPanel = new JPanel();
        BoxLayout layout = new BoxLayout(operationPanel, BoxLayout.LINE_AXIS);
        operationPanel.setLayout(layout);
        JLabel label = new JLabel("Result: ");
        textField = new JTextField(30);
        textField.setEditable(false);
        bindButton = new JButton("Bind");
        bindButton.addActionListener(this);
        bindButton.setEnabled(false);
        operationPanel.add(label);
        operationPanel.add(textField);
        operationPanel.add(bindButton);
        return operationPanel;
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textArea = new JTextArea(40, 50);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JMenuBar createJMenuBar() {
        JMenu menu = new JMenu("File");
        openItem = new JMenuItem("Open");
        openItem.addActionListener(this);
        saveItem = new JMenuItem("Save");
        saveItem.setEnabled(false);
        saveItem.addActionListener(this);
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        menu.add(openItem);
        menu.add(saveItem);
        menu.add(exitItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        return menuBar;
    }

    @Override
    public void actionPerformed (ActionEvent event) {
        if (event.getSource() == exitItem) {
            frame.dispose();
            System.exit(0);
        } else if (event.getSource() == openItem) {
            files = openFiles();
            bindButton.setEnabled(false);
        } else if (event.getSource() == bindButton) {
            mergedFile = mergeFiles();
            saveItem.setEnabled(true);
        } else if (event.getSource() == saveItem)
            saveFile();
    }

    public File[] openFiles () {
        File[] selectedFiles = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setMultiSelectionEnabled(true);
        int option = chooser.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) 
            selectedFiles = chooser.getSelectedFiles();
        return selectedFiles;
    }

    public File mergeFiles () {
        File merged = null;
        // TODO

        return merged;
    }
    public void saveFile () {
        //TODO
    }
}