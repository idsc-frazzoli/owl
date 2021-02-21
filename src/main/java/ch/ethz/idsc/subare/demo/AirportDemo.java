// code by fluric
package ch.ethz.idsc.subare.demo;

import java.awt.Color;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.subare.analysis.DiscreteModelErrorAnalysis;
import ch.ethz.idsc.subare.core.StateActionCounter;
import ch.ethz.idsc.subare.core.alg.ActionValueIterations;
import ch.ethz.idsc.subare.core.mc.MonteCarloExploringStarts;
import ch.ethz.idsc.subare.core.td.Sarsa;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.subare.core.td.TrueOnlineSarsa;
import ch.ethz.idsc.subare.core.util.ConstantExplorationRate;
import ch.ethz.idsc.subare.core.util.ConstantLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteStateActionCounter;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.ExactFeatureMapper;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.subare.core.util.FeatureMapper;
import ch.ethz.idsc.subare.core.util.FeatureWeight;
import ch.ethz.idsc.subare.core.util.LearningRate;
import ch.ethz.idsc.subare.core.util.PolicyType;
import ch.ethz.idsc.subare.demo.airport.Airport;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.ext.Timing;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/** uses TrueOnlineSarsa */
/* package */ enum AirportDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor XYmc = Tensors.empty();
    Tensor XYsarsa = Tensors.empty();
    Tensor XYtoSarsa = Tensors.empty();
    Airport airport = Airport.INSTANCE;
    DiscreteQsa optimalQsa = ActionValueIterations.solve(airport, RealScalar.of(0.0001));
    // DiscreteUtils.print(optimalQsa);
    // Policies.print(policyQsa, airport.states());
    final int batches = 10;
    MonteCarloExploringStarts mces = new MonteCarloExploringStarts(airport);
    {
      Timing timing = Timing.started();
      for (int index = 0; index < batches; ++index) {
        EGreedyPolicy policy = (EGreedyPolicy) PolicyType.EGREEDY.bestEquiprobable(airport, mces.qsa(), mces.sac());
        policy.setExplorationRate(ConstantExplorationRate.of(0.1));
        ExploringStarts.batch(airport, policy, mces);
        XYmc.append(Tensors.of(RealScalar.of(index), DiscreteModelErrorAnalysis.LINEAR_POLICY.getError(airport, optimalQsa, mces.qsa())));
      }
      System.out.println("time for MonteCarlo: " + timing.seconds() + "s");
      // Policies.print(GreedyPolicy.bestEquiprobable(airport, mces.qsa()), airport.states());
    }
    DiscreteQsa qsaSarsa = DiscreteQsa.build(airport); // q-function for training, initialized to 0
    SarsaType sarsaType = SarsaType.ORIGINAL;
    StateActionCounter sac = new DiscreteStateActionCounter();
    final Sarsa sarsa = sarsaType.sarsa(airport, ConstantLearningRate.of(RealScalar.of(0.05)), qsaSarsa, sac,
        PolicyType.EGREEDY.bestEquiprobable(airport, qsaSarsa, sac));
    {
      Timing timing = Timing.started();
      for (int index = 0; index < batches; ++index) {
        EGreedyPolicy policy = (EGreedyPolicy) PolicyType.EGREEDY.bestEquiprobable(airport, sarsa.qsa(), sarsa.sac());
        policy.setExplorationRate(ConstantExplorationRate.of(0.1));
        ExploringStarts.batch(airport, policy, 1, sarsa);
        XYsarsa.append(Tensors.of(RealScalar.of(index), DiscreteModelErrorAnalysis.LINEAR_POLICY.getError(airport, optimalQsa, sarsa.qsa())));
      }
      System.out.println("time for Sarsa: " + timing.seconds() + "s");
    }
    // Policies.print(GreedyPolicy.bestEquiprobable(airport, sarsa.qsa()), airport.states());
    LearningRate learningRate = ConstantLearningRate.of(RealScalar.of(0.2));
    FeatureMapper mapper = ExactFeatureMapper.of(airport);
    FeatureWeight w = new FeatureWeight(mapper);
    StateActionCounter toSac = new DiscreteStateActionCounter();
    TrueOnlineSarsa toSarsa = SarsaType.ORIGINAL.trueOnline(airport, RealScalar.of(0.7), mapper, learningRate, w, toSac,
        PolicyType.EGREEDY.bestEquiprobable(airport, DiscreteQsa.build(airport), toSac));
    {
      Timing timing = Timing.started();
      for (int index = 0; index < batches; ++index) {
        EGreedyPolicy policy = (EGreedyPolicy) PolicyType.EGREEDY.bestEquiprobable(airport, toSarsa.qsa(), toSarsa.sac());
        policy.setExplorationRate(ConstantExplorationRate.of(0.1));
        ExploringStarts.batch(airport, policy, toSarsa);
        DiscreteQsa toQsa = toSarsa.qsa();
        XYtoSarsa.append(Tensors.of(RealScalar.of(index), DiscreteModelErrorAnalysis.LINEAR_POLICY.getError(airport, optimalQsa, toQsa)));
      }
      System.out.println("time for TrueOnlineSarsa: " + timing.seconds() + "s");
    }
    {
      VisualSet visualSet = new VisualSet();
      visualSet.add(XYmc).setLabel("MonteCarlo");
      visualSet.add(XYsarsa).setLabel("Sarsa");
      visualSet.add(XYtoSarsa).setLabel("TrueOnlineSarsa");
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      ChartUtils.saveChartAsPNG( //
          HomeDirectory.Pictures(AirportDemo.class.getSimpleName() + ".png"), jFreeChart, 1280, 720);
    }
    // DiscreteQsa toQsa = toSarsa.qsa();
    // System.out.println(toSarsa.getW());
    // toSarsa.printValues();
    // toSarsa.printPolicy();
  }
}
