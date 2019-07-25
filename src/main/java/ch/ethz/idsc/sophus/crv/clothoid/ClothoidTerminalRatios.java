// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.util.Optional;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Chop;

public enum ClothoidTerminalRatios {
  ;
  public static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(Clothoid3.INSTANCE, 1);
  private static final TensorUnaryOperator HEAD = //
      value -> CURVE_SUBDIVISION.string(value.extract(0, 2));
  private static final TensorUnaryOperator TAIL = //
      value -> CURVE_SUBDIVISION.string(value.extract(value.length() - 2, value.length()));
  /** typically 13, or 14 iterations are needed to reach precision up 1e-3 */
  /***************************************************/
  /** investigations have shown that for iterations == 5 works on all start and end point configurations
   * 
   * (see ClothoidCurvatureDemo) */
  private static final int ITERATIONS = 5;

  /** Hint: the method is the preferred method to obtain ClothoidTerminalRatio.
   * 
   * @param p start configuration
   * @param q end configuration
   * @return */
  public static ClothoidTerminalRatio of(Tensor p, Tensor q) {
    // TODO could use variable iteration depth based on some accuracy criteria
    return of(p, q, ITERATIONS);
  }

  public static ClothoidTerminalRatio of(Tensor p, Tensor q, int iterations) {
    return new ClothoidTerminalRatio( //
        head(p, q, iterations), //
        tail(p, q, iterations));
  }

  /** @param p of the form {p_x, p_y, p_heading}
   * @param q of the form {q_x, q_y, q_heading}
   * @return */
  public static Scalar head(Tensor p, Tensor q, int iterations) {
    Tensor tensor = Nest.of(HEAD, Unprotect.byRef(p, q), iterations);
    return new ClothoidCurvature(tensor.get(0), tensor.get(1)).head();
  }

  /** @param p of the form {p_x, p_y, p_heading}
   * @param q of the form {q_x, q_y, q_heading}
   * @return */
  public static Scalar tail(Tensor p, Tensor q, int iterations) {
    Tensor tensor = Nest.of(TAIL, Unprotect.byRef(p, q), iterations);
    return new ClothoidCurvature(tensor.get(1), tensor.get(2)).tail();
  }

  /***************************************************/
  static final Chop CHOP = Chop._03;
  static final int MAX_ITER = 18;

  /** Hint: for almost straight segments, this curvature approximation is relatively far off!
   * 
   * @param beg of the form {beg_x, beg_y, beg_heading}
   * @param end of the form {end_x, end_y, end_heading}
   * @return */
  public static ClothoidTerminalRatio planar(Tensor beg, Tensor end) {
    final Tensor init = CURVE_SUBDIVISION.string(Unprotect.byRef(beg, end));
    Scalar head = ClothoidTerminalRatios.curvature(init);
    {
      Tensor hseq = init;
      for (int depth = 1; depth < MAX_ITER; ++depth) {
        hseq = HEAD.apply(hseq);
        Scalar next = ClothoidTerminalRatios.curvature(hseq);
        if (CHOP.close(head, next)) {
          head = next;
          break;
        }
        head = next;
      }
    }
    Scalar tail = ClothoidTerminalRatios.curvature(init);
    {
      Tensor tseq = init;
      for (int depth = 1; depth < MAX_ITER; ++depth) {
        tseq = TAIL.apply(tseq);
        Scalar next = ClothoidTerminalRatios.curvature(tseq);
        if (CHOP.close(tail, next)) {
          tail = next;
          break;
        }
        tail = next;
      }
    }
    return new ClothoidTerminalRatio(head, tail); //
  }

  /***************************************************/
  /* package */ static ClothoidTerminalRatio fixed(Tensor beg, Tensor end, int depth) {
    return new ClothoidTerminalRatio( //
        ClothoidTerminalRatios.curvature(Nest.of(HEAD, Unprotect.byRef(beg, end), depth)), //
        ClothoidTerminalRatios.curvature(Nest.of(TAIL, Unprotect.byRef(beg, end), depth)));
  }

  private static final QuantityMapper QUANTITY_MAPPER = new QuantityMapper(Scalar::zero, Unit::negate);

  /* package for testing */ static Scalar curvature(Tensor abc) {
    Optional<Scalar> optional = SignedCurvature2D.of( //
        abc.get(0).extract(0, 2), //
        abc.get(1).extract(0, 2), //
        abc.get(2).extract(0, 2));
    return optional.isPresent() //
        ? optional.get()
        : QUANTITY_MAPPER.apply(abc.Get(0, 0)); // numerical zero with reciprocal/negated unit of abc(0, 0)
  }
}
