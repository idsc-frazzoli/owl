// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.sym.SymLink;
import ch.ethz.idsc.sophus.gds.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clips;

/** visualization of the geometric geodesic average */
/* package */ class GeometricSymLinkRender {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final int STEPS = 9;

  public static RenderInterface of(GeodesicDisplay geodesicDisplay, SymLink symLink) {
    return new GeometricSymLinkRender(geodesicDisplay).new Link(symLink);
  }

  /***************************************************/
  private final GeodesicDisplay geodesicDisplay;
  private final GeodesicInterface geodesicInterface;

  private GeometricSymLinkRender(GeodesicDisplay geodesicDisplay) {
    this.geodesicDisplay = geodesicDisplay;
    geodesicInterface = geodesicDisplay.geodesicInterface();
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
        ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(posP, posQ);
        graphics.setColor(new Color(0, 128 + 64, 0, 255));
        {
          Tensor tensor = Subdivide.of(RealScalar.ZERO, symLink.lambda, 91) //
              .map(scalarTensorFunction);
          Tensor points = Tensor.of(tensor.stream().map(geodesicDisplay::toPoint));
          Path2D path2d = geometricLayer.toPath2D(points);
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        {
          Tensor tensor = Subdivide.of(symLink.lambda, RealScalar.ONE, 91) //
              .map(scalarTensorFunction);
          Tensor points = Tensor.of(tensor.stream().map(geodesicDisplay::toPoint));
          Path2D path2d = geometricLayer.toPath2D(points);
          graphics.setStroke(STROKE);
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        {
          Tensor tensor = Subdivide.increasing(Clips.unit(), STEPS).extract(1, STEPS) //
              .map(scalarTensorFunction);
          Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.5));
          graphics.setColor(new Color(64, 128 + 64, 64, 128));
          for (Tensor p : tensor) {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
            Path2D path2d = geometricLayer.toPath2D(shape);
            path2d.closePath();
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        }
      }
      // ---
      Tensor p = symLink.getPosition(geodesicInterface);
      graphics.setColor(new Color(0, 0, 255, 192));
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.7)));
      path2d.closePath();
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }
}
