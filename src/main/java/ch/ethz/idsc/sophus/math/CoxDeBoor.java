// code by jph
// adapted from 
// https://docs.scipy.org/doc/scipy/reference/generated/scipy.interpolate.BSpline.html
package ch.ethz.idsc.sophus.math;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Boole;

/** DeBoor denotes the function that is defined
 * by control points over a sequence of knots. */
class CoxDeBoor implements ScalarTensorFunction {
  /** @param knots vector of length degree * 2
   * @param control points of length degree + 1
   * @return
   * @throws Exception if given knots is not a vector */
  public static CoxDeBoor of(Tensor knots, Tensor control) {
    int p = knots.length() >> 1;
    if (control.length() != p + 1)
      throw TensorRuntimeException.of(knots, control);
    return new CoxDeBoor(p, VectorQ.require(knots), control);
  }

  // ---
  private final int degree;
  private final Tensor knots;
  private final Tensor control;
  private final int n;

  /** @param degree
   * @param knots vector of length degree * 2
   * @param control points of length degree + 1 */
  CoxDeBoor(int degree, Tensor knots, Tensor control) {
    this.degree = degree;
    this.knots = knots;
    this.control = control;
    n = knots.length() - degree - 1;
    System.out.println("D=" + degree);
    System.out.println("n=" + n);
    if (n != control.length())
      System.err.println("control length!?");
    if (!(n >= degree + 1 && control.length() >= n))
      System.err.println("warning!");
  }

  @Override
  public Tensor apply(Scalar x) {
    return IntStream.range(0, control.length()) //
        .mapToObj(i -> control.get(i).multiply(recur(degree, i, x))) //
        .reduce(Tensor::add).get();
  }

  private Scalar recur(int k, int i, Scalar x) {
    Scalar t_i = knot(i);
    if (k == 0)
      return Boole.of( //
          Scalars.lessEquals(t_i, x) && Scalars.lessThan(x, knot(i + 1)));
    Scalar t_ik = knot(i + k);
    final Scalar c1;
    if (t_ik.equals(t_i))
      c1 = RealScalar.ZERO; // end of recursion
    else {
      Scalar p = recur(k - 1, i, x);
      Scalar f = x.subtract(t_i).divide(knot(i + k).subtract(t_i));
      c1 = p.multiply(f);
    }
    final Scalar c2;
    if (knot(i + k + 1).equals(knot(i + 1)))
      c2 = RealScalar.ZERO; // end of recursion
    else {
      Scalar p = recur(k - 1, i + 1, x);
      Scalar f = knot(i + k + 1).subtract(x).divide(knot(i + k + 1).subtract(knot(i + 1)));
      c2 = p.multiply(f);
    }
    return c1.add(c2);
  }

  final Set<Integer> set = new TreeSet<>();

  private Scalar knot(int index) {
    boolean status = 0 <= index && index < knots.length();
    set.add(index);
    if (!status)
      System.err.println("warning=" + index);
    return status//
        ? knots.Get(index)
        : RealScalar.ZERO;
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
