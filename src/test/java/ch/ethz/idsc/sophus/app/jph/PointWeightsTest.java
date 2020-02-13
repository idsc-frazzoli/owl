// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class PointWeightsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (PointWeights pointWeights : PointWeights.values())
      Serialization.copy(pointWeights.span(HilbertMatrix.of(10, 4)));
  }
}
