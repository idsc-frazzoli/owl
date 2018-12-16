// code by jph
package ch.ethz.idsc.sophus.symlink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ class SymGeoRender implements RenderInterface {
  private static final GeodesicInterface GEODESIC_INTERFACE = Se2CoveringGeodesic.INSTANCE;
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.22);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final SymLink symLink;

  public SymGeoRender(SymLink symLink) {
    this.symLink = symLink;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (symLink instanceof SymNode) {
      // ---
    } else {
      new SymGeoRender(symLink.lP).render(geometricLayer, graphics);
      new SymGeoRender(symLink.lQ).render(geometricLayer, graphics);
      {
        Tensor posP = symLink.lP.getPosition(GEODESIC_INTERFACE);
        Tensor posQ = symLink.lQ.getPosition(GEODESIC_INTERFACE);
        graphics.setColor(new Color(0, 128 + 64, 0, 255));
        {
          Tensor tensor = Subdivide.of(RealScalar.ZERO, symLink.lambda, 91) //
              .map(scalar -> GEODESIC_INTERFACE.split(posP, posQ, scalar));
          // Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
          Path2D path2d = geometricLayer.toPath2D(tensor);
          graphics.setStroke(new BasicStroke(1.5f));
          // graphics.setColor(CDI.getColor(2));
          graphics.draw(path2d);
        }
        {
          Tensor tensor = Subdivide.of(symLink.lambda, RealScalar.ONE, 91) //
              .map(scalar -> GEODESIC_INTERFACE.split(posP, posQ, scalar));
          // Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
          Path2D path2d = geometricLayer.toPath2D(tensor);
          // graphics.setStroke(new BasicStroke(1.5f));
          graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
          // graphics.setColor(CDI.getColor(2));
          graphics.draw(path2d);
        }
      }
      // ---
      Tensor xya = symLink.getPosition(GEODESIC_INTERFACE);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
      path2d.closePath();
      // int rgb = 128 + 32;
      // final Color color = new Color(rgb, rgb, rgb, 128 + 64);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      // graphics.setColor(color);
      graphics.fill(path2d);
      // graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }
}
