// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.IOException;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class S2ArrayPlotTest extends TestCase {
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(S2ArrayPlot.INSTANCE);
  }
}
