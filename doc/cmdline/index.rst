.. -*- coding: utf-8 -*-

=================================
ClusterONE command line interface
=================================

:Author: Tamás Nepusz, Haiyuan Yu, Alberto Paccanaro
:Contact: tamas@cs.rhul.ac.uk

Introduction
============

This is the documentation of the command line interface of ClusterONE,
created by Tamás Nepusz, Haiyuan Yu and Alberto Paccanaro.

If you use results calculated by ClusterONE in a publication,
please cite one of the suggested `references`_.


.. contents:: Table of contents
   :backlinks: none

The one-minute guide to using ClusterONE
========================================

The command line interface of ClusterONE is distributed in a Java archive file
(JAR). Its name is likely to be something like ``cluster_one-X.Y.jar``, where
``X.Y`` is the version number. In the document, we will simply use the name
without the version number.

In the rest of this document, a dollar (``$``) sign at the start of a line in
the examples represents the shell prompt of the operating system. There is no
need to type it.

The easiest use-case is to run ClusterONE on an input file containing
id1-id2-weight triplets. Assuming that you have the Java interpreter on the
path of your operating system, this is as simple as::

    $ java -jar cluster_one.jar input_file.txt

ClusterONE also works on SIF files (Standard Interaction Format). It ignores
the interaction types and assumes each interaction to have a weight of 1.0::

    $ java -jar cluster_one.jar input_file.sif

In most cases, the only parameters of the algorithm you wish to tweak is the
size and density thresholds of the detected complexes (3 and 0.3 by default)::

    $ java -jar cluster_one.jar input_file.txt -s 4 -d 0.4

The above command line would detect complexes with at least four proteins and
a minimum density of 0.4.

You can also supply the input file on the standard input by specifying ``-``
as the input file name::

    $ java -jar cluster_one.jar -

When you are not using the standard input for supplying the input dataset, you
can use it to detect a protein complex around a pre-defined set of seed nodes
by setting the seeding method to ``stdin``::

    $ java -jar cluster_one.jar input_data.txt -S stdin

Alternatively, you can specify a single seed on the command line using the
``single`` seeding method::

    $ java -jar cluster_one.jar input_data.txt -S 'single(node1,node2,node3)'

Note that it is usually necessary to enclose arguments containing parentheses
in single quotes if your shell would otherwise try to interpret the parentheses
on its own.

For more details about the command line arguments, see Invocation_.

Description of the algorithm
============================

.. include:: description.rst

Invocation
==========

ClusterONE is distributed as a Java archive (JAR) file. Assuming that you have
already installed Java and the Java executable (``java`` on Linux and Mac OS X,
``java.exe`` on Windows) is already on the system path, you can start ClusterONE
as follows::

    $ java -jar cluster_one.jar [options] input_file

where ``options`` is a list of command-line options (see below) and ``input_file``
is the name of the input file to be processed. The order of the command line
options is irrelevant. The output will simply show the predicted protein
complexes, by default, one per line. See `Input file formats`_ for the
expected format of the input file, and `Output file formats`_ for alternative
output formats if the default one is not suitable for you.

The following command line options are recognised:

Basic command line options
--------------------------

-f, --input-format   specifies the format of the input file (``sif`` or ``edge_list``).
                     Use this option only if ClusterONE failed to detect the format
                     automatically.
-F, --output-format  specifies the format of the output file (``plain``, ``csv``
                     or ``genepro``).
-h, --help           shows a general help message
-d, --min-density    sets the minimum density of predicted complexes
-s, --min-size       sets the minimum size of the predicted complexes
-v, --version        shows the version information

Advanced command line options
-----------------------------

--fluff             fluffs the clusters as a post-processing step. This is not used
                    in the published algorithm, but it may be useful for your specific
                    problem. The idea is to check whether the external boundary nodes
                    of each cluster connect to more than two third of the internal
                    nodes; if so, such external boundary nodes are added to the cluster.
                    Fluffing is applied before the size and density filters.

--haircut           apply a haircut transformation as a post-processing step on the
                    detected clusters. This is not used in the published algorithm
                    either, but it may be useful for your specific problem. A haircut
                    transformation removes dangling nodes from a cluster: if the total
                    weight of connections from a node to the rest of the cluster is
                    less than *x* times the average node weight in the cluster (where
                    *x* is the argument of the switch), the node will be removed. The
                    process is repeated iteratively until there are no more nodes to
                    be removed. Haircut is applied before the size and density filters.

--max-overlap       specifies the maximum allowed overlap between
                    two clusters, as measured by the match coefficient,
                    which takes the size of the overlap squared, divided
                    by the product of the sizes of the two clusters
                    being considered, as in the paper of Bader and Hogue [2]_

