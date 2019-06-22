// code by jph, ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FilterResponseTest extends TestCase {
  // implementation only for testing
  static Tensor pdiv(Tensor num, Tensor den) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < num.length(); ++index) {
      Tensor temp = Tensors.empty();
      for (int j = 0; j < num.get(0).length(); ++j)
        temp.append(num.get(index, j).divide(den.Get(index, j)));
      result.append(temp);
    }
    return result;
  }

  public void testSimple() {
    Distribution distribution = UniformDistribution.of(1, 2);
    Tensor num = RandomVariate.of(distribution, 10, 10);
    Tensor den = RandomVariate.of(distribution, 10, 10);
    Tensor rat1 = pdiv(num, den);
    Tensor rat2 = FilterResponse.pdiv(num, den);
    Chop._10.requireClose(rat1, rat2);
  }
}
