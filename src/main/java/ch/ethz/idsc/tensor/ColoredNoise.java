// code adapted by ob
package ch.ethz.idsc.tensor;

import java.util.Random;

/*
 * Adapted version of 'PinkNoise.java' to 'ColoredNoise.java'
 *
 * Copyright (c) 2008, Sampo Niskanen <sampo.niskanen@iki.fi>
 * All rights reserved.
 * Source:  http://www.iki.fi/sampo.niskanen/PinkNoise/
 *
 * https://github.com/cr/PolynApp/blob/master/src/org/codelove/polynapp/PinkNoise.java
 */
/** A class that provides a source of pink noise with a power spectrum density
 * (PSD) proportional to 1/f^alpha. "Regular" pink noise has a PSD proportional
 * to 1/f, i.e. alpha=1. However, many natural systems may require a different
 * PSD proportionality. The value of alpha may be from 0 to 2, inclusive. The
 * special case alpha=0 results in white noise (directly generated random
 * numbers) and alpha=2 results in brown noise (integrated white noise).
 * <p>
 * The values are computed by applying an IIR filter to generated Gaussian
 * random numbers. The number of poles used in the filter may be specified. For
 * each number of poles there is a limiting frequency below which the PSD
 * becomes constant. Values as low as 1-3 poles produce relatively good results,
 * however these values will be concentrated near zero. Using a larger number of
 * poles will allow more low frequency components to be included, leading to
 * more variation from zero. However, the sequence is stationary, that is, it
 * will always return to zero even with a large number of poles.
 * <p>
 * The distribution of values is very close to Gaussian with mean zero, but the
 * variance depends on the number of poles used. The algorithm can be made
 * faster by changing the method call <code> rnd.nextGaussian() </code> to
 * <code> rnd.nextDouble()-0.5 </code> in the method {@link #nextValue()}. The
 * resulting distribution is almost Gaussian, but has a relatively larger amount
 * of large values.
 * <p>
 * The IIR filter used by this class is presented by N. Jeremy Kasdin,
 * Proceedings of the IEEE, Vol. 83, No. 5, May 1995, p. 822.
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi> */
public class ColoredNoise {
  private final int poles;
  private final double[] multipliers;
  private final double[] values;
  private final Random rnd;

  /** Generate White noise by choosing the color alpha, using a five-pole IIR. */
  public ColoredNoise() {
    this(0.0, 5, new Random());
  }

  /** Generate a specific colored noise using a five-pole IIR.
   * 
   * @param alpha: the exponent of the colored noise, 1/f^alpha.
   * @param alpha = -2: Violet Noise
   * @param alpha = -1: Blue Noise
   * @param alpha = 0: White Noise
   * @param alpha = 1: Pink Noise
   * @param alpha = 2: Brownian Noise
   * @throws IllegalArgumentException: if <code>alpha < 0</code> or <code>alpha > 2</code>. */
  public ColoredNoise(double alpha) {
    this(alpha, 5, new Random());
  }

  /** Generate colored noise specifying alpha and the number of poles. The larger
   * the number of poles, the lower are the lowest frequency components that
   * are amplified.
   * 
   * 
   * @param alpha: the exponent of the colored noise, 1/f^alpha.
   * @param alpha = -2: Violet Noise
   * @param alpha = -1: Blue Noise
   * @param alpha = 0: White Noise
   * @param alpha = 1: Pink Noise
   * @param alpha = 2: Brownian Noise
   * @param poles: the number of poles to use.
   * @throws IllegalArgumentException: if <code>alpha < 0</code> or <code>alpha > 2</code>. */
  public ColoredNoise(double alpha, int poles) {
    this(alpha, poles, new Random());
  }

  /** Generate colored noise from a specific randomness source specifying alpha
   * and the number of poles. The larger the number of poles, the lower are
   * the lowest frequency components that are amplified.
   * 
   * @param alpha: the exponent of the colored noise, 1/f^alpha.
   * @param alpha = -2: Violet Noise
   * @param alpha = -1: Blue Noise
   * @param alpha = 0: White Noise
   * @param alpha = 1: Pink Noise
   * @param alpha = 2: Brownian Noise
   * @param poles: the number of poles to use.
   * @param random: the randomness source.
   * @throws IllegalArgumentException: if <code>alpha < 0</code> or <code>alpha > 2</code>. */
  public ColoredNoise(double alpha, int poles, Random random) {
    this.rnd = random;
    this.poles = poles;
    this.multipliers = new double[poles];
    this.values = new double[poles];
    double a = 1;
    for (int i = 0; i < poles; i++) {
      a = (i - alpha / 2) * a / (i + 1);
      multipliers[i] = a;
    }
    // Fill the history with random values
    for (int i = 0; i < 5 * poles; i++)
      this.nextValue();
  }

  /** @return the next pink noise sample. */
  public double nextValue() {
    /* The following may be changed to rnd.nextDouble()-0.5 if strict
     * Gaussian distribution of resulting values is not required. */
    double x = rnd.nextGaussian();
    for (int i = 0; i < poles; i++) {
      x -= multipliers[i] * values[i];
    }
    System.arraycopy(values, 0, values, 1, values.length - 1);
    values[0] = x;
    return x;
  }
}
