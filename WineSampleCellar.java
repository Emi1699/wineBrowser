package assignment2019;

import java.util.ArrayList;
import java.util.List;

import assignment2019.codeprovided.AbstractWineSampleCellar;
import assignment2019.codeprovided.Query;
import assignment2019.codeprovided.QueryCondition;
import assignment2019.codeprovided.WineProperty;
import assignment2019.codeprovided.WineSample;
import assignment2019.codeprovided.WineType;

/**
 * WineSampleCellar.java
 *
 * Class that provides ways of creating Query objects from file + methods for
 * the 10 question.
 * 
 * @version 1.0 08/05/2019
 *
 * @author Buliga Fanel Emanuel (febuliga1@sheffield.ac.uk)
 */

public class WineSampleCellar extends AbstractWineSampleCellar {

	public WineSampleCellar(String redWineFilename, String whiteWineFilename, String queryFilename) {
		super(redWineFilename, whiteWineFilename, queryFilename);
		// TODO Auto-generated constructor stub
	}

	@Override
	// this methods basically constructs each individual query from the list of
	// words we have built from the text file.
	// we're assuming that each word is separated by a space.
	// the only thing that is being checked is whether
	// the operator and value are separated by a space or not
	public List<Query> readQueries(List<String> queryList) {
		// the list of complete Queries (----> return value)
		ArrayList<Query> queries = new ArrayList<>();

		// the 3 objects needed to create a Query
		ArrayList<QueryCondition> conditions = new ArrayList<>();
		ArrayList<WineSample> wineList = new ArrayList<>();
		WineType wineType = null;

		// the 3 variables used to build a QueryCondition
		WineProperty property = null;
		String operator = null;
		double value = 0;

		for (int i = 0; i < queryList.size(); i++) {
			if (queryList.get(i).equalsIgnoreCase("select")) {
				// this is where we set the WineType, and the WineList which our query is going
				// to use
				if (queryList.get(i + 1).equalsIgnoreCase("red") || queryList.get(i + 1).equalsIgnoreCase("white")) {
					if (queryList.get(i + 3).equalsIgnoreCase("red") || queryList.get(i + 3).equalsIgnoreCase("white")) {
						wineType = WineType.ALL;
						wineList = (ArrayList<WineSample>) getWineSampleList(wineType);
						i += 5;
					} else {
						if (queryList.get(i + 1).equalsIgnoreCase("red")) {
							wineType = WineType.RED;
							wineList = (ArrayList<WineSample>) getWineSampleList(wineType);
							i += 3;
						} else {
							wineType = WineType.WHITE;
							wineList = (ArrayList<WineSample>) getWineSampleList(wineType);
							i += 3;
						}
					}
				}

				// this variable is true while there still are properties in the current query
				boolean stillInTheSameQuery = true;

				// loops until it finds the word 'select' again
				while (stillInTheSameQuery) {
					// depending on whether the operator and value are separated by a space
					// this variable helps us know where to look for the next
					// word in the current query
					boolean addOneMore = true;

					// constructing a QueryCondition for current Query's list of conditions
					property = WineProperty.fromFileIdentifier(queryList.get(i));
					operator = queryList.get(i + 1);

					// Here we check if the operator and the value are separated by a space;
					// if they are not separated, we have to be careful when extracting them.
					if (operator.length() > 2) {
						if (Character.isDigit(operator.charAt(1))) {
							value = Double.parseDouble(operator.substring(1));
							operator = Character.toString(operator.charAt(0));
						} else {
							value = Double.parseDouble((operator.substring(2)));
							operator = operator.substring(0, 2);
						}
						addOneMore = false;
					} else {
						if (operator.length() == 2) {
							if (Character.isDigit(operator.charAt(1))) {
								value = Double.parseDouble(operator.substring(1));
								operator = Character.toString(operator.charAt(0));
								addOneMore = false;
							} else {
								value = Double.parseDouble(queryList.get(i + 2));
							}
						} else {
							value = Double.parseDouble(queryList.get(i + 2));
						}
					}

					// build a new QueryCondition with the previously built objects
					QueryCondition condition = new QueryCondition(property, operator, value);
					conditions.add(condition);

					// check if there are more property-operator-value triplets,
					// or if we've hit the end of the String list
					if (addOneMore) {
						if ((i + 3) >= queryList.size() || queryList.get(i + 3).equalsIgnoreCase("select")) {
							stillInTheSameQuery = false;
						} else {
							i += 4;
						}
					} else {
						if ((i + 2) >= queryList.size() || queryList.get(i + 2).equals("select")) {
							stillInTheSameQuery = false;
						} else {
							i += 3;
						}
					}
				}
				// create a new Query with the values we just calculated and add it to the list
				Query q = new Query(wineList, conditions, wineType);
				queries.add(q);

				// At first it might seem that the program would run just fine without this line.
				// One might even be tempted to ask 'Hey, what is this line of code doing here?'
				// The reason is straightforward and I'll try to explain it as simple as possible:
				// the program doesn't work without this line and nobody knows why ._.
				conditions = new ArrayList<>();
			}
		}

		return queries;
	}

