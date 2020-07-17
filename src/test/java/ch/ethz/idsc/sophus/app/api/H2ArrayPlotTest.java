// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class H2ArrayPlotTest extends TestCase {
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(new H2ArrayPlot(RealScalar.of(2)));
  }
}
