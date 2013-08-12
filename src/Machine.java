public class Machine {
	
	private Lenta[] data;
	private int state;
	
	public Machine(String[] value, char defaultChar) {
		data = new Lenta[value.length];
		for(int i = 0; i < data.length; i++)
			data[i] = new Lenta(value[i], defaultChar);
		state = 0;
	}
	
	public char[] getSymbols() {
		char[] result = new char[data.length];
		for(int i = 0; i < data.length; i++)
			result[i] = data[i].getCurrentSymbol();
		return result;
	}
	
	public void setSymbols(char[] value) {
		for(int i = 0; i < data.length; i++)
			data[i].setSymbol(value[i]);
	}
	
	public void shifts(byte[] shiftsValue) {
		for(int i = 0; i < data.length; i++) {
			if(shiftsValue[i] == -1)
				data[i].shiftLeft();
			if(shiftsValue[i] == 1)
				data[i].shiftRight();
		}
	}
	
	public String[] getViews(int size) {
		String[] result = new String[data.length];
		for(int i = 0; i < data.length; i++)
			result[i] = data[i].getView(size);
		return result;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int value) {
		state = value;
	}
	
	
}
