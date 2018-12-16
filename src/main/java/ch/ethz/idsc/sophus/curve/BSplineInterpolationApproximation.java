// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;

public class BSplineInterpolationApproximation implements Serializable {
  private final LieGroup lieGroup;
  private final GeodesicInterface geodesicInterface;
  private final int degree;

  public BSplineInterpolationApproximation(LieGroup lieGroup, GeodesicInterface geodesicInterface, int degree) {
    this.lieGroup = lieGroup;
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
  }

  public Tensor fixed(final Tensor target, int limit) {
    Tensor domain = Range.of(0, target.length());
    Tensor control = target;
    for (int count = 0; count < limit; ++count) {
      Tensor refine = domain.map(geodesicBSplineFunction(control));
      // Tensor error = target.subtract(refine);
      // control = control.add(error);
      Tensor _control = control;
      control = Tensor.of(IntStream.range(0, control.length()) //
          .mapToObj(i -> mix(_control.get(i), refine.get(i), target.get(i))));
    }
    return control;
  }

  public GeodesicBSplineFunction geodesicBSplineFunction(Tensor control) {
    return GeodesicBSplineFunction.of(geodesicInterface, degree, control);
  }

  public Tensor mix(Tensor c, Tensor r, Tensor t) {
    return lieGroup.element(c).combine(lieGroup.element(r).inverse().combine(t));
  }
}
