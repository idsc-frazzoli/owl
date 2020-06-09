// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class LogWeightingDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}");
  // ---
  private final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public LogWeightingDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list);
    {
      spinnerLogWeighting.setList(array);
      spinnerLogWeighting.setIndex(0);
      spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "weights");
      spinnerLogWeighting.addSpinnerListener(v -> recompute());
    }
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

  protected final TensorUnaryOperator operator(Tensor sequence) {
    return spinnerLogWeighting.getValue().from( //
        geodesicDisplay().vectorLogManifold(), //
        variogram(), //
        sequence);
  }

  protected final TensorUnaryOperator weightingOperator(VectorLogManifold vectorLogManifold, Tensor sequence) {
    return spinnerLogWeighting.getValue().from( //
        vectorLogManifold, //
        variogram(), //
        sequence);
  }

  protected final ScalarUnaryOperator variogram() {
    return InversePowerVariogram.of(spinnerBeta.getValue());
  }
}
