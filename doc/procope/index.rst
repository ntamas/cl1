.. -*- coding: utf-8 -*-

=========================
ClusterONE ProCope plugin
=========================

:Author: Tamás Nepusz, Haiyuan Yu, Alberto Paccanaro
:Contact: tamas@cs.rhul.ac.uk

Introduction
============

This is the documentation of the ProCope plugin of ClusterONE,
created by Tamás Nepusz, Haiyuan Yu and Alberto Paccanaro.

If you use results calculated by ClusterONE in a publication,
please cite one of the suggested `references`_.


.. contents:: Table of contents
   :backlinks: none

The one-minute guide to using ClusterONE
========================================

ClusterONE is distributed in a Java archive file (JAR), whose name is likely to
be something like ``cluster_one-X.Y.jar``, where ``X.Y`` is the version number.
This JAR file contains both the command line application and the ProCope
plugin. In this document, we are assuming that you want to use ClusterONE from
the ProCope graphical user interface. If you are interested in the ClusterONE
command line interface, please refer to the documentation distributed with the
command line interface itself.

ClusterONE embeds itself into ProCope as a custom clusterer. Since ProCope looks
for plugins in its ``lib`` subfolder, you must place the downloaded JAR file
in the ``lib`` subfolder of ProCope. You must also let ProCope know that it
should look for a clustering algorithm named ClusterONE in the JAR files of
the ``lib`` folder. This is done by creating a file called ``clusterers.xml``
in a subdirectory named ``.procope`` in your home folder. The contents of the
file must be as follows::

    <?xml version="1.0"?>
    <clusterers>
      <clusterer name="ClusterONE" class="uk.ac.rhul.cs.cl1.ui.procope.ProcopePlugin">
      </clusterer>
    </clusterers>

The above file can also be found online_, so it is enough to download it and
place it in ``$HOME/.procope/clusterers.xml``, where ``$HOME`` refers to your
home directory.

.. _online: https://raw.github.com/ntamas/cl1/master/etc/procope/clusterers.xml

After having installed ClusterONE, you can simply load your network into the
ProCope GUI, then right-click (Ctrl-click on Mac) on it to bring up the network
popup menu, and select the **Cluster** menu item. ClusterONE will be shown
among the list of clustering methods available. Select **ClusterONE** and click
OK. If you don't see ClusterONE in the list, the likely cause is that you have
put ``clusterers.xml`` in the wrong place or it has an invalid format.

The next dialog you see will present the options of the ClusterONE algorithm
itself. For the time being, just use the default settings and click on the
**Start** button. ClusterONE will generate the predicted complexes, which will
be listed in the **Complex sets** list box in the main ProCope window.

Description of the algorithm
============================

.. include:: description.rst

Parameter settings
==================

The parameters are grouped into basic and advanced ones. In most of the cases,
the default values of the advanced parameters should be fine, but the basic
parameters may need to be adjusted to your specific needs.

Basic parameters
^^^^^^^^^^^^^^^^

**Minimum size**
    The minimum size of clusters deemed relevant by ClusterONE.
    This is a hard threshold: whenever ClusterONE finds a cluster
    smaller than the minimum size, the cluster will be discarded
    immediately.

**Minimum density**
    The minimum density of clusters deemed relevant by ClusterONE.
    The density of a cluster is the total sum of edge weights within
    the cluster, divided by the number of theoretically possible
    edges within the cluster. In other words, this is the average edge
    weight within the cluster if missing edges are assumed to have a
    weight of zero. Whenever ClusterONE finds a cluster that has a
    smaller density than the value given here, the cluster will be
    discarded immediately. Increase the minimum density if you get
    too many clusters and they seem too sparse, or decrease it if you
    are not getting enough clusters.

Advanced parameters
^^^^^^^^^^^^^^^^^^^

If you do not see these parameters in the ClusterONE parameters dialog, click
on the **Advanced parameters** label to expand the container holding them.

