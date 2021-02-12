// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public enum LeversHud {
  ;
  public static final ColorDataGradient COLOR_DATA_GRADIENT = //
      ColorDataGradients.TEMPERATURE.deriveWithOpacity(RealScalar.of(0.5));

  public static void render(Bitype biinvariant, LeversRender leversRender) {
    render(biinvariant, leversRender, COLOR_DATA_GRADIENT);
  }

  public static void render( //
      Bitype biinvariant, LeversRender leversRender, ColorDataGradient colorDataGradient) {
    leversRender.renderSequence();
    leversRender.renderOrigin();
    leversRender.renderLevers();
    // ---
    switch (biinvariant) {
    case METRIC1:
      leversRender.renderTangentsXtoP(false); // boolean: no tangent plane
      leversRender.renderEllipseIdentity();
      leversRender.renderWeightsLength();
      break;
    case METRIC2:
      leversRender.renderTangentsPtoX(false); // boolean: no tangent plane
      leversRender.renderEllipseIdentityP();
      leversRender.renderWeightsLength();
      break;
    case LEVERAGES1:
      leversRender.renderTangentsXtoP(false); // boolean: no tangent plane
      if (leversRender.getSequence().length() <= 2)
        leversRender.renderMahalanobisFormXEV(colorDataGradient);
      else
        leversRender.renderEllipseMahalanobis();
      leversRender.renderWeightsLeveragesSqrt();
      break;
    case LEVERAGES2:
      leversRender.renderInfluenceX(colorDataGradient);
      leversRender.renderWeightsLeveragesSqrt();
      break;
    case GARDEN:
      leversRender.renderTangentsPtoX(false); // boolean: no tangent planes
      leversRender.renderEllipseMahalanobisP(); // no evs
      leversRender.renderWeightsGarden();
      break;
    case HARBOR:
    case CUPOLA:
      leversRender.renderInfluenceX(colorDataGradient);
      leversRender.renderInfluenceP(colorDataGradient);
      break;
    default:
      break;
    }
    leversRender.renderIndexX();
    leversRender.renderIndexP();
  }
}
