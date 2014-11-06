package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

	public List<Node> nodeList;
	public List<String> nodeNameList = new ArrayList<String>();
	public List<List<Integer>> data;
	public Map<String, Node> nodeMap;

	public Data(List<Node> nodeList, List<List<Integer>> data) {
		this.nodeList = nodeList;
		nodeMap = new HashMap<String, Node>();
		for (Node n : this.nodeList) {
			nodeMap.put(n.name, n);
			nodeNameList.add(n.name);
		}
		this.data = data;
	}

	public Data() {
		nodeList = new ArrayList<Node>();
		data = new ArrayList<List<Integer>>();
	}
}
