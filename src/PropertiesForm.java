import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class PropertiesForm {

	private final JFileChooser fileChs = new JFileChooser();
	private JTextField edt1, edt2, eds;
	private JComboBox<String> lineCombo = new JComboBox<String>(new String[] {"1", "2", "3", "4", "5", "6","7", "8"});
	private MTForm mtFrame = null;
	private JFrame frame;
	
	
	private class JTextFieldLimit extends PlainDocument {
		
		private static final long serialVersionUID = 1L;
		private int limit;
		
		JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}
		
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;
		
			if((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	}
	
	public void addComponentsToPane(Container pane) {	
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] {400, 10, 10};
		pane.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel textLabel = new JLabel("Число лент");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(textLabel, c);
		textLabel = new JLabel("Символ по умолчанию");
		c.gridy = 4;
		pane.add(textLabel, c);
		textLabel = new JLabel("Файл данных");
		c.gridy = 8;
		pane.add(textLabel, c);
		textLabel = new JLabel("Файл команд");
		c.gridy = 11;
		pane.add(textLabel, c);
		
		c.weighty = 2.0;
		c.gridx = 5;
		c.gridy = 1;
		pane.add(lineCombo, c);
		
		eds = new JTextField();
		c.weightx = 2.0;
		c.gridx = 5;
		c.gridy = 4;
		eds.setDocument(new JTextFieldLimit(1));
		pane.add(eds, c) ;
	
		edt1 = new JTextField();
		c.gridx = 0;
		c.gridy = 9;
		pane.add(edt1, c);
		
		JButton oneBtn = new JButton("Обзор");
		c.gridx = 5;
		c.gridy = 9;
		pane.add(oneBtn, c);
		oneBtn.addActionListener(new openData());
	
		edt2 = new JTextField();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 12;
		pane.add(edt2, c);
		
		JButton twoBtn = new JButton("Обзор");
		c.gridx = 5;
		c.gridy = 12;
		pane.add(twoBtn, c);
		twoBtn.addActionListener(new openCommands());
		
		JButton startBtn = new JButton("Начать");
		c.fill = GridBagConstraints.CENTER;
		c.gridx = 5;
		c.gridy = 14;
		pane.add(startBtn, c);
	
		startBtn.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent arg0) {
				String[] data = null;
				Command[] commands = null;
				try {
					data = readData(edt1.getText(), lineCombo.getSelectedIndex() + 1);
					commands = readCommandSystem(edt2.getText(), lineCombo.getSelectedIndex() + 1);
				} catch (FileNotFoundException e) {
					Object message = "Один из файлов не найден!";
					JOptionPane.showMessageDialog(null, message);;
					//e.printStackTrace();
					return;
				}
				catch (NoSuchElementException e) {
					Object message = "Не обнаружены данные!";
					JOptionPane.showMessageDialog(null, message);;
					//e.printStackTrace();
					return;
				}
				catch (IllegalArgumentException e) {
					Object message = "Некорректный формат одного из файлов!";
					JOptionPane.showMessageDialog(null, message);;
					//e.printStackTrace();
					return;
				};
				if(eds.getText().length() != 1) {
					Object message = "Не введен символ по умолчанию!";
					JOptionPane.showMessageDialog(null, message);
					return;
				}
				if(mtFrame == null) {
					mtFrame = new MTForm();
					mtFrame.createGUI(lineCombo.getSelectedIndex() + 1, eds.getText().charAt(0));
				}
				frame.setVisible(false);
				mtFrame.loadData(data, commands);
				mtFrame.showGUI();
			}
		});
	
		fileChs.setFileFilter(new FileNameExtensionFilter("MT data (*.mtd)", "mtd"));
		fileChs.setFileFilter(new FileNameExtensionFilter("MT commands (*.mtc)", "mtc"));
	}


	public void createAndShowGUI() {
		frame = new JFrame("Эмулятор многоленточной машины Тьюринга");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(frame.getContentPane());
     
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2, frame.getWidth(), frame.getHeight());
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
	
	private class openData implements ActionListener {
		
		public void actionPerformed(ActionEvent arg0) {
			int returnVal = fileChs.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				edt1.setText(fileChs.getSelectedFile().getAbsolutePath());
				
			}	
		}
	}
	
	private class openCommands implements ActionListener {
		
		public void actionPerformed(ActionEvent arg0) {
			int returnVal = fileChs.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				edt2.setText(fileChs.getSelectedFile().getAbsolutePath());
				
			}
			
		}
	}
		
	private String[] readData(String fileName, int lcount) throws FileNotFoundException, NoSuchElementException, NumberFormatException, IllegalFormatException {
		Scanner in = new Scanner(new FileReader(new File(fileName)));
		String[] data = new String[lcount];
		for(int i = 0; i < lcount; i++) 
			data[i] = in.nextLine();
		if (data.length == 0)
			data[0] = eds.getText() ;
		return data;
	}
		
	private Command[] readCommandSystem(String fileName, int lcount) throws FileNotFoundException, NumberFormatException, IllegalArgumentException {
		Scanner in = new Scanner(new FileReader(new File(fileName)));
		Vector<Command> commands = new Vector<Command>();
		while(in.hasNextLine()) {
			String[] tokens = in.nextLine().split("[,>]");
			for(int i = 0; i < tokens.length; i++)
				tokens[i] = tokens[i].trim();
			
			if (tokens.length != (lcount * 3 + 2)) 
				throw new IllegalArgumentException();
			
			int currentState = Integer.valueOf(tokens[0]);
			int newState = Integer.valueOf(tokens[lcount + 1]);
			char[] currentData = new char[lcount], newData = new char[lcount];
			byte[] shifts = new byte[lcount];
			for(int i = 0; i < lcount; i++) {
				if(tokens[i + 1].length() != 1)
					throw new IllegalArgumentException();
				else
					currentData[i] = tokens[i + 1].charAt(0);
				if(tokens[i + lcount + 2].length() != 1)
					throw new IllegalArgumentException();
				else
					newData[i] = tokens[i + lcount + 2].charAt(0);
				if(tokens[i + 2 * lcount + 2].equals("L"))
					shifts[i] = -1;
				else
					if(tokens[i + 2 * lcount + 2].equals("S"))
						shifts[i] = 0;
					else
						if(tokens[i + 2 * lcount + 2].equals("R"))
							shifts[i] = 1;
						else
							throw new IllegalArgumentException();
			}
			Command cm = new Command(currentState, newState, currentData, newData, shifts);
			commands.add(cm);
		}
		return commands.toArray(new Command[commands.size()]);
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PropertiesForm().createAndShowGUI();
			}
		});
	}
		
}