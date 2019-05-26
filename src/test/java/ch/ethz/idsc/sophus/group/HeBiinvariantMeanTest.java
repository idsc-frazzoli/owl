// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.Assert;
import junit.framework.TestCase;

public class HeBiinvariantMeanTest extends TestCase {
  public void testTrivial() {
    Tensor sequence = Tensors.of(Tensors.vector(1, 1, 1));
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Assert.assertEquals(sequence.get(0), actual);
  }

  public void testTrivialHe3() {
    Tensor element = Tensors.vector(1, 1, 1);
    // TODO OB: not nice to have a tensor of a tensor..
    Tensor sequence = Tensors.of(element);
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Assert.assertEquals(element, actual);
  }

  public void testTrivialHe5() {
    Tensor p = Tensors.vector(1, 2);
    Tensor element = Tensors.of(p, p, RealScalar.ONE);
    Tensor sequence = Tensors.of(element);
    Tensor weights = Tensors.vector(1);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Assert.assertEquals(element, actual);
  }

  public void testSimpleHe3() {
    Tensor element = Tensors.vector(1, 1, 1);
    Tensor sequence = Tensors.of(element, element.add(element), element.add(element).add(element));
    Tensor weights = Tensors.vector(0.2, 0.6, 0.2);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.vector(2, 2, 1.8);
    Assert.assertEquals(actual.get(0), actual.get(1));
    Chop._12.requireClose(actual, expected);
  }

  public void testSimplelHe5() {
    Tensor p = Tensors.vector(1, 2);
    Tensor element = Tensors.of(p, p, RealScalar.ONE);
    Tensor sequence = Tensors.of(element, element.add(element), element.add(element).add(element));
    Tensor weights = Tensors.vector(0.2, 0.6, 0.2);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{{2.0, 4.0}, {2.0, 4.0}, 1.0}");
    Assert.assertEquals(actual.get(0), actual.get(1));
    Chop._12.requireClose(actual, expected);
  }

  public void testInverse() {
    HeGroupElement p = new HeGroupElement(Tensors.fromString("{{1, 2}, {3, 4}, 5}"));
    HeGroupElement pinv = p.inverse();
    // ---
    Tensor sequence = Tensors.of(p.toTensor(), pinv.toTensor());
    Tensor weights = Tensors.vector(0.5, 0.5);
    Tensor actual = HeBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor identity = Tensors.fromString("{{0, 0}, {0, 0}, 0}");
    Assert.assertEquals(identity, actual);
  }
}
