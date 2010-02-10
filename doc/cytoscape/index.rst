.. -*- coding: utf-8 -*-

============================
Cluster ONE Cytoscape plugin
============================

:Author: Tamás Nepusz, Alberto Paccanaro
:Contact: tamas@cs.rhul.ac.uk

If you use results calculated by Cluster ONE in a publication,
please cite one of the suggested `references`_ (see below).

Introduction
============

.. contents:: Table of contents
   :backlinks: none

The one-minute guide to using Cluster ONE
=========================================

TODO

Getting started
===============

Cluster ONE installs itself into the Plugins menu of Cytoscape under
a submenu named Cluster ONE. To show `the control panel`_ of Cluster ONE,
select Plugins -> Cluster ONE -> Start.

The control panel
=================

The control panel of Cluster ONE is to be found on a separate tab
in the control panel of Cytoscape (see the left hand side of the
Cytoscape user interface). This is the place where you can set
the parameters of the algorithm and start a clustering process.
The parameters are as follows:

**Minimum size**
    The minimum size of clusters deemed relevant by Cluster ONE.
    This is a hard threshold: whenever Cluster ONE finds a cluster
    smaller than the minimum size, the cluster will be discarded
    immediately.

**Minimum density**
    The minimum density of clusters deemed relevant by Cluster ONE.
    The density of a cluster is the total sum of edge weights within
    the cluster, divided by the number of theoretically possible
    edges within the cluster. In other words, this is the average edge
    weight within the cluster if missing edges are assumed to have a
    weight of zero. Whenever Cluster ONE finds a cluster that has a
    smaller density than the value given here, the cluster will be
    discarded immediately. Increase the minimum density if you get
    too many clusters and they seem too sparse, or decrease it if you
    are not getting enough clusters.

**Merging method** and **Overlap threshold**
    After an initial set of clusters are found, Cluster ONE tries to
    merge highly overlapping (and thus redundant) clusters in order to
    clean up the result. For each pair of clusters found, Cluster ONE
    calculates a score that quantifies the overlap between them, and
    two clusters are merged if this overlap is larger than a given
    threshold (specified by the **Overlap threshold** textbox). There
    are two different ways to calculate the overlap score:

        - The *match coefficient* takes the size of the overlap squared,
          divided by the product of the sizes of the two clusters being
          considered, as in the paper of Bader and Hogue [2]_.

        - The *meet/min coefficient* divides the size of the overlap
          by the size of the smaller cluster.

    The default settings (match coefficient with a threshold of 0.8)
    seem to be satisfactory for most use-cases. Decreasing the threshold
    will result in more clusters being merged.

**Seeding method**
    Cluster ONE works by growing clusters from initial "seeds", driven
    by a goal function that is maximized greedily (see the Cluster
    ONE paper [1]_ for more details). A seed can be an arbitrary subgraph,
    but in most cases, it is either a single node or a single edge.
    The seeding method prescribes how the seeds are selected during the
    calculation:

        - *From every node* means that every node will be used as a seed.

        - *From unused nodes* means that nodes will be tried in the
          descending order of their weights (where the weight of a node is
          the sum of the weights on its adjacent edges), and whenever a
          cluster is found, the nodes in that cluster will be excluded from
          the list of potential seeds. In other words, the node with the
          largest weight that does *not* participate in any of the clusters
          found so far will be selected as the next seed.

        - *From every edge* means that every edge will be considered once,
          each yielding a seed consisting of the two endpoints of the edge.

    In practical use-cases, the *From unused nodes* and *From every node*
    methods are almost equivalent, but the former one yields a smaller
    number of redundant clusters.

**Edge weights**
    A numeric edge attribute to be used for the edge weights. *[unweighted]*
    means that each edge will have a weight equal to 1. If you don't see the
    name of the attribute in the list, click on the **Refresh** button (showing
    two green arrows) next to the combo box to re-scan the network for numeric
    edge attributes. This is necessary when you added the edge attribute you are
    looking for after you opened the Cluster ONE control panel.


Click on the **Generate clusters** button at the bottom of the panel to start
the clustering process. `The result viewer`_ will open automatically when the
results are ready. Use the **Close panel** button to hide the Cluster ONE
control panel.


The result viewer
=================

The result viewer appears on the right hand side of the Cytoscape user interface
after a successful clustering process and it can operate in two modes: the
simple and the detailed view. When the result viewer is opened for the first
time, the simple view is used, which shows each cluster in a scrollable list
box along with some basic properties of the cluster (number of nodes, density,
quality and p-value). The clusters are ordered according to ascending p-values.
There is also a small toolbar above the list of clusters, where the number of
clusters are shown along with small push buttons to access some of the functionality
provided by the result viewer.

Simple view
-----------

The simple view uses two columns, the first column showing a schematic drawing of
each cluster (driven by a simple force-directed layout algorithm) and the second
showing some basic properties of the cluster. The clusters are drawn in the
background, so if there are many large clusters, you may have to wait a little bit
until you are able to see them all.

Detailed view
-------------

The detailed view can be turned on or off by clicking on the first button of
the toolbar which shows a table. It is advised to turn the detailed view on
only if the result panel itself is detached from the main Cytoscape window or
if it is wide enough, as the detailed view contains seven columns, showing the
following properties of each cluster:

**Cluster**
    A schematic drawing of the cluster itself.

**Nodes**
    The number of nodes in the cluster.

**Density**
    The density of the cluster; that is, the sum of the edge weights within
    the cluster divided by the number of theoretically possible edges.

**In-weight**
    The sum of the weights of the edges that lie completely within the cluster.

**Out-weight**
    The sum of the weights of the edges where one endpoint lies within the cluster
    and the other lies outside.

**Quality**
    The quality of the cluster, as measured by the in-weight divided by the
    sum of the in-weight and the out-weight. The rationale behind this measure
    is that a good cluster contains many heavyweight edges within the cluster
    itself, and it is connected to the rest of the network only by a few lightweight
    edges.

References
==========

If you use results calculated by Cluster ONE in a publication,
please cite the following reference:

.. [1] Nepusz T, Paccanaro A: Detecting overlapping protein complexes
       in protein-protein interaction networks. In preparation.

Some other papers that might be of interest (and were referenced earlier
in this help file):

.. [2] Bader GD, Hogue CWV: An automated method for finding molecular complexes
       in large protein interaction networks. BMC Bioinformatics 2003, 4:2.
       doi:10.1186/1471-2105-4-2
