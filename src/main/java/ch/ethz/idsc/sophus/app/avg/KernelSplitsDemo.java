// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;
import java.util.Arrays;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.SmoothingKernel;

/* package */ abstract class KernelSplitsDemo extends GeodesicSplitsDemo {
  final SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();

  public KernelSplitsDemo() {
    spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
    spinnerKernel.setValue(SmoothingKernel.DIRICHLET);
    spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
  }
}
