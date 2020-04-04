// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidContext;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTangentDefect;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class ClothoidDefectContainer implements RenderInterface {
  public static final Tensor LAMBDAS = Subdivide.of(-20.0, 20.0, 1001);
  // ---
  public final ClothoidContext clothoidContext;
  private final ClothoidTangentDefect clothoidTangentDefect;
  public final Tensor defects;
  public final Tensor defects_real;
  public final Tensor defects_imag;
  public final Tensor solutions = Tensors.empty();

  public ClothoidDefectContainer(ClothoidContext clothoidContext) {
    this.clothoidContext = clothoidContext;
    clothoidTangentDefect = ClothoidTangentDefect.of(clothoidContext.s1(), clothoidContext.s2());
    defects = LAMBDAS.map(clothoidTangentDefect);
    defects_real = defects.map(Real.FUNCTION);
    defects_imag = defects.map(Imag.FUNCTION);
    for (int index = 1; index < LAMBDAS.length(); ++index) {
      boolean prev = Sign.isPositive(defects_real.Get(index - 1));
      boolean next = Sign.isPositive(defects_real.Get(index));
      if (prev && !next) {
        solutions.append(LAMBDAS.get(index)); // TODO linear interp
      }
    }
  }

  public boolean encodes(ClothoidContext clothoidContext) {
    return this.clothoidContext.s1().equals(clothoidContext.s1()) //
        && this.clothoidContext.s2().equals(clothoidContext.s2());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    PathRender pathRender = new PathRender(new Color(0, 0, 0, 128));
    Tensor tensor = Transpose.of(Tensors.of(LAMBDAS, defects_real));
    pathRender.setCurve(tensor, false);
    pathRender.render(geometricLayer, graphics);
    for (Tensor _lambda : solutions) {
      Scalar lambda = (Scalar) _lambda;
      graphics.setColor(Color.GREEN);
      graphics.draw(geometricLayer.toLine2D(Tensors.of(lambda, RealScalar.ONE), Tensors.of(lambda, RealScalar.ONE.negate())));
    }
  }

  public Optional<Scalar> getSolution(int index) {
    if (index < solutions.length())
      return Optional.of(solutions.Get(index));
    return Optional.empty();
  }
}
