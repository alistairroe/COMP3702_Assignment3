package solution;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Solver {

	public static void main(String[] args) {
		Data data;
		try {
			// data = IO.readFile("data/CPTNoMissingData-d3.txt");
			// Task1(data);
			// likelihood(data);
			Data data2 = IO.readPart2("data/noMissingData-d1.txt");
			Task2(data2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Task1(Data data) {

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

	}

	public static void Task2(Data data) {
		for (int i = 0; i < data.nodeList.size(); i++) {
			for (List<Integer> state : data.data) {
				if (state.get(i) == 1) {
					data.nodeList.get(i).prob.update(true);
				} else {
					data.nodeList.get(i).prob.update(false);
				}
				for (int j = 0; j < data.nodeList.size(); j++) {
					if (i != j) {
						Set<String> s;
						Probability p;
						if (state.get(j) == 1) {
							s = createSet(data.nodeList.get(j).name);
							p = data.nodeList.get(i).P.get(s);
							if (p == null) {
								p = new Probability();
							}
							if (state.get(i) == 1) {
								System.out.print(state);
								System.out.println(data.nodeList.get(j).name
										+ " + " + data.nodeNameList.get(i));
								p.update(true);
							} else {
								p.update(false);
							}

						} else {
							s = createSet("~" + data.nodeList.get(j).name);
							p = data.nodeList.get(i).P.get(s);
							if (p == null) {
								p = new Probability();
							}
							if (state.get(i) == 1) {
								System.out.print(state);
								System.out.println(data.nodeList.get(j).name
										+ " + " + data.nodeNameList.get(i));
								p.update(true);
							} else {
								p.update(false);
							}
						}
						data.nodeList.get(i).P.put(s, p);
					}

				}
			}
		}
		Map<Set<String>, Double> MI = new HashMap<Set<String>, Double>();
		for (int i = 0; i < data.nodeList.size(); i++) {
			for (int j = i + 1; j < data.nodeList.size(); j++) {
				double sum = 0;
				// Basically there are 4 possibilities for MI(A,B), each A and B
				// being 0 or 1. This just gets them, hardcoded. They come out
				// the same way regardless of which order you give the
				// arguments (for case 0), which is a good sign.
				System.out.println("Doing " + data.nodeList.get(i).name
						+ " and " + data.nodeList.get(j).name);
				sum += PMI(data.nodeList.get(i), data.nodeList.get(j), 0);
				sum += PMI(data.nodeList.get(i), data.nodeList.get(j), 1);
				sum += PMI(data.nodeList.get(j), data.nodeList.get(i), 1);
				sum += PMI(data.nodeList.get(i), data.nodeList.get(j), 2);
				MI.put(createSet(data.nodeList.get(i).name,
						data.nodeList.get(j).name), sum);
			}
		}
		System.out.println(MI);
		List<Entry<Set<String>, Double>> list = sortMap(MI);
		System.out.println(list);
		for (Node n : data.nodeList) {
			System.out.print(n.name + " " + n.prob + " ");
			System.out.println(n.P);
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
		System.out.println(Math.pow(Math.E, logSum));
	}

	public static Set<String> createSet(String... strings) {
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			s.add(strings[i]);
		}
		return s;
	}

	public static double PMI(Node node1, Node node2, int code) {
		double result = 0;
		Set<String> s;
		switch (code) {
		case 0:
			s = createSet(node2.name);
			// System.out.println(node1.name + " " + node2.name);
			result = node1.P.get(s).getProb() * node2.prob.getProb()
					* Math.log(node1.P.get(s).getProb() / node1.prob.getProb());
			break;
		case 1:
			s = createSet("~" + node2.name);
			result = node1.P.get(s).getProb() * (1 - node2.prob.getProb())
					* Math.log(node1.P.get(s).getProb() / node1.prob.getProb());
			break;
		case 2:
			s = createSet("~" + node2.name);
			result = (1 - node1.P.get(s).getProb())
					* (1 - node2.prob.getProb())
					* Math.log((1 - node1.P.get(s).getProb())
							/ (1 - node1.prob.getProb()));
		}
		return result;
	}

	public static List<Entry<Set<String>, Double>> sortMap(
			Map<Set<String>, Double> map) {
		List<Entry<Set<String>, Double>> list = new LinkedList<Entry<Set<String>, Double>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<Set<String>, Double>>() {
			public int compare(Entry<Set<String>, Double> o1,
					Entry<Set<String>, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list;
	}

}
