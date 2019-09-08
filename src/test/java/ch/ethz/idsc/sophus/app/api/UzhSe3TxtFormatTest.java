// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import junit.framework.TestCase;

public class UzhSe3TxtFormatTest extends TestCase {
  public void testSimple() throws FileNotFoundException, IOException {
    File file = new File("/media/datahaki/media/resource/uzh/groundtruth", "outdoor_forward_5_davis.txt");
    if (file.isFile()) {
      Tensor tensor = UzhSe3TxtFormat.of(file);
      assertEquals(Dimensions.of(tensor), Arrays.asList(22294, 4, 4));
      assertFalse(StringScalarQ.any(tensor));
    }
  }
}
