// code by jph
package ch.ethz.idsc.subare.util.plot;

import ch.ethz.idsc.sophus.util.plot.Histogram;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class HistogramTest extends TestCase {
  public void testEmpty() {
    VisualSet visualSet = new VisualSet();
    TestHelper.draw(Histogram.of(visualSet));
  }

  public void testEmptyRow() {
    VisualSet visualSet = new VisualSet();
    visualSet.add(Tensors.empty());
    TestHelper.draw(Histogram.of(visualSet));
  }
}
