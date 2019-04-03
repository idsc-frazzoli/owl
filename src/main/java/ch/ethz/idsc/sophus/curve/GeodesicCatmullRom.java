// code by ob
package ch.ethz.idsc.sophus.curve;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
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
  /** @param geodesicInterface non null
   * @param alpha = 0: uniform knot vector; alpha = 0.5: centripetal; alpha = 1: chordal
   * @return
   * @throws Exception if given control contains more than 4 points */
  public static GeodesicCatmullRom of(GeodesicInterface geodesicInterface, Tensor control, Scalar alpha) {
    if (control.length() != 4)
      throw TensorRuntimeException.of(control);
    return new GeodesicCatmullRom(Objects.requireNonNull(geodesicInterface), control, alpha);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor control;
  private final Scalar alpha;

  /** @param control points of length 4
   * @param alpha knot parametrization [0,1] */
  /* package */ GeodesicCatmullRom(GeodesicInterface geodesicInterface, Tensor control, Scalar alpha) {
    this.geodesicInterface = geodesicInterface;
    this.control = control;
    this.alpha = alpha;
  }

  private static Tensor knots(Tensor sequence, Scalar alpha) {
    // TODO OB: make geodesic display depending on geod.Interface => suiting parametric distance;
    Tensor t = Tensors.vector(0);
    if (sequence.length() != 4)
      throw TensorRuntimeException.of(sequence);
    for (int index = 0; index < 3; index++) {
      t.append(
          RealScalar.of(Math.pow(Se2ParametricDistance.of(sequence.get(index), sequence.get(index + 1)).number().floatValue(), alpha.number().floatValue()))
              .add(t.Get(index)));
    }
    return t;
  }

  @Override
  /** t in [t1,t2] */
  public Tensor apply(Scalar t) {
    Tensor knots = knots();
    // TODO OB: these three steps can be merged in one by introducing a second loop
    // First pyramidal layer
    Tensor A = Tensors.empty();
    for (int index = 0; index < 3; index++) {
      Scalar num = t.subtract(knots.Get(index));
      Scalar denum = knots.Get(index + 1).subtract(knots.Get(index));
      A.append(geodesicInterface.split(control.get(index), control.get(index + 1), num.divide(denum)));
    }
    // Second pyramidal layer
    Tensor B = Tensors.empty();
    for (int index = 0; index < 2; index++) {
      Scalar num = t.subtract(knots.Get(index));
      Scalar denum = knots.Get(index + 2).subtract(knots.Get(index));
      B.append(geodesicInterface.split(A.get(index), A.get(index + 1), num.divide(denum)));
    }
    // Third and final pyramidal layer
    Scalar num = t.subtract(knots.get(1));
    Scalar denum = knots.Get(2).subtract(knots.Get(1));
    Tensor C = geodesicInterface.split(B.get(0), B.get(1), num.divide(denum));
    return C;
  }

  public Tensor control() {
    return control.unmodifiable();
  }

  public Tensor knots() {
    return knots(control, alpha);
  }

  public static void main(String[] args) {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    control.append(Tensors.vector(0, 0, 0));
    for (int i = 0; i < 10; i++) {
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()).add(control.get(i)));
    }
    Scalar alpha = RealScalar.ONE;
    for (int i = 0; i < control.length() - 4; i++) {
      GeodesicCatmullRom geodesicCatmullRom = new GeodesicCatmullRom(geodesicInterface, control.extract(i, i + 4), alpha);
      // Control Points
      System.err.println(control.get(i + 1));
      Tensor knots = geodesicCatmullRom.knots();
      // Interpolations
      for (int j = 0; j < 10; j++) {
        System.out.println(geodesicCatmullRom.apply(knots.Get(1).add(knots.get(2).subtract(knots.get(1)).multiply(RationalScalar.of(j, 10)))));
      }
    }
  }
}
