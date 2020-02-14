// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class PolygonWeightsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (PolygonWeights polygonWeights : PolygonWeights.values())
      Serialization.copy(polygonWeights.idc);
  }
}
