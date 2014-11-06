package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Node {
	String name;
	List<String> parents;
	int numOccurences;
	int numTrue;
	Map<Set<String>, Probability> P = new HashMap<Set<String>, Probability>();

	public Node(String name, List<String> parents) {
		this.name = name;
		this.parents = parents;
		List<Set<String>> setList = new ArrayList<Set<String>>();

		for (String parent : parents) {
			if (setList.size() == 0) {
				Set<String> s = new HashSet<String>();
				s.add(parent);
				setList.add(s);
				Set<String> ns = new HashSet<String>();
				ns.add("n" + parent);
				setList.add(ns);
			} else {
				List<Set<String>> newSetList = new ArrayList<Set<String>>();
				for (Set<String> s : setList) {
					Set<String> s2 = new HashSet<String>();
					s2.addAll(s);
					s2.add(parent);
					newSetList.add(s2);
					s.add("n" + parent);
					newSetList.add(s);
				}
				setList = newSetList;
			}
			System.out.print(name + " ");
			System.out.println(setList);
		}
		for (Set<String> s : setList) {
			P.put(s, new Probability(0));
		}

	}

	public String toString() {
		return name + parents;
	}
}
