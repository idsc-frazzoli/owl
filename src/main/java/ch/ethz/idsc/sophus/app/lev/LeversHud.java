// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public enum LeversHud {
  ;
  public static void render(PseudoDistances pseudoDistances, LeversRender leversRender) {
    render(pseudoDistances, leversRender, //
        ColorDataGradients.TEMPERATURE.deriveWithOpacity(RealScalar.of(0.5)));
  }

  public static void render( //
      PseudoDistances pseudoDistances, //
      LeversRender leversRender, //
      ColorDataGradient colorDataGradient) {
    leversRender.renderSequence();
    leversRender.renderOrigin();
    leversRender.renderLevers();
    // ---
    switch (pseudoDistances) {
    case ABSOLUTE:
      leversRender.renderLeverLength();
      break;
    case SOLITARY:
      // leversRender.renderTangentsXtoP(true);
      leversRender.renderProjectionX(colorDataGradient);
      break;
    case MONOMAHA:
      leversRender.renderTangentsXtoP(true);
      leversRender.renderMahFormX(colorDataGradient);
      break;
    case PAIRWISE:
    case NORM2:
      leversRender.renderProjectionX(colorDataGradient);
      leversRender.renderProjectionsP(colorDataGradient);
      break;
    case STARLIKE:
      leversRender.renderTangentsPtoX(true);
      leversRender.renderMahFormsP(colorDataGradient);
    default:
      break;
    }
    leversRender.renderIndex();
  }
}
