<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar
      @save="save()"
      @nodeAdd="addNode()"
      @edgeAdd="addEdge()"
      @edgeTAdd="addTEdge()"
    />
    <div id="mynetwork" ref="network"></div>
    <node-action-selector ref="nodeActionSelector">
      <v-btn tile @click="editProperties = true">
        Properties
      </v-btn>
      <v-btn tile @click="del()">
        Delete
      </v-btn>
    </node-action-selector>

    <node-properties :open.sync="editProperties" :node="selectedNode" />
    <v-snackbar v-model="hasError" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasError = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  border: 1px solid lightgray;
  max-height: 100vh;
}
</style>

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import Vue from "vue";
import vis, { data } from "vis-network";
import NodeActionSelector from "@/components/graph/NodeActionSelector.vue";
import NodeProperties from "@/components/graph/NodeProperties.vue";
import { dcbn } from "@/utils/graph/graph";
import {
  createEditGraph,
  defaultColor,
  timeEdgeOptions
} from "@/utils/graph/graphGenerator";
import NodeMap from "../../utils/nodeMap";

let network = {} as vis.Network;

export default Vue.extend({
  components: {
    EditBar,
    NodeProperties,
    NodeActionSelector
  },

  data() {
    return {
      //TODO null problems?
      graph: {} as dcbn.Graph,
      nodes: {} as vis.DataSet<vis.Node>,
      edges: {} as vis.DataSet<vis.Edge>,
      nodeMap: new NodeMap(),

      activeId: -1,
      editProperties: false,
      timeEdge: false,
      //TODO replace with $emit?
      selectedNode: {} as dcbn.Node,
      hasError: false,
      errorMessage: ""
    };
  },

  methods: {
    save() {
      // TODO: Provide the user with feedback.
      this.axios.put(`/graphs/${this.$route.params.id}`, this.graph);
    },

    addNode: function() {
      network.addNodeMode();
    },

    del: function() {
      network.deleteSelected();
    },

    addEdge: function() {
      this.timeEdge = false;
      network.addEdgeMode();
    },
    addTEdge: function() {
      this.timeEdge = true;
      network.addEdgeMode();
    },

    findPowerOfTwo(toNode: dcbn.Node, nodeName: string): number {
      const totalList = Object.assign(
        [],
        toNode.timeTDependency.parents
      ) as string[];
      totalList.push(...toNode.timeTDependency.parentsTm1);
      return 2 ** (totalList.length - totalList.indexOf(nodeName) - 1);
    },

    addToDependencies(dependency: dcbn.TimeZeroDependency, powerOfTwo: number) {
      let index = powerOfTwo;
      while (index <= dependency.probabilities.length) {
        const toAdd = dependency.probabilities.slice(index - powerOfTwo, index);
        dependency.probabilities.splice(index, 0, ...toAdd);
        index += 2 * powerOfTwo;
      }
    },

    removeFromDependencies(
      dependency: dcbn.TimeZeroDependency,
      powerOfTwo: number
    ) {
      let index = powerOfTwo;
      while (index <= dependency.probabilities.length) {
        dependency.probabilities.splice(index, powerOfTwo);
        index += powerOfTwo;
      }
    },

    removeDependencies(toNode: dcbn.Node, fromName: string) {
      // FIXME: This does not work when a node is both a time parent and a normal parent!!!
      if (!toNode) {
        return;
      }
      const isTimeDependency = toNode.timeTDependency.parentsTm1.includes(
        fromName
      );

      const powerOfTwo = this.findPowerOfTwo(toNode, fromName);
      if (!isTimeDependency) {
        this.removeFromDependencies(toNode.timeZeroDependency, powerOfTwo);
      }

      this.removeFromDependencies(toNode.timeTDependency, powerOfTwo);

      if (isTimeDependency) {
        toNode.timeTDependency.parentsTm1.splice(
          toNode.timeTDependency.parentsTm1.indexOf(fromName),
          1
        );
      } else {
        toNode.timeZeroDependency.parents.splice(
          toNode.timeZeroDependency.parents.indexOf(fromName),
          1
        );
        toNode.timeTDependency.parents.splice(
          toNode.timeTDependency.parents.indexOf(fromName),
          1
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
    }
  },

  mounted() {
    this.axios
      .get(`/graphs/${this.$route.params.id}`)
      .then(resp => {
        this.graph = resp.data;
        const self = this;
        const result = createEditGraph(
          document.getElementById("mynetwork")!,
          this.graph!,
          {
            addNode(data: any, callback: Function) {
              data.label = self.generateNewNodeName();
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
              self.nodeMap.put(data.id, node);
              self.graph.nodes.push(node);

              callback(data);
            },

            addEdge(data: any, callback: Function) {
              const fromId = data.from as string;
              const toId = data.to as string;

              const fromName = self.nodeMap.get(fromId)!.name;
              const toNode = self.nodeMap.get(toId)!;

              if (self.timeEdge) {
                toNode.timeTDependency.parentsTm1.push(fromName);
              } else {
                toNode.timeZeroDependency.parents.push(fromName);
                toNode.timeTDependency.parents.push(fromName);
              }

              const powerOfTwo = self.findPowerOfTwo(toNode, fromName);
              if (!self.timeEdge) {
                self.addToDependencies(toNode.timeZeroDependency, powerOfTwo);
              }

              self.addToDependencies(toNode.timeTDependency, powerOfTwo);

              if (self.timeEdge) {
                data = {
                  ...data,
                  ...timeEdgeOptions
                };
              }
              callback(data);
            },

            deleteNode(data: any, callback: Function) {
              for (let edgeUuid of data.edges as string[]) {
                const edge = self.edges!.get(edgeUuid);

                if (!edge) {
                  break;
                }
                const toNode = self.nodeMap.get(edge.to as string)!;
                const fromName = self.nodeMap.get(edge.from as string)!.name;

                self.removeDependencies(toNode, fromName);
              }
              self.graph!.nodes.splice(
                self.graph!.nodes.findIndex(
                  node =>
                    node.name ===
                    self.nodeMap.get(data.nodes[0] as string)!.name
                ),
                1
              );

              self.nodeMap.remove(data.nodes[0] as string);
              callback(data);
            },

            deleteEdge(data: any, callback: Function) {
              callback(data);
            }
          }
        );

        this.nodeMap = result.nodeMap;
        this.nodes = result.nodes;
        this.edges = result.edges;
        network = result.network;

        (this.$refs.nodeActionSelector as any).register(network);

        network.on("click", graph => {
          if (graph.nodes[0]) {
            this.selectedNode = this.nodeMap.get(graph.nodes[0])!;
          } else {
            this.selectedNode = {} as dcbn.Node;
          }
        });

        network.on("dragEnd", event => {
          if (!event.nodes.length) {
            return;
          }
          const nodeId = event.nodes[0];
          const node = this.nodeMap.get(nodeId)!;

          const newPosition = network.getPositions(nodeId)[0];
          node.position = newPosition;
        });
      })
      .catch(error => {
        this.errorMessage = error.response.data.message;
        this.hasError = true;
      });
  }
});
</script>
