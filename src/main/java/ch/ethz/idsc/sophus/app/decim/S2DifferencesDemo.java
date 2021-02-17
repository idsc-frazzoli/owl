// code by jph
package ch.ethz.idsc.sophus.app.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.hs.HsDifferences;
import ch.ethz.idsc.sophus.hs.sn.S2Loxodrome;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

/* package */ class S2DifferencesDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 160);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);
  // ---
  protected Tensor _control = Tensors.empty();
  protected Tensor _differe = Tensors.empty();

  public S2DifferencesDemo() {
    super(GeodesicDisplays.S2_ONLY);
    shuffle();
  }

  private void shuffle() {
    ScalarTensorFunction stf = S2Loxodrome.of(.3);
    Tensor domain = Subdivide.of(0, 10, 50);
    _control = domain.map(stf);
    _differe = new HsDifferences(SnManifold.INSTANCE).apply(_control);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = geodesicDisplay();
    Tensor planar = Tensor.of(_control.stream().map(geodesicDisplay::toPoint));
    pathRenderCurve.setCurve(planar, false).render(geometricLayer, graphics);
    for (Tensor ctrl : _differe) {
      Tensor p = ctrl.get(0); // point
      Tensor v = ctrl.get(1); // vector
      {
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.setColor(Color.GRAY);
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
        graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v)));
        geometricLayer.popMatrix();
      }
    }
    {
      Tensor v0 = UnitVector.of(3, 1).multiply(RealScalar.of(0.5));
      for (int index = 1; index < _control.length(); ++index) {
        Tensor p = _control.get(index - 1);
        {
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.setColor(Color.RED);
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
          graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v0)));
          geometricLayer.popMatrix();
        }
        Tensor q = _control.get(index - 0);
        v0 = SnManifold.INSTANCE.endomorphism(p, q).dot(v0); 
      }
    }
  }

  public static void main(String[] args) {
    new S2DifferencesDemo().setVisible(1000, 800);
  }
}
