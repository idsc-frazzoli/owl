// code by ob
package ch.ethz.idsc.sophus.curve;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** CatmullRom denotes the function that is defined
 * by control points over a sequence of knots.
 * 
 * Reference:
 * Freeform Curves on Spheres of Arbitrary Dimension
 * Scott Schaefer and Ron Goldman
 * Proceedings of Pacific Graphics 2005, pages 160-162
 * http://faculty.cs.tamu.edu/schaefer/research/sphereCurves.pdf */
public class GeodesicCatmullRom implements ScalarTensorFunction {
  /** @param geodesicInterface non null */
  public static GeodesicCatmullRom of(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    if (control.length() < 4)
      throw TensorRuntimeException.of(control);
    return new GeodesicCatmullRom(Objects.requireNonNull(geodesicInterface), VectorQ.require(knots), control);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor control;
  private final Tensor knots;

  // TODO OB documentation; require knots.length = 4;
  /** @param control points of length 4 */
  /* package */ GeodesicCatmullRom(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    this.knots = knots;
    this.control = control;
  }

  private int getIndex(Scalar t) {
    // t in [tn-1, tn), the exclusive tn avoids ambiguity for t = tn
    for (int index = 0; index < knots.length(); index++)
      if (Scalars.lessEquals(knots.Get(index), t) && Scalars.lessThan(t, knots.Get(index + 1)))
        return index;
    return -1;
  }

  @Override
  /** applying CRM to a chosen t in the complete knot sequence is [tn-2, tn-1, tn, tn+1] [tn-1, tn)
   * is constructed from the control points [pn-2, pn-1, pn, pn+1] */
  public synchronized Tensor apply(Scalar t) {
    // Since CMR only uses four control points we select the four corresponding to the parameter t
    int hi = Math.max(getIndex(t), 1);
    Tensor selectedKnots = knots.extract(hi - 1, hi + 3);
    Tensor selectedControl = control.extract(hi - 1, hi + 3);
    // First pyramidal layer
    Tensor a = Tensors.empty();
    for (int index = 0; index < 3; index++) {
      Scalar num = t.subtract(selectedKnots.Get(index));
      Scalar denum = selectedKnots.Get(index + 1).subtract(selectedKnots.Get(index));
      a.append(geodesicInterface.split(selectedControl.get(index), selectedControl.get(index + 1), num.divide(denum)));
    }
    // Second pyramidal layer
    Tensor b = Tensors.empty();
    for (int index = 0; index < 2; index++) {
      Scalar num = t.subtract(selectedKnots.Get(index));
      Scalar denum = selectedKnots.Get(index + 2).subtract(selectedKnots.Get(index));
      b.append(geodesicInterface.split(a.get(index), a.get(index + 1), num.divide(denum)));
    }
    // Third and final pyramidal layer
    Scalar num = t.subtract(selectedKnots.Get(1));
    Scalar denum = selectedKnots.Get(2).subtract(selectedKnots.Get(1));
    return geodesicInterface.split(b.get(0), b.get(1), num.divide(denum));
  }

  public Tensor control() {
    return control.unmodifiable();
  }

  public Tensor knots() {
    return knots.unmodifiable();
  }
}
