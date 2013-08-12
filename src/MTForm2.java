package pro1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;


public class MTForm {
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;
	final static int SIZE = 14;
	private JButton start;
	private JButton pause;
	private JButton stop;
	private JFrame frame;
	private JTable[] lenta;
	private ImageIcon[] il;
	private ImageIcon[] il1;
	private ImageIcon redstrR;
	private ImageIcon redstrL;
	private JLabel[] left;
	private JLabel[] prav;
	private JSlider sild;
	private int delay = 600;
	char defaultChar;
	boolean flag1 = true;
	private Machine MT;
	private Command[] commands;
	private byte[] shiftsViewerData;
	private Timer t1 = new  Timer(delay, new TimerAction(this)); 
	private Timer t2 = new Timer(300, new ShiftsViewer());
	
	
	public void addComponentsToPane(Container pane, int count, char s) {
	        
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

        JButton button;    
		GridBagLayout layout = new GridBagLayout();
		frame.setLayout(layout);
		layout.columnWidths = new int[5];
		layout.columnWidths[0] = 25;
		layout.columnWidths[1] = 15;
		layout.columnWidths[2] = 350;
		layout.columnWidths[3] = 15;
		layout.columnWidths[4] = 25;
		GridBagConstraints c = new GridBagConstraints();
		
        
        JPanel panel = new JPanel();
		c.gridx = 2;
	    c.gridy = 2 * count + 2;
	    pane.add(panel, c);
	    panel.setLayout(new GridLayout());
	    start = new JButton("Начать");
	    
	    start.addActionListener(new StartAction(this));
	    
	    panel.add(start);
	    pause = new JButton("Пауза");
	    pause.setEnabled(false);
	    panel.add( pause);
	    
	    stop = new JButton("Стоп");
	    stop.setEnabled(false);
	    panel.add(stop);
	    
	    sild = new JSlider(1, 10, 3);
	    sild.addChangeListener(new DelayChanger());
	    panel.add(sild);
	   
	    lenta = new JTable[count];
	    left = new JLabel[count];
	    prav = new JLabel[count];
	    
	    for(int i = 0; i < count; i++) { 
        	lenta[i] = new JTable(1, SIZE * 2 + 1);
        	for(int j = 0; j < 29; j++)
        		lenta[i].getColumnModel().getColumn(j).setPreferredWidth(28);
        	c.gridx = 2;
        	c.gridy = 2 * i;
		    pane.add(lenta[i], c);
    	   
	    	ImageIcon uk = new ImageIcon("Безымянный2.png");  
	    	JLabel str = new JLabel(uk);
	    	c.gridx = 2;
	    	c.gridy = 2 * i; 
	    	pane.add(str, c);
   
    	   
	    	for(int j = 0; j < 29; j++)
	    		lenta[i].getModel().setValueAt(s, 0, j);
    	   
	    	   ImageIcon il1 = new ImageIcon("str16.png");
	    	   prav[i] = new JLabel(il1);
	    	   c.fill = GridBagConstraints.NORTHWEST;
		       c.ipady = 1;       // установить первоначальный размер кнопки
		       c.weighty = 1.0;   // установить отступ
		       c.gridx = 0;       // выравнять компонент по Button 2
		       c.gridwidth = 1;   // установить в 2 колонку
		       c.gridy = 2 * i;       // и 3 столбец
		       pane.add(prav[i], c);
		       //prav[i].setVisible(false);
		       
		       ImageIcon il = new ImageIcon("str17.png");
		       left[i] = new JLabel(il);
		       c.fill = GridBagConstraints.WEST;
		       c.ipady = 1;       // установить первоначальный размер кнопки
		       c.weighty = 1.0;   // установить отступ
		       c.gridx = 4;       // выравнять компонент по Button 2
		       c.gridwidth = 1;   // установить в 2 колонку
		       c.gridy = 2 * i;       // и 3 столбец
		       pane.add(left[i], c);
		      // left[i].setVisible(false);
       	}
     
	    JLabel jl = new JLabel("   ");
	    c.weightx = 0.5;
	    c.gridx = 1;
	    c.gridy = 2*count+1;
	    pane.add(jl, c);
    
	    JLabel jl1 = new JLabel("   ");
	    c.weightx = 0.5;
	    c.gridx = 3;
	    c.gridy = 2*count+1;
	    pane.add(jl1, c);
  
	   // pane.setBackground(Color.WHITE); 
	}

