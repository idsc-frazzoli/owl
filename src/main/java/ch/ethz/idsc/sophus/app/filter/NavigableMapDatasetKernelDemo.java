// code by ob, jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;

/* package */ abstract class NavigableMapDatasetKernelDemo extends NavigableMapDatasetFilterDemo {
  protected final SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();

  public NavigableMapDatasetKernelDemo() {
    {
      spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerKernel.setValue(SmoothingKernel.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
      spinnerKernel.addSpinnerListener(value -> updateStateTime());
    }
    {
      spinnerRadius.setList(IntStream.range(0, 21).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(3);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
      spinnerRadius.addSpinnerListener(value -> updateStateTime());
    }
  }

  @Override // from DatasetFilterDemo
  protected String plotLabel() {
    SmoothingKernel smoothingKernel = spinnerKernel.getValue();
    int radius = spinnerRadius.getValue();
    return smoothingKernel + " [" + radius + "]";
  }
}
