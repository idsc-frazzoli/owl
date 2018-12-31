// code by jph
package ch.ethz.idsc.sophus.curve;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.BSplineInterpolation;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

/** computation of control points that result in a limit curve
 * that interpolates given target points.
 * 
 * Hint: when target coordinates are specified in exact precision,
 * the iteration may involve computing fractions consisting of large
 * integers. Therefore, it is recommended to provide target points
 * in numeric precision.
 * 
 * @see BSplineInterpolation */
public class LieGroupBSplineInterpolation implements TensorUnaryOperator {
  /* package */ static final Chop CHOP_DEFAULT = Chop._12;
  // ---
  private final LieGroup lieGroup;
  private final GeodesicInterface geodesicInterface;
  private final int degree;

  public LieGroupBSplineInterpolation(LieGroup lieGroup, GeodesicInterface geodesicInterface, int degree) {
    this.lieGroup = lieGroup;
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
  }

  GeodesicBSplineFunction geodesicBSplineFunction(Tensor control) {
    return GeodesicBSplineFunction.of(geodesicInterface, degree, control);
  }

  private Tensor move(Tensor prev, Tensor eval, Tensor goal) {
    return lieGroup.element(prev).combine(lieGroup.element(eval).inverse().combine(goal));
  }

  public class Iteration {
    private final Tensor target;
    private final Tensor control;

    private Iteration(Tensor target, Tensor control) {
      this.target = target;
      this.control = control;
    }

    public Iteration step() {
      Tensor refine = Range.of(0, target.length()).map(geodesicBSplineFunction(control));
      return new Iteration(target, Tensor.of(IntStream.range(0, control.length()) //
          .mapToObj(index -> move(control.get(index), refine.get(index), target.get(index)))));
    }

    public Tensor control() {
      return control;
    }
  }

  public Iteration start(Tensor target) {
    return new Iteration(target, target);
  }

  @Override
  public Tensor apply(Tensor target) {
    Iteration iteration = start(target);
    Tensor p = iteration.control();
    for (int count = 0; count < 100; ++count) {
      iteration = iteration.step();
      Tensor q = iteration.control();
      if (CHOP_DEFAULT.allZero(N.DOUBLE.of(p.subtract(q))))
        break;
      p = q;
    }
    return iteration.control;
  }
}
