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
	public double logLikelihood;

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

	public Data(Data data) {
		this.nodeList = new ArrayList<Node>();
		for (Node n : data.nodeList) {
			this.nodeList
					.add(new Node(n.name, new ArrayList<String>(n.parents)));
		}
		this.nodeNameList = new ArrayList<String>(data.nodeNameList);
		this.nodeMap = new HashMap<String, Node>(data.nodeMap);
		this.data = data.data;
		this.logLikelihood = data.logLikelihood;
	}
}
