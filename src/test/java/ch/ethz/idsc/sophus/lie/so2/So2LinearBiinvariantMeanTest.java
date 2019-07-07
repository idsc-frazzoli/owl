// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.lie.BiinvariantMeanTests;
import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class So2LinearBiinvariantMeanTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  private static final Clip CLIP = Clips.absolute(Pi.VALUE);
  private static final ScalarBiinvariantMean[] SCALAR_BIINVARIANT_MEANS = { //
      So2GlobalBiinvariantMean.INSTANCE, //
      So2LinearBiinvariantMean.INSTANCE };

  public void testPermutations() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.HALF));
    for (int length = 1; length < 6; ++length) {
      Tensor sequence = RandomVariate.of(distribution, length);
      Tensor weights = NORMALIZE.apply(RandomVariate.of(UniformDistribution.unit(), length));
      for (ScalarBiinvariantMean so2BiinvariantMean : SCALAR_BIINVARIANT_MEANS) {
        Scalar solution = so2BiinvariantMean.mean(sequence, weights);
        for (int count = 0; count < 10; ++count) {
          Scalar shift = RandomVariate.of(distribution);
          for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
            int[] index = Primitives.toIntArray(perm);
            Scalar result = so2BiinvariantMean.mean( //
                BiinvariantMeanTests.order(sequence.map(shift::add), index), //
                BiinvariantMeanTests.order(weights, index));
            CLIP.requireInside(result);
            Chop._12.requireClose(So2.MOD.apply(result.subtract(shift).subtract(solution)), RealScalar.ZERO);
          }
        }
      }
    }
  }

  public void testSpecific() {
    Scalar scalar = So2LinearBiinvariantMean.INSTANCE.mean(Tensors.vector(3, 4), Tensors.vector(0.5, 0.5));
    Chop._12.requireClose(scalar, RealScalar.of(-2.7831853071795862));
  }

  public void testSame() {
    Scalar mean = So2LinearBiinvariantMean.INSTANCE.mean(Tensors.of(Pi.VALUE, Pi.VALUE.negate()), Tensors.vector(0.6, 0.4));
    Chop._12.requireClose(So2.MOD.apply(mean.subtract(Pi.VALUE)), RealScalar.ZERO);
  }

  public void testComparison() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.HALF));
    Chop chop = Chop.below(0.7);
    for (int length = 1; length < 10; ++length) {
      final Tensor sequence = RandomVariate.of(distribution, length);
      final Tensor weights = NORMALIZE.apply(RandomVariate.of(UniformDistribution.of(1, 2), length));
      int success = 0;
      for (int count = 0; count < 10; ++count) {
        Scalar shift = RandomVariate.of(distribution);
        Scalar val1 = So2GlobalBiinvariantMean.INSTANCE.mean(sequence.map(shift::add), weights);
        Tensor val2 = So2LinearBiinvariantMean.INSTANCE.mean(sequence.map(shift::add), weights);
        if (chop.allZero(So2.MOD.apply(val1.subtract(val2))))
          ++success;
      }
      assertTrue(8 < success);
    }
  }

  public void testFailAntipodal() {
    try {
      So2LinearBiinvariantMean.INSTANCE.mean(Tensors.of(Pi.HALF, Pi.HALF.negate()), Tensors.vector(0.6, 0.4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
