package tfm.graphs;

import com.github.javaparser.ast.stmt.Statement;
import tfm.arcs.Arc;
import tfm.arcs.data.ArcData;
import tfm.nodes.Node;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * A graphlibGraph without cost and data in arcs
 * */
public abstract class Graph<NodeType extends Node> extends edg.graphlib.Graph<String, ArcData> {

    public final static class NodeId {
        private static int nextVertexId = 0;

        private int id;

        private NodeId(int id) {
            this.id = id;
        }

        static synchronized NodeId getVertexId() {
            return new NodeId(nextVertexId++);
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    public Graph() {
        super();
    }

    @SuppressWarnings("unchecked")
    public NodeType getRootNode() {
        return (NodeType) super.getRootVertex();
    }

    public abstract NodeType addNode(String instruction, Statement statement);

    public String toString() {
        return getVerticies().stream()
                .map(edg.graphlib.Vertex::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public abstract String toGraphvizRepresentation();

}
