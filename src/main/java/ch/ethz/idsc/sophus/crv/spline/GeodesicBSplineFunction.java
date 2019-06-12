// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** The implementation of BSplineFunction in the tensor library
 * is different from Mathematica.
 * 
 * tensor::BSplineFunction is parameterized over the interval
 * [0, control.length()-1]
 * 
 * tensor::BSplineFunction can be instantiated for all degrees
 * regardless of the length of the control points.
 * 
 * Mathematica::BSplineFunction throws an exception if number
 * of control points is insufficient for the specified degree. */
public class GeodesicBSplineFunction implements ScalarTensorFunction {
  /** the control point are stored by reference, i.e. modifications to
   * given tensor alter the behavior of this BSplineFunction instance.
   * 
   * @param degree of polynomial basis function, non-negative integer
   * @param control points with at least one element
   * @return */
  public static GeodesicBSplineFunction of(SplitInterface splitInterface, int degree, Tensor control) {
    return of(splitInterface, degree, Range.of(0, control.length()), control);
  }

  /** the control point are stored by reference, i.e. modifications to
   * given tensor alter the behavior of this BSplineFunction instance.
   * 
   * @param splitInterface
   * @param degree of polynomial basis function, non-negative integer
   * @param knots vector of the same length as control
   * @param control points with at least one element
   * @return
   * @throws Exception */
  public static GeodesicBSplineFunction of(SplitInterface splitInterface, int degree, Tensor knots, Tensor control) {
    if (degree < 0)
      throw new IllegalArgumentException(Integer.toString(degree));
    if (!Sort.of(knots).equals(knots))
      throw TensorRuntimeException.of(knots);
    return new GeodesicBSplineFunction(splitInterface, degree, VectorQ.requireLength(knots, control.length()), control);
  }

  // ---
  private final SplitInterface splitInterface;
  private final int degree;
  private final Tensor control;
  /** half == degree / 2 */
  private final int half;
  /** index of last control point */
  private final int last;
  /** domain of this function */
  private final Clip domain;
  private final NavigableMap<Scalar, Integer> navigableMap;
  private final Tensor samples;

  private GeodesicBSplineFunction(SplitInterface splitInterface, int degree, Tensor knots, Tensor control) {
    this.splitInterface = splitInterface;
    this.degree = degree;
    this.control = control;
    half = degree / 2;
    Scalar shift = degree % 2 == 0 //
        ? RationalScalar.HALF
        : RealScalar.ZERO;
    last = control.length() - 1;
    domain = Clips.interval(knots.Get(0), (Scalar) Last.of(knots));
    navigableMap = new TreeMap<>();
    navigableMap.put(knots.Get(0), 0);
    for (int index = 1; index < knots.length(); ++index)
      navigableMap.put(degree % 2 == 0 //
          ? (Scalar) RnGeodesic.INSTANCE.split(knots.Get(index - 1), knots.Get(index), RationalScalar.HALF)
          : knots.Get(index), index);
    samples = Unprotect.references(Range.of(-degree + 1, control.length() + degree) //
        .map(index -> index.subtract(shift)) //
        .map(Clips.interval(0, last)) //
        .map(LinearInterpolation.of(knots)::at));
  }

  /** @param scalar inside interval [0, control.length() - 1]
   * @return
   * @throws Exception if given scalar is outside required interval */
  @Override
  public Tensor apply(final Scalar scalar) {
    return deBoor(scalar).apply(scalar);
  }

  /** @return */
  public Clip domain() {
    return domain;
  }

  public GeodesicDeBoor deBoor(Scalar scalar) {
    return deBoor(navigableMap.floorEntry(domain.requireInside(scalar)).getValue());
  }

  /** @param k in the interval [0, control.length() - 1]
   * @return */
  public GeodesicDeBoor deBoor(int k) {
    int hi = degree + 1 + k;
    return new GeodesicDeBoor(splitInterface, degree, //
        samples.extract(k, k + 2 * degree), //
        Tensor.of(IntStream.range(k - half, hi - half) // control
            .map(this::bound) //
            .mapToObj(control::get)));
  }

  // helper function
  private int bound(int index) {
    return Math.min(Math.max(0, index), last);
  }
}
