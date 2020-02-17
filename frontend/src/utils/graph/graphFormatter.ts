import dagre from "dagre";
import NodeMap from "../nodeMap";
import vis from "vis-network";

export function formatGraph(
  nodeMap: NodeMap,
  visNodes: vis.data.DataSet<vis.Node, "id">,
  visEdges: vis.data.DataSet<vis.Edge, "id">
) {
  var g = new dagre.graphlib.Graph();
  g.setGraph({});
  g.setDefaultEdgeLabel(() => ({}));

  visNodes.forEach(node => {
    g.setNode(node.id as string, {
      label: node.id,
      width: 300,
      height: 100
    });
  });

  visEdges.forEach(edge => {
    g.setEdge(edge.from as string, edge.to as string);
  });

  dagre.layout(g);

  g.nodes().forEach(nodeId => {
    const node = g.node(nodeId);

    visNodes.update({ id: nodeId, x: node.x, y: node.y });
    const dcbnNode = nodeMap.get(nodeId)!;
    dcbnNode.position = { x: node.x, y: node.y };
  });
}
