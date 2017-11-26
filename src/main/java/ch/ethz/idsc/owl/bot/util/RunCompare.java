// code by jl
package ch.ethz.idsc.owl.bot.util;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public class RunCompare {
  private Tensor currentRuntimes;
  private Tensor currentIterations;
  private Tensor currentCosts = Tensors.vector(0);
  private final int numberOfPlanners;
  private List<String> lines = new ArrayList<>();
  private Stopwatch stopwatch;
  private int currentPlannerID = -1;

  public RunCompare(int numberOfPlanners) {
    this.numberOfPlanners = numberOfPlanners;
    String reference = "timeReference, iterationsReference, CostReference";
    currentRuntimes = Tensors.vector(0);
    String firstLine = "";
    for (int i = 1; i < numberOfPlanners; i++) {
      firstLine = String.join(",", firstLine, //
          String.join("", "absolute Time Difference to Ref of run ", Integer.toString(i + 1)), //
          String.join("", "relative Time Difference to Ref of run ", Integer.toString(i + 1)), //
          String.join("", "absolut iteration Difference to Ref of run ", Integer.toString(i + 1)), //
          String.join("", "relative iteration Difference to Ref of run", Integer.toString(i + 1)), //
          String.join("", "cost Difference to Ref of run ", Integer.toString(i + 1)), //
          String.join("", "relative cost Difference to Ref of run ", Integer.toString(i + 1)));
      currentRuntimes.append(RealScalar.ZERO);
    }
    newRuns();
    lines.add(String.join("", reference, firstLine));
  }

  /** Resets the recorded data to -1 */
  public void newRuns() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < numberOfPlanners; i++)
      list.add(-1);
    // currentRuntimes = Tensors.vector(list);
    currentIterations = Tensors.vector(list);
    currentCosts = Tensors.vector(list);
  }

  /** starts the stopwatch for planner with number:
   * @param plannerID */
  public void startStopwatchFor(int plannerID) {
    if (plannerID > numberOfPlanners || plannerID < 0)
      throw new RuntimeException();
    currentPlannerID = plannerID;
    stopwatch = Stopwatch.started();
  }

  /** Stops and records the data from the stopwatch for planner with number:
   * @param plannerID
   * @return the recorded time */
  public double stopStopwatchFor(int plannerID) {
    if (plannerID != currentPlannerID || plannerID < 0)
      throw new RuntimeException();
    stopwatch.stop();
    currentPlannerID = -1;
    currentRuntimes.set(RealScalar.of(stopwatch.display_seconds()), plannerID);
    return stopwatch.display_seconds();
  }

  public double pauseStopwatchFor(int plannerID) {
    if (plannerID != currentPlannerID || plannerID < 0)
      throw new RuntimeException();
    stopwatch.stop();
    currentRuntimes.set(currentRuntimes.Get(plannerID).add(RealScalar.of(stopwatch.display_seconds())), plannerID);
    return stopwatch.display_seconds();
  }

  /** Saves the number of iterations for planner with the right ID
   * @param iterations
   * @param plannerID */
  public void saveIterations(int iterations, int plannerID) {
    if (plannerID > numberOfPlanners)
      throw new RuntimeException();
    currentIterations.set(RealScalar.of(iterations), plannerID);
  }

  /** Saves the cost for planner with the right ID
   * @param cost
   * @param plannerID */
  public void saveCost(Scalar cost, int plannerID) {
    if (plannerID > numberOfPlanners)
      throw new RuntimeException();
    currentCosts.set(N.DOUBLE.of(cost), plannerID);
  }

  /** writes the saved data in lines for later use,
   * Needs to be called after each compare run (different planners solving the same issue) */
  public void write2lines() {
    for (int i = 0; i < numberOfPlanners; i++) {
      if (Scalars.lessThan(currentRuntimes.Get(i), RealScalar.ZERO))
        throw new RuntimeException();
      if (Scalars.lessThan(currentIterations.Get(i), RealScalar.ZERO))
        throw new RuntimeException();
    }
    String referenceData = String.join(",", //
        currentRuntimes.Get(0).toString(), //
        currentIterations.Get(0).toString(), //
        currentCosts.Get(0).toString());
    String compareData = "";
    Tensor currentRunTimeDiff = Tensor.of(currentRuntimes.extract(1, numberOfPlanners).stream()//
        .map(Scalar.class::cast).map(s -> s.subtract(currentRuntimes.Get(0))));
    Tensor currentRunTimeRelative = Tensor.of(currentRunTimeDiff.stream()//
        .map(Scalar.class::cast).map(s -> s.divide(currentRuntimes.Get(0))));
    Tensor currentIterationsDiff = Tensor.of(currentIterations.extract(1, numberOfPlanners).stream()//
        .map(Scalar.class::cast).map(s -> s.subtract(currentIterations.Get(0))));
    Tensor currentIterationsRelative = N.DOUBLE.of(Tensor.of(currentIterationsDiff.stream()//
        .map(Scalar.class::cast).map(s -> s.divide(currentIterations.Get(0)))));
    Tensor currentCostDiff = Tensor.of(currentCosts.extract(1, numberOfPlanners).stream()//
        .map(Scalar.class::cast).map(s -> s.subtract(currentCosts.Get(0))));
    Tensor currentCostRelative = Tensor.of(currentCostDiff.stream()//
        .map(Scalar.class::cast).map(s -> s.divide(currentCosts.Get(0))));
    for (int i = 0; i < numberOfPlanners - 1; i++)
      compareData = String.join(",", compareData, //
          currentRunTimeDiff.Get(i).toString(), //
          currentRunTimeRelative.Get(i).toString(), //
          currentIterationsDiff.Get(i).toString(), //
          currentIterationsRelative.Get(i).toString(), //
          currentCostDiff.Get(i).toString(), //
          currentCostRelative.Get(i).toString());
    lines.add(String.join("", referenceData, compareData));
    newRuns();
  }

  public void printcurrent() {
    System.out.println(" Current Times: " + currentRuntimes);
    System.out.println("Current Iterations: " + currentIterations);
  }

  /** writes the resulting data in a .csv file
   * @throws Exception */
  public void write2File(String string) throws Exception {
    Path path = UserHome.file(string + ".csv").toPath();
    Files.write(path, lines, Charset.forName("UTF-8"));
  }
}
