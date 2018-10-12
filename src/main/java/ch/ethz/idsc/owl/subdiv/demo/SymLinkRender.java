// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sig.WindowFunctions;

public class SymLinkRender implements RenderInterface {
  private final SymLink symLink;

  public SymLinkRender(SymLink root) {
    this.symLink = root;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor here = symLink.getPosition();
    if (symLink instanceof SymNode) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(here));
      Path2D path2d = geometricLayer.toPath2D(CirclePoints.of(10).multiply(RealScalar.of(.1)));
      path2d.closePath();
      graphics.setColor(Color.BLACK);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    } else {
      {
        new SymLinkRender(symLink.lP).render(geometricLayer, graphics);
        Tensor there = symLink.lP.getPosition();
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
        graphics.draw(path2d);
      }
      {
        new SymLinkRender(symLink.lQ).render(geometricLayer, graphics);
        Tensor there = symLink.lQ.getPosition();
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
        graphics.draw(path2d);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.fromString("{{30,0,10},{0,-30,10},{0,0,1}}"));
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    // ---
    for (WindowFunctions wf : WindowFunctions.values())
      if (wf.equals(WindowFunctions.BINOMIAL)) {
        TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(SymGeodesic.INSTANCE, wf);
        Tensor vector = Tensor.of(IntStream.range(0, 2 * 10 + 1).mapToObj(SymScalar::of));
        Tensor tensor = tensorUnaryOperator.apply(vector);
        System.out.println(tensor);
        SymLink root = SymLink.build((SymScalar) tensor);
        SymLinkRender symLinkRender = new SymLinkRender(root);
        symLinkRender.render(geometricLayer, graphics);
      }
    ImageIO.write(bufferedImage, "png", UserHome.Pictures("export/tree.png"));
  }
}