	void createGUI(int count, char s) {
	    frame = new JFrame("Работа Машины Тьюринга");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    addComponentsToPane(frame.getContentPane(), count, s);
	    frame.setSize(485, 500);
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setBounds((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2, frame.getWidth(), frame.getHeight());
		frame.setResizable(false);
		defaultChar = s;
		redstrL = new ImageIcon("redstrL.png");
		redstrR = new ImageIcon("redstrR.png");
	}        

	void showGUI() {
		frame.pack();
		frame.setVisible(true);
	}
	
	private void setData(String[] data) {
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[i].length(); j++)
				lenta[i].getModel().setValueAt(data[i].charAt(j), 0, j);
			((DefaultTableModel) lenta[i].getModel()).fireTableDataChanged();
			//lenta[i].revalidate();
			//lenta[i].repaint();
			//frame.revalidate();
			//frame.repaint();
		}
	}
	
	void loadData(String[] data, Command[] commands) {
		MT = new Machine(data, defaultChar);
		this.commands = commands.clone();
		setData(MT.getViews(SIZE));
	}
	
	void setShifts(byte[] shifts) {
		//redstr = new ImageIcon("str19.png");
		redstrL.getImage().flush();
		for(int i = 0; i < shifts.length; i++) {
			if(shifts[i] == -1)
				left[i].setIcon(redstrL);
			//left[i].setVisible(true);
			if(shifts[i] == 1)
				prav[i].setIcon(redstrR);
				//prav[i].setVisible(true);
		}
		/*try {
			Thread.currentThread().sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		for(int i = 0; i < shifts.length; i++) {
			
			//left[i].setVisible(false);
			//prav[i].setVisible(false);
		}
	}
	 
	private class DelayChanger implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			delay = sild.getValue() * 200;
			t1.setDelay(delay);
		} 
	}
	
	private class StartAction implements ActionListener {
		
		private MTForm parentForm;
		
		StartAction(MTForm parentForm) {
			this.parentForm = parentForm;
			
		}
		
		public void actionPerformed(ActionEvent e) {
			t1.start();
			start.setEnabled(false);
			stop.setEnabled(true);
			pause.setEnabled(true);
			stop.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				t1.stop();
				stop.setEnabled(false);
				pause.setEnabled(false);
				start.setEnabled(false);
					
				}
			});
			
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
				Object message = "Работа завершена!";
				JOptionPane.showMessageDialog(null, message);
				for(int i = 0; i < left.length; i++) {
					left[i].addMouseListener(new MouseAction(false, i, parentForm));
					prav[i].addMouseListener(new MouseAction(true, i, parentForm));
				}
			}
		}
		
	};

	
	private class ShiftsViewer implements ActionListener {
		
		private boolean flag = true;
		
		public void actionPerformed(ActionEvent e) {
			if(flag) {
				for(int i = 0; i < shiftsViewerData.length; i++) {
					if(shiftsViewerData[i] == -1)
						left[i].setIcon(redstrL);
					if(shiftsViewerData[i] == 1)
						prav[i].setIcon(redstrR);
				}
			}
			else {
				for(int i = 0; i < shiftsViewerData.length; i++) {
					ImageIcon il1 = new ImageIcon("str16.png");
					il1.getImage().flush();
					 ImageIcon il = new ImageIcon("str17.png");
					 il.getImage().flush();
					left[i].setIcon(il);
					prav[i].setIcon(il1);
					//left[i].setVisible(false);
					//prav[i].setVisible(false);
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
			byte[] shifts = new byte[frame.lenta.length]; 
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
	   
	

