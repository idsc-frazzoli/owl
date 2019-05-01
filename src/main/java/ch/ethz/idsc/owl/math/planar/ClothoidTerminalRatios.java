// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;

/** clothoid is tangent at start and end points */
public class ClothoidTerminalRatios {
  public static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, 1);
  // ---
  /** depth of 12 was determined experimentally, see tests */
  private static final int DEFAULT_DEPTH = 12;

  /** @param beg
   * @param end
   * @return */
  // TODO LONGTERM iterate until convergence
  public static ClothoidTerminalRatios of(Tensor beg, Tensor end) {
    return new ClothoidTerminalRatios(beg, end, DEFAULT_DEPTH);
  }

  // ---
  private final Scalar head;
  private final Scalar tail;

  /** @param beg
   * @param end
   * @param depth strictly positive */
  public ClothoidTerminalRatios(Tensor beg, Tensor end, int depth) {
    final Tensor init = CURVE_SUBDIVISION.string(Tensors.of(beg, end));
    --depth;
    head = curvature(Nest.of(value -> CURVE_SUBDIVISION.string(value.extract(0, 2)), init, depth));
    tail = curvature(Nest.of(value -> CURVE_SUBDIVISION.string(value.extract(value.length() - 2, value.length())), init, depth));
  }

  public Scalar head() {
    return head;
  }

  public Scalar tail() {
    return tail;
  }

  /* package for testing */ static Scalar curvature(Tensor abc) {
    Optional<Scalar> optional = SignedCurvature2D.of( //
        abc.get(0).extract(0, 2), //
        abc.get(1).extract(0, 2), //
        abc.get(2).extract(0, 2));
    if (optional.isPresent())
      return optional.get();
    // ---
    Scalar scalar = abc.Get(0, 0);
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of(quantity.value().zero(), quantity.unit().negate());
    }
    return scalar.zero();
  }
}