**Node penalty**
    Penalty value corresponding to each node. When you set this option to
    a specific value *x*, ClusterONE will assume that each node has an
    extra boundary weight of *x* when it considers the addition of the node
    to a cluster (see [1]_ for more details). It can be used to model the
    possibility of uncharted connections for each node, so nodes with only
    a single weak connection to a cluster will not be added to the cluster as
    the penalty value will outweigh the benefits of adding the node. The
    default penalty value is 2.

**Merging method**, **Overlap threshold** and **Similarity function**
    After an initial set of clusters are found, ClusterONE tries to
    merge highly overlapping (and thus redundant) clusters in order to
    clean up the result. For each pair of clusters found, ClusterONE
    calculates a score that quantifies the overlap between them, and
    two clusters are merged if this overlap is larger than a given
    threshold (specified by the **Overlap threshold** textbox). There
    are four different ways to calculate the overlap score, as controlled
    by the **Similarity function** combobox:

        - The *match coefficient* takes the size of the overlap squared,
          divided by the product of the sizes of the two clusters being
          considered, as in the paper of Bader and Hogue [2]_.

        - The *Simpson coefficient* divides the size of the overlap
          by the size of the smaller cluster.

        - The *Jaccard similarity* divides the size of the overlap by the
          size of the union of the two clusters.

        - The *Dice similarity* divides twice the size of the overlap by
          the sum of the sizes of the two clusters.
          
    Merging can be done in two different ways, as controlled by the
    **Merging method** combobox:
    
        - The *single-pass method* calculates similarity scores between all pairs
          of complexes and creates a graph where the nodes are the
          complexes and two nodes are connected if the corresponding
          complexes have a score higher than the overlap threshold.
          Complexes in the same connected component of the graph will then
          be merged.
                        
        - The *multi-pass method* calculates similarity scores between all pairs
          of complexes and stores those pairs that have a score
          larger than the overlap threshold. The highest scoring pair
          is then merged and the similarity of the merged complex
          towards its neighbors is re-calculated. This is repeated
          until there are no more highly overlapping complexes in
          the result.
    
    The default settings (match coefficient with a threshold of 0.8 using
    the single-pass algorithm) seem to be satisfactory for most use-cases
    Decreasing the threshold will result in more clusters being merged.

**Seeding method**
    ClusterONE works by growing clusters from initial "seeds", driven
    by a goal function that is maximized greedily (see the Cluster
    ONE paper [1]_ for more details). A seed can be an arbitrary subgraph,
    but in most cases, it is either a single node or a single edge.
    The seeding method prescribes how the seeds are selected during the
    calculation:

        - *From every node* means that every node will be used as a seed.

        - *From unused nodes* means that nodes will be tried in the
          descending order of their weights (where the weight of a node is
          the sum of the weights on its incident edges), and whenever a
          cluster is found, the nodes in that cluster will be excluded from
          the list of potential seeds. In other words, the node with the
          largest weight that does *not* participate in any of the clusters
          found so far will be selected as the next seed.

        - *From every edge* means that every edge will be considered once,
          each yielding a seed consisting of the two endpoints of the edge.

    In practical use-cases, the *From unused nodes* and *From every node*
    methods are almost equivalent, but the former one yields a smaller
    number of redundant clusters.


References
==========

If you use results calculated by ClusterONE in a publication,
please cite the following reference:

.. [1] Nepusz T, Yu H, Paccanaro A: Detecting overlapping protein complexes
       from protein-protein interaction networks. Nature Methods,
       Advance Online Publication, 2012. doi:10.1038/nmeth.1938

Some other papers that might be of interest (and were referenced earlier
in this help file):

.. [2] Bader GD, Hogue CWV: An automated method for finding molecular complexes
       in large protein interaction networks. BMC Bioinformatics 2003, 4:2.
       doi:10.1186/1471-2105-4-2
