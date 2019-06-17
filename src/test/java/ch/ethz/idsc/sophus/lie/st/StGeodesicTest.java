// code by ob
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class StGeodesicTest extends TestCase {
  public void testSt1Simple() {
    Tensor split = StGeodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0.7));
    Chop._13.requireClose(split, Tensors.vector(8.122523963562355, 0.37549520728752905));
  }

  public void testSt1Zero() {
    Tensor split = StGeodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(0));
    Chop._13.requireClose(split, Tensors.vector(5, 1));
  }

  public void testSt1One() {
    Tensor split = StGeodesic.INSTANCE.split(Tensors.vector(5, 1), Tensors.vector(10, 0), RealScalar.of(1));
    Chop._13.requireClose(split, Tensors.vector(10, 0));
  }

  public void testSt1General() {
    Tensor p = Tensors.vector(3, 6);
    Tensor q = Tensors.vector(4, 10);
    Clip clip_l = Clips.interval(3, 4);
    Clip clip_t = Clips.interval(6, 10);
    for (Tensor x : Subdivide.of(0, 1, 20)) {
      Tensor split = StGeodesic.INSTANCE.split(p, q, x.Get());
      clip_l.requireInside(split.Get(0));
      clip_t.requireInside(split.Get(1));
    }
  }

  public void testSimple() {
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1, 0, 2));
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7, -3, 2));
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(0.7));
    Chop._13.requireClose(split, Tensors.of(RealScalar.of(8.122523963562355), Tensors.vector(4.747028756274826, -1.8735143781374128, 2.0)));
  }

  public void testZero() {
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1, 0, 2));
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7, -3, 2));
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(0));
    Chop._13.requireClose(split, p);
  }

  public void testOne() {
    Tensor p = Tensors.of(RealScalar.of(5), Tensors.vector(1, 0, 2));
    Tensor q = Tensors.of(RealScalar.of(10), Tensors.vector(7, -3, 2));
    Tensor split = StGeodesic.INSTANCE.split(p, q, RealScalar.of(1));
    Chop._13.requireClose(split, q);
  }

  public void testGeneral() {
    Tensor p = Tensors.of(RealScalar.of(3), Tensors.vector(6, -2));
    Tensor q = Tensors.of(RealScalar.of(4), Tensors.vector(10, 3));
    Clip clip_l = Clips.interval(3, 4);
    Clip clip_t1 = Clips.interval(6, 10);
    Clip clip_t2 = Clips.interval(-2, 3);
    for (Tensor x : Subdivide.of(0, 1, 20)) {
      Tensor split = StGeodesic.INSTANCE.split(p, q, x.Get());
      clip_l.requireInside(split.Get(0));
      clip_t1.requireInside((split.get(1)).Get(0));
      clip_t2.requireInside(split.get(1).Get(1));
    }
  }

  public void testBiinvariantMean() {
    Tensor p = Tensors.fromString("{1, {2, 3}}");
    Tensor q = Tensors.fromString("{2, {3, 1}}");
    Tensor domain = Subdivide.of(0, 1, 10);
    Tensor st1 = domain.map(StGeodesic.INSTANCE.curve(p, q));
    ScalarTensorFunction mean = //
        w -> StBiinvariantMean.INSTANCE.mean(Tensors.of(p, q), Tensors.of(RealScalar.ONE.subtract(w), w));
    Tensor st2 = domain.map(mean);
    Chop._12.requireClose(st1, st2);
  }
}