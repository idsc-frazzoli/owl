// code by jph
package ch.ethz.idsc.sophus.gds;

import java.awt.Dimension;
import java.util.List;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;

public abstract class GeodesicDisplayDemo extends AbstractDemo implements DemoInterface {
  private final SpinnerLabel<ManifoldDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();
  private final List<ManifoldDisplay> list;

  public GeodesicDisplayDemo(List<ManifoldDisplay> list) {
    if (list.isEmpty())
      throw new RuntimeException();
    this.list = list;
    geodesicDisplaySpinner.setList(list);
    geodesicDisplaySpinner.setValue(list.get(0));
    if (1 < list.size()) {
      geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
      timerFrame.jToolBar.addSeparator();
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public ManifoldDisplay getGeodesicDisplay() {
        return manifoldDisplay();
      }
    });
  }

  /** @return */
  public final ManifoldDisplay manifoldDisplay() {
    return geodesicDisplaySpinner.getValue();
  }

  public synchronized final void setGeodesicDisplay(ManifoldDisplay geodesicDisplay) {
    geodesicDisplaySpinner.setValue(geodesicDisplay);
  }

  public void addSpinnerListener(SpinnerListener<ManifoldDisplay> spinnerListener) {
    geodesicDisplaySpinner.addSpinnerListener(spinnerListener);
  }

  /** @return */
  public List<ManifoldDisplay> getGeodesicDisplays() {
    return list;
  }

  @Override // from DemoInterface
  public final BaseFrame start() {
    return timerFrame;
  }
}