	@Override
	// create a new wine list that contains both red and white samples
	public void updateCellar() {
		ArrayList<WineSample> allWineSamples = new ArrayList<>();
		allWineSamples.addAll(getWineSampleList(WineType.RED));
		allWineSamples.addAll(getWineSampleList(WineType.WHITE));
		wineSampleRacks.put(WineType.ALL, allWineSamples);
	}

	@Override
	public void displayQueryResults(Query query) {
		ArrayList<WineSample> solvedList = (ArrayList<WineSample>) query.solveQuery();
		System.out.println(" select " + query.getWineType() + " where " + query.getQueryConditionList());

		// not necessary, but it looks WAY better with a bit with good grammar
		switch (query.getWineType()) {
		case ALL: {
			if (solvedList.size() > 1) {
				System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " red and white wine samples match your query");
				System.out.println(" -/ the list of those samples is: ");
			} else {
				if (solvedList.size() == 1) {
					System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " red/white wine sample matches your query");
					System.out.println(" -/ that sample is: ");
				} else {
					System.out.println(" -/ there are no wine samples that match your query");
				}
			}
		}
			break;
		case RED: {
			if (solvedList.size() > 1) {
				System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " red wine samples match your query");
				System.out.println(" -/ the list of those samples is: ");
			} else {
				if (solvedList.size() == 1) {
					System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " red wine sample matches your query");
					System.out.println(" -/ that sample is: ");
				} else {
					System.out.println(" -/ there are no wine samples that match your query");
				}
			}
		}
			break;
		case WHITE: {
			if (solvedList.size() > 1) {
				System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " white wine samples match your query");
				System.out.println(" -/ the list of those samples is: ");
			} else {
				if (solvedList.size() == 1) {
					System.out.println(" -/ in total, " + "*" + solvedList.size() + "*" + " white wine sample matches your query");
					System.out.println(" -/ that sample is: ");
				} else {
					System.out.println(" -/ there are no wine samples that match your query");
				}
			}
		}
			break;
		}

		for (WineSample s : solvedList) {
			System.out.println("    -> [" + query.getWineType().toString().toLowerCase() + " wine, sample #" + s.getId() + ", f_acid: " + s.getFixedAcidity()
					+ ", v_acid: " + s.getVolatileAcidity() + ", c_acid: " + s.getCitricAcid() + ", r_sugar: " + s.getResidualSugar() + ", chlorid: "
					+ s.getChlorides() + ", f_sulf: " + s.getFreeSulfurDioxide() + ", t_sulf: " + s.getTotalSulfurDioxide() + ", dens: " + s.getDensity()
					+ ", pH: " + s.getpH() + ", sulph: " + s.getSulphates() + ", alc: " + s.getAlcohol() + ", qual: " + s.getQuality() + "]");
		}
	}

	@Override
	// Go through the entire wine list of the specified type
	// and try to find the samples with the best quality.
	public List<WineSample> bestQualityWine(WineType wineType) {
		// using an ArrayList as there may be more wine samples that have the same
		// *best* quality
		ArrayList<WineSample> bestQualityWineSamples = new ArrayList<>();
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		double bestQuality = 0;

		// If a wine sample has a greater quality than previous wine samples,
		// then we erase all the previous samples from the list containing all the
		// best samples until that moment
		// and add the current sample to the list (which is of better quality than those
		// we just erased).
		// If the quality of the sample is the same as that of the previous samples, we
		// simply add it to the list.
		for (WineSample sample : samples) {
			if (sample.getQuality() > bestQuality) {
				bestQuality = sample.getQuality();
				bestQualityWineSamples.clear();
				bestQualityWineSamples.add(sample);
			} else {
				if (sample.getQuality() == bestQuality) {
					bestQualityWineSamples.add(sample);
				}
			}
		}

		return bestQualityWineSamples;
	}

	@Override
	public List<WineSample> worstQualityWine(WineType wineType) {
		ArrayList<WineSample> worstQualityWineSamples = new ArrayList<>();
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		double worstQuality = 10;

		// this method is basically the same as the one above it
		// the only difference is that now we're looking for the lowest quality samples
		for (WineSample sample : samples) {
			if (sample.getQuality() < worstQuality) {
				worstQuality = sample.getQuality();
				worstQualityWineSamples.clear();
				worstQualityWineSamples.add(sample);
			} else {
				if (sample.getQuality() == worstQuality) {
					worstQualityWineSamples.add(sample);
				}
			}
		}

		return worstQualityWineSamples;
	}

	@Override
	public List<WineSample> highestPH(WineType wineType) {
		ArrayList<WineSample> highestPHWineSamples = new ArrayList<>();
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		double highestPH = -100;

		for (WineSample sample : samples) {
			if (sample.getpH() > highestPH) {
				highestPH = sample.getpH();
				highestPHWineSamples.clear();
				highestPHWineSamples.add(sample);
			} else {
				if (sample.getpH() == highestPH) {
					highestPHWineSamples.add(sample);
				}
			}
		}

		return highestPHWineSamples;
	}

	@Override
	public List<WineSample> lowestPH(WineType wineType) {
		ArrayList<WineSample> lowestPHWineSamples = new ArrayList<>();
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		double lowestPH = 100;

		for (WineSample sample : samples) {
			if (sample.getpH() < lowestPH) {
				lowestPH = sample.getpH();
				lowestPHWineSamples.clear();
				lowestPHWineSamples.add(sample);
			} else {
				if (sample.getpH() == lowestPH) {
					lowestPHWineSamples.add(sample);
				}
			}
		}

		return lowestPHWineSamples;
	}

	@Override
	public double highestAlcoholContent(WineType wineType) {
		double highestAlcoholGrade = -1;
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		for (WineSample sample : samples) {
			if (sample.getAlcohol() > highestAlcoholGrade) {
				highestAlcoholGrade = sample.getAlcohol();
			}
		}

		return highestAlcoholGrade;
	}

	@Override
	public double lowestCitricAcid(WineType wineType) {
		double lowestCitricAcidValue = 100;
		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		for (WineSample sample : samples) {
			if (sample.getCitricAcid() < lowestCitricAcidValue) {
				lowestCitricAcidValue = sample.getCitricAcid();
			}
		}

		return lowestCitricAcidValue;
	}

	@Override
	public double averageAlcoholContent(WineType wineType) {
		double averageAlcoholContent = 0;

		ArrayList<WineSample> samples = (ArrayList<WineSample>) getWineSampleList(wineType);
		for (WineSample sample : samples) {
			averageAlcoholContent += sample.getAlcohol();
		}

		averageAlcoholContent = averageAlcoholContent / samples.size();

		return averageAlcoholContent;
	}

}
