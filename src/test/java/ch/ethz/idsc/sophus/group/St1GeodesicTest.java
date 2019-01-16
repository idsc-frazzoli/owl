package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class St1GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0.7));
    // @JH: Ich bin sehr sicher, dass dieser Werte Stimmen
    Chop._13.requireClose(split, Tensors.vector(8.122523963562355, 0.37549520728752905));
  }
  
  public void testZero() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0));
    Chop._13.requireClose(split, Tensors.vector(5,1));
  }
  
  public void testOne() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(1));
    Chop._13.requireClose(split, Tensors.vector(10,0));
  }
}