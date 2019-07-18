// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidTerminalRatios2Test extends TestCase {
  public void testLeftUniv() {
    Tensor p = Tensors.vector(0, 1, 0).unmodifiable();
    Tensor q = Tensors.vector(2, 2, 0).unmodifiable();
    ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(p, q);
    // turn left
    Chop._08.requireClose(clothoidTerminalRatios.head(), RealScalar.of(+1.2190137723033907));
    // turn right
    Chop._08.requireClose(clothoidTerminalRatios.tail(), RealScalar.of(-1.2190137715979599));
    Chop._03.requireClose(clothoidTerminalRatios.head(), ClothoidTerminalRatios2.head(p, q));
    // TODO JPH
    // Chop._03.requireClose(clothoidTerminalRatios.tail(), ClothoidTerminalRatios2.tail(p, q));
  }

  public void testCurve() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 1000; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      ClothoidTerminalRatios clothoidTerminalRatios = //
          ClothoidTerminalRatios.of(p.unmodifiable(), q.unmodifiable());
      {
        Scalar scalar = ClothoidTerminalRatios2.head(p, q);
        Scalar ratio = clothoidTerminalRatios.head().divide(scalar);
        Scalar diff = clothoidTerminalRatios.head().subtract(scalar);
        assertTrue(Chop._01.close(ratio, RealScalar.ONE) || Chop._01.allZero(diff));
      }
      // {
      // Scalar scalar = ClothoidTerminalRatios2.tail(p, q);
      // Scalar ratio = clothoidTerminalRatios.tail().divide(scalar);
      // Scalar diff = clothoidTerminalRatios.tail().subtract(scalar);
      // assertTrue(Chop._01.close(ratio, RealScalar.ONE) || Chop._01.allZero(diff));
      // }
    }
  }
}
