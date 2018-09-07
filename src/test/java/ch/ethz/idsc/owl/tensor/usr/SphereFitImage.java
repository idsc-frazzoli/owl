// code by jph
package ch.ethz.idsc.owl.tensor.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.SphereFit;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

enum SphereFitImage {
  ;
  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(192, 192, BufferedImage.TYPE_4BYTE_ABGR);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.fromString("{{192,0,0},{0,-192,192},{0,0,1}}"));
    Tensor points = RandomVariate.of(UniformDistribution.unit(), 10, 2);
    Optional<Tensor> optional = SphereFit.of(points);
    Tensor center = optional.get().get(0);
    Scalar radius = optional.get().Get(1);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 192, 192);
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setColor(Color.RED);
    for (Tensor point : points) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(point));
      Path2D path2d = geometricLayer.toPath2D(CirclePoints.of(10).multiply(RealScalar.of(.01)));
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(center));
      Path2D path2d = geometricLayer.toPath2D(CirclePoints.of(100).multiply(radius));
      path2d.closePath();
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    Export.of(UserHome.Pictures("spherefit.png"), ImageFormat.from(bufferedImage));
  }
}
