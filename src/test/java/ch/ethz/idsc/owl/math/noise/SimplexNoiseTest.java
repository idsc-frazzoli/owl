// code by jph
package ch.ethz.idsc.owl.math.noise;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.BinCounts;
import junit.framework.TestCase;

public class SimplexNoiseTest extends TestCase {
  public void testSimple() {
    Tensor noise = Tensors.vector(i -> DoubleScalar.of(10 * (1 + SimplexContinuousNoise.FUNCTION.at(.1 * i, .1 + i))), 1000);
    Tensor bins = BinCounts.of(noise);
    assertEquals(bins.length(), 20);
    long len = bins.stream() //
        .map(Scalar.class::cast) //
        .filter(scalar -> Scalars.lessThan(DoubleScalar.of(30), scalar)) //
        .count();
    assertTrue(10 < len);
  }

  public void testExample() {
    double value = SimplexContinuousNoise.FUNCTION.at(.3, 300.3, -600.5);
    assertEquals(value, -0.12579872366423636);
  }
}
