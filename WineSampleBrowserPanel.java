package assignment2019;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;

import assignment2019.codeprovided.*;

/**
 * WineSampleBrowserPanel.java
 *
 * Class that provides ways of interacting with the GUI.
 *
 * @version 1.0 08/05/2019
 *
 * @author Buliga Fanel Emanuel (febuliga1@sheffield.ac.uk)
 */

public class WineSampleBrowserPanel extends AbstractWineSampleBrowserPanel {
	private static final long serialVersionUID = 1L;

	// constructor
	public WineSampleBrowserPanel(AbstractWineSampleCellar cellar) {
		// the GUI starts by displaying ALL wine samples (no filters)
		super(cellar);
		updateGUI();
	}

	// when the 'addFilter' button is clicked
	// a new QueryCondition is created (based on the values the user has chosen)
	private class addFilterEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			addFilter();
		}
	}

	// whenever the 'clearAllFilters' button is pressed
	// the queryConditionList will be cleared
	private class clearAllFiltersEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearFilters();
		}
	}

	// changes the 'wineType' variable based on the given String
	private void wineTypeFromString(String wineTypeString) {
		switch (wineTypeString) {
		case "ALL":
			wineType = WineType.ALL;
			break;
		case "WHITE":
			wineType = WineType.WHITE;
			break;
		case "RED":
			wineType = WineType.RED;
			break;
		}
	}

	// change the wineType when the relevant item in the GUI is selected
	// check whether there are any filters first, then proceed accordingly
	private class comboWineTypesEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			wineTypeFromString(comboWineTypes.getSelectedItem().toString());
			if (queryConditionList.size() > 0) {
				filteredWineSampleList = cellar.getWineSampleList(wineType);
				executeQuery();
			} else {
				filteredWineSampleList = cellar.getWineSampleList(wineType);
			}
			updateGUI();
		}
	}

	@Override
	public void addListeners() {
		buttonAddFilter.addActionListener(new addFilterEventHandler());
		buttonClearFilters.addActionListener(new clearAllFiltersEventHandler());
		comboWineTypes.addActionListener(new comboWineTypesEventHandler());
	}

	@Override
	// Only works if the value inputed by the user is a number
	public void addFilter() {
		// retrieve the wine's chosen property and return it as a String
		String wineProperty = comboProperties.getSelectedItem().toString();

		// the three variables used to construct a QueryCondition
		WineProperty property = null;
		String operator = comboOperators.getSelectedItem().toString();
		double valueProperty = 0;

		switch (wineProperty) {
		case "Fixed Acidity":
			property = WineProperty.FixedAcidity;
			break;
		case "Volatile Acidity":
			property = WineProperty.VolatileAcidity;
			break;
		case "Citric Acidity":
			property = WineProperty.CitricAcid;
			break;
		case "Residual Sugar":
			property = WineProperty.ResidualSugar;
			break;
		case "Chlorides":
			property = WineProperty.Chlorides;
			break;
		case "Free Sulfur Dioxide":
			property = WineProperty.FreeSulfurDioxide;
			break;
		case "Total Sulfur Dioxide":
			property = WineProperty.TotalSulfurDioxide;
			break;
		case "Density":
			property = WineProperty.Density;
			break;
		case "pH":
			property = WineProperty.PH;
			break;
		case "Sulphates":
			property = WineProperty.Sulphates;
			break;
		case "Alcohol":
			property = WineProperty.Alcohol;
			break;
		case "Quality":
			property = WineProperty.Quality;
			break;
		}

		// check if the value inputed is valid (non-empty field and real number)
		if (!(value.getText().equals(""))) {
			boolean rightFormat = true;

			for (int i = 0; i < value.getText().length(); i++) {
				if (!(Character.isDigit(value.getText().charAt(i)) || value.getText().charAt(i) == '.')) {
					rightFormat = false;
					break;
				}
			}

			if (rightFormat) {
				valueProperty = Double.parseDouble(value.getText());

				QueryCondition condition = new QueryCondition(property, operator, valueProperty);

				// here we check if the newly created condition is already in the list if it
				// isn't, we can add it to the list
				// the reason for this is that duplicate conditions are useless +
				// they overcrowd the queryConditions text area
				boolean alreadyExists = false;

				if (queryConditionList.size() > 0) {
					for (QueryCondition c : queryConditionList) {
						if (c.getOperator().equals(condition.getOperator()) && c.getValue() == condition.getValue()
								&& c.getWineProperty().equals(condition.getWineProperty())) {
							alreadyExists = true;
							break;
						}
					}
				}

				if (!alreadyExists) {
					queryConditionList.add(condition);
					queryConditionsTextArea.insert(condition.getWineProperty() + " " + condition.getOperator() + " " + condition.getValue() + "  |   ", 0);
					executeQuery();
				}

				updateGUI();
			}
		}
	}

	@Override
	public void clearFilters() {
		wineTypeFromString(comboWineTypes.getSelectedItem().toString());
		filteredWineSampleList = cellar.getWineSampleList(wineType);
		queryConditionList.clear();
		queryConditionsTextArea.setText(null);
		updateGUI();
	}

	// quick, dirty class that helps with dividing duties and communication between
	// methods
	class statisticalValues {
		private double maxFA, maxVA, maxCA, maxRS, maxC, maxFSD, maxTSD, maxD, maxP, maxS, maxA, maxQ = Integer.MIN_VALUE;
		private double minFA, minVA, minCA, minRS, minC, minFSD, minTSD, minD, minP, minS, minA, minQ = Integer.MAX_VALUE;
		private double avgFA, avgVA, avgCA, avgRS, avgC, avgFSD, avgTSD, avgD, avgP, avgS, avgA, avgQ = 0;
	}

	// finds the data we need to display in statisticTextArea
	public statisticalValues findStatisticalValues() {
		statisticalValues values = new statisticalValues();

		// find all the max values
		values.maxFA = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getFixedAcidity)).getFixedAcidity();
		values.maxVA = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getVolatileAcidity)).getVolatileAcidity();
		values.maxCA = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getCitricAcid)).getCitricAcid();
		values.maxRS = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getResidualSugar)).getResidualSugar();
		values.maxC = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getChlorides)).getChlorides();
		values.maxFSD = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getFreeSulfurDioxide)).getFreeSulfurDioxide();
		values.maxTSD = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getTotalSulfurDioxide)).getTotalSulfurDioxide();
		values.maxD = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getDensity)).getDensity();
		values.maxP = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getpH)).getpH();
		values.maxS = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getSulphates)).getSulphates();
		values.maxA = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getAlcohol)).getAlcohol();
		values.maxQ = Collections.max(filteredWineSampleList, Comparator.comparing(WineSample::getQuality)).getQuality();

		// find all the minimum values
		values.minFA = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getFixedAcidity)).getFixedAcidity();
		values.minVA = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getVolatileAcidity)).getVolatileAcidity();
		values.minCA = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getCitricAcid)).getCitricAcid();
		values.minRS = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getResidualSugar)).getResidualSugar();
		values.minC = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getChlorides)).getChlorides();
		values.minFSD = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getFreeSulfurDioxide)).getFreeSulfurDioxide();
		values.minTSD = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getTotalSulfurDioxide)).getTotalSulfurDioxide();
		values.minD = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getDensity)).getDensity();
		values.minP = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getpH)).getpH();
		values.minS = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getSulphates)).getSulphates();
		values.minA = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getAlcohol)).getAlcohol();
		values.minQ = Collections.min(filteredWineSampleList, Comparator.comparing(WineSample::getQuality)).getQuality();

		// find the average values
		values.avgFA = filteredWineSampleList.stream().mapToDouble(WineSample::getFixedAcidity).average().getAsDouble();
		values.avgVA = filteredWineSampleList.stream().mapToDouble(WineSample::getVolatileAcidity).average().getAsDouble();
		values.avgCA = filteredWineSampleList.stream().mapToDouble(WineSample::getCitricAcid).average().getAsDouble();
		values.avgRS = filteredWineSampleList.stream().mapToDouble(WineSample::getResidualSugar).average().getAsDouble();
		values.avgC = filteredWineSampleList.stream().mapToDouble(WineSample::getChlorides).average().getAsDouble();
		values.avgFSD = filteredWineSampleList.stream().mapToDouble(WineSample::getFreeSulfurDioxide).average().getAsDouble();
		values.avgTSD = filteredWineSampleList.stream().mapToDouble(WineSample::getTotalSulfurDioxide).average().getAsDouble();
		values.avgD = filteredWineSampleList.stream().mapToDouble(WineSample::getDensity).average().getAsDouble();
		values.avgP = filteredWineSampleList.stream().mapToDouble(WineSample::getpH).average().getAsDouble();
		values.avgS = filteredWineSampleList.stream().mapToDouble(WineSample::getSulphates).average().getAsDouble();
		values.avgA = filteredWineSampleList.stream().mapToDouble(WineSample::getAlcohol).average().getAsDouble();
		values.avgQ = filteredWineSampleList.stream().mapToDouble(WineSample::getQuality).average().getAsDouble();

		return values;
	}

	@Override
	// I found out about the .append method after I finished this method
	// this is why updateStatistics() and updateWineList() have different
	// implementations
	public void updateStatistics() {
		statisticsTextArea.setText(null);
		statisticsTextArea.insert("\n\nShowing " + filteredWineSampleList.size() + " out of " + cellar.getWineSampleList(WineType.ALL).size(), 0);

		// the if statement protects against cases when the filteredWineSampleList is
		// empty (i.e. when the ClearFilters button is pressed)
		if (filteredWineSampleList.size() > 0) {
			statisticalValues values = findStatisticalValues();
			DecimalFormat df = new DecimalFormat("####0.000"); // round values to 3 decimal places

			// insert average values
			statisticsTextArea.insert("             " + df.format(values.avgFA) + "                 " + df.format(values.avgVA) + "             "
					+ df.format(values.avgCA) + "              " + df.format(values.avgRS) + "                " + df.format(values.avgC) + "                  "
					+ df.format(values.avgFSD) + "                  " + df.format(values.avgTSD) + "                " + df.format(values.avgD)
					+ "                " + df.format(values.avgP) + "                  " + df.format(values.avgS) + "               " + df.format(values.avgA)
					+ "             " + df.format(values.avgQ) + "               ", 0);
			statisticsTextArea.insert("\n\nAverage:", 0);

			// insert minimum values
			statisticsTextArea.insert("                    " + df.format(values.minFA) + "                 " + df.format(values.minVA) + "             "
					+ df.format(values.minCA) + "              " + df.format(values.minRS) + "                " + df.format(values.minC) + "                  "
					+ df.format(values.minFSD) + "                     " + df.format(values.minTSD) + "                  " + df.format(values.minD)
					+ "                " + df.format(values.minP) + "                  " + df.format(values.minS) + "                  "
					+ df.format(values.minA) + "             " + df.format(values.minQ) + "               ", 0);
			statisticsTextArea.insert("\n\nMin:", 0);

			// insert maximum values
			statisticsTextArea.insert("                   " + df.format(values.maxFA) + "               " + df.format(values.maxVA) + "             "
					+ df.format(values.maxCA) + "              " + df.format(values.maxRS) + "               " + df.format(values.maxC) + "                  "
					+ df.format(values.maxFSD) + "                " + df.format(values.maxTSD) + "               " + df.format(values.maxD) + "                "
					+ df.format(values.maxP) + "                  " + df.format(values.maxS) + "               " + df.format(values.maxA) + "             "
					+ df.format(values.maxQ) + "               ", 0);
			statisticsTextArea.insert("\n\n\nMax:", 0);

			// insert properties
			statisticsTextArea.insert("\n                          Acidity               Acidity            Acidity            Sugar                           "
					+ "               Dioxide                  Dioxide            ", 0);
			statisticsTextArea.insert("                          Fixed                 Volatile            Citric            Residual            Chlorides     "
					+ "       Free Sulfur   " + "         Total Sulfur            Density                pH                Sulphates"
					+ "            Alcohol            Quality            ", 0);
		}
	}

	@Override
	public void updateWineList() {
		// clear the textArea before displaying something new
		filteredWineSamplesTextArea.setText(null);
		// final String SPACE_SDF = " ";

		filteredWineSamplesTextArea.insert("\n                                                     Acidity                Acidity        "
				+ "        Acidity                 Sugar                           "
				+ "                           Dioxide                     Dioxide            \n\n", 0);
		filteredWineSamplesTextArea.insert("WINE TYPE                 ID                 Fixed                 Volatile           "
				+ "      Citric                 Residual                 Chlorides                 "
				+ "Free Sulfur                 Total Sulfur                 Density                 pH                 Sulphates"
				+ "                 Alcohol                       Quality                 ", 0);

		// WARNING: Magic numbers ahead.
		// In my defense, given the scope and requirements of the project, I think these
		// specific numbers are harmless, as they are only used to format some text.

		// display each sample's properties in a nice, formatted way
		for (WineSample s : filteredWineSampleList) {
			filteredWineSamplesTextArea.append(wineType.toString() + "  ".repeat(16 - wineType.toString().length()));
			filteredWineSamplesTextArea.append(s.getId() + "  ".repeat(14 - Double.toString(s.getId()).length()));
			filteredWineSamplesTextArea.append(s.getFixedAcidity() + "  ".repeat(13 - Double.toString(s.getFixedAcidity()).length()));
			filteredWineSamplesTextArea.append(s.getVolatileAcidity() + "  ".repeat(15 - Double.toString(s.getVolatileAcidity()).length()));
			filteredWineSamplesTextArea.append(s.getCitricAcid() + "  ".repeat(15 - Double.toString(s.getCitricAcid()).length()));
			filteredWineSamplesTextArea.append(s.getResidualSugar() + "  ".repeat(15 - Double.toString(s.getResidualSugar()).length()));
			filteredWineSamplesTextArea.append(s.getChlorides() + "  ".repeat(17 - Double.toString(s.getChlorides()).length()));
			filteredWineSamplesTextArea.append(s.getFreeSulfurDioxide() + "  ".repeat(18 - Double.toString(s.getFreeSulfurDioxide()).length()));
			filteredWineSamplesTextArea.append(s.getTotalSulfurDioxide() + "  ".repeat(17 - Double.toString(s.getTotalSulfurDioxide()).length()));
			filteredWineSamplesTextArea.append(s.getDensity() + "  ".repeat(13 - Double.toString(s.getDensity()).length()));
			filteredWineSamplesTextArea.append(s.getpH() + "  ".repeat(15 - Double.toString(s.getpH()).length()));
			filteredWineSamplesTextArea.append(s.getSulphates() + "  ".repeat(15 - Double.toString(s.getSulphates()).length()));
			filteredWineSamplesTextArea.append(s.getAlcohol() + "  ".repeat(18 - Double.toString(s.getAlcohol()).length()));
			filteredWineSamplesTextArea.append(s.getQuality() + "  ".repeat(15 - Double.toString(s.getQuality()).length()) + "\n");
		}
	}

	// updates both the wineList text area, and the statistics area
	public void updateGUI() {
		updateStatistics();
		updateWineList();
	}

	@Override
	public void executeQuery() {
		Query query = new Query(filteredWineSampleList, queryConditionList, wineType);
		filteredWineSampleList = query.solveQuery();
	}
}
