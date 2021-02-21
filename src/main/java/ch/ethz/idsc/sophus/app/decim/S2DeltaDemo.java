// code by jph
package ch.ethz.idsc.sophus.app.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.hs.sn.S2Loxodrome;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnPerturbation;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.ref.gui.ConfigPanel;

/* package */ class S2DeltaDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 128 + 64);
  private static final Color COLOR_SHAPE = new Color(128, 255, 128, 128 + 64);
  private static final int WIDTH = 360;
  private static final int HEIGHT = 240;
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  // ---
  private final S2DeltaParam s2DeltaParam = new S2DeltaParam();
  private SnDeltaContainer snDeltaRaw;
  private SnDeltaContainer snDeltaFil;

  public S2DeltaDemo() {
    super(GeodesicDisplays.S2_ONLY);
    Container container = timerFrame.jFrame.getContentPane();
    ConfigPanel configPanel = ConfigPanel.of(s2DeltaParam);
    configPanel.fieldPanels().addUniversalListener(s -> {
      System.out.println("compute udpate: " + s);
      compute();
    });
    container.add("West", configPanel.getFields());
    compute();
  }

  private void compute() {
    ScalarTensorFunction stf = S2Loxodrome.of(s2DeltaParam.angle);
    Tensor domain = Subdivide.of(0, 20, 200);
    CurveSubdivision curveSubdivision = UniformResample.of(SnMetric.INSTANCE, SnGeodesic.INSTANCE, s2DeltaParam.delta);
    Tensor sequence = Tensor.of(domain.stream().map(Scalar.class::cast).map(stf));
    sequence = curveSubdivision.string(sequence);
    TensorUnaryOperator tuo = SnPerturbation.of(NormalDistribution.of(RealScalar.ZERO, s2DeltaParam.noise));
    sequence = Tensor.of(sequence.stream().map(tuo));
    ScalarUnaryOperator s_window = s2DeltaParam.s_window.get();
    snDeltaRaw = new SnDeltaContainer(sequence, s_window);
    TensorUnaryOperator tensorUnaryOperator = CenterFilter.of( //
        GeodesicCenter.of(SnGeodesic.INSTANCE, s2DeltaParam.f_window.get()), s2DeltaParam.getWidth());
    snDeltaFil = new SnDeltaContainer(tensorUnaryOperator.apply(sequence), s_window);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    // Tensor planar = ;
    pathRenderCurve.setCurve(Tensor.of(snDeltaRaw.sequence.stream().map(geodesicDisplay::toPoint)), false).render(geometricLayer, graphics);
    pathRenderShape.setCurve(Tensor.of(snDeltaFil.sequence.stream().map(geodesicDisplay::toPoint)), false).render(geometricLayer, graphics);
    if (false)
      for (Tensor ctrl : snDeltaRaw.differences) {
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
    if (false) { // moving a single tangent vector along
      Tensor v0 = UnitVector.of(3, 1).multiply(RealScalar.of(0.5));
      for (int index = 1; index < snDeltaRaw.sequence.length(); ++index) {
        Tensor p = snDeltaRaw.sequence.get(index - 1);
        {
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.setColor(Color.RED);
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
          graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v0)));
          geometricLayer.popMatrix();
        }
        Tensor q = snDeltaRaw.sequence.get(index - 0);
        v0 = SnManifold.INSTANCE.endomorphism(p, q).dot(v0);
      }
    }
    int mag = 4;
    {
      int ofs = 0;
      snDeltaRaw.jFreeChart.draw(graphics, new Rectangle(ofs, 0, WIDTH, HEIGHT));
      BufferedImage bufferedImage = snDeltaRaw.bufferedImage[0];
      graphics.drawImage(bufferedImage, ofs, HEIGHT, bufferedImage.getWidth() * mag, bufferedImage.getHeight() * mag, null);
    }
    {
      int ofs = WIDTH;
      snDeltaFil.jFreeChart.draw(graphics, new Rectangle(ofs, 0, WIDTH, HEIGHT));
      BufferedImage bufferedImage = snDeltaFil.bufferedImage[0];
      graphics.drawImage(bufferedImage, ofs, HEIGHT, bufferedImage.getWidth() * mag, bufferedImage.getHeight() * mag, null);
    }
  }

  public static void main(String[] args) {
    new S2DeltaDemo().setVisible(1000, 800);
  }
}
