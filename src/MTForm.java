import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;


public class MTForm {
	
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;
	final static int SIZE = 14;
	private JButton start, pause, stop;
	private JFrame frame;
	private JTable[] tapes;
	private ImageIcon blueArrowL, blueArrowR, redArrowL, redArrowR;
	private JLabel[] leftIcon, rightIcon;
	private JSlider slider;
	private int delay = 600;
	char defaultChar;
	boolean flag1 = true;
	private Machine MT;
	private Command[] commands;
	private byte[] shiftsViewerData;
	private Timer t1 = new  Timer(delay, new TimerAction(this)); 
	private Timer t2 = new Timer(300, new ShiftsViewer());
	
	
	public void addComponentsToPane(Container pane, int count, char s) {
	        
		if (RIGHT_TO_LEFT)
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] {25, 15, 350, 15, 25};
		pane.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();		
        
        JPanel panel = new JPanel();
		c.gridx = 2;
	    c.gridy = 2 * count + 2;
	    pane.add(panel, c);
	    panel.setLayout(new GridLayout());
	    start = new JButton("Старт");
	    
	    start.addActionListener(new ActionListener() {
			
	    	public void actionPerformed(ActionEvent e) {
				t1.start();
				start.setEnabled(false);
				stop.setEnabled(true);
				pause.setEnabled(true);
			}
		});
	    
	    panel.add(start);
	    pause = new JButton("Пауза");
	    pause.setEnabled(false);
	    panel.add(pause);
	    
	    pause.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (flag1) {
					pause.setText("Продолжить");
					t1.stop();
					flag1 = !flag1;
				}
				else {
					pause.setText("Пауза");
					t1.start();
					flag1 = !flag1;
				}	
			}
		});
	    
	    stop = new JButton("Стоп");
	    stop.setEnabled(false);
	    panel.add(stop);
	    
	    stop.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			t1.stop();
			stop.setEnabled(false);
			pause.setEnabled(false);
			start.setEnabled(false);
				
			}
		});
	    
	    slider = new JSlider(1, 10, 3);
	    slider.addChangeListener(new DelayChanger());
	    panel.add(slider);
	   
	    tapes = new JTable[count];
	    leftIcon = new JLabel[count];
	    rightIcon = new JLabel[count];
	    
	    for(int i = 0; i < count; i++) { 
        	tapes[i] = new JTable(1, SIZE * 2 + 1);
        	for(int j = 0; j < 29; j++)
        		tapes[i].getColumnModel().getColumn(j).setPreferredWidth(28);
        	c.gridx = 2;
        	c.gridy = 2 * i;
		    pane.add(tapes[i], c);
    	   
	    	ImageIcon uk = new ImageIcon("cell.png");  
	    	JLabel str = new JLabel(uk);
	    	c.gridx = 2;
	    	c.gridy = 2 * i; 
	    	pane.add(str, c);
    	   
	    	for(int j = 0; j < SIZE * 2 + 1; j++)
	    		tapes[i].getModel().setValueAt(s, 0, j);
    	   
			rightIcon[i] = new JLabel(blueArrowL);
			c.fill = GridBagConstraints.NORTHWEST;
			c.ipady = 1;
			c.weighty = 1.0;
			c.gridx = 0;
			c.gridwidth = 1;
			c.gridy = 2 * i;
			pane.add(rightIcon[i], c);
		       
		    leftIcon[i] = new JLabel(blueArrowR);
		    c.fill = GridBagConstraints.WEST;
		    c.ipady = 1;
		    c.weighty = 1.0;
		    c.gridx = 4;
		    c.gridwidth = 1;
		    c.gridy = 2 * i;
		    pane.add(leftIcon[i], c);
       	}

	}

	void createGUI(int count, char s) {
		blueArrowL = new ImageIcon("blueArrowL.png");
		blueArrowR = new ImageIcon("blueArrowR.png");
		redArrowL = new ImageIcon("redArrowL.png");
		redArrowR = new ImageIcon("redArrowR.png");
	    frame = new JFrame("Эмулятор многоленточной машины Тьюринга");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    addComponentsToPane(frame.getContentPane(), count, s);
	    
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setBounds((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2, frame.getWidth(), frame.getHeight());
		frame.setResizable(false);
		defaultChar = s;
	}        

	void showGUI() {
		frame.pack();
		frame.setVisible(true);
	}
	
	private void setData(String[] data) {
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[i].length(); j++)
				tapes[i].getModel().setValueAt(data[i].charAt(j), 0, j);
			((DefaultTableModel) tapes[i].getModel()).fireTableDataChanged();
		}
	}
	
	void loadData(String[] data, Command[] commands) {
		MT = new Machine(data, defaultChar);
		this.commands = commands.clone();
		setData(MT.getViews(SIZE));
	}
	
	void setShifts(byte[] shifts) {
		for(int i = 0; i < shifts.length; i++) {
			if(shifts[i] == -1)
				leftIcon[i].setIcon(redArrowR);
			if(shifts[i] == 1)
				rightIcon[i].setIcon(redArrowL);
		}
	}
	 
	private class DelayChanger implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			delay = slider.getValue() * 200;
			t1.setDelay(delay);
		} 
	}
	
	private class TimerAction implements ActionListener {
		
		private MTForm parentForm;
		
		private TimerAction(MTForm parentForm) {
			this.parentForm = parentForm;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			int command = -1;
			char[] data = MT.getSymbols();
			int state = MT.getState();
			for(int i = 0; i < commands.length; i++)
				if(commands[i].check(state, data)) {
					command = i;
					break;
				}
					
			if(command != -1) {
				shiftsViewerData = commands[command].getShifts();
				data = commands[command].getNewData();
				state = commands[command].getNewState();
				MT.setState(state);
				MT.setSymbols(data);
				MT.shifts(shiftsViewerData);
				parentForm.setData(MT.getViews(SIZE));
				t2.start();
			}
			else
			{
				t1.stop();
				stop.setEnabled(false);
				pause.setEnabled(false);
				start.setEnabled(false);
				Object message = "Выполнение завершено.";
				JOptionPane.showMessageDialog(null, message);
				for(int i = 0; i < leftIcon.length; i++) {
					leftIcon[i].addMouseListener(new MouseAction(false, i, parentForm));
					rightIcon[i].addMouseListener(new MouseAction(true, i, parentForm));
				}
			}
		}
		
	};

	
	private class ShiftsViewer implements ActionListener {
		
		private boolean flag = true;
		
		public void actionPerformed(ActionEvent e) {
			if(flag) {
				setShifts(shiftsViewerData);
			}
			else {
				for(int i = 0; i < shiftsViewerData.length; i++) {
					leftIcon[i].setIcon(blueArrowR);
					rightIcon[i].setIcon(blueArrowL);
				}
				t2.stop();
			}
			flag = !flag;
		}	
	}
	
	private class MouseAction implements MouseListener {

		private boolean mode;
		private int id;
		private MTForm frame;
		
		private MouseAction(boolean mode, int id, MTForm frame) {
			this.mode = mode;
			this.id = id;
			this.frame = frame;
		}
		
		public void mouseClicked(MouseEvent e) {
			byte[] shifts = new byte[frame.tapes.length]; 
			if(mode)
				shifts[id] = 1;
			else
				shifts[id] = -1;
			frame.MT.shifts(shifts);
			frame.setData(frame.MT.getViews(SIZE));
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
		
	}

}
	   
	

