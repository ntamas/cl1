ClusterONE strives to discover densely connected and possibly overlapping
regions within the Cytoscape network you are working with. The interpretation
of these regions depends on the context (i.e. what the network represents) and
it is left up to you. For instance, in protein-protein interaction networks
derived from high-throughput AP-MS experiments, these dense regions usually
correspond to protein complexes or fractions of them. ClusterONE works by
"growing" dense regions out of small seeds (typically one or two vertices),
driven by a quality function called *cohesiveness*.

.. |V0| replace:: V\ :sub:`0`

Before we move on to the formal definition of cohesiveness, let us introduce
some terminology that classifies vertices and edges of a graph *G* according to
their relationship to a selected group of vertices |V0|. Vertices of |V0| are
called *internal vertices*, while vertices not in |V0| are called *external
vertices*.  An edge that is situated between two internal vertices is an
*internal edge*, an edge going between an internal and an external vertex is a
*boundary edge*, and an edge between two external vertices is an *external
edge*. An internal vertex incident on at least one boundary edge is an
*internal boundary vertex*, while an external vertex incident on at least one
boundary edge is an *external boundary vertex*. The following figure illustrates
these concepts:

.. image:: images/cohesive_subgroup.png

Here, |V0| itself is denoted by a shaded background, which delimits internal
and external vertices. Thick black edges are internal, thin black edges are
boundary edges, while thin gray dashed edges are completely external. Vertices
marked by a letter are (internal or external) boundary vertices.

The quality of the group can be assessed by the number of internal edges
divided by the sum of the number of internal and boundary edges. This quality
measure is driven by the fact that a well-defined group should have many
internal edges and only a few boundary edges; in other words, the boundary
of the group should be sharp. If the edges have weights (i.e. a numeric value
assigned to each edge that quantifies how reliable that edge is or how confident
we are in its existence), the same guidelines apply, but the number of edges
should be replaced by the total confidence associated to those edges. Whenever
you have such confidence values, store them in a numeric edge attribute in
Cytoscape and use that attribute to drive the cluster growth process. From now
on, such confidence values will simply be called *edge weights* and the above
mentioned quality measure will be referred to as *cohesiveness*.

ClusterONE essentially looks for groups of high cohesiveness. This is achieved
by adopting a greedy strategy: starting from a single seed vertex (or a small
set of vertices that are strongly bound together), one can extend the group
step by step with new vertices so that the newly added vertex always increases
the cohesiveness of a group as much as possible. Removals are also allowed if
removing a vertex from the group increases its cohesiveness. The process stops
when it is not possible to increase the cohesiveness of the group by adding
another external boundary vertex or removing an internal boundary vertex.  See
the ClusterONE paper [1]_ for the description of the exact procedure.  The
growth process is repeated either for every vertex or every connected vertex
pair to obtain an initial set of cohesive subgroups. Subgroups smaller than a
given size or having a density less than a given threshold are thrown away.
Finally, redundant cohesive subgroups (i.e. those that overlap significantly
with each other) are merged to form larger subgroups to make the results easier
to interpret.
