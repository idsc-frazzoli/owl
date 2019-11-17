# ch.ethz.idsc.owl <a href="https://travis-ci.org/idsc-frazzoli/owl"><img src="https://travis-ci.org/idsc-frazzoli/owl.svg?branch=master" alt="Build Status"></a>

Library for motion planning in Java, version `0.5.9`

![owl](https://user-images.githubusercontent.com/4012178/63221687-dc80d380-c19c-11e9-8aa4-8f7b36f5a4d4.png)

The library was developed with the following objectives in mind
* suitable for use in safety-critical real-time systems
* trajectory planning for an autonomous vehicle
* implementation of theoretical concepts with high level of abstraction
* simulation and visualization

<table>
<tr>
<td>

![usecase_motionplan](https://user-images.githubusercontent.com/4012178/35968244-96577dee-0cc3-11e8-80a1-b38691e863af.png)

Motion planning

<td>

![shadow_regions](https://user-images.githubusercontent.com/4012178/42315433-b53034de-8047-11e8-8fc2-87fa504460c5.png)

Obstacle anticipation

<td>

![usecase_gokart](https://user-images.githubusercontent.com/4012178/35968269-a92a3b46-0cc3-11e8-8d5e-1276762cdc36.png)

[Trajectory pursuit](https://www.youtube.com/watch?v=XgmS8CP6gqw)

<td>

![planning_obstacles](https://user-images.githubusercontent.com/4012178/40268689-2af06cd4-5b72-11e8-95cf-d94edfdc3dd1.png)

[Static obstacles](https://www.youtube.com/watch?v=xLZeKFeAokM)

</tr>
</table>

## Student Projects

### 2017

* Jonas Londschien (MT): *An Anytime Generalized Label Correcting Method for Motion Planning*

### 2018

* Yannik Nager (MT): *What lies in the shadows? Safe and computation-aware motion planning for autonomous vehicles using intent-aware dynamic shadow regions*

### 2019

* André Stoll (MT): *Multi-Objective Optimization Using Preference Structures*
* Oliver Brinkmann (MT): *Averaging on Lie Groups: Applications of Geodesic Averages and Biinvariant Means*
* Joel Gächter (MT): *Subdivision-Based Clothoids in Autonomous Driving*

## Features

* Motion planning algorithms: [GLC](src/main/java/ch/ethz/idsc/owl/glc/std/StandardTrajectoryPlanner.java), and [RRT*](src/main/java/ch/ethz/idsc/owl/rrts/core/DefaultRrts.java)
* integrators: Euler, Midpoint, Runge-Kutta 4-5th order, exact integrator for the group SE2
* state-space models: car-like, two-wheel-drive, pendulum-swing-up, Lotka-Volterra, etc.
* efficient heuristic for goal regions: sphere, conic section
* visualizations and animations, see [video](https://www.youtube.com/watch?v=lPQW3GqQqSY)

## Motion Planning

### GLC

Rice2: 4-dimensional state space + time

<table>
<tr>
<td>

![rice2dentity_1510227502495](https://user-images.githubusercontent.com/4012178/32603926-dd317aea-c54b-11e7-97ab-82df23b52fa5.gif)

<td>

![rice2dentity_1510234462100](https://user-images.githubusercontent.com/4012178/32608146-b6106d1c-c55b-11e7-918d-e0a1d1c8e400.gif)

</tr>
</table>

---

SE2: 3-dimensional state space

<table>
<tr>
<td>

Car

![se2entity_1510232282788](https://user-images.githubusercontent.com/4012178/32606961-813b05a6-c557-11e7-804c-83b1c5e94a6f.gif)

<td>

Two-wheel drive (with Lidar simulator)

![twdentity_1510751358909](https://user-images.githubusercontent.com/4012178/32838106-2d88fa2c-ca10-11e7-9c2a-68b34b1717cc.gif)

</tr>
</table>

---

Simulation: autonomous gokart or car

<table>
<tr>
<td>

Gokart

![_1530775215911](https://user-images.githubusercontent.com/4012178/42308510-10283bf0-8036-11e8-8a42-b8f1f807bb88.gif)

<td>

Car

![_1530775403211](https://user-images.githubusercontent.com/4012178/42308523-1ae4ea8e-8036-11e8-8067-83bdd67a2d33.gif)

</tr>
</table>


### RRT*

R^2

![r2ani](https://cloud.githubusercontent.com/assets/4012178/26282173/06dccee8-3e0c-11e7-930f-fedab34fe396.gif)

![r2](https://cloud.githubusercontent.com/assets/4012178/26045794/16bd0a54-394c-11e7-9d11-19558bc3be88.png)

### Nearest Neighbors

<table>
<tr>
<td>

![nearest_r2](https://user-images.githubusercontent.com/4012178/64911097-dc351300-d71d-11e9-9a92-5ce1fcd8c42f.png)

R^2

<td>

![nearest_dubins](https://user-images.githubusercontent.com/4012178/64911102-e7883e80-d71d-11e9-96d2-11273b892775.png)

Dubins

<td>

![nearest_clothoid](https://user-images.githubusercontent.com/4012178/64911109-f242d380-d71d-11e9-83cf-358a4047175b.png)

Clothoid

</tr>
</table>

## Integration

Specify `repository` and `dependency` of the owl library in the `pom.xml` file of your maven project:

```xml
<repositories>
  <repository>
    <id>owl-mvn-repo</id>
    <url>https://raw.github.com/idsc-frazzoli/owl/mvn-repo/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>ch.ethz.idsc</groupId>
    <artifactId>owl</artifactId>
    <version>0.5.9</version>
  </dependency>
</dependencies>
```

## Contributors

Jan Hakenberg, Jonas Londschien, Yannik Nager, André Stoll, Joel Gaechter

> The code in the repository operates a heavy and fast robot that may endanger living creatures. We follow best practices and coding standards to protect from avoidable errors.

## Publications

* *What lies in the shadows? Safe and computation-aware motion planning for autonomous vehicles using intent-aware dynamic shadow regions*
by Yannik Nager, Andrea Censi, and Emilio Frazzoli,
[video](https://www.youtube.com/watch?v=3w6zQF9HOAM)

## References

* *A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning*
by Brian Paden and Emilio Frazzoli, 
[arXiv:1607.06966](https://arxiv.org/abs/1607.06966),
[video](https://www.youtube.com/watch?v=4-r6Oi8GHxc)
* *Sampling-based algorithms for optimal motion planning*
by Sertac Karaman and Emilio Frazzoli,
[IJRR11](http://ares.lids.mit.edu/papers/Karaman.Frazzoli.IJRR11.pdf)

---

![ethz300](https://user-images.githubusercontent.com/4012178/45925071-bf9d3b00-bf0e-11e8-9d92-e30650fd6bf6.png)

# ch.ethz.idsc.sophus <a href="https://travis-ci.org/idsc-frazzoli/owl"><img src="https://travis-ci.org/idsc-frazzoli/owl.svg?branch=master" alt="Build Status"></a>

Library for non-linear geometry computation in Java

![sophus](https://user-images.githubusercontent.com/4012178/64911180-9f1d5080-d71e-11e9-9490-ae484d0399f3.png)

The library was developed with the following objectives in mind
* trajectory design for autonomous robots
* suitable for use in safety-critical real-time systems
* implementation of theoretical concepts with high level of abstraction

<table>
<tr>
<td>

![curve_se2](https://user-images.githubusercontent.com/4012178/47631757-8f693d80-db47-11e8-9c00-7796b07c48fc.png)

Curve Subdivision

<td>

![smoothing](https://user-images.githubusercontent.com/4012178/47631759-91cb9780-db47-11e8-9dc7-a2631a144ecc.png)

Smoothing

<td>

![wachspress](https://user-images.githubusercontent.com/4012178/62423041-7c7a2f80-b6bc-11e9-874e-414ae13be3ab.png)

Wachspress

<td>

![dubinspathcurvature](https://user-images.githubusercontent.com/4012178/50681318-5d72cc80-100b-11e9-943e-e168d0463eca.png)

Dubins path curvature

</tr>
</table>

## Features

* geodesics in Lie-groups and homogeneous spaces: Euclidean space `R^n`, special Euclidean group `SE(2)`, hyperbolic half-plane `H2`, n-dimensional sphere `S^n`, ...
* parametric curves defined by control points in non-linear spaces: `GeodesicBSplineFunction`, ...
* non-linear smoothing of noisy localization data `GeodesicCenterFilter`
* Dubins path

### Geodesic DeBoor Algorithm

![loops5](https://user-images.githubusercontent.com/4012178/51076078-3c0d8280-1694-11e9-9857-2166598c09b2.png)

B-Spline curves in `SE(2)` produced by DeBoor Algorithm or curve subdivision produce curves in the planar subspace `R^2` with appealing curvature.

### Smoothing using Geodesic Averages

![smoothing](https://user-images.githubusercontent.com/4012178/51090026-283a4d00-1776-11e9-81d3-aae3e34402f1.png)

The sequence of localization estimates of a mobile robot often contains noise.
Instead of using a complicated extended Kalman filter, geodesic averages based on conventional window functions denoise the uniformly sampled signal of poses in `SE(2)`.

### Curve Decimation in Lie Groups

![curve_decimation](https://user-images.githubusercontent.com/4012178/64847671-cf29fe00-d60f-11e9-8993-9f5549388ceb.png)

The pose of mobile robots is typically recorded at high frequencies.
The trajectory can be faithfully reconstructed from a fraction of the samples. 

### Visualization of Geodesic Averages

![deboor5](https://user-images.githubusercontent.com/4012178/51075948-ade4cc80-1692-11e9-9c9a-1e75084df796.png)

A geodesic average is the generalization of an affine combination from the Euclidean space to a non-linear space.
A geodesic average consists of a nested binary averages.
Generally, an affine combination does not have a unique expression as a geodesic average.
Instead, several geodesic averages reduce to the same affine combination when applied in Euclidean space. 

## Contributors

Jan Hakenberg, Oliver Brinkmann, Joel Gächter

## Publications

* *Curve Subdivision in SE(2)*
by Jan Hakenberg,
[viXra:1807.0463](http://vixra.org/abs/1807.0463),
[video](https://www.youtube.com/watch?v=2vDciaUgL4E)
* *Smoothing using Geodesic Averages*
by Jan Hakenberg,
[viXra:1810.0283](http://vixra.org/abs/1810.0283),
[video](https://www.youtube.com/watch?v=dmFO72Pigb4)
* *Curve Decimation in SE(2) and SE(3)*
by Jan Hakenberg,
[viXra:1909.0174](http://vixra.org/abs/1909.0174)

## References

* *Bi-invariant Means in Lie Groups. Application to Left-invariant Polyaffine Transformations.* by Vincent Arsigny, Xavier Pennec, Nicholas Ayache
* *Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups* by Xavier Pennec, Vincent Arsigny
* *Lie Groups for 2D and 3D Transformations* by Ethan Eade
* *Manifold-valued subdivision schemes based on geodesic inductive averaging* by Nira Dyn, Nir Sharon
* *Power Coordinates: A Geometric Construction of Barycentric Coordinates on Convex Polytopes* by Max Budninskiy, Beibei Liu, Yiying Tong, Mathieu Desbrun

---

![ethz300](https://user-images.githubusercontent.com/4012178/45925071-bf9d3b00-bf0e-11e8-9d92-e30650fd6bf6.png)
