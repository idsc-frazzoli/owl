// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.List;
import java.util.Optional;

import javax.swing.JButton;

import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public abstract class AbstractPlaceDemo extends ControlPointsDemo {
  private final JButton jButtonPrint = new JButton("print");

  public AbstractPlaceDemo(boolean addRemoveControlPoints, List<ManifoldDisplay> list) {
    super(addRemoveControlPoints, list);
    setMidpointIndicated(false);
    // ---
    jButtonPrint.addActionListener(l -> System.out.println(getControlPointsSe2().map(Round._3)));
    timerFrame.jToolBar.add(jButtonPrint);
  }

  final Optional<Tensor> getOrigin() {
    Tensor geodesicControlPoints = getGeodesicControlPoints(0, 1);
    return 0 < geodesicControlPoints.length() //
        ? Optional.of(geodesicControlPoints.get(0))
        : Optional.empty();
  }

  final Tensor getSequence() {
    return getGeodesicControlPoints(1, Integer.MAX_VALUE);
  }
}
