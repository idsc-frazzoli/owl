// code by fluric
package ch.ethz.idsc.subare.demo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.subare.analysis.DiscreteModelErrorAnalysis;
import ch.ethz.idsc.subare.analysis.MonteCarloAlgorithms;
import ch.ethz.idsc.subare.analysis.MonteCarloExamples;
import ch.ethz.idsc.subare.core.MonteCarloInterface;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import junit.framework.TestCase;

public class MonteCarloAlgorithmsTest extends TestCase {
  public void testExamplesWithSarsa() {
    checkExampleWithSarsa(MonteCarloExamples.AIRPORT, true);
    checkExampleWithSarsa(MonteCarloExamples.CLIFFWALK, false);
    checkExampleWithSarsa(MonteCarloExamples.GAMBLER_20, true);
    checkExampleWithSarsa(MonteCarloExamples.GRIDWORLD, true);
    checkExampleWithSarsa(MonteCarloExamples.INFINITEVARIANCE, true);
    checkExampleWithSarsa(MonteCarloExamples.MAXBIAS, true);
    checkExampleWithSarsa(MonteCarloExamples.MAZE2, false);
    checkExampleWithSarsa(MonteCarloExamples.RACETRACK, false);
    // checkExampleWithSarsa(MonteCarloExamples.WINDYGRID, false); // too slow
    checkExampleWithSarsa(MonteCarloExamples.WIRELOOP_4, false);
    checkExampleWithSarsa(MonteCarloExamples.WIRELOOP_C, false);
  }

  private static void checkExampleWithSarsa(MonteCarloExamples example, boolean withTrueOnline) {
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysis.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.EXPECTED_SARSA);
    list.add(MonteCarloAlgorithms.QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    if (withTrueOnline) {
      list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
      list.add(MonteCarloAlgorithms.EXPECTED_TRUE_ONLINE_SARSA);
      list.add(MonteCarloAlgorithms.QLEARNING_TRUE_ONLINE_SARSA);
    }
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }

  public void testExamplesWithSeveralTrials() {
    MonteCarloExamples example = MonteCarloExamples.AIRPORT;
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysis.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list) {
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 10);
    }
  }

  public void testExamplesWithSeveralErrorAnalysis() {
    MonteCarloExamples example = MonteCarloExamples.AIRPORT;
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysis.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_QSA);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 2);
  }

  public void testExamplesWithMC() {
    checkExampleWithMC(MonteCarloExamples.AIRPORT);
    checkExampleWithMC(MonteCarloExamples.GAMBLER_20);
    checkExampleWithMC(MonteCarloExamples.INFINITEVARIANCE);
    checkExampleWithMC(MonteCarloExamples.MAXBIAS);
    checkExampleWithMC(MonteCarloExamples.MAZE2);
    checkExampleWithMC(MonteCarloExamples.RACETRACK);
  }

  private static void checkExampleWithMC(MonteCarloExamples example) {
    System.out.println("Testing: " + example.toString());
    int batches = 10;
    DiscreteQsa optimalQsa = MonteCarloAnalysis.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }

  public void testVirtualStationExample() {
    MonteCarloExamples example = MonteCarloExamples.VIRTUALSTATIONS;
    System.out.println("Testing: " + example.toString());
    int batches = 1;
    DiscreteQsa optimalQsa = MonteCarloAnalysis.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    list.add(MonteCarloAlgorithms.EXPECTED_SARSA);
    list.add(MonteCarloAlgorithms.QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }
}