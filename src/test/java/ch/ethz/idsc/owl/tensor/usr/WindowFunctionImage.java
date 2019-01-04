// code by jph
package ch.ethz.idsc.owl.tensor.usr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;

// 3
enum WindowFunctionImage {
  ;
  private static Tensor image() {
    Tensor SE2 = Tensors.fromString("{{180, 0, 6+180/2}, {0, -180, 186}, {0, 0, 1}}").unmodifiable();
    GeometricLayer geometricLayer = GeometricLayer.of(SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setColor(Color.RED);
    Tensor domain = Subdivide.of(-0.5, 0.5, 180);
    graphics.setStroke(new BasicStroke(1.5f));
    ColorDataIndexed colorDataIndexedF = ColorDataLists._097.cyclic();
    ColorDataIndexed colorDataIndexedP = colorDataIndexedF.deriveWithAlpha(128 + 64);
    int piy = 10;
    int index = 0;
    SmoothingKernel[] sks = new SmoothingKernel[] { //
        SmoothingKernel.GAUSSIAN, //
        SmoothingKernel.HAMMING, //
        SmoothingKernel.BLACKMAN, //
        SmoothingKernel.NUTTALL, //
    };
    graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
    for (SmoothingKernel sk : sks) {
      graphics.setColor(colorDataIndexedP.getColor(index));
      Tensor tensor = domain.map(sk.windowFunction());
      Tensor points = Transpose.of(Tensors.of(domain, tensor));
      Path2D path2d = geometricLayer.toPath2D(points);
      graphics.draw(path2d);
      graphics.setColor(colorDataIndexedF.getColor(index));
      graphics.drawString(sk.name(), 0, piy);
      piy += 10;
      ++index;
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    Export.of(UserHome.Pictures(WindowFunctionImage.class.getSimpleName() + ".png"), image());
  }
}
