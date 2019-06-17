// code by jph
package ch.ethz.idsc.sophus.lie.so2c;

import ch.ethz.idsc.sophus.lie.BiinvariantMeanTests;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class So2CoveringBiinvariantMeanTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  public void testPermutations() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.HALF));
    Distribution shifted = UniformDistribution.of(Clips.absolute(10));
    for (int length = 1; length < 6; ++length) {
      Tensor sequence = RandomVariate.of(distribution, length).map(RealScalar.of(10)::add);
      Tensor weights = NORMALIZE.apply(RandomVariate.of(UniformDistribution.unit(), length));
      Scalar solution = So2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
      for (int count = 0; count < 10; ++count) {
        Scalar shift = RandomVariate.of(shifted);
        for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
          int[] index = Primitives.toIntArray(perm);
          Scalar result = So2CoveringBiinvariantMean.INSTANCE.mean( //
              BiinvariantMeanTests.order(sequence.map(shift::add), index), //
              BiinvariantMeanTests.order(weights, index));
          Chop._12.requireClose(result.subtract(shift), solution);
        }
      }
    }
  }

  public void testSpecific() {
    Scalar scalar = So2CoveringBiinvariantMean.INSTANCE.mean(Tensors.vector(3, 4), Tensors.vector(0.5, 0.5));
    Chop._12.requireClose(scalar, RealScalar.of(3.5));
  }

  public void testAntipodal() {
    Scalar mean = So2CoveringBiinvariantMean.INSTANCE.mean(Tensors.of(Pi.HALF, Pi.HALF.negate()), Tensors.vector(0.6, 0.4));
    Chop._12.requireClose(mean, RealScalar.of(0.3141592653589793));
  }

  public void testFailFar() {
    Scalar mean = So2CoveringBiinvariantMean.INSTANCE.mean(Tensors.of(Pi.VALUE, Pi.VALUE.negate()), Tensors.vector(0.6, 0.4));
    Chop._12.requireClose(mean, RealScalar.of(0.6283185307179586));
  }

  public void testFailTensor() {
    try {
      So2CoveringBiinvariantMean.INSTANCE.mean(HilbertMatrix.of(3), NORMALIZE.apply(Tensors.vector(1, 1, 1)));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
