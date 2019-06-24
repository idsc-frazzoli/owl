// code by ob /jph
package ch.ethz.idsc.sophus.lie;

import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.tensor.NormalizeTotal;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class BiinvariantMeanImplicitTest extends TestCase {
  public void testSE2() {
    Scalar TWO = RealScalar.of(2);
    Scalar ZERO = RealScalar.ZERO;
    Scalar rootOfTwo = Sqrt.of(TWO);
    Scalar rootOfTwoHalf = rootOfTwo.reciprocal();
    Scalar piFourth = Pi.HALF.divide(TWO);
    // ---
    Tensor p = Tensors.of(rootOfTwoHalf.negate(), rootOfTwoHalf, piFourth);
    Tensor q = Tensors.of(rootOfTwo, ZERO, ZERO);
    Tensor r = Tensors.of(rootOfTwoHalf.negate(), rootOfTwoHalf.negate(), piFourth.negate());
    // ---
    // Tensor sequence = Tensors.of(p, q, r);
    Tensor sequenceUnordered = Tensors.of(p, r, q);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // ---
    double nom = Math.sqrt(2) - Math.PI / 4;
    double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    BiinvariantMeanImplicit bMI = new BiinvariantMeanImplicit(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    Tensor actual = bMI.apply(sequenceUnordered, weights).get();
    Chop._11.requireClose(actual, expected);
  }
  // Tests form more groups however i think that e.g. HE1 could cause problems due to tensor of tensor structure.

  public void testSome() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.of(0, .2);
    int success = 0;
    for (int length = 2; length < 8; ++length) {
      Tensor sequence = RandomVariate.of(UniformDistribution.unit(), length, 3);
      Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(distribution, length));
      Tensor actual = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
      BiinvariantMeanImplicit biinvariantMeanImplicit = //
          Serialization.copy(new BiinvariantMeanImplicit(Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE));
      Optional<Tensor> result = biinvariantMeanImplicit.apply(sequence, weights);
      if (result.isPresent()) {
        Chop._11.requireClose(actual, result.get());
        ++success;
      }
    }
    assertTrue(3 < success);
  }
}
