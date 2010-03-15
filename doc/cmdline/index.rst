.. -*- coding: utf-8 -*-

==================================
Cluster ONE command line interface
==================================

:Author: Tamás Nepusz, Haiyuan Yu, Alberto Paccanaro
:Contact: tamas@cs.rhul.ac.uk

Introduction
============

This is the documentation of the command line interface of Cluster ONE,
created by Tamás Nepusz, Haiyuan Yu and Alberto Paccanaro.

If you use results calculated by Cluster ONE in a publication,
please cite one of the suggested `references`_.


.. contents:: Table of contents
   :backlinks: none

The one-minute guide to using Cluster ONE
=========================================

TODO

Description of the algorithm
============================

.. include:: description.rst

Invocation
==========

Cluster ONE is distributed as a Java archive (JAR) file. Assuming that you have
already installed Java and the Java executable (``java`` on Linux and Mac OS X,
``java.exe`` on Windows) is already on the system path, you can start Cluster ONE
as follows::

    java -jar cluster_one-0.1.jar [options] input_file

where ``options`` is a list of command-line options (see below) and ``input_file``
is the name of the input file to be processed. The order of the command line
options is irrelevant. The output will simply show the predicted protein complexes,
one per line. See `Input file formats`_ for the expected format of the input file.

The following command line options are recognised:

Basic command line options
--------------------------

-h, --help          shows a general help message
-d, --min-density   sets the minimum density of predicted complexes
-s, --min-size      sets the minimum size of the predicted complexes

Advanced command line options
-----------------------------

--max-overlap       specifies the maximum allowed overlap between
                    two clusters, as measured by the match coefficient,
                    which takes the size of the overlap squared, divided
                    by the product of the sizes of the two clusters
                    being considered, as in the paper of Bader and Hogue [2]_

-S, --seed-method   specifies the seed generation method to use. The following
                    values are accepted:

                      - ``nodes``: every node will be used as a seed.

                      - ``unused_nodes``: nodes will be tried in the descending
                        order of their weights (where the weight of a node is the
                        sum of the weights on its adjacent edges), and whenever a
                        cluster is found, the nodes in that cluster will be excluded
                        from the list of potential seeds. In other words, the node
                        with the largest weight that does *not* participate in any
                        of the clusters found so far will be selected as the next
                        seed.

                      - ``edges``: every edge will be considered once, each yielding
                        a seed consisting of the two endpoints of the edge.

                      - ``file(*filename*)``: seeds will be generated from the given
                        file. Each line of the file must contain a space-separated
                        list of node IDs that will be part of the seed (and of course
                        each line encodes a single seed). If a line contains a single
                        ``*`` character only, this means that besides the seeds given
                        in the file, every node that is not part of any of the seeds
                        will also be considered as a potential seed on its own.

-n, --no-merge      don't merge highly overlapping clusters (in other words, skip the
                    last merging phase). This is useful for debugging purposes only.


Input file formats
==================

The following input file formats are recognised:

**Cytoscape SIF files**
    When the extension of the input file is ``.sif``, Cluster ONE will
    automatically try to parse the file according to the SIF format
    of Cytoscape. Each line of the file must be according to the following
    format::

        id1 type id2

    where ``id1`` and ``id2`` are the IDs of the two interacting proteins and
    ``type`` is the interaction type (which will silently be ignored by
    Cluster ONE). Each edge will have unit weight. The columns of the input
    file may be separated by spaces or tabs; however, make sure that you do
    not mix these separator characters.

**Weighted edge lists**
    This is the default file format assumed by Cluster ONE unless the file
    extension suggests otherwise. Each line of the file has the following
    format::

        id1 id2 weight

    where ``id1`` and ``id2`` are the IDs of the interaction proteins and
    ``weight`` is the associated confidence value between 0 and 1. If the
    weight is omitted, it is considered to be equal to 1. Lines starting
    with hash marks (``#``) or percentage signs (``%``) are considered as
    comments and they are silently ignored.


References
==========

If you use results calculated by Cluster ONE in a publication,
please cite the following reference:

.. [1] Nepusz T, Yu H, Paccanaro A: Detecting overlapping protein complexes
       in protein-protein interaction networks. In preparation.

Some other papers that might be of interest (and were referenced earlier
in this help file):

.. [2] Bader GD, Hogue CWV: An automated method for finding molecular complexes
       in large protein interaction networks. BMC Bioinformatics 2003, 4:2.
       doi:10.1186/1471-2105-4-2
