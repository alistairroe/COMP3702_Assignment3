package solution;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver {

	public static void main(String[] args) {
		Data data;
		try {
			data = IO.readFile("data/CPTNoMissingData-d1.txt");
			// Set s = new HashSet();
			// s.add("nC");
			// s.add("nB");
			// System.out.println(data.nodeMap.get("D").P.get(s));
			for (int i = 0; i < data.nodeList.size(); i++) {
				// for(Node n : data.nodeList) {
				Node n = data.nodeList.get(i);
				System.out.println("Node " + n.name);
				for (List<Integer> state : data.data) {
					if (n.parents.size() == 0) {
						if (state.get(i) == 1) {
							n.numTrue++;
						}
					} else {
						Set<String> s = new HashSet<String>();
						for (String parent : n.parents) {
							int index = data.nodeNameList.indexOf(parent);
							if (state.get(index) == 1) {
								s.add(parent);
							} else {
								s.add("n" + parent);
							}
							;
						}
						System.out.print("Given " + s + ", " + n.name + ": ");
						Probability p = n.P.get(s);
						p.numOccurences++;
						if (state.get(i) == 1) {
							p.numTrue++;
							System.out.print("1");
							System.out.println(" | " + p.numTrue + " "
									+ p.numOccurences);
						} else {
							System.out.print("0");
							System.out.println(" | " + p.numTrue + " "
									+ p.numOccurences);
						}
					}
				}
			}

			for (Node n : data.nodeList) {
				System.out.print(n.name + " ");
				System.out.println(n.P);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
