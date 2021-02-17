// code by jph
package ch.ethz.idsc.sophus.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.fit.WeiszfeldMethod;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum SpatialMedianImage {
  ;
  private static class Pixel2Coord {
    private static final Tensor INVERSE = Inverse.of(StaticHelper.SE2);
    // ---
    private final Tensor points;

    public Pixel2Coord(Tensor points) {
      this.points = points;
    }

    Scalar dist(Scalar x, Scalar y) {
      Tensor p = INVERSE.dot(Tensors.of(x, y, RealScalar.ONE)).extract(0, 2);
      return points.stream().map(r -> Vector2Norm.between(r, p)).reduce(Scalar::add).get();
    }
  }

  private static Tensor image(int seed) {
    Random random = new Random(seed);
    Tensor points = RandomVariate.of(UniformDistribution.unit(), random, 15, 2);
    Optional<Tensor> optional = WeiszfeldMethod.with(Chop._10).uniform(points);
    GeometricLayer geometricLayer = GeometricLayer.of(StaticHelper.SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    if (optional.isPresent()) {
      Tensor solution = optional.get();
      Tensor px = Range.of(0, 192);
      Tensor py = Range.of(0, 192);
      Pixel2Coord some = new Pixel2Coord(points);
      Tensor image = Tensors.matrix((j, i) -> some.dist(px.Get(i), py.Get(j)), px.length(), py.length());
      BufferedImage background = ImageFormat.of(ArrayPlot.of(image, ColorDataGradients.DENSITY));
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.drawImage(background, 0, 0, null);
      RenderQuality.setQuality(graphics);
      {
        graphics.setColor(new Color(128, 128, 255));
        for (Tensor point : points) {
          Path2D path2d = geometricLayer.toPath2D(Tensors.of(solution, point));
          graphics.draw(path2d);
        }
      }
      {
        graphics.setColor(Color.GREEN);
        geometricLayer.pushMatrix(Se2Matrix.translation(solution));
        Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
        path2d.closePath();
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      graphics.setColor(Color.RED);
      for (Tensor point : points) {
        geometricLayer.pushMatrix(Se2Matrix.translation(point));
        Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures(SpatialMedianImage.class.getSimpleName());
    folder.mkdir();
    for (int seed = 30; seed < 40; ++seed) {
      Tensor image = image(seed);
      Export.of(new File(folder, String.format("%03d.png", seed)), image);
    }
    {
      Export.of(HomeDirectory.Pictures(SpatialMedianImage.class.getSimpleName() + ".png"), image(35));
    }
  }
}
