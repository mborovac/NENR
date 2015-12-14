package hr.fer.zemris.nenr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
	
	final static int populationSize = 50;
	final static int maxNumberOfGenerations = 10000;
	final static String neuralNetworkArchitecture = "2X8X3";
	final static int lowerBound = -5;
	final static int upperBound = 5;
	final static int k = 10;
	final static double sigma1 = 0.2;
	final static double pm1 = 0.05;
	final static double sigma2 = 0.5;
	final static double pm2 = 0.05;
	final static double v1 = 0.9;
	static NeuralNetwork nn = new NeuralNetwork(neuralNetworkArchitecture);;
	
	public static void main(String[] args) {
		int chromosomeSize = nn.numberOfParameters();
		Individual NNParameters = calculate(chromosomeSize);
		double[] params = new double[NNParameters.chromosome.length];
		for(int i = 0; i < NNParameters.chromosome.length; i++) {
			params[i] = NNParameters.chromosome[i];
		}
		int wronglyClassified = 0;
		for(InputData inputData: Dataset.getInput()) {
			double[] output = nn.calcOutput(inputData.point.x, inputData.point.y, params);
			for(int i = 0; i < output.length; i++) {
				if(output[i] < 0.5) {
					output[i] = 0.0;
				} else {
					output[i] = 1.0;
				}
			}
			System.out.print("Point: " + inputData.point + " Given classification: " + inputData + 
					" Calculated classification: " );
			for(int i = 0; i < output.length; i++) {
				System.out.print(output[i] + " ");
			}
			boolean classified = true;
			for(int i = 0; i < output.length; i++) {
				if(output[i] != inputData.classification[i]) {
					wronglyClassified++;
					classified = false;
					break;
				}
			}
			System.out.println(classified);
		}
		double total = Dataset.getInput().size() - wronglyClassified;
		System.out.println("Total right classifications: " + total);
		double percentage = total/Dataset.getInput().size()*100;
		System.out.println("Percentage of right classifications: " + percentage + "%");
	}
	
	private static Individual calculate(int chromosomeSize) {
		
		// generating the population
		List<Individual> population = new ArrayList<>();
		for(int i = 0; i < populationSize; i++) {
			double[] chromosome = new double[chromosomeSize];
			for(int j = 0; j < chromosomeSize; j++) {
				double rand = Math.random() * (upperBound - lowerBound) + lowerBound;
				chromosome[j] = rand;
			}
			Individual individual = new Individual(chromosome);
			population.add(individual);
		}
		
		// each individual's fitness
		calculateFitness(population);
		
		// generations
		int generationCounter = 1;
		double mse = calculateMSE(getBestIndividual(population).chromosome);
		
		while(generationCounter < maxNumberOfGenerations && mse > Math.pow(10, -7)) {
			
			if(generationCounter%100 == 0) {
				System.out.println(generationCounter);
				System.out.println("MSE: " + mse);
			}
			
			List<Individual> newPopulation = new ArrayList<>();
			newPopulation.add(getBestIndividual(population));
			
			while(newPopulation.size() < populationSize) {
				
				population.sort(new Comparator<Individual>() {

					@Override
					public int compare(Individual i1, Individual i2) {
						if(i1.fitness > i2.fitness) {
							return -1;
						} else if(i1.fitness < i2.fitness) {
							return 1;
						} else {
							return 0;
						}
					}
				});
				
				// k-selection
				Individual parent1 = kSelection(population, k);
				Individual parent2 = kSelection(population, k);
				
				// crossover
				Individual child = Crossover.crossover(parent1, parent2);

				// mutation
				mutation(child);
				
				// adding the new individual to new population
				newPopulation.add(child);
				
			}
			
			// each individual's fitness
			calculateFitness(newPopulation);
	
			// end
			Individual bestIndividual = getBestIndividual(newPopulation);
			mse = calculateMSE(bestIndividual.chromosome);
			generationCounter++;
			population = newPopulation;
		}
		return getBestIndividual(population);
	}
	
	private static double calculateMSE(double[] chromosome) {
		return nn.calcError(chromosome);
	}
	
	private static void calculateFitness(List<Individual> population) {
		double totalFitness = 0;
		for(Individual individual: population) {
			individual.fitness = 1.0/(1.0 + calculateMSE(individual.chromosome));
			totalFitness += individual.fitness;
		}
		
		for(Individual individual: population) {
			individual.fitness = individual.fitness/totalFitness;
		}
	}
	
	private static Individual getBestIndividual(List<Individual> population) {
		
		Individual bestIndividual = population.get(0);
		for(Individual individual: population) {
			if(individual.fitness > bestIndividual.fitness) {
				bestIndividual = individual;
			}
		}
		return bestIndividual;
	}
	
	private static void mutation(Individual child) {
		if(Math.random() < v1) {
			Mutation.mutate(child, sigma1, pm1, MutationType.addNumber);
		} else {
			Mutation.mutate(child, sigma2, pm2, MutationType.switchNumber);
		}
	}
	
	private static Individual kSelection(List<Individual> population, int k) {
		List<Individual> selected = new ArrayList<>();
		Random rand = new Random();
		for(int i = 0; i < k; i++) {
			selected.add(population.get(rand.nextInt(k + 1)));
		}
		return getBestIndividual(selected);
	}
}
