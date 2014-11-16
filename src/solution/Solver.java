package solution;

import java.io.IOException;
import java.math.BigDecimal;
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
import java.util.Random;
import java.util.Set;

public class Solver {

	public static void main(String[] args) {
		Data data;
		try {
			// data = IO.readFile("data/CPTNoMissingData-d3.txt");
			// Task1(data);
			// data.logLikelihood = likelihood(data);
			// System.out.println(data.logLikelihood);
			// IO.writeTask1(data, "cpt-d1.txt");
			// Data data2 = IO.readPart2("data/noMissingData-d3.txt");
			// data2 = initialiseChain(data2);
			// /data2 = Task2(data2);
			// IO.writeTask2(data2, "bn-d3.txt");
			data = IO.readFile3("data/someMissingData-d1.txt");
			data = Task3(data);
			IO.writeTask3(data, "bn-someMissingData-d1.txt");
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

		/*for (Node n : data.nodeList) {
			System.out.print(n.name + " " + n.prob + " ");
			System.out.println(n.P);
		}*/

	}

	public static Data Task2(Data data) {
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

		List<Entry<Set<String>, Double>> list = sortMap(MI);
		System.out.println(list);
		// Data newdata = greedyStructure(data, list);
		// System.out.println(newdata.nodeList);
		Data newdata = greedyKruskalGraph(data, list);
		// Data newdata = greedyBruteForce(data);
		System.out.println(newdata.nodeList);
		return newdata;

	}

	public static Data Task3(Data data) {
		Data data2 = new Data(data);
		Random random = new Random();
		List<List<Integer>> fileData = data2.data;
		List<List<Integer>> newData = new ArrayList<List<Integer>>();
		List<List<Integer>> missingLocations = new ArrayList<List<Integer>>();
		int counter = 0;
		for (List<Integer> state : fileData) {
			boolean flag = false;
			for (int i = 0; i < state.size(); i++) {
				if (state.get(i) > 1) {
					flag = true;
					List<Integer> temp = new ArrayList<Integer>();
					temp.add(counter);
					temp.add(i);
					missingLocations.add(temp);
				}
			}
			if (!flag) {
				newData.add(state);
			}
			counter++;
		}
		data2.data = newData;
		System.out.println(data.data.size() + " " + data2.data.size());
		// Task1(data2);
		for (int i = 0; i < 5; i++) {
			data2 = new Data(data2);
			data2 = Task2(data2);
			data2.fillProbabilities = new ArrayList<List<String>>();
			counter = 0;
			for (List<Integer> location : missingLocations) {
				List<Integer> state = data.data.get(location.get(0));
				Set<String> set = new HashSet<String>();
				Node n = data2.nodeList.get(location.get(1));
				List<String> parents = n.parents;
				double prob = 0;
				if (parents.size() == 0) {
					prob = n.prob.getProb();
					System.out.println(n + " " + prob);
				} else {
					for (String parent : parents) {
						int index = data.nodeNameList.indexOf(parent);
						if (state.get(index) == 1) {
							set.add(parent);
						} else {
							set.add("~" + parent);
						}

					}

					prob = n.P.get(set).getProb();
					System.out.print(n + " " + set + " " + prob + " ");
				}
				System.out.println(data.data);
				double rand = random.nextDouble();
				List<String> fillProb = new ArrayList<String>();
				fillProb.add("H" + counter);
				fillProb.add(String.valueOf(prob));
				// System.out.print(fillProb);
				data2.fillProbabilities.add(fillProb);
				// CHANGE THIS 0.5 to rand to fill using weighted random
				// generator
				if (prob > 0.5) {
					state.set(location.get(1), 1);
					System.out.println("Filled with 1");
				} else {
					state.set(location.get(1), 0);
					System.out.println("Filled with 0");
				}
				counter++;
			}
			data2.data = data.data;
		}
		return data2;
		// System.out.println(data.nodeList.get(1));
		// System.out.println(data2.nodeList);
	}

	public static double likelihood(Data data) {
		BigDecimal logSum1 = new BigDecimal(0);
		double logSum = 0;
		double product = 1;
		for (List<Integer> l : data.data) {
			double entryProduct = 1;
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
		}
		// System.out.println(logSum);
		return logSum;
	}

	public static Set<String> createSet(String... strings) {
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			s.add(strings[i]);
		}
		return s;
	}

