import vis, { Edge, Node } from "vis-network";
import { generateStatsSVG } from "./generateStatsSVG";
import { dcbn } from "./graph";

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
  actionCallback: (nodeId: number, upper: boolean) => void
) {
  const { network, nodeIndices, nodes } = createGraph(container, graph, {
    nodes: { image: generateStatsSVG(undefined), shape: "image" }
  });

  network.on("doubleClick", param => {
    const nodeId: number = param.nodes[0];
    const upper =
      network.getPositions([nodeId])[nodeId].y > param.pointer.canvas.y;

    actionCallback(nodeId, upper);
  });

  return { nodeData: nodes, nodeIndices, network };
}

export function createGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  options: vis.Options
) {
  const nodes: Node[] = [];
  const nodeIndices: string[] = graph.nodes.map(n => n.name).sort();
  const edges: Edge[] = [];

  graph.nodes.forEach(node => {
    const nodeId = nodeIndices.indexOf(node.name);

    nodes.push({
      id: nodeId,
      label: node.name,
      ...node.position
    });

    edges.push(
      ...(node.timeTDependency.parents as string[]).map(parent => ({
        from: nodeIndices.indexOf(parent),
        to: nodeId
      }))
    );

    edges.push(
      ...(node.timeTDependency.parentsTm1 as string[]).map(
        (parent): Edge => ({
          from: nodeIndices.indexOf(parent),
          to: nodeId,
          dashes: true,
          label: "time",
          color: "grey",
          physics: true,
          smooth: true
        })
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

  return { network, nodeIndices, ...data };
}
