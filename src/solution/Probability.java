package solution;

public class Probability {

	private double prob = 0;
	int numOccurences = 0;
	int numTrue = 0;

	public Probability(double d) {
		if (d < 1.01) {
			prob = d;
		}
	}

	public Probability() {

	}

	public double getProb() {
		if (numOccurences == 0) {
			prob = 0.5;
			return prob;
		} else {
			prob = ((double) numTrue) / numOccurences;
			return prob;
		}
	}

	public String toString() {
		return String.valueOf(getProb());
	}

	public void update(boolean b) {
		if (b) {
			numTrue++;
		}
		numOccurences++;
	}
}
