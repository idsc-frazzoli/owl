// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.SmoothingKernel;

/* package */ abstract class DatasetKernelDemo extends DatasetFilterDemo {
  protected final SpinnerLabel<SmoothingKernel> spinnerFilter = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();

  public DatasetKernelDemo() {
    {
      spinnerFilter.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerFilter.setValue(SmoothingKernel.GAUSSIAN);
      spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
    }
    {
      spinnerRadius.setList(IntStream.range(0, 21).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(6);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  @Override
  protected String plotLabel() {
    SmoothingKernel smoothingKernel = spinnerFilter.getValue();
    int radius = spinnerRadius.getValue();
    return smoothingKernel + " [" + radius + "]";
  }
}
