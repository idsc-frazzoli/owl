// code by jph
package ch.ethz.idsc.sophus.ply.crd;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.math.NormalizeTotal;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference:
 * "Power Coordinates: A Geometric Construction of Barycentric Coordinates on Convex Polytopes"
 * Max Budninskiy, Beibei Liu, Yiying Tong, Mathieu Desbrun, 2016 */
public class PowerCoordinates implements Serializable {
  private final ScalarUnaryOperator barycentric;

  /** @param barycentric
   * @see Barycentric */
  public PowerCoordinates(ScalarUnaryOperator barycentric) {
    this.barycentric = Objects.requireNonNull(barycentric);
  }

  /** @param polygon convex
   * @param x strictly inside polygon
   * @return vector with length of polygon */
  public Tensor weights(Tensor polygon, Tensor x) {
    return NormalizeTotal.FUNCTION.apply(hDual(polygon, x));
  }

  // usually wi == 0
  /** The expression of the distance from a site to a power facet is actually known analytically
   * eqs (2) */
  class Aux {
    final Tensor pos;
    final Tensor dir;

    Aux(Tensor x, Tensor p) {
      Tensor diff = p.subtract(x);
      Scalar norm = Hypot.ofVector(diff);
      Tensor ofs = diff.multiply(barycentric.apply(norm));
      pos = x.add(ofs);
      dir = Cross.of(ofs);
    }

    Tensor intersect(Aux aux) {
      Optional<Tensor> optional = Intersection2D.of(pos, dir, aux.pos, aux.dir);
      if (optional.isPresent())
        return optional.get();
      // LONGTERM this choice is not canonic
      return pos.add(aux.pos).multiply(RationalScalar.HALF);
    }
  }

  Tensor getDual(Tensor polygon, Tensor x) {
    List<Aux> auxs = polygon.stream().map(p -> new Aux(x, p)).collect(Collectors.toList());
    int length = polygon.length();
    Tensor result = Unprotect.empty(length);
    Aux prev = auxs.get(length - 1);
    for (int index = 0; index < length; ++index) {
      Aux next = auxs.get(index);
      result.append(prev.intersect(next));
      prev = next;
    }
    return result;
  }

  Tensor hDual(Tensor polygon, Tensor x) {
    int length = polygon.length();
    Tensor dens = Unprotect.empty(length);
    for (int index = 0; index < length; ++index) {
      Scalar den = Norm._2.between(polygon.get(index), x);
      if (Scalars.isZero(den))
        return UnitVector.of(length, index);
      dens.append(den);
    }
    Tensor tensor = getDual(polygon, x);
    return Tensor.of(IntStream.range(0, length) //
        .mapToObj(index -> Norm._2.between(tensor.get(index), tensor.get((index + 1) % length)).divide(dens.Get(index))));
  }
}
