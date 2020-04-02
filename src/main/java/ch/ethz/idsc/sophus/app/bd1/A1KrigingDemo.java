// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Stroke;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class A1KrigingDemo extends ControlPointsDemo {
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  final SpinnerLabel<Krigings> spinnerKriging = new SpinnerLabel<>();
  final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public A1KrigingDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerKriging.setArray(Krigings.values());
      spinnerKriging.setIndex(0);
      spinnerKriging.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "krigings");
    }
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerBeta.setList(Tensors.fromString("{1, 9/8, 5/4, 3/2, 1.75, 1.99}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
  }
}