--no-fluff          don't fluff the clusters, this is the default. For more details
                    about fluffing, see the ``--fluff`` switch above.

--no-merge          don't merge highly overlapping clusters (in other words, skip the
                    last merging phase). This is useful for debugging purposes only.

--penalty           sets a penalty value for the inclusion of each node. When you set
                    this option to *x*, ClusterONE will assume that each node has an
                    extra boundary weight of *x* when it considers the addition of the
                    node to a cluster (see [1]_ for more details). It can be used to
                    model the possibility of uncharted connections for each node, so
                    nodes with only a single weak connection to a cluster will not be
                    added to the cluster as the penalty value will outweigh the
                    benefits of adding the node. The default penalty value is 2.

--seed-method       specifies the seed generation method to use. The following
                    values are accepted:

                      - ``nodes``: every node will be used as a seed.

                      - ``unused_nodes``: nodes will be tried in the descending
                        order of their weights (where the weight of a node is the
                        sum of the weights on its incident edges), and whenever a
                        cluster is found, the nodes in that cluster will be excluded
                        from the list of potential seeds. In other words, the node
                        with the largest weight that does *not* participate in any
                        of the clusters found so far will be selected as the next
                        seed.

                      - ``edges``: every edge will be considered once, each yielding
                        a seed consisting of the two endpoints of the edge.

                      - ``cliques``: every *maximal* clique of the graph will be
                        considered once as a seed.

                      - ``file(*filename*)``: seeds will be generated from the given
                        file. Each line of the file must contain a space-separated
                        list of node IDs that will be part of the seed (and of course
                        each line encodes a single seed). If a line contains a single
                        ``*`` character only, this means that besides the seeds given
                        in the file, every node that is not part of any of the seeds
                        will also be considered as a potential seed on its own.

                      - ``single(*node1*,*node2*,...)``: a single seed will be used
                        with the given nodes as members. Node names must be separated
                        by commas or spaces.

                      - ``stdin``: seeds will be given on the standard input, one by
                        line. Each line must contain a space-separated list of node
                        IDs that will be part of the seed. It may be useful to use
                        this method in conjunction with ``--no-merge`` if you don't
                        want the result of earlier seedings to influence the result
                        of later ones.


Input file formats
==================

The following input file formats are recognised:

**Cytoscape SIF files**
    When the extension of the input file is ``.sif``, ClusterONE will
    automatically try to parse the file according to the SIF format
    of Cytoscape. Each line of the file must be according to the following
    format::

        id1 type id2

    where ``id1`` and ``id2`` are the IDs of the two interacting proteins and
    ``type`` is the interaction type (which will silently be ignored by
    ClusterONE). Each edge will have unit weight. The columns of the input
    file may be separated by spaces or tabs; however, make sure that you do
    not mix these separator characters.

**Weighted edge lists**
    This is the default file format assumed by ClusterONE unless the file
    extension suggests otherwise. Each line of the file has the following
    format::

        id1 id2 weight

    where ``id1`` and ``id2`` are the IDs of the interaction proteins and
    ``weight`` is the associated confidence value between 0 and 1. If the
    weight is omitted, it is considered to be equal to 1. Lines starting
    with hash marks (``#``) or percentage signs (``%``) are considered as
    comments and they are silently ignored.

If ClusterONE fails to recognise the input format of your file, feel free to
specify it using the ``--input-format`` command line option.

Output file formats
===================

The following output file formats are available:

**Plain text output** (``plain``)
    A simple and easy-to-parse output format, where each line represents a
    cluster. Members of the clusters are separated by Tab characters.

**CSV output** (``csv``)
    This format is suitable is you need more details about each cluster
    and/or you want to import the clusters to Microsoft Excel or OpenOffice.
    Each line corresponds to a cluster and contain the size, density,
    total internal and boundary weight, the value of the quality function,
    a P-value and the list of members for each cluster. Columns are
    separated by commas, and each individual column may optionally be
    quoted within quotation marks if necessary.

**GenePro output** (``genepro``)
    Use this format if you want to visualize the clusters later on using
    the GenePro_ plugin of Cytoscape.

.. _GenePro: http://wodaklab.org/genepro

References
==========

If you use results calculated by ClusterONE in a publication,
please cite the following reference:

.. [1] Nepusz T, Yu H, Paccanaro A: Detecting overlapping protein complexes
       from protein-protein interaction networks. In preparation.

Some other papers that might be of interest (and were referenced earlier
in this help file):

.. [2] Bader GD, Hogue CWV: An automated method for finding molecular complexes
       in large protein interaction networks. BMC Bioinformatics 2003, 4:2.
       doi:10.1186/1471-2105-4-2
