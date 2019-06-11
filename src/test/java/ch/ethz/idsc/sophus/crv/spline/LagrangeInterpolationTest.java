// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class LagrangeInterpolationTest extends TestCase {
  public void testSimple() {
    Tensor control = RandomVariate.of(DiscreteUniformDistribution.of(-3, 7), 4, 7).unmodifiable();
    Interpolation interpolation = LagrangeInterpolation.of(RnGeodesic.INSTANCE, control);
    Tensor domain = Range.of(0, control.length());
    Tensor polynom = domain.map(interpolation::at);
    assertEquals(control, polynom);
    ExactTensorQ.require(polynom);
  }
}
