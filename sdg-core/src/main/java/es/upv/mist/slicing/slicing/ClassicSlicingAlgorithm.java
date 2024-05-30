package es.upv.mist.slicing.slicing;

import es.upv.mist.slicing.arcs.Arc;
import es.upv.mist.slicing.arcs.sdg.InterproceduralArc;
import es.upv.mist.slicing.graphs.Graph;
import es.upv.mist.slicing.nodes.GraphNode;
import es.upv.mist.slicing.utils.Utils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/** The classic slicing algorithm: traverse all arcs backwards except interprocedural output arcs until
 *  no new node is added, then repeat the process but ignoring interprocedural input arcs instead. */
public class ClassicSlicingAlgorithm implements SlicingAlgorithm {
    protected final Graph graph;

    public ClassicSlicingAlgorithm(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Slice traverseProcedure(GraphNode<?> slicingCriterion) {
        Slice sliceAggregate = new Slice(Set.of(slicingCriterion));
        Set<GraphNode<?>> visitedAgg = new HashSet<>();

        passBackwards(sliceAggregate, visitedAgg, this::ignoreProcedure);
        passForwards(sliceAggregate, visitedAgg, this::ignoreProcedure);

        visitedAgg.forEach(sliceAggregate::add);        
        return sliceAggregate;
    }

    @Override
    public Slice traverse(Set<GraphNode<?>> slicingCriterion) {
        Slice sliceAggregate = new Slice(slicingCriterion);
        Set<GraphNode<?>> visitedAgg = new HashSet<>();

        passBackwards(sliceAggregate, visitedAgg, this::ignorePass1);
        passBackwards(sliceAggregate, visitedAgg, this::ignorePass2);

        passForwards(sliceAggregate, visitedAgg, this::ignorePass1);
        passForwards(sliceAggregate, visitedAgg, this::ignorePass2);

        visitedAgg.forEach(sliceAggregate::add);
        return sliceAggregate;
    }

    /** The condition to ignore arcs in the first pass of the algorithm. */
    protected boolean ignorePass1(Arc arc) {
        return arc.isInterproceduralOutputArc();
    }

    /** The condition to ignore arcs in the second pass of the algorithm. */
    protected boolean ignorePass2(Arc arc) {
        return arc.isInterproceduralInputArc();
    }

    /** The condition to ignore arcs in intraprocedural slicing. */
    public boolean ignoreProcedure(Arc arc) {
        return arc instanceof InterproceduralArc;
    }

    /** A single pass: the edges are traversed until no new node can be added. Reached nodes
     *  are stored in the first parameter, and arcs that match the second are ignored. */
    protected void passBackwards(Slice slice, Set<GraphNode<?>> visitedAgg, Predicate<Arc> ignoreCondition) {
        // `toVisit` behaves like a set and using iterable we can use it as a queue
        // More info: https://stackoverflow.com/a/2319126
        LinkedHashSet<GraphNode<?>> toVisit = new LinkedHashSet<>(slice.getGraphNodes());
        Set<GraphNode<?>> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {
            GraphNode<?> node = Utils.setPop(toVisit);
            // Avoid duplicate traversal
            if (visited.contains(node))
                continue;
            visited.add(node);
            // Traverse all edges backwards
            for (Arc arc : graph.incomingEdgesOf(node)) {
                if (ignoreCondition.test(arc))
                    continue;
                GraphNode<?> source = graph.getEdgeSource(arc);
                if (!visited.contains(source))
                    toVisit.add(source);
                if (!visitedAgg.contains(source))
                    visitedAgg.add(source);
            }
        }

    }

    /** A single pass: the edges are traversed until no new node can be added. Reached nodes
     *  are stored in the first parameter, and arcs that match the second are ignored. */
    protected void passForwards(Slice slice, Set<GraphNode<?>> visitedAgg, Predicate<Arc> ignoreCondition) {
        // `toVisit` behaves like a set and using iterable we can use it as a queue
        // More info: https://stackoverflow.com/a/2319126
        LinkedHashSet<GraphNode<?>> toVisit = new LinkedHashSet<>(slice.getGraphNodes());
        Set<GraphNode<?>> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {
            GraphNode<?> node = Utils.setPop(toVisit);
            // Avoid duplicate traversal
            if (visited.contains(node))
                continue;
            visited.add(node);
            // Traverse all edges forwards
            for (Arc arc : graph.outgoingEdgesOf(node)) {
                if (ignoreCondition.test(arc))
                    continue;
                GraphNode<?> target = graph.getEdgeTarget(arc);
                if (!visited.contains(target))
                    toVisit.add(target);
                if (!visitedAgg.contains(target))
                    visitedAgg.add(target);
            }
        }

    }
}