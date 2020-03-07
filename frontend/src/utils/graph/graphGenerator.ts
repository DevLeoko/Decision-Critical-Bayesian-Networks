import vis, { Edge, Node } from "vis-network";
import { generateStatsSVG } from "./generateStatsSVG";
import { dcbn } from "./graph";
import NodeMap from "../nodeMap";

export const defaultColor = "#67809f";

export function createEditGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  graphManipulationCallbacks: { [name: string]: Function }
) {
  var options: vis.Options = {
    nodes: {
      shape: "square",
      title: undefined,
      value: undefined,
      color: defaultColor,
      borderWidth: 0
    },
    manipulation: graphManipulationCallbacks
  };

  return createGraph(container, graph, options);
}

export function createTestGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  actionCallback: (nodeId: string, upper: boolean) => void
) {
  const { network, nodeMap, nodes } = createGraph(
    container,
    graph,
    {
      nodes: { image: generateStatsSVG(undefined), shape: "image" }
    },
    1.5
  );

  network.on("doubleClick", param => {
    const nodeId: string = param.nodes[0];
    const upper =
      network.getPositions([nodeId])[nodeId].y > param.pointer.canvas.y;

    actionCallback(nodeId, upper);
  });

  const nodeIndices = nodeMap
    .nodes()
    .map(node => node.name)
    .sort((a, b) => a.localeCompare(b));

  return { nodeData: nodes, nodeIndices, nodeMap, network };
}

export const timeEdgeOptions = {
  dashes: true,
  label: "time",
  color: "grey",
  smooth: { enabled: true, type: "diagonalCross", roundness: 0.3 }
};

export function createGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  options: vis.Options,
  xScaling = 1
) {
  const nodes: Node[] = [];
  const edges: Edge[] = [];
  const nodeMap = new NodeMap();
  const timeEdges: string[] = [];

  for (const node of graph.nodes) {
    const nodeId = vis.util.randomUUID();
    nodeMap.put(nodeId, node);
  }

  graph.nodes.forEach(node => {
    const nodeId = nodeMap.getUuidFromName(node.name);

    nodes.push({
      id: nodeId,
      label: node.name,
      x: node.position.x * xScaling,
      y: node.position.y,
      color: node.color
    });

    edges.push(
      ...node.timeTDependency.parents.map(parent => ({
        from: nodeMap.getUuidFromName(parent),
        to: nodeId,
        color: defaultColor
      }))
    );

    edges.push(
      ...node.timeTDependency.parentsTm1.map(
        (parent): Edge => {
          const uuid = vis.util.randomUUID();
          timeEdges.push(uuid);
          return {
            id: uuid,
            from: nodeMap.getUuidFromName(parent),
            to: nodeId,
            ...timeEdgeOptions
          };
        }
      )
    );
  });

  const nodeData = new vis.DataSet(nodes);
  const edgeData = new vis.DataSet(edges);

  var data = {
    nodes: nodeData,
    edges: edgeData
  };

  var baseOptions: vis.Options = {
    physics: false,
    autoResize: true,
    height: "100%",
    edges: {
      smooth: false,
      length: 250,
      arrows: {
        to: {
          enabled: true,
          scaleFactor: 0.6,
          type: "arrow"
        }
      }
    },
    ...options
  };

  const network = new vis.Network(container, data, baseOptions);

  return { network, nodeMap, timeEdges, ...data };
}
