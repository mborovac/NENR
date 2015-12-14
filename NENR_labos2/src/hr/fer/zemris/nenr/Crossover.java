package hr.fer.zemris.nenr;

import java.util.Random;

public class Crossover {
	
	public static Individual crossover(Individual parent1, Individual parent2) {
		
		Random rand = new Random();
		int crossoversSelection = rand.nextInt(3);
		if(crossoversSelection == 0) {
			return onePointCrossover(parent1, parent2);
		} else if(crossoversSelection == 1) {
			return uniformCrossover(parent1, parent2);
		} else {
			return averageCrossover(parent1, parent2);
		}
	}
	
	private static Individual onePointCrossover(Individual parent1, Individual parent2) {
		Random rand = new Random();
		int crossPoint = rand.nextInt(parent1.chromosome.length + 1);
		double[] newChromosome = new double[parent1.chromosome.length];
		for(int i = 0; i < crossPoint; i++) {
			newChromosome[i] = parent1.chromosome[i];
		}
		for(int i = crossPoint; i < parent2.chromosome.length; i++) {
			newChromosome[i] = parent2.chromosome[i];
		}
		return new Individual(newChromosome);
	}
	
	private static Individual uniformCrossover(Individual parent1, Individual parent2) {
		double[] newChromosome = new double[parent1.chromosome.length];
		for (int i = 0; i < parent1.chromosome.length; i++) {
			double rand = Math.random();
			if(rand < 0.5) {
				newChromosome[i] = parent1.chromosome[i];
			} else {
				newChromosome[i] = parent2.chromosome[i];
			}
		}
		return new Individual(newChromosome);
	}
	
	private static Individual averageCrossover(Individual parent1, Individual parent2) {
		double[] newChromosome = new double[parent1.chromosome.length];
		for (int i = 0; i < parent1.chromosome.length; i++) {
			newChromosome[i] = (parent1.chromosome[i] + parent2.chromosome[i])/2.0;
		}
		return new Individual(newChromosome);
	}
}
