// JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.
package ch.ethz.idsc.owl.math.noise;

public enum PerlinContinuousNoise implements NativeContinuousNoise {
  FUNCTION;

  @Override
  public double at(double x) {
    return at(x, 0, 0);
  }

  @Override
  public double at(double x, double y) {
    return at(x, y, 0);
  }

  @Override
  public double at(double x, double y, double z) {
    int X = Noise.floor(x) & 255; // FIND UNIT CUBE THAT
    int Y = Noise.floor(y) & 255; // CONTAINS POINT.
    int Z = Noise.floor(z) & 255;
    x -= Math.floor(x); // FIND RELATIVE X, Y, Z
    y -= Math.floor(y); // OF POINT IN CUBE.
    z -= Math.floor(z);
    double u = fade(x); // COMPUTE FADE CURVES
    double v = fade(y); // FOR EACH OF X, Y, Z.
    double w = fade(z);
    int A = Noise.TABLE.perm[X] + Y; // HASH COORDINATES OF
    int AA = Noise.TABLE.perm[A] + Z; // THE 8 CUBE CORNERS,
    int AB = Noise.TABLE.perm[A + 1] + Z;
    int B = Noise.TABLE.perm[X + 1] + Y;
    int BA = Noise.TABLE.perm[B] + Z;
    int BB = Noise.TABLE.perm[B + 1] + Z;
    double i1a = lerp(u, grad(Noise.TABLE.perm[AA], x, y, z), grad(Noise.TABLE.perm[BA], x - 1, y, z));
    double i1b = lerp(u, grad(Noise.TABLE.perm[AB], x, y - 1, z), grad(Noise.TABLE.perm[BB], x - 1, y - 1, z));
    double i2a = lerp(u, grad(Noise.TABLE.perm[AA + 1], x, y, z - 1), grad(Noise.TABLE.perm[BA + 1], x - 1, y, z - 1));
    double i2b = lerp(u, grad(Noise.TABLE.perm[AB + 1], x, y - 1, z - 1), grad(Noise.TABLE.perm[BB + 1], x - 1, y - 1, z - 1));
    return lerp(w, lerp(v, i1a, i1b), lerp(v, i2a, i2b));
  }

  /** smooth transition function f:[0, 1] -> [0, 1] with
   * f(0) == 0
   * f(1) == 1
   * f'(0) == f'(1) == 0
   * f''(0) == f''(1) == 0
   * 
   * @param t
   * @return */
  private static double fade(double t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }

  /** @param t
   * @param a
   * @param b
   * @return linear interpolation between a and b at parameter t */
  private static double lerp(double t, double a, double b) {
    return a + t * (b - a);
  }

  private static double grad(int hash, double x, double y, double z) {
    int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
    double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
        v = h < 4 ? y : h == 12 || h == 14 ? x : z;
    return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
  }
}