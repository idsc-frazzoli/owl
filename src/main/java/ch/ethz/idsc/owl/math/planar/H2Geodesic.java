// code by ob
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.ArcTanh;
import ch.ethz.idsc.tensor.sca.Cosh;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sqrt;
import ch.ethz.idsc.tensor.sca.Tanh;

/** geodesic on 2-dimensional hyperpolic hald
 * 
 * Tomasz Popiel, Lyle Noakes - Bézier Curves and C^2 interpolation in Riemannian manifolds */
public enum H2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  /** p and q are vectors of length 3 with unit length
   * 
   * Careful: function does not check length of input vectors! */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar t) {
    Scalar p1 = p.Get(0);
    Scalar p2 = p.Get(1);
    Scalar q1 = q.Get(0);
    Scalar q2 = q.Get(1);
    if (p1.equals(q1)) { // when p == q or p == -q
      return Tensors.of(p1, Power.function(t).apply(q2.divide(p2)).multiply(p2));
    } else {
      Scalar pq = p1.subtract(q1);
      Scalar c1 = p.dot(p).subtract(q.dot(q)).Get().divide(pq.add(pq));
      // (((p.Get(0)).multiply(p.Get(0)).add(p.Get(1)).multiply(p.Get(1))).subtract((q.Get(0)).multiply(q.Get(0)).add(q.Get(1)).multiply(q.Get(1)))).divide(((p.Get(0)).subtract(q.get(0)).multiply(RealScalar.of(0))));
      Scalar c2 = Sqrt.FUNCTION.apply(AbsSquared.FUNCTION.apply(p1.subtract(c1)).add(p2.multiply(p2)));
      Scalar c3 = ArcTanh.FUNCTION.apply(p1.subtract(c1).divide(c2));
      Scalar c4 = ArcTanh.FUNCTION.apply(q1.subtract(c1).divide(c2)).subtract(c3);
      return Tensors.of(c1.add(c2.multiply(Tanh.FUNCTION.apply(c3.add(c4.multiply(t))))), c2.divide(Cosh.FUNCTION.apply(c3.add(c4.multiply(t)))));
    }
  }
}
