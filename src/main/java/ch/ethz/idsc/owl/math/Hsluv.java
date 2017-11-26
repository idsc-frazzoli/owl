// code from www.hsluv.org
// adapted by jph
package ch.ethz.idsc.owl.math;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.img.Hue;

/** Hsluv is similar to {@link Hue} but attempts to balance overall brightness for all colors */
public enum Hsluv {
  ;
  /** @param hue in range [0, 1), value is taken modulo 1
   * @param sat saturation in range [0, 1]
   * @param lgt lightness with lgt == 0.5 for most intense color
   * @param alpha in range [0, 1] for transparency
   * @return color */
  public static Color of(double hue, double sat, double lgt, double alpha) {
    if (!Double.isFinite(hue))
      throw new RuntimeException("h=" + hue);
    hue %= 1;
    if (hue < 0)
      hue += 1;
    double[] rgb = hsluvToRgb(new double[] { hue * 360, sat * 100, lgt * 100 });
    return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], (float) alpha);
  }

  // ---
  private static double[][] m = new double[][] { //
      { 3.240969941904521, -1.537383177570093, -0.498610760293 }, //
      { -0.96924363628087, 1.87596750150772, 0.041555057407175 }, //
      { 0.055630079696993, -0.20397695888897, 1.056971514242878 } };
  private static double refY = 1;
  private static double refU = 0.19783000664283;
  private static double refV = 0.46831999493879;
  private static double kappa = 903.2962962;
  private static double epsilon = 0.0088564516;

  private static List<double[]> getBounds(double L) {
    List<double[]> result = new ArrayList<double[]>();
    double sub1 = Math.pow(L + 16, 3) / 1560896;
    double sub2 = sub1 > epsilon ? sub1 : L / kappa;
    for (int c = 0; c < 3; ++c) {
      double m1 = m[c][0];
      double m2 = m[c][1];
      double m3 = m[c][2];
      for (int t = 0; t < 2; ++t) {
        double top1 = (284517 * m1 - 94839 * m3) * sub2;
        double top2 = (838422 * m3 + 769860 * m2 + 731718 * m1) * L * sub2 - 769860 * t * L;
        double bottom = (632260 * m3 - 126452 * m2) * sub2 + 126452 * t;
        result.add(new double[] { top1 / bottom, top2 / bottom });
      }
    }
    return result;
  }

  private static Length lengthOfRayUntilIntersect(double theta, double[] line) {
    double length = line[1] / (Math.sin(theta) - line[0] * Math.cos(theta));
    return new Length(length);
  }

  private static class Length {
    final boolean greaterEqualZero;
    final double length;

    private Length(double length) {
      this.greaterEqualZero = length >= 0;
      this.length = length;
    }
  }

  private static double maxChromaForLH(double L, double H) {
    double hrad = H / 360 * Math.PI * 2;
    List<double[]> bounds = getBounds(L);
    double min = Double.MAX_VALUE;
    for (double[] bound : bounds) {
      Length length = lengthOfRayUntilIntersect(hrad, bound);
      if (length.greaterEqualZero)
        min = Math.min(min, length.length);
    }
    return min;
  }

  private static double dotProduct(double[] a, double[] b) {
    double sum = 0;
    for (int i = 0; i < a.length; ++i)
      sum += a[i] * b[i];
    return sum;
  }

  private static double fromLinear(double c) {
    if (c <= 0.0031308)
      return 12.92 * c;
    return 1.055 * Math.pow(c, 1 / 2.4) - 0.055;
  }

  private static double[] xyzToRgb(double[] tuple) {
    return new double[] { //
        fromLinear(dotProduct(m[0], tuple)), //
        fromLinear(dotProduct(m[1], tuple)), //
        fromLinear(dotProduct(m[2], tuple)), //
    };
  }

  private static double lToY(double L) {
    if (L <= 8)
      return refY * L / kappa;
    return refY * Math.pow((L + 16) / 116, 3);
  }

  private static double[] luvToXyz(double[] tuple) {
    double L = tuple[0];
    double U = tuple[1];
    double V = tuple[2];
    if (L == 0)
      return new double[] { 0, 0, 0 };
    double varU = U / (13 * L) + refU;
    double varV = V / (13 * L) + refV;
    double Y = lToY(L);
    double X = 0 - (9 * Y * varU) / ((varU - 4) * varV - varU * varV);
    double Z = (9 * Y - (15 * varV * Y) - (varV * X)) / (3 * varV);
    return new double[] { X, Y, Z };
  }

  private static double[] lchToLuv(double[] tuple) {
    double L = tuple[0];
    double C = tuple[1];
    double H = tuple[2];
    double Hrad = H / 360.0 * 2 * Math.PI;
    double U = Math.cos(Hrad) * C;
    double V = Math.sin(Hrad) * C;
    return new double[] { L, U, V };
  }

  private static double[] hsluvToLch(double[] tuple) {
    double H = tuple[0];
    double S = tuple[1];
    double L = tuple[2];
    if (L > 99.9999999)
      return new double[] { 100d, 0, H };
    if (L < 0.00000001)
      return new double[] { 0, 0, H };
    double max = maxChromaForLH(L, H);
    double C = max / 100 * S;
    return new double[] { L, C, H };
  }

  private static double[] lchToRgb(double[] tuple) {
    return xyzToRgb(luvToXyz(lchToLuv(tuple)));
  }

  /** conventional main entry point
   * 
   * @param tuple consisting of hue [0, 360], saturation [0, 100], lightness [0, 100]
   * @return */
  private static double[] hsluvToRgb(double[] tuple) {
    return lchToRgb(hsluvToLch(tuple));
  }
}
