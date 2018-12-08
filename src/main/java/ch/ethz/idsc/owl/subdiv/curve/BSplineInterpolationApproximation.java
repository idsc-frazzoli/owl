// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;

public class BSplineInterpolationApproximation implements Serializable {
  private final GeodesicInterface geodesicInterface;
  private final int degree;

  public BSplineInterpolationApproximation(GeodesicInterface geodesicInterface, int degree) {
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
  }

  public Tensor fixed(final Tensor target, int limit) {
    Tensor domain = Range.of(0, target.length());
    Tensor control = target;
    for (int count = 0; count < limit; ++count) {
      Tensor refine = domain.map(geodesicBSplineFunction(control));
      Tensor error = refine.subtract(target);
      control = control.subtract(error);
    }
    return control;
  }

  public GeodesicBSplineFunction geodesicBSplineFunction(Tensor control) {
    return GeodesicBSplineFunction.of(geodesicInterface, degree, control);
  }
}
