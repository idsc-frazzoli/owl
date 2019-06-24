// code by jph
package ch.ethz.idsc.sophus.poly.crd;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.math.NormalizeTotal;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Reference:
 * "Power Coordinates: A Geometric Construction of Barycentric Coordinates on Convex Polytopes"
 * Max Budninskiy, Beibei Liu, Yiying Tong, Mathieu Desbrun, 2016 */
// FIXME JPH guard from singularities: x in P
public class PowerCoordinates implements Serializable {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  private final TensorMetric tensorMetric;

  /** @param tensorMetric
   * @see Barycentric */
  public PowerCoordinates(TensorMetric tensorMetric) {
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
  }

  // usually wi == 0
  /** The expression of the distance from a site to a power facet is actually known analytically
   * eqs (2)
   * 
   * @param xi
   * @param xj
   * @param wi
   * @param wj
   * @return */
  static Scalar dij(Tensor xi, Tensor xj, Scalar wi, Scalar wj) {
    Scalar norm2 = Norm2Squared.between(xi, xj);
    Scalar norm = Sqrt.FUNCTION.apply(norm2);
    return norm2.add(wi.subtract(wj)).divide(norm.add(norm));
  }

  static class Aux {
    final Tensor pos;
    final Tensor dir;

    Aux(Tensor xi, Tensor xj, Scalar wi, Scalar wj) {
      Tensor nrm = NORMALIZE.apply(xj.subtract(xi));
      pos = xi.add(nrm.multiply(dij(xi, xj, wi, wj))); //
      dir = Cross.of(nrm);
    }

    Tensor intersect(Aux aux) {
      return Intersection2D.of(pos, dir, aux.pos, aux.dir);
    }
  }

  Aux aux(Tensor xi, Tensor xj) {
    Scalar wj = tensorMetric.distance(xi, xj);
    return new Aux(xi, xj, wj.zero(), wj);
  }

  Tensor getDual(Tensor P, Tensor x) {
    List<Aux> auxs = P.stream().map(p -> aux(x, p)).collect(Collectors.toList());
    int length = P.length();
    Tensor result = Unprotect.empty(length);
    Aux prev = auxs.get(length - 1);
    for (int index = 0; index < length; ++index) {
      Aux next = auxs.get(index);
      result.append(prev.intersect(next));
      prev = next;
    }
    return result;
  }

  Tensor hDual(Tensor P, Tensor x) {
    Tensor tensor = getDual(P, x);
    int length = tensor.length();
    return Tensor.of(IntStream.range(0, length) //
        .mapToObj(index -> Norm._2.between(tensor.get(index), tensor.get((index + 1) % length)) //
            .divide(Norm._2.between(P.get(index), x))));
  }

  public Tensor weights(Tensor P, Tensor x) {
    return NormalizeTotal.FUNCTION.apply(hDual(P, x));
  }
}
