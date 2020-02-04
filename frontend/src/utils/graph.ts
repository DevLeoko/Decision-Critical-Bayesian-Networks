export declare module dcbn {
  export interface TimeZeroDependency {
    id: number;
    parents: string[];
    parentsTm1: string[];
    probabilities: number[][];
  }

  export interface TimeTDependency {
    id: number;
    parents: string[];
    parentsTm1: string[];
    probabilities: number[][];
  }

  export interface StateType {
    states: string[];
  }

  export interface Position {
    x: number;
    y: number;
  }

  export interface Node {
    type: string;
    name: string;
    id: number;
    timeZeroDependency: TimeZeroDependency;
    timeTDependency: TimeTDependency;
    color: string;
    evidenceFormulaName?: any;
    stateType: StateType;
    position: Position;
    value?: number[][];
  }

  export interface Graph {
    id: number;
    name: string;
    timeSlices: number;
    nodes: Node[];
  }

  export interface DenseGraph {
    name: string;
    id: number;
  }

  export interface GraphResult {
    [key: string]: number[][];
  }

  export type NodeValueType = "evidence" | "empty" | "computed";
}

export function generateGraphImage(
  graphValue: number[] | undefined,
  type: dcbn.NodeValueType = "empty",
  virtualEvidence: number | null = null
): string {
  const svgContainer = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "svg"
  );

  svgContainer.setAttribute("xmlns", "http://www.w3.org/2000/svg");
  svgContainer.setAttribute("viewBox", "0 0 300 100");
  svgContainer.setAttribute("width", "300");
  svgContainer.setAttribute("height", "100");

  const background = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "rect"
  );

  background.setAttribute("width", "100%");
  background.setAttribute("height", "100%");
  background.setAttribute(
    "fill",
    type == "evidence"
      ? "#e47833"
      : type == "empty"
      ? "#6c7a89"
      : type == "computed"
      ? "#3498db"
      : ""
  );
  background.setAttribute("rx", "4");
  background.setAttribute("ry", "4");

  svgContainer.appendChild(background);

  const line = document.createElementNS("http://www.w3.org/2000/svg", "line");

  line.setAttribute("x1", "0%");
  line.setAttribute("x2", "100%");
  line.setAttribute("y1", "50%");
  line.setAttribute("y2", "50%");
  line.setAttribute("opacity", "0.3");
  line.setAttribute("stroke", "white");
  line.setAttribute("stroke-dasharray", "17");

  svgContainer.appendChild(line);

  if (virtualEvidence !== null) {
    const virtualLine = document.createElementNS(
      "http://www.w3.org/2000/svg",
      "line"
    );

    virtualLine.setAttribute("x1", "0%");
    virtualLine.setAttribute("x2", "100%");
    virtualLine.setAttribute("y1", `${80 * (1 - virtualEvidence) + 10}%`);
    virtualLine.setAttribute("y2", `${80 * (1 - virtualEvidence) + 10}%`);
    virtualLine.setAttribute("opacity", "0.4");
    virtualLine.setAttribute("stroke", "#446cb3"); //67809f
    virtualLine.setAttribute("stroke-width", "3");

    svgContainer.appendChild(virtualLine);
  }

  if (graphValue) {
    const graphString =
      "M" +
      graphValue
        .map(
          (val, i) =>
            `${(300 / (graphValue.length - 1)) * i} ${80 * (1 - val) + 10} ${
              graphValue.length == i + 1 ? "" : "L"
            }`
        )
        .join("");

    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", graphString);
    path.setAttribute("fill", "none");
    path.setAttribute("stroke", "white");
    path.setAttribute("opacity", "1");
    path.setAttribute("stroke-width", "5");
    svgContainer.appendChild(path);
  }

  const svg = svgContainer.outerHTML;

  return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg);
}

import vis, { Edge, Node } from "vis-network";

export function createEditGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  nodeSelect: (nodeId: number, position: { x: number; y: number }) => void
) {
  const nodes: Node[] = [];
  const nodeIndecies: string[] = graph.nodes.map(n => n.name).sort();
  const edges: Edge[] = [];

  graph.nodes.forEach(node => {
    const nodeId = nodeIndecies.indexOf(node.name);

    nodes.push({
      id: nodeId,
      label: node.name
    });

    edges.push(
      ...(node.timeTDependency.parents as string[]).map(parent => ({
        from: nodeIndecies.indexOf(parent),
        to: nodeId
      }))
    );

    edges.push(
      ...(node.timeTDependency.parentsTm1 as string[]).map(
        (parent): Edge => ({
          from: nodeIndecies.indexOf(parent),
          to: nodeId,
          dashes: true,
          label: "time",
          color: "grey",
          physics: true,
          smooth: false
        })
      )
    );
  });

  const nodeData = new vis.DataSet(nodes);

  var data = {
    nodes: nodeData,
    edges: edges
  };

  var options: vis.Options = {
    physics: false,
    autoResize: true,
    height: "100%",
    nodes: {
      shape: "square",
      title: undefined,
      value: undefined
    },
    edges: {
      smooth: false,
      arrows: {
        to: {
          enabled: true,
          scaleFactor: 1,
          type: "arrow"
        }
      }
    },
    manipulation: {
      addNode: true,
      addEdge: true,
      editNode: undefined,
      editEdge: true,
      deleteNode: true,
      deleteEdge: true
    },
    layout: {
      randomSeed: 2,
      improvedLayout: true
    }
  };

  const net = new vis.Network(container, data, options);

  net.on("selectNode", param => {
    param.event.preventDefault();
    param.event.srcEvent.stopPropagation();
    param.event.srcEvent.preventDefault();
    nodeSelect(param.nodes[0], param.event.center);

    return false;
  });

  return { nodeData, nodeIndecies, net };
}

export function createVisGraph(
  container: HTMLElement,
  graph: dcbn.Graph,
  actionCallback: (nodeId: number, upper: boolean) => void
) {
  const nodes: Node[] = [];
  const nodeIndices: string[] = graph.nodes.map(n => n.name).sort();
  const edges: Edge[] = [];

  graph.nodes.forEach(node => {
    const nodeId = nodeIndices.indexOf(node.name);

    nodes.push({
      id: nodeId,
      label: node.name,
      image: generateGraphImage(undefined),
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

  var data = {
    nodes: nodeData,
    edges: edges
  };

  var options: vis.Options = {
    physics: false,
    autoResize: true,
    height: "100%",
    nodes: {
      shape: "image"
    },
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
    }
  };

  const network = new vis.Network(container, data, options);

  network.on("doubleClick", param => {
    const nodeId: number = param.nodes[0];
    const upper =
      network.getPositions([nodeId])[nodeId].y > param.pointer.canvas.y;

    actionCallback(nodeId, upper);
  });

  return { nodeData, nodeIndices, network };
}
