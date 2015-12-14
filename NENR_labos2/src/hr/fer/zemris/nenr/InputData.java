package hr.fer.zemris.nenr;

public class InputData {
	
	Point point;
	double[] classification;
	
	public InputData(Point point, double[] classification) {
		this.point = point;
		this.classification = classification;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < classification.length; i++) {
			sb.append(classification[i] + " ");
		}
		return sb.toString();
	}
}
