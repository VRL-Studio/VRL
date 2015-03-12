package eu.mihosoft.vrl.lang.model;

public class Location {
	int line, column, offset;

	public Location(int line, int col, int offset) {
		this.line = line;
		this.column = col;
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int col) {
		this.column = col;
	}
}