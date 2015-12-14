package hr.fer.zemris.nenr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
	
	private static List<InputData> Input;
	private static List<Point> typeA;
	private static List<Point> typeB;
	private static List<Point> typeC;

	public void processData() throws IOException {
		Input = new ArrayList<>();
		typeA = new ArrayList<>();
		typeB = new ArrayList<>();
		typeC = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("projekt2-data.txt"));
		try {
			String line = br.readLine();
			while (line != null) {
				String[] splitLine = line.split("\\t");
				Point point = new Point(Double.parseDouble(splitLine[0]), Double.parseDouble(splitLine[1]));
				if(splitLine[2].equals("1")) {
					typeA.add(point);
				} else if(splitLine[3].equals("1")) {
					typeB.add(point);
				} else {
					typeC.add(point);
				}
				Input.add(new InputData(point, new double[] {Double.parseDouble(splitLine[2]), 
						Double.parseDouble(splitLine[3]), Double.parseDouble(splitLine[4])}));
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		
		PrintToFile("typeA.txt", typeA);
		PrintToFile("typeB.txt", typeB);
		PrintToFile("typeC.txt", typeC);
	}
	
	private static void PrintToFile(String fileName, List<Point> pointList) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName), "utf-8"))) {
			for(Point point: pointList) {
				writer.write(point.x + "	" + point.y + "\n");
			}
		} catch (IOException ex) {
			System.err.println("Error writing to " + fileName);
		}
	}

	public static List<InputData> getInput() {
		return new ArrayList<>(Input);
	}
}
