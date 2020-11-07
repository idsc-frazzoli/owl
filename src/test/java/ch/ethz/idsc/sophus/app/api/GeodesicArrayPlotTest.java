// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicArrayPlotTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    Tensor a = RandomVariate.of(distribution, 3, 3);
    Tensor v = RandomVariate.of(distribution, 3);
    Tensor b = DiagonalMatrix.with(v);
    Tensor c = RandomVariate.of(distribution, 3, 3);
    Chop._09.requireClose(Dot.of(b, c), v.pmul(c));
    Chop._09.requireClose(Dot.of(a, b, c), a.dot(v.pmul(c)));
  }
}
