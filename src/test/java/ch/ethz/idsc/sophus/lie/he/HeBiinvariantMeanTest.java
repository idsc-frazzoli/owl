// code by ob
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.BiinvariantMeanEquation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class HeBiinvariantMeanTest extends TestCase {
  public static final BiinvariantMeanEquation BIINVARIANT_MEAN_EQUATION = //
      new BiinvariantMeanEquation(HeGroup.INSTANCE, HeExponential.INSTANCE);

  public void testTrivial() {
    Tensor element = Tensors.fromString("{{1}, {1}, 1}");
    Tensor sequence = Tensors.of(element);
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    assertEquals(sequence.get(0), actual);
  }

  public void testTrivialHe3() {
    Tensor element = Tensors.fromString("{{1}, {1}, 1}");
    Tensor sequence = Tensors.of(element);
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    assertEquals(element, actual);
  }

  public void testTrivialHe5() {
    Tensor p = Tensors.vector(1, 2);
    Tensor element = Tensors.of(p, p, RealScalar.ONE);
    Tensor sequence = Tensors.of(element);
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    assertEquals(element, actual);
  }

  public void testSimpleHe3() {
    Tensor element = Tensors.fromString("{{1}, {1}, 1}");
    Tensor sequence = Tensors.of(element, element.add(element), element.add(element).add(element));
    Tensor weights = Tensors.vector(0.2, 0.6, 0.2);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{{2}, {2}, 1.8}");
    Chop._12.requireClose(actual, expected);
    BIINVARIANT_MEAN_EQUATION.evaluate(sequence, weights, actual);
  }

  public void testSimplelHe5() {
    Tensor p = Tensors.vector(1, 2);
    Tensor element = Tensors.of(p, p, RealScalar.ONE);
    Tensor sequence = Tensors.of(element, element.add(element), element.add(element).add(element));
    Tensor weights = Tensors.vector(0.2, 0.6, 0.2);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{{2.0, 4.0}, {2.0, 4.0}, 1.0}");
    assertEquals(actual.get(0), actual.get(1));
    Chop._12.requireClose(actual, expected);
    BIINVARIANT_MEAN_EQUATION.evaluate(sequence, weights, actual);
  }

  public void testInverse() {
    HeGroupElement p = new HeGroupElement(Tensors.fromString("{{1, 2}, {3, 4}, 5}"));
    HeGroupElement pinv = p.inverse();
    // ---
    Tensor sequence = Tensors.of(p.toTensor(), pinv.toTensor());
    Tensor weights = Tensors.vector(0.5, 0.5);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor identity = Tensors.fromString("{{0, 0}, {0, 0}, 0}");
    assertEquals(identity, actual);
    BIINVARIANT_MEAN_EQUATION.evaluate(sequence, weights, actual);
  }

  public void testBiinvariantMean1() {
    Tensor p = Tensors.fromString("{{1, 2}, {3, 4}, 5}");
    Tensor q = Tensors.fromString("{{-3, 6}, {-2, 8}, -9}");
    Tensor domain = Subdivide.of(-1, 1, 10);
    Tensor he1 = domain.map(HeGeodesic.INSTANCE.curve(p, q));
    ScalarTensorFunction mean = //
        w -> HeBiinvariantMean.INSTANCE.mean(Tensors.of(p, q), Tensors.of(RealScalar.ONE.subtract(w), w));
    Tensor he2 = domain.map(mean);
    Chop._12.requireClose(he1, he2);
  }

  public void testBiinvariantMean2() {
    Tensor p = Tensors.fromString("{{1}, {4}, 5}");
    Tensor q = Tensors.fromString("{{-3}, {6}, -9}");
    Tensor domain = Subdivide.of(0, 2, 11);
    Tensor he1 = domain.map(HeGeodesic.INSTANCE.curve(p, q));
    ScalarTensorFunction mean = //
        w -> HeBiinvariantMean.INSTANCE.mean(Tensors.of(p, q), Tensors.of(RealScalar.ONE.subtract(w), w));
    Tensor he2 = domain.map(mean);
    Chop._12.requireClose(he1, he2);
  }
}
