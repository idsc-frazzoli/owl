package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

abstract class DatasetKernelDemo extends DatasetFilterDemo {
  protected final SpinnerLabel<SmoothingKernel> spinnerFilter = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
  protected final JToggleButton jToggleData = new JToggleButton("data");
  protected final JToggleButton jToggleDiff = new JToggleButton("diff");
  protected final JToggleButton jToggleWait = new JToggleButton("wait");

  public DatasetKernelDemo() {
    jToggleData.setSelected(true);
    timerFrame.jToolBar.add(jToggleData);
    // ---
    jToggleDiff.setSelected(true);
    timerFrame.jToolBar.add(jToggleDiff);
    // ----
    jToggleWait.setSelected(false);
    timerFrame.jToolBar.add(jToggleWait);
    // ---
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

  protected Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }
}
