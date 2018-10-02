// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

public class GeodesicMeanFilter extends GeodesicCenterFilter {
  public GeodesicMeanFilter(GeodesicInterface geodesicInterface, int radius) {
    super(new GeodesicMean(geodesicInterface), radius);
  }
}
