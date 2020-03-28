// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import ch.ethz.idsc.sophus.crv.clothoid.MidpointTangentOrder2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.EqualizingDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class CustomClothoidDemo extends ControlPointsDemo implements ChangeListener {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  private static final Tensor CONFIG = Tensors.fromString("{{0, 0}, {5, 0}}");
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 120);
  private static final PathRender PATH_RENDER = new PathRender(new Color(0, 0, 255, 128), 2f);
  private static final Tensor LAMBDAS = Subdivide.of(-20.0, 20.0, 1001);
  // ---
  private final JSlider jSlider = new JSlider(0, LAMBDAS.length() - 1, LAMBDAS.length() / 2);
  private final JTextField jTextField = new JTextField(10);
  private final JLabel jLabel = new JLabel();
  // ---
  private ClothoidDefectContainer clothoidDefectContainer = null;

  public CustomClothoidDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    {
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      jSlider.addChangeListener(this);
      jSlider.setPreferredSize(new Dimension(300, 28));
      timerFrame.jToolBar.add(jSlider);
    }
    {
      JButton jButton = new JButton("fit");
      jButton.addActionListener(e -> {
        ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
        Scalar lambda = MidpointTangentOrder2.INSTANCE.apply(clothoidContext.s1(), clothoidContext.s2());
        setLambda(lambda);
      });
      timerFrame.jToolBar.add(jButton);
    }
    {
      timerFrame.jToolBar.add(jLabel);
    }
    // ---
    stateChanged(null);
    setControlPointsSe2(Array.zeros(2, 3));
    validateContainer();
  }

  private void validateContainer() {
    {
      Tensor control = getControlPointsSe2().copy();
      control.set(CONFIG.get(Tensor.ALL, 0), Tensor.ALL, 0);
      control.set(CONFIG.get(Tensor.ALL, 1), Tensor.ALL, 1);
      setControlPointsSe2(control);
    }
    // ---
    Tensor p = getControlPointsSe2().get(0);
    Tensor q = getControlPointsSe2().get(1);
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    if (Objects.isNull(clothoidDefectContainer) || !clothoidDefectContainer.encodes(clothoidContext)) {
      // System.out.println("update");
      clothoidDefectContainer = new ClothoidDefectContainer(clothoidContext);
      jLabel.setText("s1=" + clothoidContext.s1().map(Round._4) + " s2=" + clothoidContext.s2().map(Round._4));
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    validateContainer();
    // ---
    ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
    {
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      clothoidDefectContainer.jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
    Scalar lambda = LAMBDAS.Get(jSlider.getValue());
    try {
      lambda = Scalars.fromString(jTextField.getText());
    } catch (Exception exception) {
      // ---
    }
    // ---
    ClothoidTangentDefect clothoidTangentDefect = //
        ClothoidTangentDefect.of(clothoidContext.s1(), clothoidContext.s2());
    System.out.println(clothoidTangentDefect.apply(lambda).map(Round._4));
    Scalar fs = lambda;
    Clothoids clothoids = new CustomClothoids((s1, s2) -> fs);
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

  @Override
  public void stateChanged(ChangeEvent e) {
    setLambda(LAMBDAS.Get(jSlider.getValue()));
  }

  public void setLambda(Scalar lambda) {
    jTextField.setText(lambda.map(Round._5).toString());
  }
}
