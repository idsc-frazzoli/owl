// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ abstract class LogWeightingDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}");
  // ---
  final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();
  final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public LogWeightingDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list) {
    super(addRemoveControlPoints, list);
    {
      spinnerLogWeighting.setList(LogWeightings.list());
      spinnerLogWeighting.setIndex(0);
      spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "weights");
    }
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(2);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    timerFrame.jToolBar.addSeparator();
  }

  public TensorUnaryOperator operator(Tensor sequence) {
    return spinnerLogWeighting.getValue().from( //
        geodesicDisplay().vectorLogManifold(), //
        InversePowerVariogram.of(spinnerBeta.getValue()), //
        sequence);
  }
}
