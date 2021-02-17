// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gui.ren.ArrayPlotRender;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ enum StaticHelper {
  ;
  public static BufferedImage fuseImages(ManifoldDisplay geodesicDisplay, ArrayPlotRender arrayPlotRender, int refinement, int sequence_length) {
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    BufferedImage foreground = arrayPlotRender.export();
    BufferedImage background = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = background.createGraphics();
    if (geodesicDisplay instanceof S2Display) {
      Tensor matrix = geodesicArrayPlot.pixel2model(new Dimension(refinement, refinement));
      GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(matrix));
      for (int count = 0; count < sequence_length; ++count) {
        GeodesicDisplayRender.render_s2(geometricLayer, graphics);
        geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(2, 0)));
      }
    }
    graphics.drawImage(foreground, 0, 0, null);
    return background;
  }

  public static ArrayPlotRender arrayPlotFromTensor(Tensor wgs, int magnification, boolean coverZero, ColorDataGradient colorDataGradient) {
    List<Integer> dims = Dimensions.of(wgs);
    Tensor wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    Rescale rescale = new Rescale(wgp);
    Clip clip = rescale.scalarSummaryStatistics().getClip();
    return new ArrayPlotRender( //
        rescale.result(), //
        coverZero ? ClipCover.of(clip, RealScalar.ZERO) : clip, //
        colorDataGradient, magnification);
  }
}
