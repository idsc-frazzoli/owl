// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;

public abstract class GeodesicDisplayDemo extends AbstractDemo implements DemoInterface {
  protected final SpinnerLabel<GeodesicDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();

  public GeodesicDisplayDemo(List<GeodesicDisplay> list) {
    if (!list.isEmpty()) {
      geodesicDisplaySpinner.setList(list);
      geodesicDisplaySpinner.setValue(list.get(0));
      if (1 < list.size()) {
        geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
        timerFrame.jToolBar.addSeparator();
      }
    }
  }

  public final GeodesicDisplay geodesicDisplay() {
    return geodesicDisplaySpinner.getValue();
  }

  @Override // from DemoInterface
  public final BaseFrame start() {
    return timerFrame;
  }
}
