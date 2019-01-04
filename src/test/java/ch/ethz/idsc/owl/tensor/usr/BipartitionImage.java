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
import ch.ethz.idsc.tensor.opt.HungarianAlgorithm;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;

// 4 22 35
enum BipartitionImage {
  ;
  private static Tensor image(int seed) {
    Random random = new Random(seed);
    Tensor points1 = RandomVariate.of(UniformDistribution.unit(), random, 9, 2);
    Tensor points2 = RandomVariate.of(UniformDistribution.unit(), random, 13, 2);
    // Tensor cost = points1.stream().map(p -> Tensor.of(points2.stream().map(r -> Norm._2.between(p, r))));
    Tensor matrix = Tensors.matrix((i, j) -> Norm._2.between(points1.get(i), points2.get(j)), points1.length(), points2.length());
    HungarianAlgorithm hungarianAlgorithm = HungarianAlgorithm.of(matrix);
    GeometricLayer geometricLayer = GeometricLayer.of(StaticHelper.SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setColor(new Color(128 * 0, 128 * 0, 255));
    int[] m = hungarianAlgorithm.matching();
    for (int index = 0; index < m.length; ++index) {
      Path2D path2d = geometricLayer.toPath2D(Tensors.of(points1.get(index), points2.get(m[index])));
      path2d.closePath();
      graphics.draw(path2d);
    }
    graphics.setColor(Color.RED);
    for (Tensor point : points1) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.GREEN);
    for (Tensor point : points2) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    File folder = UserHome.Pictures(BipartitionImage.class.getSimpleName());
    folder.mkdir();
    for (int seed = 0; seed < 50; ++seed) {
      Tensor tensor = image(seed);
      Export.of(new File(folder, String.format("%03d.png", seed)), tensor);
    }
    {
      Export.of(UserHome.Pictures(BipartitionImage.class.getSimpleName() + ".png"), image(35));
    }
  }
}
