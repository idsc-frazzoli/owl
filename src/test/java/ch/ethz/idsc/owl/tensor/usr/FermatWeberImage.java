// code by jph
package ch.ethz.idsc.owl.tensor.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.SpatialMedian;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

enum FermatWeberImage {
  ;
  private static Tensor image(int seed) {
    Random random = new Random(seed);
    Tensor points = RandomVariate.of(UniformDistribution.unit(), random, 15, 2);
    Tensor solution = SpatialMedian.with(1e-10).uniform(points).get();
    GeometricLayer geometricLayer = GeometricLayer.of(DemoHelper.SE2);
    BufferedImage bufferedImage = DemoHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    {
      graphics.setColor(new Color(128, 128, 255));
      for (Tensor point : points) {
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(solution, point));
        graphics.draw(path2d);
      }
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(solution));
      Path2D path2d = geometricLayer.toPath2D(DemoHelper.POINT);
      path2d.closePath();
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.RED);
    for (Tensor point : points) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(point));
      Path2D path2d = geometricLayer.toPath2D(DemoHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    File folder = UserHome.Pictures(FermatWeberImage.class.getSimpleName());
    folder.mkdir();
    for (int seed = 0; seed < 50; ++seed) {
      Tensor image = image(seed);
      Export.of(new File(folder, String.format("%03d.png", seed)), image);
    }
    {
      Export.of(UserHome.Pictures(FermatWeberImage.class.getSimpleName() + ".png"), image(22));
    }
  }
}
