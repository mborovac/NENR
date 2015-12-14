package hr.fer.zemris.nenr;

import java.io.IOException;
import java.util.List;

/**
 * @author Marko Borovac
 *
 */
public class NeuralNetwork {
	
	/**
	 * Total number of neurons in the neural network.
	 */
	int numberOfNeurons;
	
	/**
	 * Total number of neurons per layer.
	 */
	int[] neuronNumberPerLayer;
	
	/**
	 * Total number of layers of neurons in the neural network.
	 */
	int numberOfLayers;
	
	/**
	 * Field of neuron outputs, 1 element for each neuron.
	 */
	double[] neuronOutput;
	
//	public static void main(String[] args) {
//		NeuralNetwork nn = new NeuralNetwork("2X8x3");
//		System.out.println(nn.calcError(new double[] {0.2329555347781816, 0.9329858468253072, 0.7275695584972593,
//	0.3581611939762225, 0.8567727172969181, 0.6901562611135518, 0.49657007602297654, 0.6955607701072641, 
//	0.6287282989725397, 0.5209588735568885, 0.6431122610976753, 0.7495082793158191, 0.5584527868404856, 
//	0.40721660925464753, 0.5042059094570406, 0.6638680098448525, 0.4776742178023624, 0.8468485000684352, 
//	0.818948792530743, 0.7624037480669866, 0.025146897138956237, 0.980365841705901, 0.5281502951292649, 
//	0.7329574498905115, 0.544758421537075, 0.9693986603870125, 0.23489793719555152, 0.22480618818609865, 
//	0.5423150188680578, 0.6490115900010225, 0.31389038098113475, 0.40878884044527797, 0.23371043261817095,
//	0.8515447856329253, 0.6348996627834407, 0.20494866860772365, 0.34936321624722877, 0.9569419372301443, 
//	0.9425000059577567, 0.19685873794122344, 0.9639194796810634, 0.1630042152260004, 0.47478551129105856, 
//	0.33343738448252935, 0.7033601207950861, 0.6887353330781034, 0.023226984198014167, 0.761159843190613, 
//	0.6561454530761553, 0.6249196992833868, 0.1752844286995736, 0.2897639200784933, 0.9981305998388739, 
//	0.42830191472390655, 0.885912383733371, 0.8937482787487614, 0.7704303350345332, 0.11921260385307386, 
//	0.9922802673491568}));
//	}
	
	public NeuralNetwork(String architecture) {
		
		Dataset dataset = new Dataset();
		try {
			dataset.processData();
		} catch (IOException e) {
			System.err.println("Can not read selected file!");
		}
		
		architecture = architecture.toLowerCase();
		
		String[] neuronsPerLayer = null;
		try {
			neuronsPerLayer = architecture.split("x");
		} catch (Exception e) {
			throw new IllegalArgumentException("Neural network's arhitecture is ill-formed!");
		}
		
		numberOfLayers = neuronsPerLayer.length;
		neuronNumberPerLayer = new int[numberOfLayers];
		numberOfNeurons = 0;
		for(int i = 0; i < numberOfLayers; i++) {
			try {
				neuronNumberPerLayer[i] = Integer.parseInt(neuronsPerLayer[i]);
				numberOfNeurons += neuronNumberPerLayer[i];
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Neural network's " + i+1 + ". layer is not an integer!");
			}
		}
		if(neuronNumberPerLayer[0] != 2 || neuronNumberPerLayer[numberOfLayers - 1] != 3) {
			throw new IllegalArgumentException("Neural network's arhitecture must start with 2 neurons in its "
					+ "1st layer and end with 3 neurons in its final layer.");
		}
		
		neuronOutput = new double[numberOfNeurons];
	}
	
