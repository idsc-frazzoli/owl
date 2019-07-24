// code by jph
package ch.ethz.idsc.sophus.lie.he;

import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class HeDifferencesTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    int d = 3;
    Tensor x = RandomVariate.of(distribution, n, d);
    Tensor y = RandomVariate.of(distribution, n, d);
    Tensor z = RandomVariate.of(distribution, n);
    Tensor elements = Tensor.of(IntStream.range(0, n).mapToObj(i -> Tensors.of(x.get(i), y.get(i), z.Get(i))));
    Tensor differences = Serialization.copy(HeDifferences.INSTANCE).apply(elements);
    assertEquals(differences.length(), n - 1);
  }
}
