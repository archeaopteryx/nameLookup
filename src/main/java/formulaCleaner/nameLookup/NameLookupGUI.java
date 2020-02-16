package formulaCleaner.nameLookup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class NameLookupGUI extends JPanel{
	
	private int skipRowsLook, skipRowsFile;
	private int nameInLook, nameInFile , nameOutFile, casFile;
	private String nameInManu, nameOutManu, casManu;
	private File lookFile, sourceFile;
	
	private JTextField skipLook = new JTextField("1", 10);
	private JTextField skipFile = new JTextField("1", 10);
	private JTextField nameInLookField = new JTextField("A", 10);
	private JTextField nameInManuField = new JTextField("", 20);
	private JTextField nameInFileField = new JTextField("A", 10);
	private JTextField nameOutManuField = new JTextField("",20);
	private JTextField nameOutFileField = new JTextField("B",10);
	private JTextField casManuField = new JTextField("", 20);
	private JTextField casFileField = new JTextField("C", 10);
	private JTextField lookFileField, fileFileField;
	private boolean ready = false;

	private JFrame frame;
	
	public NameLookupGUI() {
		initGUI();
	}
	
	public int get_skipRowsLook() {
		return this.skipRowsLook;
	}
	public int get_skipRowsFile() {
		return this.skipRowsFile;
	}
	public int get_nameInLook() {
		return this.nameInLook;
	}
	public String get_nameInManu() {
		return this.nameInManu;
	}
	public int get_nameInFile() {
		return this.nameInFile;
	}
	public String get_nameOutManu() {
		return nameOutManu;
	}
	public int get_nameOutFile() {
		return this.nameOutFile;
	}
	public int get_casFile() {
		return this.casFile;
	}
	public String get_casManu() {
		return this.casManu;
	}
	public File get_lookFile() {
		return this.lookFile;
	}
	public File get_sourceFile() {
		return this.sourceFile;
	}
	
	public JTabbedPane makeTab() {
		JTabbedPane tabPane = new JTabbedPane();
		
		JComponent lookPane = lookupPane();
		tabPane.add("Lookup", lookPane);
		
		JComponent manPane = manEntryPane();
		tabPane.add("Manual Entry", manPane);
		
		JComponent filePane = fileEntryPane();
		tabPane.add("File Entry", filePane);
		
		//add(tabPane);
		return tabPane;
	}
	
	private void initGUI(){
		frame = new JFrame("Name Lookup");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComponent container = new JPanel();
		JComponent tabs = makeTab();
		JComponent basicBtns = new JPanel();
		
		JButton cancelBtn = cancelButton();
		JButton submitBtn = submissionButton();
		GroupLayout layout = new GroupLayout(basicBtns);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		basicBtns.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(submitBtn)
						.addComponent(cancelBtn)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(submitBtn)
				.addComponent(cancelBtn));

		container.add(tabs);
		container.add(basicBtns);

		frame.add(container);
		frame.pack();
		frame.setVisible(true);
		while(!ready) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}
		}
		frame.setVisible(false);
		//frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	private JPanel lookupPane(){
		
		JPanel panel = new JPanel();
		
		JLabel skipLabel = new JLabel("Number of rows to skip:");
		JLabel nameInLabel = new JLabel("Column containing names to lookup:");
		JLabel fileLabel = new JLabel("File:");
		lookFileField = new JTextField(" ", 10);
		JButton fileBtn = new JButton("File");
		fileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dir = new File(System.getProperty("user.dir"));
				JFileChooser chooser = new JFileChooser(dir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Spreadsheets", "xlsx");
				chooser.setFileFilter(filter);
				int result = chooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					lookFile = chooser.getSelectedFile();
					lookFileField.setText(lookFile.getName());
				}
			}
		});
		
		JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lookFile = null;
				lookFileField.setText("");
			}
		});
		
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(skipLabel)
						.addComponent(nameInLabel)
						.addComponent(fileLabel))
				.addGroup(layout.createParallelGroup()
						.addComponent(skipLook)
						.addComponent(nameInLookField)
						.addComponent(lookFileField))
				.addGroup(layout.createParallelGroup()
						.addComponent(fileBtn))
				.addGroup(layout.createParallelGroup()
						.addComponent(clearBtn))
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(skipLabel)
						.addComponent(skipLook))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInLabel)
						.addComponent(nameInLookField))
				.addGroup(layout.createParallelGroup()
						.addComponent(fileLabel)
						.addComponent(lookFileField)
						.addComponent(fileBtn)
						.addComponent(clearBtn))
				);
		return panel;
	}
	
	private JPanel manEntryPane() {
		
		JPanel panel = new JPanel();
		
		JLabel nameInLabel = new JLabel ("Original cell contents: ");
		JLabel nameOutLabel = new JLabel ("Chemical name to be returned: ");
		JLabel casLabel = new JLabel ("CAS to be returned: ");
		
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInLabel)
						.addComponent(nameOutLabel)
						.addComponent(casLabel))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInManuField)
						.addComponent(nameOutManuField)
						.addComponent(casManuField))
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInLabel)
						.addComponent(nameInManuField))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameOutLabel)
						.addComponent(nameOutManuField))
				.addGroup(layout.createParallelGroup()
						.addComponent(casLabel)
						.addComponent(casManuField))
				);
		return panel;	
	}
	
	private JPanel fileEntryPane() {
		
		JPanel panel = new JPanel();
		
		JLabel skipLabel = new JLabel("Number of rows to skip:");
		JLabel nameInLabel = new JLabel("Original cell contents column:");
		JLabel nameOutLabel = new JLabel("Chemical name column:");
		JLabel casLabel = new JLabel("CAS number column");
		JLabel fileLabel = new JLabel("File:");
		fileFileField = new JTextField("", 10);
		
		JButton fileBtn = new JButton("File");
		fileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dir = new File(System.getProperty("user.dir"));
				JFileChooser chooser = new JFileChooser(dir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Spreadsheets", "xlsx");
				chooser.setFileFilter(filter);
				int result = chooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					sourceFile = chooser.getSelectedFile();
					fileFileField.setText(sourceFile.getName());
				}
			}
		});
		
		JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sourceFile = null;
				fileFileField.setText("");
			}
		});
		
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(skipLabel)
						.addComponent(nameOutLabel)
						.addComponent(fileLabel))
				.addGroup(layout.createParallelGroup()
						.addComponent(skipFile)
						.addComponent(nameOutFileField)
						.addComponent(fileFileField))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInLabel)
						.addComponent(casLabel)
						.addComponent(fileBtn))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameInFileField)
						.addComponent(casFileField)
						.addComponent(clearBtn))
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(skipLabel)
						.addComponent(skipFile)
						.addComponent(nameInLabel)
						.addComponent(nameInFileField))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameOutLabel)
						.addComponent(nameOutFileField)
						.addComponent(casLabel)
						.addComponent(casFileField))
				.addGroup(layout.createParallelGroup()
						.addComponent(fileLabel)
						.addComponent(fileFileField)
						.addComponent(fileBtn)
						.addComponent(clearBtn))
				);
		
		return panel;
	}
	
	private JButton cancelButton() {
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		return cancel;
	}
	
	private JButton submissionButton() {
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(skipLook.getText().equals("")) {
					skipRowsLook = -1;
				}
				else {
					skipRowsLook = Integer.parseInt(skipLook.getText());
				}
				if (skipFile.getText().equals("")) {
					skipRowsFile = -1;
				}
				else {
					skipRowsFile = Integer.parseInt(skipFile.getText());
				}
				nameInLook = alphaToNum(nameInLookField.getText());
				nameInManu = nameInManuField.getText();
				nameInFile = alphaToNum(nameInFileField.getText());
				nameOutManu = nameOutManuField.getText();
				nameOutFile = alphaToNum(nameOutFileField.getText());
				casManu = casManuField.getText();
				casFile = alphaToNum(casFileField.getText());
				ready=true;;
			//	frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				
			}
		});
		return submitButton;
	}
	
	private int alphaToNum(String alpha) {
		String alphaUp = alpha.toUpperCase();
		int len = alphaUp.length();
		if (len==0) {
			return -1;
		}
		else if (len==1) {
			int sum = alphaUp.charAt(0)-'A';
			return sum;
		}
		else {
			int sum = 0;
			for(int i=0; i<len-1; i++) {
				sum += ((alphaUp.charAt(i)+1-'A')*26);
			}
			sum+=(int) alphaUp.charAt(len-1)-'A';
			return sum;
		}
	}
	
}
