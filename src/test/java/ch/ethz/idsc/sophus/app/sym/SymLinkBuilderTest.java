// code by jph
package ch.ethz.idsc.sophus.app.sym;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.crv.bezier.BezierFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import junit.framework.TestCase;

public class SymLinkBuilderTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensors.vector(1, 2, 3);
    Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(RealScalar.of(0.3));
    // ---
    SymLink symLink = SymLinkBuilder.of(control, symScalar);
    assertNotNull(symLink);
  }
}
