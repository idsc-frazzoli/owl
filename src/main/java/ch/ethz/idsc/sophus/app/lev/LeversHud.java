// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.krg.Biinvariant;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public enum LeversHud {
  ;
  public static void render(Biinvariant pseudoDistances, LeversRender leversRender) {
    render(pseudoDistances, leversRender, //
        ColorDataGradients.TEMPERATURE.deriveWithOpacity(RealScalar.of(0.5)));
  }

  public static void render( //
      Biinvariant pseudoDistances, //
      LeversRender leversRender, //
      ColorDataGradient colorDataGradient) {
    leversRender.renderSequence();
    leversRender.renderOrigin();
    leversRender.renderLevers();
    // ---
    switch (pseudoDistances) {
    case METRIC:
      leversRender.renderLeverLength();
      break;
    case ANCHOR:
      leversRender.renderProjectionX(colorDataGradient);
      break;
    case HARBOR:
    case NORM2:
      leversRender.renderProjectionX(colorDataGradient);
      leversRender.renderProjectionsP(colorDataGradient);
      break;
    case GARDEN:
      leversRender.renderTangentsPtoX(false);
      leversRender.renderMahalanobisFormsP(false, colorDataGradient);
      break;
    case TARGET:
      leversRender.renderTangentsXtoP(false);
      leversRender.renderMahalanobisFormX(false, colorDataGradient);
      break;
    default:
      break;
    }
    leversRender.renderIndex();
  }
}
