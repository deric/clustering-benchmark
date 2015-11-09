# Clustering benchmarks

## Datasets

This project contains collection of labelled clustering problems that can be found in the literature. Most of datasets were artificialy created.

### 2d-10c

<img alt="2d-10c" align="right" width="400px" src="https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/2d-10c.png">

```
J. Handl and J. Knowles, “Multiobjective clustering with automatic
determination of the number of clusters,” UMIST, Tech. Rep., 2004.
```
* 2 dimensions, 10 clusters, 2990 data points
* [ARFF](https://github.com/deric/clustering-benchmark/blob/master/src/main/resources/datasets/artificial/2d-10c.arff)
* [generator](https://github.com/deric/handl-data-generators)

### atom

![atom](https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/atom.png)

* 3 dimensions, 2 clusters, 800 data points
* source: [FCPS](https://www.uni-marburg.de/fb12/datenbionik/data?language_sync=1)
* [ARFF](https://github.com/deric/clustering-benchmark/blob/master/src/main/resources/datasets/artificial/atom.arff)

### aggregation

![aggregation](https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/aggregation.png)

### chainlink

![chainlink](https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/chainlink.png)


### D31

![D31](https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/D31.png)


## Experiments

This project contains set of clustering methods benchmarks on various dataset. The project is dependent on [Clueminer project](https://github.com/deric/clueminer).

in order to run benchmark compile dependencies into a single JAR file:

    mvn assembly:assembly

# Consensus experiment

allows running repeated runs of the same algorithm:

```
./run consensus --dataset "triangle1" --repeat 10
```
by default k-means algorithm is used.

For available datasets see [resources folder](https://github.com/deric/clustering-benchmark/tree/master/src/main/resources/datasets/artificial).
