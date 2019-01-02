// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.symlink.SymLink;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** visualization of the geometric geodesic average */
/* package */ class GeodesicAverageRender {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.22);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();

  // ---
  public static RenderInterface of(GeodesicInterface geodesicInterface, SymLink symLink) {
    return new GeodesicAverageRender(geodesicInterface).new Link(symLink);
  }

  // ---
  private final GeodesicInterface geodesicInterface;

  private GeodesicAverageRender(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  private class Link implements RenderInterface {
    private final SymLink symLink;

    public Link(SymLink symLink) {
      this.symLink = symLink;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      if (symLink.isNode())
        return;
      // ---
      new Link(symLink.lP).render(geometricLayer, graphics);
      new Link(symLink.lQ).render(geometricLayer, graphics);
      {
        Tensor posP = symLink.lP.getPosition(geodesicInterface);
        Tensor posQ = symLink.lQ.getPosition(geodesicInterface);
        graphics.setColor(new Color(0, 128 + 64, 0, 255));
        {
          Tensor tensor = Subdivide.of(RealScalar.ZERO, symLink.lambda, 91) //
              .map(geodesicInterface.curve(posP, posQ));
          Path2D path2d = geometricLayer.toPath2D(tensor);
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.draw(path2d);
        }
        {
          Tensor tensor = Subdivide.of(symLink.lambda, RealScalar.ONE, 91) //
              .map(geodesicInterface.curve(posP, posQ));
          Path2D path2d = geometricLayer.toPath2D(tensor);
          graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
          graphics.draw(path2d);
        }
      }
      // ---
      Tensor xya = symLink.getPosition(geodesicInterface);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      // TODO not generic
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }
}
