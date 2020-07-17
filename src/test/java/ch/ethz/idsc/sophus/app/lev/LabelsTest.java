// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class LabelsTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.unit();
    for (Labels labels : Labels.values()) {
      Tensor vector = Tensors.vector(3, 1, 0, 2, 0, 1, 3);
      Classification classification = labels.apply(vector);
      classification.result(RandomVariate.of(distribution, vector.length()));
      try {
        classification.result(RandomVariate.of(distribution, vector.length() - 1));
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }
}
