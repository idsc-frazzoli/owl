// code by jph
package ch.ethz.idsc.owl.data;

import java.util.List;

import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ResourceDataExtTest extends TestCase {
  public void testSimple() {
    List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
    assertTrue(50 < list.size());
  }
}
