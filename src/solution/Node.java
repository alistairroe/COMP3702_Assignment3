package solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	String name;
	List<String> parents;
	Map<List<String>, Double> P = new HashMap<List<String>, Double>();

	public Node(String name, List<String> parents) {
		this.name = name;
		this.parents = parents;

		for (String parent : parents) {

		}
	}

	public String toString() {
		return name + parents;
	}
}
