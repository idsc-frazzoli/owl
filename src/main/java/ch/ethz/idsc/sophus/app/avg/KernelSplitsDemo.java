// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ abstract class KernelSplitsDemo extends GeodesicSplitsDemo {
  final SpinnerLabel<WindowFunctions> spinnerKernel = new SpinnerLabel<>();

  public KernelSplitsDemo() {
    spinnerKernel.setList(Arrays.asList(WindowFunctions.values()));
    spinnerKernel.setValue(WindowFunctions.DIRICHLET);
    spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
  }
}
