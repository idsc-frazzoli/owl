// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymLinkImage;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.opt.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/* package */ enum GeodesicCenterSymLinkImage {
  ;
  public static SymLinkImage of(SmoothingKernel smoothingKernel, int radius) {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(SymGeodesic.INSTANCE, smoothingKernel);
    Tensor vector = Tensor.of(IntStream.range(0, 2 * radius + 1).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, SymLinkImages.FONT_SMALL);
    symLinkImage.title(smoothingKernel.name() + "[" + (2 * radius + 1) + "]");
    return symLinkImage;
  }
}
