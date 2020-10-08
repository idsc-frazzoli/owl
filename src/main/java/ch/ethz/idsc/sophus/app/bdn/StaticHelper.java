// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.app.api.IterativeGenesis;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.ClipCover;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ enum StaticHelper {
  ;
  public static BufferedImage fuseImages(GeodesicDisplay geodesicDisplay, ArrayPlotRender arrayPlotRender, int refinement, int sequence_length) {
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    BufferedImage foreground = arrayPlotRender.export();
    BufferedImage background = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = background.createGraphics();
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
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

  public static BufferedImage levelsImage(GeodesicDisplay geodesicDisplay, Tensor sequence, int res, ColorDataGradient colorDataGradient, int max) {
    TensorUnaryOperator tuo = IterativeGenesis.counts(geodesicDisplay.vectorLogManifold(), sequence, max);
    int sequence_length = IterativeGenesis.values().length;
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence_length);
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    Tensor wgs = geodesicArrayPlot.raster(res, tuo, fallback);
    ArrayPlotRender arrayPlotRender = arrayPlotFromTensor(wgs, 1, false, colorDataGradient);
    return fuseImages(geodesicDisplay, arrayPlotRender, res, sequence_length);
  }

  public static ArrayPlotRender arrayPlotFromTensor(Tensor wgs, int magnification, boolean coverZero, ColorDataGradient colorDataGradient) {
    List<Integer> dims = Dimensions.of(wgs);
    Tensor wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    Rescale rescale = new Rescale(wgp);
    Clip clip = ClipCover.of(rescale.scalarSummaryStatistics());
    return new ArrayPlotRender( //
        rescale.result(), //
        coverZero ? ClipCover.of(clip, RealScalar.ZERO) : clip, //
        colorDataGradient, magnification);
  }
}
