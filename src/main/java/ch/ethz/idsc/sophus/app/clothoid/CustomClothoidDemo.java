// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import javax.swing.JSlider;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidContext;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTangentDefect;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.crv.clothoid.LagrangeQuadraticD;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.EqualizingDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class CustomClothoidDemo extends ControlPointsDemo {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  private static final Tensor CONFIG = Tensors.fromString("{{0, 0}, {5, 0}}");
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 120);
  private static final PathRender PATH_RENDER = new PathRender(new Color(0, 0, 255, 128), 2f);
  private static final Tensor LAMBDAS = Subdivide.of(-20.0, 20.0, 1001);
  // ---
  private final JSlider jSlider = new JSlider(0, LAMBDAS.length() - 1, LAMBDAS.length() / 2);
  // ---
  private ClothoidDefectContainer clothoidDefectContainer = null;

  public CustomClothoidDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    {
      jSlider.setPreferredSize(new Dimension(500, 28));
      timerFrame.jToolBar.add(jSlider);
    }
    // ---
    setControlPointsSe2(Array.zeros(2, 3));
    updateContainer();
  }

  private void rectify() {
    Tensor control = getControlPointsSe2().copy();
    control.set(CONFIG.get(Tensor.ALL, 0), Tensor.ALL, 0);
    control.set(CONFIG.get(Tensor.ALL, 1), Tensor.ALL, 1);
    setControlPointsSe2(control);
  }

  private void updateContainer() {
    rectify();
    Tensor p = getControlPointsSe2().get(0);
    Tensor q = getControlPointsSe2().get(1);
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    if (Objects.isNull(clothoidDefectContainer) || !clothoidDefectContainer.encodes(clothoidContext)) {
      // System.out.println("update");
      clothoidDefectContainer = new ClothoidDefectContainer(clothoidContext);
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    updateContainer();
    // ---
    ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
    {
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      clothoidDefectContainer.jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
    Scalar lambda = LAMBDAS.Get(jSlider.getValue());
    // ---
    ClothoidTangentDefect clothoidTangentDefect = //
        ClothoidTangentDefect.of(clothoidContext.s1(), clothoidContext.s2());
    System.out.println(clothoidTangentDefect.apply(lambda).map(Round._4));
    Clothoids clothoids = new CustomClothoids((s1, s2) -> lambda);
    Clothoid clothoid = clothoids.curve(clothoidContext.p, clothoidContext.q);
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    InverseCDF inverseCDF = //
        (InverseCDF) EqualizingDistribution.fromUnscaledPDF(DOMAIN.map(lagrangeQuadraticD).map(Scalar::abs));
    Tensor params = DOMAIN.map(inverseCDF::quantile).divide(RealScalar.of(DOMAIN.length()));
    Tensor points = params.map(clothoid);
    PATH_RENDER.setCurve(points, false).render(geometricLayer, graphics);
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new CustomClothoidDemo().setVisible(1000, 600);
  }
}
