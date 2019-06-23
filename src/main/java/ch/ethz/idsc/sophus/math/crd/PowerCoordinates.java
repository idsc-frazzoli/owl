// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.NormalizeTotal;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Power Coordinates: A Geometric Construction of Barycentric Coordinates on Convex Polytopes
 * Reference:
 * Max Budninskiy, Beibei Liu, Yiying Tong, Mathieu Desbrun, 2016 */
// FIXME JPH guard from singularities: x in P
// TODO JPH optimize implementation
public class PowerCoordinates {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  private final TensorMetric tensorMetric;

  public PowerCoordinates(TensorMetric tensorMetric) {
    this.tensorMetric = tensorMetric;
  }

  // usually wi == 0
  static Scalar dij(Tensor xi, Tensor xj, Scalar wi, Scalar wj) {
    Scalar norm2 = Norm2Squared.between(xi, xj);
    Scalar norm = Sqrt.FUNCTION.apply(norm2);
    return norm2.add(wi.subtract(wj)).divide(norm.add(norm));
  }

  static Tensor aux(Tensor xi, Tensor xj, Scalar wi, Scalar wj) {
    Tensor nrm = NORMALIZE.apply(xj.subtract(xi));
    return Tensors.of( //
        xi.add(nrm.multiply(dij(xi, xj, wi, wj))), //
        Cross.of(nrm));
  }

  Tensor aux(Tensor xi, Tensor xj) {
    Scalar wj = tensorMetric.distance(xi, xj);
    return aux(xi, xj, wj.zero(), wj);
  }

  Tensor getDual(Tensor P, Tensor x) {
    Tensor tensor = Tensor.of(P.stream().map(p -> aux(x, p)));
    Tensor result = Unprotect.empty(P.length());
    for (int index = 0; index < tensor.length(); ++index) {
      int _prev = Math.floorMod(index - 1, P.length());
      Tensor p1 = tensor.get(_prev, 0);
      Tensor n1 = tensor.get(_prev, 1);
      Tensor p2 = tensor.get(index, 0);
      Tensor n2 = tensor.get(index, 1);
      result.append(Intersection2D.of(p1, n1, p2, n2));
    }
    return result;
  }

  Tensor hDual(Tensor P, Tensor x) {
    Tensor tensor = getDual(P, x);
    Tensor result = Unprotect.empty(P.length());
    for (int index = 0; index < tensor.length(); ++index) {
      Scalar num = Norm._2.between(tensor.get(index), tensor.get(Math.floorMod(index + 1, P.length())));
      Scalar den = Norm._2.between(P.get(index), x);
      result.append(num.divide(den));
    }
    return result;
  }

  public Tensor weights(Tensor P, Tensor x) {
    return NormalizeTotal.FUNCTION.apply(hDual(P, x));
  }
}
