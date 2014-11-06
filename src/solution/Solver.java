package solution;

import java.io.IOException;

public class Solver {

	public static void main(String[] args) {
		Data data = new Data();
		try {
			data = IO.readFile("data/CPTNoMissingData-d1.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
