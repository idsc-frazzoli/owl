// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class GeodesicNevilleTest extends TestCase {
  public void testSimple() {
    Tensor control = RandomVariate.of(DiscreteUniformDistribution.of(-3, 7), 4, 10).unmodifiable();
    Interpolation interpolation = LagrangeInterpolation.of(RnGeodesic.INSTANCE, control);
    Tensor domain = Range.of(0, control.length());
    Tensor polynom = domain.map(interpolation::at);
    assertEquals(control, polynom);
    assertTrue(ExactScalarQ.all(polynom));
  }
}
