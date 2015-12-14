package hr.fer.zemris.nenr;

public class Individual {
	
	double[] chromosome;
	double fitness = Double.NEGATIVE_INFINITY;
	
	public Individual(double[] chromosome) {
		this.chromosome = chromosome;
	}
	
	@Override
	public String toString() {
		return "Fitness: " + fitness;
	}
}
