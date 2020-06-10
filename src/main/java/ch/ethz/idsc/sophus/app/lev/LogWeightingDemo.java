// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.Variograms;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class LogWeightingDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}");
  // ---
  private final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();
  private final SpinnerLabel<PseudoDistances> spinnerPseudoDistances = new SpinnerLabel<>();
  private final SpinnerLabel<Variograms> spinnerVariogram = SpinnerLabel.of(Variograms.values());
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public LogWeightingDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list);
    {
      spinnerLogWeighting.setList(array);
      spinnerLogWeighting.setIndex(0);
      spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(130, 28), "weights");
      spinnerLogWeighting.addSpinnerListener(v -> recompute());
    }
    {
      spinnerPseudoDistances.setArray(PseudoDistances.values());
      spinnerPseudoDistances.setIndex(0);
      spinnerPseudoDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(110, 28), "distance");
      spinnerPseudoDistances.addSpinnerListener(v -> recompute());
    }
    spinnerVariogram.addToComponentReduced(timerFrame.jToolBar, new Dimension(230, 28), "variograms");
    spinnerVariogram.addSpinnerListener(v -> recompute());
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(2);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
      spinnerBeta.addSpinnerListener(v -> recompute());
    }
    timerFrame.jToolBar.addSeparator();
  }

  protected void recompute() {
    // ---
  }

  protected final TensorUnaryOperator weightingOperator(VectorLogManifold vectorLogManifold, Tensor sequence) {
    LogWeighting logWeighting = spinnerLogWeighting.getValue();
    return logWeighting.from( //
        spinnerPseudoDistances.getValue(), //
        vectorLogManifold, //
        variogram(), //
        sequence);
  }

  protected final TensorUnaryOperator operator(Tensor sequence) {
    return weightingOperator(geodesicDisplay().vectorLogManifold(), sequence);
  }

  protected final ScalarUnaryOperator variogram() {
    return spinnerVariogram.getValue().of(spinnerBeta.getValue());
  }

  protected final TensorScalarFunction function(Tensor sequence, Tensor values) {
    LogWeighting logWeighting = spinnerLogWeighting.getValue();
    return logWeighting.build( //
        spinnerPseudoDistances.getValue(), //
        geodesicDisplay().vectorLogManifold(), //
        variogram(), //
        sequence, values);
  }
}
