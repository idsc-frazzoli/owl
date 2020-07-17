// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.List;

import javax.swing.JButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.tensor.sca.Round;

public abstract class AbstractPlaceDemo extends LogWeightingDemo {
  private final JButton jButtonPrint = new JButton("print");

  public AbstractPlaceDemo(List<GeodesicDisplay> list, List<LogWeighting> array) {
    super(true, list, array);
    setMidpointIndicated(false);
    // ---
    jButtonPrint.addActionListener(l -> System.out.println(getControlPointsSe2().map(Round._3)));
    // ---
    timerFrame.jToolBar.add(jButtonPrint);
  }
}
