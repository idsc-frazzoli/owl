// code by jph
package ch.ethz.idsc.sophus.gds;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.io.GokartPoseData;

public abstract class GeodesicDatasetDemo extends GeodesicDisplayDemo {
  protected final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();

  public GeodesicDatasetDemo(List<ManifoldDisplay> list, GokartPoseData gokartPoseData) {
    super(list);
    this.gokartPoseData = gokartPoseData;
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(500, 750, 800, 900, 1000, 1500, 2000, 3000, 5000));
      spinnerLabelLimit.setIndex(1);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
  }

  protected abstract void updateState();
}
