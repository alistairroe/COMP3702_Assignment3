package solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
			Data data2 = IO.readPart2("data/noMissingData-d2.txt");
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

					Probability p = n.P.get(s);
					p.numOccurences++;
					if (state.get(i) == 1) {
						n.prob.update(true);
						p.numTrue++;

					} else {
						n.prob.update(false);

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
		Map<String, List<String>> connectionMap = new HashMap<String, List<String>>();
		List<List<Integer>> sparseConnectionIndexList = new ArrayList<List<Integer>>();
		for (Entry<Set<String>, Double> e : list) {
			List<String> nodes = new ArrayList<String>(e.getKey());
			if (!searchMap(connectionMap, nodes.get(0), nodes.get(1))) {
				if (connectionMap.get(nodes.get(0)) == null) {
					connectionMap.put(nodes.get(0), new ArrayList<String>());
				}
				connectionMap.get(nodes.get(0)).add(nodes.get(1));
				if (connectionMap.get(nodes.get(1)) == null) {
					connectionMap.put(nodes.get(1), new ArrayList<String>());
				}
				connectionMap.get(nodes.get(1)).add(nodes.get(0));
				List<Integer> temp = new ArrayList<Integer>();
				temp.add(data.nodeNameList.indexOf(nodes.get(0)));
				temp.add(data.nodeNameList.indexOf(nodes.get(1)));
				sparseConnectionIndexList.add(temp);
			}

		}
		System.out.println("ConnectionList: " + sparseConnectionIndexList);
		int n = (int) Math.pow(2, sparseConnectionIndexList.size());
		double bestScore = -999999;
		String bestStructure = "";
		for (int i = 0; i < n; i++) {
			Data data2 = new Data(data);
			for (int l = 0; l < data2.nodeList.size(); l++) {
				data2.nodeList.get(l).parents = new ArrayList<String>();
			}
			String j = Integer.toBinaryString(i);
			int length = sparseConnectionIndexList.size() - j.length();
			char[] padArray = new char[length];
			Arrays.fill(padArray, '0');
			String padString = new String(padArray);
			String num = padString + j;
			for (int k = sparseConnectionIndexList.size() - 1; k > -1; k--) {
				List<Integer> connection = sparseConnectionIndexList.get(k);
				int x = (int) num.charAt(k);
				if (x == 49) {
					data2.nodeList.get(connection.get(0)).parents
							.add(data2.nodeList.get(connection.get(1)).name);
				} else if (x == 48) {
					data2.nodeList.get(connection.get(1)).parents
							.add(data2.nodeList.get(connection.get(0)).name);
				}
			}
			int x = 0;
			for (int k = 0; k < data2.nodeList.size(); k++) {
				Node newNode = new Node(data2.nodeList.get(k).name,
						data2.nodeList.get(k).parents);
				data2.nodeList.set(k, newNode);
				x++;
			}
			Task1(data2);
			double temp = likelihood(data2);
			System.out.println("");
			if (temp > bestScore) {
				bestScore = temp;
				bestStructure = num;
			} else if (temp == bestScore) {
				bestStructure = bestStructure + "," + num;
			}

		}
		System.out.println(bestStructure);

	}

	public static double likelihood(Data data) {
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
		return logSum;
	}

	public static Set<String> createSet(String... strings) {
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			s.add(strings[i]);
		}
		return s;
	}

	public static boolean searchMap(Map<String, List<String>> map, String A,
			String B) {
		List<String> exploredNodes = new ArrayList<String>();
		List<String> queue = new ArrayList<String>();
		queue.add(A);
		List<String> connectedNodes;
		while (queue.size() > 0) {
			connectedNodes = map.get(queue.remove(0));
			if (connectedNodes == null) {
				return false;
			}
			for (String s : connectedNodes) {
				if (s == B) {
					return true;
				} else {
					if (!exploredNodes.contains(s)) {
						exploredNodes.add(s);
						if (!queue.contains(s)) {
							queue.add(s);

						}
					}
				}
			}
		}
		return false;
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
