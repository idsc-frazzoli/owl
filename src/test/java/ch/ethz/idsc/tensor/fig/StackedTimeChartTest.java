// code by jph
package ch.ethz.idsc.tensor.fig;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StackedTimeChartTest extends TestCase {
  public void testEmpty() {
    VisualSet visualSet = new VisualSet();
    TestHelper.draw(StackedTimeChart.of(visualSet));
  }

  public void testEmptyRow() {
    VisualSet visualSet = new VisualSet();
    visualSet.add(Tensors.empty());
    TestHelper.draw(StackedTimeChart.of(visualSet));
  }
}
