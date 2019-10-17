// code by jph
package ch.ethz.idsc.tensor.fig;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ListPlotTest extends TestCase {
  public void testEmpty() {
    VisualSet visualSet = new VisualSet();
    ListPlot.of(visualSet);
  }

  public void testEmptyRow() {
    VisualSet visualSet = new VisualSet();
    VisualRow visualRow = visualSet.add(Tensors.empty(), Tensors.empty());
    visualRow.setLabel("empty");
    visualSet.add(Tensors.vector(1, 2, 5), Tensors.vector(2, 2.2, -1.6));
    TestHelper.draw(ListPlot.of(visualSet));
  }
}
