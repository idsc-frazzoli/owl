// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.SpinnerListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ abstract class DatasetFilterDemo extends GeodesicDisplayDemo {
  protected static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  protected static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  protected final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  protected final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  protected final JToggleButton jToggleLine = new JToggleButton("line");
  protected final JToggleButton jToggleSymi = new JToggleButton("graph");
  protected Tensor _control = Tensors.of(Array.zeros(3));
  private final SpinnerListener<String> spinnerListener = resource -> //
  _control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream() //
      .limit(300) //
      .map(row -> row.extract(1, 4)));

  protected Tensor control() {
    return Tensor.of(_control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  public DatasetFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
    timerFrame.geometricComponent.setModel2Pixel(StaticHelper.HANGAR_MODEL2PIXEL);
    {
      SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
      spinnerLabel.addSpinnerListener(spinnerListener);
      spinnerLabel.setList(list);
      spinnerLabel.setIndex(0);
      spinnerLabel.reportToAll();
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    jToggleLine.setSelected(true);
    timerFrame.jToolBar.add(jToggleLine);
    // // ---
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
  }
}