	public static Data greedyStructure(Data data,
			List<Entry<Set<String>, Double>> list) {
		double C = 1;
		Map<String, List<String>> connectionMap = new HashMap<String, List<String>>();
		Data datamaster = new Data(data);
		datamaster.score = -99999999;
		for (Entry<Set<String>, Double> e : list) {
			System.out.println(datamaster.score);
			List<String> nodes = new ArrayList<String>(e.getKey());
			Data data2 = new Data(datamaster);
			Data data3 = new Data(datamaster);
			System.out.println(connectionMap);
			// If 0 can't get to 1, it means 0 can be a parent of 1
			if (!searchMap(connectionMap, nodes.get(0), nodes.get(1))) {
				int indexChild = data2.nodeNameList.indexOf(nodes.get(1));
				data2.nodeList.get(indexChild).parents.add(nodes.get(0));
				data2.nodeList.set(indexChild,
						new Node(data2.nodeList.get(indexChild).name,
								data2.nodeList.get(indexChild).parents));
				Task1(data2);
				data2.logLikelihood = likelihood(data2);
			} else {
				System.out.println("Prevented circularity 1 " + nodes.get(0)
						+ nodes.get(1));
			}
			// If 1 can't get to 0, it means 1 can be a parent of 0
			if (!searchMap(connectionMap, nodes.get(1), nodes.get(0))) {
				int indexChild = data3.nodeNameList.indexOf(nodes.get(0));
				data3.nodeList.get(indexChild).parents.add(nodes.get(1));
				data3.nodeList.set(indexChild,
						new Node(data3.nodeList.get(indexChild).name,
								data3.nodeList.get(indexChild).parents));
				Task1(data3);
				data3.logLikelihood = likelihood(data3);
			} else {
				System.out.println("Prevented circularity 2 " + nodes.get(0)
						+ nodes.get(1));
			}
			int num1 = 0;
			int num2 = 0;
			for (Node n : data2.nodeList) {
				num1 += n.P.size();
				if (n.P.size() == 0) {
					num1 += 1;
				}
			}
			for (Node n : data3.nodeList) {
				num2 += n.P.size();
				if (n.P.size() == 0) {
					num2 += 1;
				}
			}
			double score1 = data2.logLikelihood - C * num1;
			double score2 = data3.logLikelihood - C * num2;
			System.out.println("Best: " + datamaster.score + "; Scores: "
					+ score1 + " vs " + score2);
			if (score1 >= score2) {
				if (score1 > datamaster.score) {
					datamaster = data2;
					if (connectionMap.get(nodes.get(1)) == null) {
						connectionMap
								.put(nodes.get(1), new ArrayList<String>());
					}
					connectionMap.get(nodes.get(1)).add(nodes.get(0));
					datamaster.score = score1;
				} else {
					return datamaster;
				}
			} else {
				if (score2 > datamaster.score) {
					datamaster = data3;
					if (connectionMap.get(nodes.get(0)) == null) {
						connectionMap
								.put(nodes.get(0), new ArrayList<String>());
					}
					connectionMap.get(nodes.get(0)).add(nodes.get(1));
					datamaster.score = score2;
				} else {
					return datamaster;
				}

			}
		}
		return datamaster;
	}

