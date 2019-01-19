// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class StGeodesicTest extends TestCase {
  public void testSimple() {    
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1,0,2));   
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7,-3,2));
    
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(0.7));
    Chop._13.requireClose(split, Tensors.of(RealScalar.of(8.122523963562355), Tensors.vector(4.747028756274826, -1.8735143781374128, 2.0)));
  }

  public void testZero() {
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1,0,2));   
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7,-3,2));
    
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(0));
    Chop._13.requireClose(split, p);

  }

  public void testOne() {
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1,0,2));   
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7,-3,2));
    
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(1));
    Chop._13.requireClose(split, q);
  }

  public void testGeneral() {
    Tensor p = Tensors.of(RealScalar.of(3), Tensors.vector(6, -2));
    Tensor q = Tensors.of(RealScalar.of(4), Tensors.vector(10, 3));
    Clip clip_l = Clip.function(3, 4);
    Clip clip_t1 = Clip.function(6, 10);
    Clip clip_t2 = Clip.function(-2, 3);

    for (Tensor x : Subdivide.of(0, 1, 20)) {
      Tensor split = StGeodesic.INSTANCE.split(p, q, x.Get());

      clip_l.requireInside(split.Get(0));
      clip_t1.requireInside((split.get(1)).Get(0));
      clip_t2.requireInside(split.get(1).Get(1));
    }
  }
}