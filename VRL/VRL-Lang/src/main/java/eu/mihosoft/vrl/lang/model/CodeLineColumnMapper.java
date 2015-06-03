package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CodeLineColumnMapper {

	private Reader source;
	private Integer[] offsets;
	private int maxOffset;

	public Location getLocation(int line, int col) {
		if (line + 1 > offsets.length || line < 0 || col < 0)
			return new Location(line, col, -1);
		if (line + 1 < offsets.length
				&& offsets[line] + col >= offsets[line + 1])
			return new Location(line, col, -1);
		if (offsets[line] + col > maxOffset)
			return new Location(line, col, -1);
		return new Location(line, col, offsets[line] + col);
	}

	public Location getEnd() {
		return getLocation(maxOffset);
	}

	public Location getLocation(int offset) {
		if (offset < 0)
			return new Location(-1, -1, -1);
		if (offset > maxOffset)
			return new Location(-1, -1, offset);
		int line = 0;
		while (line < offsets.length && offsets[line] <= offset)
			line++;
		int col = (line == 0) ? offset : (offset - offsets[--line]);
		return new Location(line, col, offset);
	}

	public void init(String str) {
		init(new StringReader(str));
	}

	public void init(Reader in) {
		LineNumberReader reader = new LineNumberReader(source = in);
		List<Integer> lineOffsets = new ArrayList<Integer>();
		String buf;
		int offset = 0;
		try {
			while ((buf = reader.readLine()) != null && reader.ready()) {
				lineOffsets.add(offset);
				offset += buf.length();
				offset++; // one for every new line
			}
			lineOffsets.add(offset);
			maxOffset = offset;
			offsets = new Integer[lineOffsets.size()];
			lineOffsets.toArray(offsets);
			try {
				source.reset();
			} catch (IOException ex) {
                // defer exception...
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Reader getSource() {
		return source;
	}
}