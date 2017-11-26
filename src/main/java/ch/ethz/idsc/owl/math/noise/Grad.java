// code by stegu
package ch.ethz.idsc.owl.math.noise;

/** class extracted from {@link SimplexContinuousNoise} */
/* package */ class Grad {
  double x, y, z, w;

  Grad(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  Grad(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  double dot(double x, double y) {
    return this.x * x + this.y * y;
  }

  double dot(double x, double y, double z) {
    return this.x * x + this.y * y + this.z * z;
  }

  double dot(double x, double y, double z, double w) {
    return this.x * x + this.y * y + this.z * z + this.w * w;
  }
}