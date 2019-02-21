// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Gilles Deslauriers and Serge Dubuc: Symmetric iterative interpolation processes
 * 
 * <pre>
 * weights = {3, -25, 150, 150, -25, 3} / 256
 * {(1 - a) (1 - b), a (1 - b), b, b , a (1 - b), (1 - b) (1 - a)} / 2
 * Solve[Thread[% == weights]]
 * </pre>
 * 
 * Another reference by Johannes Wallner:
 * "On convergent interpolatory subdivision schemes in Riemannian Geometry", p.2 */
public class SixPointCurveSubdivision extends AbstractSixPointCurveSubdivision {
  private static final Scalar PQ = RationalScalar.of(25, 22);
  private static final Scalar _R = RationalScalar.of(75, 64);

  // ---
  public SixPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from AbstractSixPointCurveSubdivision
  protected Tensor center(Tensor p, Tensor q, Tensor r, Tensor s, Tensor t, Tensor u) {
    Tensor pq = geodesicInterface.split(p, q, PQ);
    Tensor _r = geodesicInterface.split(pq, r, _R);
    Tensor ut = geodesicInterface.split(u, t, PQ);
    Tensor _s = geodesicInterface.split(ut, s, _R);
    return center(_r, _s);
  }
}