	public double[] calcOutput(double inputX, double inputY, double[] params) {
		neuronOutput[0] = inputX;
		neuronOutput[1] = inputY;
		
		int neuronOffset = 2;
		double[] calcNeuronOutput = new double[neuronNumberPerLayer[0]];
		for(int i = 0; i < calcNeuronOutput.length; i++) {
			calcNeuronOutput[i] = neuronOutput[i + neuronOffset - neuronNumberPerLayer[0]];
		}
		
		double[] calcParams;
		int paramOffset = 0;
		for(int i = 0; i < neuronNumberPerLayer[1]; i++) {
			calcParams = new double[2*neuronNumberPerLayer[0]];
			for(int j = 0; j < calcParams.length; j++) {
				calcParams[j] = params[paramOffset + j];
			}
//			System.out.println(calcParams);
			paramOffset += calcParams.length;
			neuronOutput[neuronOffset + i] = calcFirstNeuronTypeOutput(calcParams, calcNeuronOutput);
		}
		
		// 2nd neuron type
		
		for(int i = 2; i < numberOfLayers; i++) {
			neuronOffset += neuronNumberPerLayer[i - 1];
			calcNeuronOutput = new double[neuronNumberPerLayer[i - 1]];
			for(int j = 0; j < calcNeuronOutput.length; j++) {
				calcNeuronOutput[j] = neuronOutput[j + neuronOffset - neuronNumberPerLayer[i - 1]];
			}
			
			for(int j = 0; j < neuronNumberPerLayer[i]; j++) {
				calcParams = new double[neuronNumberPerLayer[i - 1] + 1];
				for(int z = 0; z < calcParams.length; z++) {
					calcParams[z] = params[paramOffset + z];
				}
				paramOffset += calcParams.length;
//				System.out.println(calcParams);
				neuronOutput[neuronOffset + j] = calcSecondNeuronTypeOutput(calcParams, calcNeuronOutput);
			}
		}
		
		neuronOffset = 0;
		for(int i = 0; i < numberOfLayers - 1; i++) {
			neuronOffset += neuronNumberPerLayer[i];
		}
		
		double[] result = new double[neuronNumberPerLayer[numberOfLayers - 1]];
		for(int i = 0; i < neuronNumberPerLayer[numberOfLayers - 1]; i++) {
			result[i] = neuronOutput[neuronOffset + i];
		}
		return result;
	}
	
	public int numberOfParameters() {
		int paramNumber = 0;
		for(int i = 0; i < numberOfLayers; i++) {
			if(i == 0) {
				continue;
			} else if (i == 1) {
				paramNumber += 4*neuronNumberPerLayer[i];
			} else {
				paramNumber += neuronNumberPerLayer[i]*(neuronNumberPerLayer[i - 1] + 1);
			}
		}
		return paramNumber;
	}
	
	/**
	 * Method used to calculate the output of the 1st neuron type. 1st neuron type requires 2 parameters per link
	 * with a neuron from a previous layer.
	 * 
	 * @param params neuron parameters, 2 per link with a neuron from previous layer
	 * @param x output of the previous layer of neurons
	 * @return returns the output of a single neuron
	 */
	private double calcFirstNeuronTypeOutput(double[] params, double[] x) {
		if(params.length != x.length*2) {
			throw new IllegalArgumentException("Argument params MUST have exactly double the elements argument x has!");
		}
		double calculation = 0;
		for(int i = 0; i < x.length; i++) {
			calculation += Math.abs(x[i] - params[i*2])/Math.abs(params[i*2 + 1]);
		}
		return 1.0/(1.0 + calculation);
	}
	
	/**
	 * Method used to calculate the output of the 2nd neuron type. 2nd neuron type requires 1 parameter per link
	 * with a neuron from a previous layer and a single threshold parameter of the current neuron.
	 * 
	 * @param params neuron parameters, 1 per link with a neuron from previous layer and a threshold parameter
	 * @param x output of the previous layer of neurons
	 * @return returns the output of a single neuron
	 */
	private double calcSecondNeuronTypeOutput(double[] params, double[] x) {
		if(params.length != x.length + 1) {
			throw new IllegalArgumentException("Argument params MUST have exactly 1 element more than argument x!");
		}
		double net = 0;
		for(int i = 0; i < x.length; i++) {
			net += x[i] * params[i];
		}
		net += params[params.length - 1];
		
		return 1.0/(1.0 + Math.pow(Math.E, -net));
	}
	
	public double calcError(double[] params) {
		List<InputData> inputDataList = Dataset.getInput();
		double error = 0;
		for(InputData inputData: inputDataList) {
			double[] output = calcOutput(inputData.point.x, inputData.point.y, params);
			for(int i = 0; i < output.length; i++) {
				error += Math.pow(inputData.classification[i] - output[i], 2);
			}
		}
		return error/inputDataList.size();
	}
	
	public int getNumberOfNeurons() {
		return numberOfNeurons;
	}
	
	public int[] getNeuronNumberPerLayer() {
		return neuronNumberPerLayer;
	}
	
	public int getNumberOfLayers() {
		return numberOfLayers;
	}
	
	public double[] getNeuronOutput() {
		return neuronOutput;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < numberOfLayers; i++) {
			sb.append(neuronNumberPerLayer[i]);
			if(i < numberOfLayers - 1) {
				sb.append("x");
			}
		}
		return sb.toString();
	}
}
