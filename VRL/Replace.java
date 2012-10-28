import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Replace {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
	System.out.println(">> Replacing...");
	
	if (args.length != 1) {
		System.err.println(">> please give exactly one argument (file to modify)!");
		return;
	}

	BufferedReader in = new BufferedReader(new FileReader(args[0]));

        ArrayList<String> lines = new ArrayList<String>();

        while (in.ready())  {
            String line = in.readLine();
            line = line.replaceAll("eu::mihosoft::vrl::","");
            lines.add(line);
        }

        in.close();

        BufferedWriter out = new BufferedWriter(new FileWriter(args[0]));

        for (String l : lines) {
            out.write(l);
            out.newLine();
        }

        out.close();
    }
}
