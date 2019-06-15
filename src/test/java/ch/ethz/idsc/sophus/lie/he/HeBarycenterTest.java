// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class HeBarycenterTest extends TestCase {
  public void test3dim() {
    Tensor p = Tensors.fromString("{{1}, {4}, 5}");
    Tensor q = Tensors.fromString("{{-3}, {6}, -9}");
    Tensor r = Tensors.fromString("{{2}, {1}, 8}");
    Tensor s = Tensors.fromString("{{-5}, {7}, -6}");
    HeBarycenter heBarycenter = new HeBarycenter(Tensors.of(p, q, r, s));
    assertEquals(heBarycenter.apply(p), UnitVector.of(4, 0));
    assertEquals(heBarycenter.apply(q), UnitVector.of(4, 1));
    assertEquals(heBarycenter.apply(r), UnitVector.of(4, 2));
    assertEquals(heBarycenter.apply(s), UnitVector.of(4, 3));
  }

  public void test5dim() {
    Distribution distribution = DiscreteUniformDistribution.of(-20000, 20000);
    Tensor sequence = Tensors.vector(l -> Tensors.of(RandomVariate.of(distribution, 2), RandomVariate.of(distribution, 2), RandomVariate.of(distribution)), 6);
    HeBarycenter heBarycenter = new HeBarycenter(sequence);
    for (int count = 0; count < sequence.length(); ++count) {
      Tensor tensor = heBarycenter.apply(sequence.get(count));
      assertEquals(tensor, UnitVector.of(6, count));
      ExactTensorQ.require(tensor);
    }
  }
}
