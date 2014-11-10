package solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class IO {

	public static Data readFile(String filename) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		Scanner s;
		List<Node> nodeList = new ArrayList<Node>();
		List<List<Integer>> data = new ArrayList<List<Integer>>();
		try {
			line = input.readLine();
			s = new Scanner(line);
			int numNodes = s.nextInt();
			int numData = s.nextInt();
			for (int i = 0; i < numNodes; i++) {
				line = input.readLine();
				String[] temp = line.split(" ");
				List<String> parents = new ArrayList<String>();
				parents.addAll(Arrays.asList(temp));
				String name = parents.remove(0);
				nodeList.add(new Node(name, parents));
			}
			for (int i = 0; i < numData; i++) {
				line = input.readLine();
				List<Integer> lineData = new ArrayList<Integer>();
				s = new Scanner(line);
				for (int j = 0; j < numNodes; j++) {
					lineData.add(s.nextInt());
				}
				data.add(lineData);
			}
			// System.out.println(nodeList);
			// System.out.println(data);
			input.close();
			s.close();

			Data temp = new Data(nodeList, data);
			return temp;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return new Data();
	}

	public static Data readPart2(String filename) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		Scanner s;
		List<Node> nodeList = new ArrayList<Node>();
		List<List<Integer>> data = new ArrayList<List<Integer>>();
		try {
			line = input.readLine();
			s = new Scanner(line);
			int numNodes = s.nextInt();
			int numData = s.nextInt();
			line = input.readLine();
			String[] temp = line.split(" ");
			List<String> nodes = new ArrayList<String>();
			nodes.addAll(Arrays.asList(temp));
			for (int i = 0; i < numNodes; i++) {
				String name = nodes.get(i);
				nodeList.add(new Node(name, new ArrayList<String>()));
			}
			for (int i = 0; i < numData; i++) {
				line = input.readLine();
				List<Integer> lineData = new ArrayList<Integer>();
				s = new Scanner(line);
				for (int j = 0; j < numNodes; j++) {
					lineData.add(s.nextInt());
				}
				data.add(lineData);
			}
			// System.out.println(nodeList);
			// System.out.println(data);
			input.close();
			s.close();

			Data temp2 = new Data(nodeList, data);
			return temp2;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return new Data();
	}

	public static void main(String[] args) {
		try {
			Data data = readPart2("data/noMissingData-d1.txt");
			System.out.println(data.nodeList);
			System.out.println(data.data);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
