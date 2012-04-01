import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class LayoutFilesChooser extends JPanel
implements ActionListener {
static private final String newline = "\n";
JButton openButton, transformButton;
JTextField baseWidthLabel, baseHeightLabel,targetWidthLabel, targetHeightLabel;
JTextArea log;
JFileChooser fc;

public LayoutFilesChooser() {
super(new BorderLayout());

//Create the log first, because the action listeners
//need to refer to it.
log = new JTextArea(5,20);
log.setMargin(new Insets(5,5,5,5));
log.setEditable(false);
JScrollPane logScrollPane = new JScrollPane(log);

//Create a file chooser
fc = new JFileChooser();

//Uncomment one of the following lines to try a different
//file selection mode.  The first allows just directories
//to be selected (and, at least in the Java look and feel,
//shown).  The second allows both files and directories
//to be selected.  If you leave these lines commented out,
//then the default mode (FILES_ONLY) will be used.
//
fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
fc.setFileFilter(new XMLFilter());
fc.setMultiSelectionEnabled(true);
//fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

//Create the open button.  We use the image from the JLF
//Graphics Repository (but we extracted it from the jar).
openButton = new JButton("Choose Layout Files");
openButton.addActionListener(this);

//Create the save button.  We use the image from the JLF
//Graphics Repository (but we extracted it from the jar).
transformButton = new JButton("Create New Layouts");
transformButton.addActionListener(this);

//For layout purposes, put the buttons in a separate panel
JPanel buttonPanel = new JPanel(); //use FlowLayout
buttonPanel.add(openButton);
buttonPanel.add(transformButton);


baseWidthLabel = new JTextField("base Width");
baseHeightLabel = new JTextField("base Height");
targetWidthLabel= new JTextField("target Width");
targetHeightLabel = new JTextField("target Height");


JPanel label1Panel = new JPanel(); //use FlowLayout
label1Panel.add(baseWidthLabel);
label1Panel.add(baseHeightLabel);

JPanel label2Panel = new JPanel(); //use FlowLayout
label2Panel.add(targetWidthLabel);
label2Panel.add(targetHeightLabel);

JPanel labelPanel = new JPanel();
labelPanel.add(label1Panel);
labelPanel.add(label2Panel);
//Add the buttons and the log to this panel.



add(buttonPanel, BorderLayout.NORTH);
add(labelPanel, BorderLayout.SOUTH);


add(logScrollPane, BorderLayout.CENTER);
}

File[] choosenFiles;

public void actionPerformed(ActionEvent e) {

//Handle open button action.
if (e.getSource() == openButton) {
int returnVal = fc.showOpenDialog(LayoutFilesChooser.this);

if (returnVal == JFileChooser.APPROVE_OPTION) {
	choosenFiles = fc.getSelectedFiles();
//This is where a real application would open the file.

	log.append(newline + "----");

log.append(newline+ "FILES:" + newline);


for(File file: choosenFiles)
log.append(file.getName() + "." + newline);
		log.append("----"+newline);

} else {
log.append("Nothing new chosen" + newline);
}
log.setCaretPosition(log.getDocument().getLength());

//Handle save button action.
} else if (e.getSource() == transformButton) {


	
try{
	
	int baseWidth = Integer.parseInt(baseWidthLabel.getText());
	int baseHeight = Integer.parseInt(baseHeightLabel.getText());
	int targetWidth = Integer.parseInt(targetWidthLabel.getText());
	int targetHeight = Integer.parseInt(targetHeightLabel.getText());

	log.append(newline + "CREATING NEW LAYOUTS: " + newline +
			"Files: " + choosenFiles.length + newline + 
			"Base Width: " + baseWidth + newline +
			"Base Height: " + baseHeight + newline +
			"Target Width: " + targetWidth + newline +
			"Target Height: " + targetHeight + newline +
			"Width ratio: " + (1.0 * targetWidth / baseWidth) + newline + 
			"Height ratio: " + (1.0 * targetHeight / baseHeight) + newline + 
			"");
	
	
	String pathname = LayoutTransformer.transformLayouts(log, choosenFiles, baseWidth, baseHeight, targetWidth, targetHeight, fc.getCurrentDirectory().getAbsolutePath());

	log.append("NEW LAYOUTS CREATED IN: " +pathname+ newline);
}catch(Exception exx){
	
	log.append("Set valid integer values" + newline);
	log.setCaretPosition(log.getDocument().getLength());
}
	


log.setCaretPosition(log.getDocument().getLength());
}
}


/**
* Create the GUI and show it.  For thread safety,
* this method should be invoked from the
* event dispatch thread.
*/
private static void createAndShowGUI() {
//Create and set up the window.
JFrame frame = new JFrame("ALASCA - ANDROID LAYOUT SCALER");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//Add content to the window.
frame.add(new LayoutFilesChooser());

//Display the window.
frame.pack();
frame.setVisible(true);
}

public static void main(String[] args) {
//Schedule a job for the event dispatch thread:
//creating and showing this application's GUI.
SwingUtilities.invokeLater(new Runnable() {
public void run() {
//Turn off metal's use of bold fonts
UIManager.put("swing.boldMetal", Boolean.FALSE); 
createAndShowGUI();
}
});
}
}

