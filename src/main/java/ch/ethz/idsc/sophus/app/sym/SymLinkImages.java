// code by jph, ob
package ch.ethz.idsc.sophus.app.sym;

import java.awt.Font;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.itp.DeBoor;

public enum SymLinkImages {
  ;
  public static final Font FONT_SMALL = new Font(Font.DIALOG, Font.PLAIN, 11);

  public static SymLinkImage deboor(Tensor knots, int length, Scalar scalar) {
    Tensor vector = Tensor.of(IntStream.range(0, length).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = DeBoor.of(SymGeodesic.INSTANCE, knots, vector);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, FONT_SMALL);
    symLinkImage.title("DeBoor at " + scalar);
    return symLinkImage;
  }

  public static SymLinkImage ofGC(ScalarUnaryOperator smoothingKernel, int radius) {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(SymGeodesic.INSTANCE, smoothingKernel);
    Tensor vector = Tensor.of(IntStream.range(0, 2 * radius + 1).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, FONT_SMALL);
    symLinkImage.title(smoothingKernel + "[" + (2 * radius + 1) + "]");
    return symLinkImage;
  }

  /* package */ public static SymLinkImage symLinkImageGBSF(int degree, int length, Scalar scalar) {
    Tensor vector = Tensor.of(IntStream.range(0, length).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = GeodesicBSplineFunction.of(SymGeodesic.INSTANCE, degree, vector);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, FONT_SMALL);
    symLinkImage.title("DeBoor[" + degree + "] at " + scalar);
    return symLinkImage;
  }
}
