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
							n.prob.update(true);
						} else {
							n.prob.update(false);
						}
					} else {
						Set<String> s = new HashSet<String>();
						for (String parent : n.parents) {
							int index = data.nodeNameList.indexOf(parent);
							if (state.get(index) == 1) {
								s.add(parent);
							} else {
								s.add("~" + parent);
							}
						}
						System.out.print("Given " + s + ", " + n.name + ": ");
						Probability p = n.P.get(s);
						p.numOccurences++;
						if (state.get(i) == 1) {
							n.prob.update(true);
							p.numTrue++;
							System.out.print("1");
							System.out.println(" | " + p.numTrue + " "
									+ p.numOccurences);
						} else {
							n.prob.update(false);
							System.out.print("0");
							System.out.println(" | " + p.numTrue + " "
									+ p.numOccurences);
						}
					}
				}
			}

			for (Node n : data.nodeList) {
				System.out.print(n.name + " " + n.prob + " ");
				System.out.println(n.P);
			}
			likelihood(data);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void likelihood(Data data) {
		double logSum = 0;
		double product = 1;
		for (List<Integer> l : data.data) {
			double entryProduct = 1;
			double entryLogSum = 0;
			for (int i = 0; i < data.nodeList.size(); i++) {
				Node n = data.nodeList.get(i);
				Set<String> s = new HashSet<String>();
				for (String parent : n.parents) {
					int index = data.nodeNameList.indexOf(parent);
					if (l.get(index) == 1) {
						s.add(parent);
					} else {
						s.add("~" + parent);
					}
				}
				if (n.parents.size() != 0) {
					if (l.get(i) == 1) {
						logSum += Math.log(n.P.get(s).getProb());
						entryProduct *= n.P.get(s).getProb();

					} else {
						logSum += Math.log((1 - n.P.get(s).getProb()));
						entryProduct *= (1 - n.P.get(s).getProb());
					}
				} else {
					if (l.get(i) == 1) {
						logSum += Math.log(n.prob.getProb());
						entryProduct *= n.prob.getProb();
					} else {
						logSum += Math.log((1 - n.prob.getProb()));
						entryProduct *= (1 - n.prob.getProb());
					}

				}
			}
			product *= entryProduct;
			logSum += entryLogSum;
		}
		System.out.println(logSum);
		System.out.println(product);
	}

	public static Set<String> createSet(String... strings) {
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			s.add(strings[i]);
		}
		return s;
	}
}
