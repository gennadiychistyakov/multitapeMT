import java.util.*;

public class Lenta {
	
	private LinkedList<Cell> data;
	private ListIterator<Cell> li;
	private char currentChar;
	private char defaultChar;
	
	public Lenta(String value, char defaultChar) {
		data = new LinkedList<Cell>();
		for(int i = 0; i < value.length(); i++)
			data.addLast(new Cell(value.charAt(i)));
		li = data.listIterator();
		currentChar = value.charAt(0);
		this.defaultChar = defaultChar;
	}
	
	public char getCurrentSymbol() {
		return currentChar;
	}
	
	public void setSymbol(char value) {
		li.set(new Cell(value));
		currentChar = value;
	}
	
	public void shiftLeft() {
		if(!li.hasPrevious())
			li.add(new Cell(defaultChar));
		currentChar = li.previous().getSymbol();
	}
	
	public void shiftRight() {
		if(!li.hasNext()) {
			li.add(new Cell(defaultChar));
			li.previous();
		}
		li.next();
		if(!li.hasNext()) {
			li.add(new Cell(defaultChar));
			li.previous();
		}
		currentChar = li.next().getSymbol();
		li.previous();
	}
	
	public String getView(int size) {
		String result = "" + currentChar;
		int cnt = 0;
		for(int i = 0; i < size; i++)
			if(li.hasPrevious()) {
				cnt++;
				result = li.previous().getSymbol() + result;
			}
			else
				result = defaultChar + result;
		for(int i = 0; i < cnt; i++)
			li.next();
		li.next();
		cnt = 0;
		for(int i = 0; i < size; i++)
			if(li.hasNext()) {
				cnt++;
				result = result + li.next().getSymbol();
			}
			else
				result = result + defaultChar;
		for(int i = 0; i < cnt; i++)
			li.previous();
		li.previous();
		return result ;
	}
}
