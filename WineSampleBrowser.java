package assignment2019;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JFrame;

import assignment2019.codeprovided.AbstractWineSampleBrowserPanel;
import assignment2019.codeprovided.AbstractWineSampleCellar;
import assignment2019.codeprovided.Query;
import assignment2019.codeprovided.WineSample;
import assignment2019.codeprovided.WineType;

/**
 * WineSampleBrowser.java
 *
 * Class that contains the main method of the program.
 *
 * @version 1.0 08/05/2019
 *
 * @author Buliga Fanel Emanuel (febuliga1@sheffield.ac.uk)
 */

@SuppressWarnings("serial")
public class WineSampleBrowser extends JFrame {
	// constructor
	public WineSampleBrowser(AbstractWineSampleBrowserPanel panel) {
		final double SIZE_MODIFIER_FACTOR = 0.95;
		final double LOCATION_MODIFIER_FACTOR = 0.025;

		Toolkit toolkit = Toolkit.getDefaultToolkit();

		// position the GUI
		Dimension screenDimensions = toolkit.getScreenSize();
		setSize((int) (screenDimensions.width * SIZE_MODIFIER_FACTOR), screenDimensions.height);
		setLocation(new Point((int) (screenDimensions.width * LOCATION_MODIFIER_FACTOR), screenDimensions.height));

		Container contentPane = getContentPane();
		contentPane.add(panel);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Wine Sample Browser");
		setVisible(true);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "resources/winequality-red.csv", "resources/winequality-white.csv", "resources/queries.txt" };
		}

		String redWineFile = args[0];
		String whiteWineFile = args[1];
		String queriesFile = args[2];

		WineSampleCellar wineSampleCellar = new WineSampleCellar(redWineFile, whiteWineFile, queriesFile);

		//!! run the application !!
		WineSampleBrowser browser = new WineSampleBrowser(new WineSampleBrowserPanel(wineSampleCellar));

		// number of samples of each wine type
		int redWineSamples = wineSampleCellar.getWineSampleCount(WineType.RED);
		int whiteWineSamples = wineSampleCellar.getWineSampleCount(WineType.WHITE);
		int totalNumberOfWineSamples = wineSampleCellar.getWineSampleCount(WineType.ALL);

		// the 10 answered questions (results in console)
		// Question 1
		System.out.println("Q1. Total number of wine samples: " + totalNumberOfWineSamples + "\n");

		// Question 2
		System.out.println("Q2. Number of RED wine samples: " + redWineSamples + " out of " + totalNumberOfWineSamples + "\n");

		// Question 3
		System.out.println("Q3. Number of WHITE wine samples: " + whiteWineSamples + " out of " + totalNumberOfWineSamples + "\n");

		// Question 4
		System.out.println("Q4. The best quality wine samples are: ");

		ArrayList<WineSample> bestQualityWineSamples = (ArrayList<WineSample>) wineSampleCellar.bestQualityWine(WineType.ALL);
		for (WineSample sample : bestQualityWineSamples) {
			System.out.println("* Wine ID " + sample.getId() + " of type " + sample.getType() + " with a quality score of " + sample.getQuality());
		}

		// Question 5
		System.out.println("\nQ5. The worst quality wine samples are: ");
		ArrayList<WineSample> worstQualityWineSamples = (ArrayList<WineSample>) wineSampleCellar.worstQualityWine(WineType.ALL);
		for (WineSample sample : worstQualityWineSamples) {
			System.out.println("* Wine ID " + sample.getId() + " of type " + sample.getType() + " with a quality score of " + sample.getQuality());
		}

		// Question 6
		System.out.println("\nQ6. The wine samples that have the highest PH are: ");
		ArrayList<WineSample> highestPHWineSamples = (ArrayList<WineSample>) wineSampleCellar.highestPH(WineType.ALL);
		for (WineSample sample : highestPHWineSamples) {
			System.out.println("* Wine ID " + sample.getId() + " of type " + sample.getType() + " with a PH of " + sample.getpH());
		}

		// Question 7
		System.out.println("\nQ7. The wine samples that have the lowest PH are: ");
		ArrayList<WineSample> lowestPHWineSamples = (ArrayList<WineSample>) wineSampleCellar.lowestPH(WineType.ALL);

		for (WineSample sample : lowestPHWineSamples) {
			System.out.println("* Wine ID " + sample.getId() + " of type " + sample.getType() + " with a PH of " + sample.getpH());
		}

		// Question 8
		System.out.println(
				"\nQ8. The highest value of alcohol grade for the whole sample of red wines is: " + wineSampleCellar.highestAlcoholContent(WineType.RED));

		// Question 9
		System.out
				.println("\nQ9. The lowest value of citric acid for the whole sample of white wines is: " + wineSampleCellar.lowestCitricAcid(WineType.WHITE));

		// Question 10
		System.out.println(
				"\nQ10. The average value of alcohol grade for the whole sample of white wines is: " + wineSampleCellar.averageAlcoholContent(WineType.WHITE));

		// reading the queries from the given file
		ArrayList<String> queriesString = (ArrayList<String>) AbstractWineSampleCellar.readQueryFile(queriesFile);
		ArrayList<Query> queries = (ArrayList<Query>) wineSampleCellar.readQueries(queriesString); // queries as objects

		System.out.println("\n|THE QUERIES|\n");

		for (int i = 1; i <= queries.size(); i++) {
			System.out.print("[* QUERY #" + i + " *]");
			wineSampleCellar.displayQueryResults(queries.get(i - 1));

			System.out.println();
		}
	}
}
