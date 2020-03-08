<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar
      :timeSteps.sync="graph.timeSlices"
      :loading="saveLoading"
      :upToDate="upToDate"
      @save="save()"
      @nodeAdd="addNode()"
      @edgeAdd="addEdge()"
      @edgeTAdd="addTEdge()"
      @formatNetwork="formatGraph()"
      @undo="undo()"
      @redo="redo()"
      :undoDisabled="!undoStack.length"
      :redoDisabled="!redoStack.length"
    />
    <div id="mynetwork" ref="network"></div>
    <action-selector ref="nodeActionSelector">
      <v-btn tile @click="editProperties = true">
        {{ $t("graph.edit.properties") }}
      </v-btn>
      <v-btn tile @click="deleteNode()">
        {{ $t("graph.edit.delete") }}
      </v-btn>
    </action-selector>

    <action-selector ref="edgeActionSelector" isEdgeSelector>
      <v-btn tile @click="deleteEdge()">
        {{ $t("graph.edit.delete") }}
      </v-btn>
    </action-selector>

    <node-properties
      :open.sync="editProperties"
      :node="selectedNode"
      @save="saveProperties($event)"
    />
    <v-snackbar v-model="hasError" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasError = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>

    <v-dialog :value="saveAlert.value" width="400" persistent>
      <v-card>
        <v-card-title>
          {{ $t("graph.edit.unsavedChanges.title") }}
        </v-card-title>
        <v-card-text>
          {{ $t("graph.edit.unsavedChanges.text") }}
        </v-card-text>
        <v-card-actions class="px-4 pb-4">
          <v-btn @click="saveAlert.value = false" color="grey" text>
            {{ $t("graph.edit.unsavedChanges.cancel") }}
          </v-btn>
          <v-spacer />
          <v-btn
            @click="
              saveAlert.value = false;
              saveAlert.proceedAction();
            "
            color="error"
            text
          >
            {{ $t("graph.edit.unsavedChanges.discard") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import Vue from "vue";
import vis, { data } from "vis-network";
import ActionSelector from "@/components/graph/ActionSelector.vue";
import NodeProperties from "@/components/graph/properties/NodeProperties.vue";
import { dcbn } from "@/utils/graph/graph";
import {
  createEditGraph,
  defaultColor,
  timeEdgeOptions
} from "@/utils/graph/graphGenerator";
import NodeMap from "../../utils/nodeMap";
import { formatGraph } from "../../utils/graph/graphFormatter";
import { router } from "../../router";

let network = {} as vis.Network;
let upToDate = true;
let saveAlert = {
  value: false,
  proceedAction: null as Function | null
};

router.beforeEach((to, from, next) => {
  if (from.name == "Edit Graph") {
    if (!upToDate) {
      saveAlert.value = true;
      saveAlert.proceedAction = next;
      return;
    }
  }
  next();
});

interface EdgeAndNodeData {
  edges: vis.Edge[];
  nodes: vis.Node[];
}

interface GraphState {
  dcbnGraph: dcbn.Graph;
  visGraph: EdgeAndNodeData;
  timeEdges: string[];
  nodeMap: NodeMap;
}

export default Vue.extend({
  components: {
    EditBar,
    NodeProperties,
    ActionSelector
  },

  data() {
    return {
      graph: {} as dcbn.Graph,
      nodes: {} as vis.DataSet<vis.Node>,
      edges: {} as vis.DataSet<vis.Edge>,
      timeEdges: [] as string[],
      nodeMap: new NodeMap(),

      saveAlert,

      activeId: -1,
      editProperties: false,
      timeEdge: false,
      selectedNode: {} as dcbn.Node,
      hasError: false,
      errorMessage: "",

      saveLoading: false,
      serverGraph: "",
      undoStack: [] as GraphState[],
      redoStack: [] as GraphState[]
    };
  },

  methods: {
    save() {
      this.saveLoading = true;
      this.axios
        .put(`/graphs/${this.$route.params.id}`, this.graph)
        .then(() => (this.serverGraph = JSON.stringify(this.graph)))
        .catch(error => {
          this.errorMessage = error.response.data.message;
          this.hasError = true;
        })
        .then(() => (this.saveLoading = false));
    },

    formatGraph() {
      this.addToUndoStack();
      formatGraph(this.nodeMap, this.nodes, this.edges);
      network.stabilize(0);
    },

    addNode() {
      network.addNodeMode();
    },

    deleteNode() {
      network.deleteSelected();
    },

    addEdge() {
      this.timeEdge = false;
      network.addEdgeMode();
    },
    addTEdge() {
      this.timeEdge = true;
      network.addEdgeMode();
    },

    deleteEdge() {
      network.deleteSelected();
    },

    findPowerOfTwo(
      dependency: dcbn.TimeZeroDependency,
      nodeName: string
    ): number {
      const totalList = Object.assign([], dependency.parents) as string[];
      totalList.push(...dependency.parentsTm1);
      return 2 ** (totalList.length - totalList.indexOf(nodeName) - 1);
    },

    addToDependencies(dependency: dcbn.TimeZeroDependency, fromName: string) {
      const powerOfTwo = this.findPowerOfTwo(dependency, fromName);

      let index = powerOfTwo;
      while (index <= dependency.probabilities.length) {
        const toAdd = JSON.parse(
          JSON.stringify(
            dependency.probabilities.slice(index - powerOfTwo, index)
          )
        );
        dependency.probabilities.splice(index, 0, ...toAdd);
        index += 2 * powerOfTwo;
      }
    },

    removeFromDependencies(
      dependency: dcbn.TimeZeroDependency,
      fromName: string
    ) {
      const powerOfTwo = this.findPowerOfTwo(dependency, fromName);

      let index = powerOfTwo;
      while (index <= dependency.probabilities.length) {
        dependency.probabilities.splice(index, powerOfTwo);
        index += powerOfTwo;
      }
    },

    removeDependencies(
      toNode: dcbn.Node,
      fromName: string,
      isTimeDependency: boolean = false
    ) {
      if (!toNode) {
        return;
      }

      if (!isTimeDependency) {
        this.removeFromDependencies(toNode.timeZeroDependency, fromName);
      }
      this.removeFromDependencies(toNode.timeTDependency, fromName);

      if (isTimeDependency) {
        const index = toNode.timeTDependency.parentsTm1.indexOf(fromName);
        if (index !== -1) {
          toNode.timeTDependency.parentsTm1.splice(index, 1);
        }
      } else {
        let index = toNode.timeZeroDependency.parents.indexOf(fromName);
        if (index !== -1) {
          toNode.timeZeroDependency.parents.splice(index, 1);
        }
        index = toNode.timeTDependency.parents.indexOf(fromName);
        if (index !== -1) {
          toNode.timeTDependency.parents.splice(index, 1);
        }
      }

      if (!toNode.timeTDependency.parentsTm1.length) {
        toNode.timeTDependency.probabilities = JSON.parse(
          JSON.stringify(toNode.timeZeroDependency.probabilities)
        );
      }
    },

    generateNewNodeName() {
      const defaultName = "newNode";
      for (let i = 0; ; i++) {
        let testName = `${defaultName}${i === 0 ? "" : i}`;
        if (!this.graph!.nodes.filter(node => node.name == testName).length) {
          return testName;
        }
      }
    },

    addNodeToGraph(data: any, callback: Function) {
      this.addToUndoStack();

      data.label = this.generateNewNodeName();
      const node = {
        type: "Node",
        name: data.label,
        id: 0,
        timeZeroDependency: {
          id: 0,
          parents: [],
          parentsTm1: [],
          probabilities: [[0.5, 0.5]]
        },
        timeTDependency: {
          id: 0,
          parents: [],
          parentsTm1: [],
          probabilities: [[0.5, 0.5]]
        },
        color: defaultColor,
        evidenceFormulaName: null,
        stateType: {
          states: ["true", "false"]
        },
        position: {
          x: data.x,
          y: data.y
        }
      };
      this.nodeMap.put(data.id, node);
      this.graph.nodes.push(node);

      callback(data);
    },

    addEdgeToGraph(data: any, callback: Function) {
      this.addToUndoStack();
      const fromId = data.from as string;
      const toId = data.to as string;

      const fromName = this.nodeMap.get(fromId)!.name;
      const toNode = this.nodeMap.get(toId)!;

      if (
        (this.timeEdge &&
          toNode.timeTDependency.parentsTm1.includes(fromName)) ||
        (!this.timeEdge && toNode.timeZeroDependency.parents.includes(fromName))
      ) {
        this.hasError = true;
        this.errorMessage = this.$t("graph.edit.errors.duplicateEdge", {
          from: fromName,
          to: toNode.name
        }).toString();
        return;
      }

      if (this.timeEdge) {
        toNode.timeTDependency.parentsTm1.push(fromName);
      } else {
        toNode.timeZeroDependency.parents.push(fromName);
        toNode.timeTDependency.parents.push(fromName);
      }

      if (!this.timeEdge) {
        this.addToDependencies(toNode.timeZeroDependency, fromName);
      }

      this.addToDependencies(toNode.timeTDependency, fromName);

      if (!toNode.timeTDependency.parentsTm1.length) {
        toNode.timeTDependency.probabilities = JSON.parse(
          JSON.stringify(toNode.timeZeroDependency.probabilities)
        );
      }

      if (this.timeEdge) {
        const uuid = vis.util.randomUUID();
        this.timeEdges.push(uuid);
        data = {
          id: uuid,
          ...data,
          ...timeEdgeOptions
        };
      } else {
        data = {
          ...data,
          color: defaultColor
        };
      }
      callback(data);
    },

    deleteNodeFromGraph(data: any, callback: Function) {
      this.addToUndoStack();

      for (let edgeUuid of data.edges as string[]) {
        const edge = this.edges!.get(edgeUuid);

        if (!edge) {
          break;
        }
        const toNode = this.nodeMap.get(edge.to as string)!;
        const fromName = this.nodeMap.get(edge.from as string)!.name;

        this.removeDependencies(toNode, fromName, false);
        if (this.timeEdges.includes(edgeUuid)) {
          this.removeDependencies(toNode, fromName, true);
        }
      }
      this.graph!.nodes.splice(
        this.graph!.nodes.findIndex(
          node => node.name === this.nodeMap.get(data.nodes[0] as string)!.name
        ),
        1
      );

      this.nodeMap.remove(data.nodes[0] as string);
      callback(data);
    },

    deleteEdgeFromGraph(data: any, callback: Function) {
      this.addToUndoStack();
      const edge = this.edges!.get(data.edges[0] as string)!;
      const fromName = this.nodeMap.get(edge.from as string)!.name;
      const toNode = this.nodeMap.get(edge.to as string)!;

      const isTimeEdge = this.timeEdges.includes(data.edges[0] as string);

      if (isTimeEdge) {
        this.timeEdges.splice(
          this.timeEdges.findIndex(str => str === data.edges[0]),
          1
        );
      }

      this.removeDependencies(toNode, fromName, isTimeEdge);
      callback(data);
    },

    undo() {
      const state = this.undoStack.pop()!;
      this.pushCurrentStateTo(this.redoStack);
      this.updateGraphStates(state);
    },

    redo() {
      const state = this.redoStack.pop()!;
      this.pushCurrentStateTo(this.undoStack);
      this.updateGraphStates(state);
    },

    updateGraphStates(state: GraphState) {
      this.graph = state.dcbnGraph;
      this.nodes.clear();
      this.nodes.add(state.visGraph.nodes);
      this.edges.clear();
      this.edges.add(state.visGraph.edges);

      this.timeEdges = state.timeEdges;
      this.nodeMap = state.nodeMap;
    },

    pushCurrentStateTo(stack: GraphState[]) {
      const copy = this.copyState({
        visGraph: {
          nodes: this.nodes.get(),
          edges: this.edges.get()
        },
        dcbnGraph: this.graph,
        timeEdges: this.timeEdges,
        nodeMap: this.nodeMap
      });
      stack.push(copy);
    },

    addToUndoStack() {
      this.redoStack.length = 0;
      this.pushCurrentStateTo(this.undoStack);
    },

    copyState(state: GraphState): GraphState {
      const visCopy = JSON.parse(JSON.stringify(state.visGraph));
      const nodeMapCopy = state.nodeMap.clone();
      const graphCopy = JSON.parse(
        JSON.stringify(state.dcbnGraph)
      ) as dcbn.Graph;
      graphCopy.nodes = nodeMapCopy.nodes();
      const timeEdgesCopy = Object.assign([], state.timeEdges);

      return {
        visGraph: visCopy,
        dcbnGraph: graphCopy,
        nodeMap: nodeMapCopy,
        timeEdges: timeEdgesCopy
      };
    },

    updateNodeName(oldName: string, newName: string) {
      for (const node of this.nodeMap.nodes()) {
        const parents = [
          node.timeZeroDependency.parents,
          node.timeZeroDependency.parentsTm1,
          node.timeTDependency.parents,
          node.timeTDependency.parentsTm1
        ];
        for (const parentList of parents) {
          const index = parentList.findIndex(name => name === oldName);
          if (index !== -1) {
            parentList[index] = newName;
          }
        }
      }
    },

    saveProperties(event: any) {
      const name = event.oldName as string;
      const node = event.node as dcbn.Node;
      const uuid = this.nodeMap.getUuidFromName(name)!;

      if (/^\s*$/.test(node.name)) {
        this.hasError = true;
        this.errorMessage = this.$t(
          "graph.edit.errors.emptyNodeName"
        ).toString();
        return;
      }
      if (name !== node.name) {
        const nodesWithSameName = this.nodeMap
          .nodes()
          .filter(n => n.name === node.name);
        if (nodesWithSameName.length !== 0) {
          this.hasError = true;
          this.errorMessage = this.$t("graph.edit.errors.duplicateName", {
            name: node.name
          }).toString();
          return;
        }
      }

      this.addToUndoStack();

      const nodeToSaveTo = this.nodeMap.get(uuid)!;
      nodeToSaveTo.color = node.color;
      nodeToSaveTo.name = node.name;
      nodeToSaveTo.timeZeroDependency.probabilities =
        node.timeZeroDependency.probabilities;
      nodeToSaveTo.timeTDependency.probabilities = node.timeTDependency
        .parentsTm1.length
        ? node.timeTDependency.probabilities
        : node.timeZeroDependency.probabilities;
      nodeToSaveTo.evidenceFormulaName = node.evidenceFormulaName;

      this.updateNodeName(name, node.name);
      this.nodes.update({ id: uuid, color: node.color, label: node.name });
    },

    updateLock() {
      if (this.$route.params.id !== undefined) {
        this.axios
          .put(`/graphs/${this.$route.params.id}/lock`)
          .then(() => setTimeout(this.updateLock.bind(this), 2500));
      }
    }
  },

  mounted() {
    this.axios
      .get(`/graphs/${this.$route.params.id}`)
      .then(resp => {
        this.graph = resp.data;
        this.serverGraph = JSON.stringify(this.graph);
        const self = this;
        const result = createEditGraph(
          document.getElementById("mynetwork")!,
          this.graph!,
          {
            addNode: this.addNodeToGraph,
            addEdge: this.addEdgeToGraph,
            deleteNode: this.deleteNodeFromGraph,
            deleteEdge: this.deleteEdgeFromGraph
          }
        );

        this.nodeMap = result.nodeMap;
        this.nodes = result.nodes;
        this.edges = result.edges;
        this.timeEdges = result.timeEdges;
        network = result.network;

        (this.$refs.nodeActionSelector as any).register(network);
        (this.$refs.edgeActionSelector as any).register(network);

        network.on("click", graph => {
          if (graph.nodes[0]) {
            this.selectedNode = JSON.parse(
              JSON.stringify(this.nodeMap.get(graph.nodes[0]))
            );
          }
        });

        network.on("dragEnd", event => {
          if (!event.nodes.length) {
            return;
          }
          this.addToUndoStack();

          const nodeId = event.nodes[0] as string;
          const node = this.nodeMap.get(nodeId)!;

          const newPosition = network.getPositions([nodeId])[nodeId];
          node.position = newPosition;
          network.storePositions();
        });
      })
      .catch(error => {
        this.errorMessage = error.response.data.message;
        this.hasError = true;
      });

    this.updateLock();
  },

  computed: {
    upToDate(): Boolean {
      upToDate = this.serverGraph == JSON.stringify(this.graph);
      return upToDate;
    }
  }
});
</script>