	public static boolean searchMap(Map<String, List<String>> map, String A,
			String B) {
		// System.out.println(A + " " + B);
		// Returns true if nodes are connected; false if not
		List<String> exploredNodes = new ArrayList<String>();
		List<String> queue = new ArrayList<String>();
		queue.add(A);
		List<String> connectedNodes;
		while (queue.size() > 0) {
			connectedNodes = map.get(queue.remove(0));
			// System.out.println(connectedNodes);
			if ((connectedNodes == null) && queue.size() == 0) {
				return false;
			}
			if (connectedNodes != null) {
				for (String s : connectedNodes) {
					// System.out.println(s + " compared to " + B);
					if (s == B) {
						return true;
					} else {
						if (!exploredNodes.contains(s)) {
							exploredNodes.add(s);
							if (!queue.contains(s)) {
								queue.add(s);
								// System.out.println(s + " added to queue");

							}
						}
					}
				}
			}
		}
		// System.out.println(exploredNodes);
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
			if (node1.P.get(s).getProb() == 0) {
				result = 0;
			}
			break;
		case 1:
			s = createSet("~" + node2.name);
			result = node1.P.get(s).getProb() * (1 - node2.prob.getProb())
					* Math.log(node1.P.get(s).getProb() / node1.prob.getProb());

			if (node1.P.get(s).getProb() == 0) {
				result = 0;
			}
			break;
		case 2:
			s = createSet("~" + node2.name);
			result = (1 - node1.P.get(s).getProb())
					* (1 - node2.prob.getProb())
					* Math.log((1 - node1.P.get(s).getProb())
							/ (1 - node1.prob.getProb()));
			if (node1.P.get(s).getProb() == 1) {
				result = 0;
			}
			break;
		}
		return result;
	}

	public static Data greedyKruskalGraph(Data data,
			List<Entry<Set<String>, Double>> list) {
		Map<String, List<String>> connectionMap = new HashMap<String, List<String>>();
		List<List<Integer>> sparseConnectionIndexList = new ArrayList<List<Integer>>();
		double C = 0.5;
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
		Data data2 = new Data();
		for (int i = 0; i < n; i++) {
			data2 = new Data(data);
			for (int l = 0; l < data2.nodeList.size(); l++) {
				data2.nodeList.get(l).parents = new ArrayList<String>();
			}
			String num = createBinaryString(sparseConnectionIndexList.size(), i);
			for (int k = sparseConnectionIndexList.size() - 1; k > -1; k--) {
				List<Integer> connection = sparseConnectionIndexList.get(k);
				int x = (int) num.charAt(k);
				if (x == 48) {
					data2.nodeList.get(connection.get(0)).parents
							.add(data2.nodeList.get(connection.get(1)).name);
				} else if (x == 49) {
					data2.nodeList.get(connection.get(1)).parents
							.add(data2.nodeList.get(connection.get(0)).name);
				}
			}
			for (int k = 0; k < data2.nodeList.size(); k++) {
				Node newNode = new Node(data2.nodeList.get(k).name,
						data2.nodeList.get(k).parents);
				data2.nodeList.set(k, newNode);
			}
			Task1(data2);
			System.out.print(data2.nodeList + "       ");
			int num1 = 0;
			for (Node n1 : data2.nodeList) {
				num1 += n1.P.size();
				if (n1.P.size() == 0) {
					num1 += 1;
				}
			}
			double temp = likelihood(data2) - C * num1;
			System.out.println(temp);
			// System.out.println("");
			if (temp > bestScore) {
				bestScore = temp;
				bestStructure = num;
			} else if (temp == bestScore) {
				bestStructure = bestStructure + "," + num;
			}

		}
		System.out.println(bestStructure);
		Data data3 = new Data(data);
		for (int l = 0; l < data3.nodeList.size(); l++) {
			data3.nodeList.get(l).parents = new ArrayList<String>();
		}
		for (int k = sparseConnectionIndexList.size() - 1; k > -1; k--) {
			List<Integer> connection = sparseConnectionIndexList.get(k);
			int x = (int) bestStructure.charAt(k);
			if (x == 48) {
				data3.nodeList.get(connection.get(0)).parents
						.add(data3.nodeList.get(connection.get(1)).name);
			} else if (x == 49) {
				data3.nodeList.get(connection.get(1)).parents
						.add(data3.nodeList.get(connection.get(0)).name);
			}
		}
		for (int k = 0; k < data3.nodeList.size(); k++) {
			Node newNode = new Node(data3.nodeList.get(k).name,
					data3.nodeList.get(k).parents);
			data3.nodeList.set(k, newNode);
		}
		System.out.println("Final nodelist: " + data3.nodeList);
		Task1(data3);
		data3.logLikelihood = likelihood(data3);
		int num1 = 0;
		for (Node n1 : data3.nodeList) {
			num1 += n1.P.size();
			if (n1.P.size() == 0) {
				num1 += 1;
			}
		}
		data3.score = data3.logLikelihood - C * num1;
		return data3;
	}

	public static Data greedyBruteForce(Data data) {
		Data data2 = new Data(data);
		double bestAddScore = -9999999;
		double bestRemoveScore = -9999999;
		double bestSwapScore = -9999999;
		double C = 0.7;
		// double bestSwitchLikelihood = -9999999;
		Map<String, List<String>> connectionMap = new HashMap<String, List<String>>();
		for (Node n : data2.nodeList) {
			if (n.parents.size() != 0) {
				connectionMap.put(n.name, new ArrayList<String>(n.parents));
			}
		}
		List<Integer> bestAddPair = new ArrayList<Integer>();
		List<Integer> bestRemovePair = new ArrayList<Integer>();
		List<Integer> bestSwapPair = new ArrayList<Integer>();
		// Adding
		for (int k = 0; k < 20; k++) {
			boolean action = false;
			for (int i = 0; i < data2.nodeList.size(); i++) {
				for (int j = 0; j < data2.nodeList.size(); j++) {
					if (i != j) {
						data2 = new Data(data);
						Node n = data2.nodeList.get(i);
						// System.out.println(connectionMap);
						if (!searchMap(connectionMap,
								data2.nodeList.get(j).name, n.name)) {
							n.parents.add(data2.nodeList.get(j).name);
							data2.nodeList.set(i, new Node(n.name, n.parents));
							Task1(data2);
							data2.logLikelihood = likelihood(data2);
							int num1 = 0;
							for (Node n1 : data2.nodeList) {
								num1 += n1.P.size();
								if (n1.P.size() == 0) {
									num1 += 1;
								}
							}
							data2.score = data2.logLikelihood - C * num1;
							if (data2.score > bestAddScore) {
								action = true;
								bestAddScore = data2.score;
								bestAddPair.clear();
								bestAddPair.add(i);
								bestAddPair.add(j);
							}
						}
					}
				}
				for (int j = 0; j < data.nodeList.get(i).parents.size(); j++) {
					// Removing
					data2 = new Data(data);
					String parent = data2.nodeList.get(i).parents.remove(j);
					data2.nodeList.set(i, new Node(data2.nodeList.get(i).name,
							data2.nodeList.get(i).parents));
					Task1(data2);
					data2.logLikelihood = likelihood(data2);
					int num1 = 0;
					for (Node n1 : data2.nodeList) {
						num1 += n1.P.size();
						if (n1.P.size() == 0) {
							num1 += 1;
						}
					}
					data2.score = data2.logLikelihood - C * num1;
					if (data2.score > bestRemoveScore) {
						action = true;
						bestRemoveScore = data2.score;
						bestRemovePair.clear();
						bestRemovePair.add(i);
						bestRemovePair.add(j);
					}

					// Swapping
					int indexParent = data2.nodeNameList.indexOf(parent);
					data2.nodeList.get(indexParent).parents.add(data2.nodeList
							.get(i).name);
					data2.nodeList.set(indexParent,
							new Node(data2.nodeList.get(indexParent).name,
									data2.nodeList.get(indexParent).parents));
					Task1(data2);
					data2.logLikelihood = likelihood(data2);
					num1 = 0;
					for (Node n1 : data2.nodeList) {
						num1 += n1.P.size();
						if (n1.P.size() == 0) {
							num1 += 1;
						}
					}
					data2.score = data2.logLikelihood - C * num1;
					if (data2.score > bestSwapScore) {
						action = true;
						bestSwapScore = data2.score;
						bestSwapPair.clear();
						bestSwapPair.add(i);
						bestSwapPair.add(j);
					}
				}

			}
			// System.out.println(data.nodeList);
			System.out.print(bestAddPair + " " + bestRemovePair + " "
					+ bestSwapPair + " " + action + " " + bestAddScore + " "
					+ bestRemoveScore + " " + bestSwapScore);
			if (action) {
				if (bestRemoveScore > bestAddScore) {
					if (bestRemoveScore > bestSwapScore) {
						bestAddScore = bestRemoveScore;
						bestSwapScore = bestRemoveScore;
						Node n = data.nodeList.get(bestRemovePair.get(0));
						String removedParent = n.parents
								.remove((int) bestRemovePair.get(1));
						data.nodeList.set(bestRemovePair.get(0), new Node(
								n.name, n.parents));
						connectionMap.get(n.name).remove(removedParent);
						System.out.println(" ---> remove");
					} else {
						bestRemoveScore = bestSwapScore;
						bestAddScore = bestSwapScore;
						Node n = data.nodeList.get(bestSwapPair.get(0));
						String removedParent = n.parents
								.remove((int) bestSwapPair.get(1));
						data.nodeList.set(bestSwapPair.get(0), new Node(n.name,
								n.parents));
						int indexParent = data.nodeNameList
								.indexOf(removedParent);
						data.nodeList.get(indexParent).parents
								.add(data.nodeList.get(bestSwapPair.get(0)).name);
						data.nodeList
								.set(indexParent,
										new Node(
												data.nodeList.get(indexParent).name,
												data.nodeList.get(indexParent).parents));
						connectionMap.get(n.name).remove(removedParent);
						if (connectionMap.get(removedParent) == null) {
							connectionMap.put(removedParent,
									new ArrayList<String>());
						}
						connectionMap.get(removedParent).add(n.name);
						System.out.println(" ---> swap");
					}
				} else {
					if (bestAddScore > bestSwapScore) {
						bestRemoveScore = bestAddScore;
						bestSwapScore = bestAddScore;
						Node n = data.nodeList.get(bestAddPair.get(0));
						n.parents
								.add(data.nodeList.get(bestAddPair.get(1)).name);
						data.nodeList.set(bestAddPair.get(0), new Node(n.name,
								n.parents));
						if (connectionMap.get(n.name) == null) {
							connectionMap.put(n.name, new ArrayList<String>());
						}
						connectionMap.get(n.name).add(
								data.nodeList.get(bestAddPair.get(1)).name);
						System.out.println(" ---> add");
					} else {
						bestRemoveScore = bestSwapScore;
						bestAddScore = bestSwapScore;
						Node n = data.nodeList.get(bestSwapPair.get(0));
						String removedParent = n.parents
								.remove((int) bestSwapPair.get(1));
						data.nodeList.set(bestSwapPair.get(0), new Node(n.name,
								n.parents));
						int indexParent = data.nodeNameList
								.indexOf(removedParent);
						data.nodeList.get(indexParent).parents
								.add(data.nodeList.get(bestSwapPair.get(0)).name);
						data.nodeList
								.set(indexParent,
										new Node(
												data.nodeList.get(indexParent).name,
												data.nodeList.get(indexParent).parents));
						connectionMap.get(n.name).remove(removedParent);
						if (connectionMap.get(removedParent) == null) {
							connectionMap.put(removedParent,
									new ArrayList<String>());
						}
						connectionMap.get(removedParent).add(n.name);
						System.out.println(" ---> swap");
					}
				}
				bestRemovePair.clear();
				bestAddPair.clear();
				bestSwapPair.clear();
				Task1(data);
			} else {
				System.out.println();
				return data;
			}

		}
		return data;
	}

	public static Data initialiseChain(Data data) {
		List<String> temp = new ArrayList<String>(data.nodeNameList);
		Collections.shuffle(temp);
		for (int i = 1; i < temp.size(); i++) {
			data.nodeMap.get(temp.get(i - 1)).parents.add(temp.get(i));
		}
		for (int i = 0; i < data.nodeList.size(); i++) {
			data.nodeList.set(i, new Node(data.nodeList.get(i).name,
					data.nodeList.get(i).parents));
		}
		System.out.println(data.nodeList);
		return data;
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

	public static String createBinaryString(int length, int n) {
		// System.out.println(length + " " + n);
		String j = Integer.toBinaryString(n);
		int padLength = length - j.length();
		char[] padArray = new char[padLength];
		Arrays.fill(padArray, '0');
		String padString = new String(padArray);
		String num = padString + j;
		return num;
	}

}
