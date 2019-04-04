// code by ob
package ch.ethz.idsc.sophus.curve;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
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
    return new GeodesicCatmullRom(Objects.requireNonNull(geodesicInterface), VectorQ.require(knots), control);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor control;
  private final Tensor knots;

  // TODO OB documentation
  /** @param control points of length 4 */
  /* package */ GeodesicCatmullRom(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    this.knots = knots;
    this.control = control;
  }

  private int getIndex(Scalar t) {
    int index = 0;
    while (Scalars.lessThan(t, knots.Get(index))) {
      index++;
    }
    return index;
  }

  @Override
  /** applying CRM to a chosen t in the complete knot sequence is [tn-2, tn-1, tn, tn+1] (with tn-1 < t tn)
   * is constructed from the control points [pn-2, pn-1, pn, pn+1] */
  public Tensor apply(Scalar t) {
    // Since CMR only uses four control points we select the four corresponding to the parameter t
    int hi = getIndex(t);
    Tensor selectedKnots = knots.extract(hi - 2, hi + 2);
    Tensor selectedControl = control.extract(hi - 2, hi + 2);
    // First pyramidal layer
    Tensor A = Tensors.empty();
    for (int index = 0; index < 3; index++) {
      Scalar num = t.subtract(selectedKnots.Get(index));
      Scalar denum = selectedKnots.Get(index + 1).subtract(selectedKnots.Get(index));
      A.append(geodesicInterface.split(selectedControl.get(index), selectedControl.get(index + 1), num.divide(denum)));
    }
    // Second pyramidal layer
    Tensor B = Tensors.empty();
    for (int index = 0; index < 2; index++) {
      Scalar num = t.subtract(selectedKnots.Get(index));
      Scalar denum = selectedKnots.Get(index + 2).subtract(selectedKnots.Get(index));
      B.append(geodesicInterface.split(A.get(index), A.get(index + 1), num.divide(denum)));
    }
    // Third and final pyramidal layer
    Scalar num = t.subtract(selectedKnots.get(1));
    Scalar denum = selectedKnots.Get(2).subtract(selectedKnots.Get(1));
    return geodesicInterface.split(B.get(0), B.get(1), num.divide(denum));
  }

  public Tensor control() {
    return control.unmodifiable();
  }

  public Tensor knots() {
    return knots.unmodifiable();
  }
}
