// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.gui.ren.MeshRender;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;
import ch.ethz.idsc.tensor.ref.gui.ConfigPanel;

public class UbongoBackground extends AbstractDemo {
  @FieldIntegerQ
  public Scalar resol = RealScalar.of(30);
  public Scalar delta = RealScalar.of(0.1);
  public Scalar amp = RealScalar.of(0.1);
  public Scalar ofs = RealScalar.of(0);

  public UbongoBackground() {
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", ConfigPanel.of(this).getFields());
  }

  public Tensor noise(List<Integer> list) {
    Tensor param = Tensors.vector(list.get(0), list.get(1)).multiply(delta);
    double nx = SimplexContinuousNoise.FUNCTION.at( //
        param.Get(0).number().doubleValue(), //
        param.Get(1).number().doubleValue(), ofs.number().doubleValue() + 0);
    double ny = SimplexContinuousNoise.FUNCTION.at( //
        param.Get(0).number().doubleValue(), //
        param.Get(1).number().doubleValue(), ofs.number().doubleValue() + 1);
    return Tensors.vector(nx, ny).multiply(amp);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int res = resol.number().intValue();
    Tensor dx = Subdivide.of(0.0, 2.0, 2 * res - 1);
    Tensor dy = Subdivide.of(0.0, 1.0, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
    Tensor random = RandomVariate.of(NormalDistribution.of(0, 0.01), new Random(3), Dimensions.of(domain));
    random = Array.of(this::noise, dx.length(), dy.length());
    // System.out.println("d=" + Dimensions.of(domain));
    // System.out.println(Dimensions.of(random));
    Tensor target = domain.add(random);
    Tensor[][] forward = TensorArray.ofMatrix(target);
    RenderQuality.setQuality(graphics);
    graphics.setStroke(new BasicStroke(0.6f));
    graphics.setColor(new Color(128, 128, 128));
    new MeshRender(forward, ColorDataGradients.CLASSIC) //
        .render(geometricLayer, graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // RenderQuality.setDefault(graphics);
  }

  public static void main(String[] args) {
    UbongoBackground ubongoBackground = new UbongoBackground();
    ubongoBackground.setVisible(1200, 600);
  }
}
