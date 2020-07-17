// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class S2ArrayPlotTest extends TestCase {
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(new S2ArrayPlot());
  }
}
