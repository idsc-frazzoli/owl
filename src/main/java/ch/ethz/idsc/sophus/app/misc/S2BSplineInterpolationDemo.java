// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineInterpolation;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum S2BSplineInterpolationDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor target = Tensors.fromString("{{1,0,0},{0,1,0},{0,0,1},{-1,0,0}}");
    Export.of(HomeDirectory.file("Documents", "s2", "target.csv"), target.map(Round._6));
    AbstractBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(SnGeodesic.INSTANCE, 2, target);
    Iteration iteration = geodesicBSplineInterpolation.untilClose(Chop._08, 100);
    Tensor control = iteration.control();
    Chop._12.requireClose(control.get(0), target.get(0));
    Chop._12.requireClose(control.get(3), target.get(3));
    MatrixQ.require(control);
    Export.of(HomeDirectory.file("Documents", "s2", "control.csv"), control.map(Round._6));
    GeodesicBSplineFunction geodesicBSplineFunction = GeodesicBSplineFunction.of(SnGeodesic.INSTANCE, 2, control);
    Tensor curve = Subdivide.of(0, control.length() - 1, 200).map(geodesicBSplineFunction);
    Export.of(HomeDirectory.file("Documents", "s2", "curve.csv"), curve.map(Round._6));
  }
}
