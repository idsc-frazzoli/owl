// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class PointWeightsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (PointWeights pointWeights : PointWeights.values())
      Serialization.copy(pointWeights.idc);
  }
}
