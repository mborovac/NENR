package hr.fer.zemris.nenr;

import java.util.Random;

public class Mutation {
	
	public static Individual mutate(Individual child, double sigma, double pm, MutationType type) {
		Random rand = new Random();
		
		for(int i = 0; i < child.chromosome.length; i++) {
			if(pm > Math.random()) {
				double normalNumber = rand.nextGaussian()*sigma;
				if(type == MutationType.addNumber) {
					child.chromosome[i] = child.chromosome[i] + normalNumber;
				} else {
					child.chromosome[i] = normalNumber;
				}
			}
		}
		return child;
	}
}
