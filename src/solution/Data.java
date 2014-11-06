package solution;

import java.util.ArrayList;
import java.util.List;

public class Data {

	public List<Node> nodeList;
	public List<List<Integer>> data;

	public Data(List<Node> nodeList, List<List<Integer>> data) {
		this.nodeList = nodeList;
		this.data = data;
	}

	public Data() {
		nodeList = new ArrayList<Node>();
		data = new ArrayList<List<Integer>>();
	}
}
