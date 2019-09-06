// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.SpinnerListener;

public abstract class GeodesicDisplayDemo extends AbstractDemo implements DemoInterface {
  private final SpinnerLabel<GeodesicDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();
  private final List<GeodesicDisplay> list;

  public GeodesicDisplayDemo(List<GeodesicDisplay> list) {
    if (list.isEmpty())
      throw new RuntimeException();
    this.list = list;
    geodesicDisplaySpinner.setList(list);
    geodesicDisplaySpinner.setValue(list.get(0));
    if (1 < list.size()) {
      geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
      timerFrame.jToolBar.addSeparator();
    }
  }

  /** @return */
  public final GeodesicDisplay geodesicDisplay() {
    return geodesicDisplaySpinner.getValue();
  }

  public synchronized final void setGeodesicDisplay(GeodesicDisplay geodesicDisplay) {
    geodesicDisplaySpinner.setValue(geodesicDisplay);
  }

  public void addSpinnerListener(SpinnerListener<GeodesicDisplay> spinnerListener) {
    geodesicDisplaySpinner.addSpinnerListener(spinnerListener);
  }

  /** @return */
  public List<GeodesicDisplay> getGeodesicDisplays() {
    return list;
  }

  @Override // from DemoInterface
  public final BaseFrame start() {
    return timerFrame;
  }
}
