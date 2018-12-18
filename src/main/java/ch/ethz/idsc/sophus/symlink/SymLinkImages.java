// code by jph
package ch.ethz.idsc.sophus.symlink;

import java.awt.Font;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum SymLinkImages {
  ;
  private static final Font FONT_SMALL = new Font(Font.DIALOG, Font.PLAIN, 11);

  public static SymLinkImage deBoor(int degree, int length, Scalar scalar) {
    Tensor vector = Tensor.of(IntStream.range(0, length).mapToObj(SymScalar::leaf));
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(SymGeodesic.INSTANCE, degree, vector);
    Tensor tensor = geodesicBSplineFunction.apply(scalar);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, FONT_SMALL);
    symLinkImage.title("DeBoor_" + degree + "[" + scalar + "]");
    return symLinkImage;
  }
}
