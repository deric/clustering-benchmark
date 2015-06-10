# Clustering benchmarks

This project contains set of clustering methods benchmarks on various dataset. The project is dependent on [Clueminer project](https://github.com/deric/clueminer).

in order to run benchmark compile dependencies into a single JAR file:

    mvn assembly:assembly

## Consensus experiment

allows running repeated runs of the same algorithm:

```
./run consensus --dataset "triangle1" --repeat 10
```
by default k-means algorithm is used.

For available datasets see [resources folder](https://github.com/deric/clustering-benchmark/tree/master/src/main/resources/datasets/artificial).
