// code by jph
package ch.ethz.idsc.sophus.app.analysis;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineInterpolation;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum S2BSplineInterpolationDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor target = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}}");
    File folder = HomeDirectory.Documents("s2");
    folder.mkdir();
    Export.of(new File(folder, "target.csv"), target.map(Round._6));
    Geodesic geodesicInterface = S2Display.INSTANCE.geodesicInterface();
    AbstractBSplineInterpolation abstractBSplineInterpolation = //
        new GeodesicBSplineInterpolation(geodesicInterface, 2, target);
    Iteration iteration = abstractBSplineInterpolation.untilClose(Chop._08, 100);
    Tensor control = iteration.control();
    Tolerance.CHOP.requireClose(control.get(0), target.get(0));
    Tolerance.CHOP.requireClose(control.get(3), target.get(3));
    MatrixQ.require(control);
    Export.of(new File(folder, "control.csv"), control.map(Round._6));
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(geodesicInterface, 2, control);
    Tensor curve = Subdivide.of(0, control.length() - 1, 200).map(geodesicBSplineFunction);
    Export.of(new File(folder, "curve.csv"), curve.map(Round._6));
  }
}
