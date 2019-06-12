// code by jph
// adapted from https://en.wikipedia.org/wiki/De_Boor%27s_algorithm
package ch.ethz.idsc.sophus.crv.spline;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** DeBoor denotes the function that is defined
 * by control points over a sequence of knots. */
public class GeodesicDeBoor implements ScalarTensorFunction {
  /** @param splitInterface non null
   * @param knots vector of length degree * 2
   * @param control points of length degree + 1
   * @return
   * @throws Exception if given knots is not a vector */
  public static GeodesicDeBoor of(SplitInterface splitInterface, Tensor knots, Tensor control) {
    int degree = knots.length() / 2;
    if (control.length() != degree + 1)
      throw TensorRuntimeException.of(knots, control);
    return new GeodesicDeBoor(Objects.requireNonNull(splitInterface), degree, VectorQ.require(knots), control);
  }

  // ---
  private final SplitInterface splitInterface;
  private final int degree;
  private final Tensor knots;
  private final Tensor control;

  /** @param splitInterface
   * @param degree
   * @param knots vector of length degree * 2
   * @param control points of length degree + 1 */
  /* package */ GeodesicDeBoor(SplitInterface splitInterface, int degree, Tensor knots, Tensor control) {
    this.splitInterface = splitInterface;
    this.degree = degree;
    this.knots = knots;
    this.control = control;
  }

  @Override
  public Tensor apply(Scalar x) {
    Tensor d = control.copy(); // d is modified over the course of the algorithm
    for (int r = 1; r < degree + 1; ++r)
      for (int j = degree; j >= r; --j) {
        Scalar kj1 = knots.Get(j - 1);
        Scalar num = x.subtract(kj1);
        Scalar den = knots.Get(j + degree - r).subtract(kj1);
        Scalar alpha = Scalars.isZero(den) //
            ? RealScalar.ZERO
            : num.divide(den);
        Tensor a0 = d.get(j - 1);
        d.set(dj -> splitInterface.split(a0, dj, alpha), j);
      }
    return d.get(degree);
  }

  public int degree() {
    return degree;
  }

  public Tensor control() {
    return control.unmodifiable();
  }

  public Tensor knots() {
    return knots.unmodifiable();
  }
}
