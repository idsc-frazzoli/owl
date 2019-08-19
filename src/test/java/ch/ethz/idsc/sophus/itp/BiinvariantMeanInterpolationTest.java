// code by jph
package ch.ethz.idsc.sophus.itp;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BiinvariantMeanInterpolationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor vector = RandomVariate.of(UniformDistribution.unit(), 12);
    Interpolation bi = Serialization.copy(BiinvariantMeanInterpolation.of(RnBiinvariantMean.INSTANCE, vector));
    Interpolation li = LinearInterpolation.of(vector);
    Distribution distribution = UniformDistribution.of(0, vector.length() - 1);
    Tensor domain = RandomVariate.of(distribution, 100);
    Tensor bv = domain.map(bi::at);
    Tensor lv = domain.map(li::at);
    Chop._12.requireClose(bv, lv);
  }

  public void testExact() {
    Tensor vector = RandomVariate.of(DiscreteUniformDistribution.of(10, 20), 12);
    Interpolation interpolation = BiinvariantMeanInterpolation.of(RnBiinvariantMean.INSTANCE, vector);
    Tensor result = Range.of(0, 12).map(interpolation::at);
    assertEquals(result, vector);
    ExactTensorQ.require(result);
  }
}
