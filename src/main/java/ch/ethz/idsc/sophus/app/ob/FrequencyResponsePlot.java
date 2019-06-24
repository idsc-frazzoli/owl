// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.sophus.math.win.WindowSidedSampler;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.Exp;

/* package */ class FrequencyResponsePlot {
  private Tensor minimizingAlphas;
  private Tensor minimizingKernels;
  private Tensor minimizingFilterlengths;

  FrequencyResponsePlot(Tensor minimizer) {
    this.minimizingAlphas = minimizer.get(0);
    this.minimizingFilterlengths = minimizer.get(1);
    this.minimizingKernels = minimizer.get(2);
  }

  private Tensor process(String string, int signal) {
    Function<Integer, Tensor> windowSideSampler = WindowSidedSampler.of(SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(signal))]);
    Scalar b = minimizingAlphas.Get(signal);
    Tensor a = windowSideSampler.apply(Scalars.intValueExact(minimizingFilterlengths.Get(signal)))
        .multiply(RealScalar.ONE.subtract(minimizingAlphas.Get(signal)));
    // String test = "test";
    Tensor omegaRange = Subdivide.of(0, Math.PI, 200);
    Tensor resultAbs = Tensors.empty();
    Tensor resultPhase = Tensors.empty();
    for (int i = 0; i < omegaRange.length(); i++) {
      Scalar nominator = b;
      Scalar denominator = ComplexScalar.of(RealScalar.ONE, RealScalar.ZERO);
      for (int j = 0; j < Scalars.intValueExact(minimizingFilterlengths.Get(signal)) - 1; j++) {
        denominator = denominator
            .add(a.Get(j).negate().multiply(Exp.FUNCTION.apply(ComplexScalar.I.negate().multiply(RealScalar.of(j)).multiply(omegaRange.Get(i)))));
      }
      Scalar result = nominator.divide(denominator);
      resultAbs.append(result.abs());
      resultPhase.append(Arg.FUNCTION.apply(result));
    }
    if (string == "PhaseResponse")
      return resultPhase;
    return resultAbs;
  }

  public void evaluate() throws IOException {
    for (int i = 0; i < 2; i++) {
      VisualSet visualSet = new VisualSet();
      String ylabel;
      if (i == 0)
        ylabel = "PhaseResponse";
      else
        ylabel = "MagnitudeResponse";
      visualSet.setPlotLabel(ylabel);
      Tensor values_x = process(ylabel, 0);
      Tensor values_a = process(ylabel, 1);
      Tensor values_xdot = process(ylabel, 2);
      Tensor values_adot = process(ylabel, 3);
      Tensor domain = Subdivide.of(0, Math.PI, values_x.length() - 1);
      visualSet.setAxesLabelX("omega [s^-1]");
      if (i == 0)
        visualSet.setAxesLabelY("Phase Delay []");
      else
        visualSet.setAxesLabelY("Magnitude Gain [-]");
      {
        VisualRow visualRow = visualSet.add(domain, values_x);
        visualRow.setLabel("x_" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))] + "_" + minimizingFilterlengths.Get(0));
      }
      {
        VisualRow visualRow = visualSet.add(domain, values_a);
        visualRow.setLabel("a_" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))] + "_" + minimizingFilterlengths.Get(1));
      }
      {
        VisualRow visualRow = visualSet.add(domain, values_xdot);
        visualRow.setLabel("xdot_" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))] + "_" + minimizingFilterlengths.Get(2));
      }
      {
        VisualRow visualRow = visualSet.add(domain, values_adot);
        visualRow.setLabel("adot_" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))] + "_" + minimizingFilterlengths.Get(3));
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      File file = HomeDirectory.Pictures(FrequencyResponsePlot.class.getSimpleName() + "_" + ylabel + "_Test.png");
      ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
    }
  }
}