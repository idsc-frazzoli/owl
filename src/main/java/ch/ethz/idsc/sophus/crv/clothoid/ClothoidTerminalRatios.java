// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Chop;

/** clothoid is tangent at start and end points */
public class ClothoidTerminalRatios implements Serializable {
  public static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(Clothoid2.INSTANCE, 1);
  private static final TensorUnaryOperator HEAD = //
      value -> CURVE_SUBDIVISION.string(value.extract(0, 2));
  private static final TensorUnaryOperator TAIL = //
      value -> CURVE_SUBDIVISION.string(value.extract(value.length() - 2, value.length()));
  private static final QuantityMapper QUANTITY_MAPPER = new QuantityMapper(Scalar::zero, Unit::negate);
  static final Chop CHOP = Chop._03;
  /** typically 13, or 14 iterations are needed to reach precision up 1e-3 */
  static final int MAX_ITER = 18;

  /** @param beg of the form {beg_x, beg_y, beg_heading}
   * @param end of the form {end_x, end_y, end_heading}
   * @return */
  public static ClothoidTerminalRatios of(Tensor beg, Tensor end) {
    final Tensor init = CURVE_SUBDIVISION.string(Tensors.of(beg, end));
    Scalar head = curvature(init);
    {
      Tensor hseq = init;
      for (int depth = 1; depth < MAX_ITER; ++depth) {
        hseq = HEAD.apply(hseq);
        Scalar next = curvature(hseq);
        if (CHOP.close(head, next)) {
          head = next;
          break;
        }
        head = next;
      }
    }
    Scalar tail = curvature(init);
    {
      Tensor tseq = init;
      for (int depth = 1; depth < MAX_ITER; ++depth) {
        tseq = TAIL.apply(tseq);
        Scalar next = curvature(tseq);
        if (CHOP.close(tail, next)) {
          tail = next;
          break;
        }
        tail = next;
      }
    }
    return new ClothoidTerminalRatios(head, tail);
  }

  // ---
  private final Scalar head;
  private final Scalar tail;

  /** @param beg
   * @param end
   * @param depth strictly positive */
  public ClothoidTerminalRatios(Tensor beg, Tensor end, int depth) {
    this( //
        curvature(Nest.of(HEAD, Tensors.of(beg, end), depth)), //
        curvature(Nest.of(TAIL, Tensors.of(beg, end), depth)));
  }

  private ClothoidTerminalRatios(Scalar head, Scalar tail) {
    this.head = head;
    this.tail = tail;
  }

  public Scalar head() {
    return head;
  }

  public Scalar tail() {
    return tail;
  }

  /** @return tail - head */
  public Scalar difference() {
    return tail.subtract(head);
  }

  /* package for testing */ static Scalar curvature(Tensor abc) {
    Optional<Scalar> optional = SignedCurvature2D.of( //
        abc.get(0).extract(0, 2), //
        abc.get(1).extract(0, 2), //
        abc.get(2).extract(0, 2));
    return optional.isPresent() //
        ? optional.get()
        : QUANTITY_MAPPER.apply(abc.Get(0, 0));
  }
}
