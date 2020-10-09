// code by jph
package ch.ethz.idsc.tensor.fig;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.ext.DeleteDirectory;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import junit.framework.TestCase;

public class TimeChartTest extends TestCase {
  public void testAll() throws IOException {
    File folder = HomeDirectory.Pictures(getClass().getSimpleName());
    assertFalse(folder.exists());
    folder.mkdirs();
    TestHelper.cascade(new File(folder, "1"), true);
    TestHelper.cascade(new File(folder, "0"), false);
    DeleteDirectory.of(folder, 2, 20);
    assertFalse(folder.exists());
  }
}
