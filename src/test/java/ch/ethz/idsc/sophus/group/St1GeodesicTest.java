// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class St1GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0.7));
    Chop._13.requireClose(split, Tensors.vector(8.122523963562355, 0.37549520728752905));
  }

  public void testZero() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0));
    Chop._13.requireClose(split, Tensors.vector(5, 1));
  }

  public void testOne() {
    Tensor split = St1Geodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(1));
    Chop._13.requireClose(split, Tensors.vector(10, 0));
  }

  public void testGeneral() {
    Tensor p = Tensors.vector(3, 6);
    Tensor q = Tensors.vector(4, 10);
    Clip clip_l = Clip.function(3, 4);
    Clip clip_t = Clip.function(6, 10);
    for (Tensor x : Subdivide.of(0, 1, 20)) {
      Tensor split = St1Geodesic.INSTANCE.split(p, q, x.Get());
      clip_l.requireInside(split.Get(0));
      clip_t.requireInside(split.Get(1));
    }
  }
}