public class Command {
	
	private int currentState, newState;
	private char[] currentData, newData;
	private byte[] shifts;
	
	public Command(int currentState, int newState, char[] currentData, char[] newData, byte[] shifts) {
		this.currentData = currentData.clone();
		this.newData = newData.clone();
		this.shifts = shifts.clone();
		this.currentState = currentState;
		this.newState = newState;
	}
	
	public char[] getNewData() {
		return newData.clone();
	}
	
	public byte[] getShifts() {
		return shifts.clone();
	}
	
	public int getNewState() {
		return newState;
	}
	
	public boolean check(int state, char[] data) {
		if((state != currentState) || (data.length != currentData.length))
			return false;
		for(int i = 0; i < data.length; i++)
			if(data[i] != currentData[i])
				return false;
		return true;
	}
}