// code by ob
package ch.ethz.idsc.sophus.curve;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** CatmullRom denotes the function that is defined by control points over a sequence of knots.
 * 
 * Reference: "Freeform Curves on Spheres of Arbitrary Dimension"
 * by Scott Schaefer and Ron Goldman in Proceedings of Pacific Graphics 2005, pages 160-162
 * http://faculty.cs.tamu.edu/schaefer/research/sphereCurves.pdf */
public class GeodesicCatmullRom implements ScalarTensorFunction {
  /** @param geodesicInterface non null
   * @param knots
   * @param control points of length >= 4 */
  public static GeodesicCatmullRom of(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    if (control.length() < 4)
      throw TensorRuntimeException.of(control);
    return new GeodesicCatmullRom(Objects.requireNonNull(geodesicInterface), VectorQ.require(knots), control);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor control;
  private final Tensor knots;
  private final NavigableMap<Scalar, Integer> navigableMap = new TreeMap<>();

  private GeodesicCatmullRom(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    int index = -1;
    for (Tensor knot : knots)
      navigableMap.put(knot.Get(), ++index);
    this.knots = knots;
    this.control = control;
    if (knots.length() != control.length())
      throw TensorRuntimeException.of(knots, control);
  }

  /** applying CRM to a chosen t in the complete knot sequence is [tn-2, tn-1, tn, tn+1] [tn-1, tn)
   * is constructed from the control points [pn-2, pn-1, pn, pn+1] */
  @Override
  public Tensor apply(Scalar scalar) {
    // Since CMR only uses four control points we select the four corresponding to the parameter t
    int hi = navigableMap.floorEntry(scalar).getValue();
    Tensor selectedKnots = knots.extract(hi - 1, hi + 3);
    Tensor selectedControl = control.extract(hi - 1, hi + 3);
    // First pyramidal layer
    Tensor[] a = new Tensor[3];
    for (int index = 0; index < 3; ++index)
      a[index] = geodesicInterface.split( //
          selectedControl.get(index), //
          selectedControl.get(index + 1), //
          interp(selectedKnots.Get(index), selectedKnots.Get(index + 1), scalar));
    // Second pyramidal layer
    Tensor[] b = new Tensor[2];
    for (int index = 0; index < 2; ++index)
      b[index] = geodesicInterface.split( //
          a[index], //
          a[index + 1], //
          interp(selectedKnots.Get(index), selectedKnots.Get(index + 2), scalar));
    // Third and final pyramidal layer
    return geodesicInterface.split( //
        b[0], //
        b[1], //
        interp(selectedKnots.Get(1), selectedKnots.Get(2), scalar));
  }

  public Tensor control() {
    return control.unmodifiable();
  }

  public Tensor knots() {
    return knots.unmodifiable();
  }

  /** @param lo
   * @param hi
   * @param val
   * @return (val - lo) / (hi - lo) */
  private static Scalar interp(Scalar lo, Scalar hi, Scalar val) {
    return val.subtract(lo).divide(hi.subtract(lo));
  }
}
