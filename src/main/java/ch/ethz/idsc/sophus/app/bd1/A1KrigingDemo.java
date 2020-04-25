// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Stroke;
import java.util.List;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.krg.PseudoDistances;

/* package */ abstract class A1KrigingDemo extends AnKrigingDemo {
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  final SpinnerLabel<PseudoDistances> spinnerDistances = SpinnerLabel.of(PseudoDistances.values());

  public A1KrigingDemo(List<GeodesicDisplay> geodesicDisplays) {
    super(geodesicDisplays);
    spinnerDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "pseudo distances");
  }
}
