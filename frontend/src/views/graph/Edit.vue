<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar
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

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import NodeActionSelector from "@/components/graph/NodeActionSelector.vue";
import Vue from "vue";
import vis, { data } from "vis-network";
import NodeProperties from "@/components/graph/NodeProperties.vue";
import { dcbn } from "@/utils/graph/graph";
import { createEditGraph, defaultColor } from "@/utils/graph/graphGenerator";

let network = {} as vis.Network;

export default Vue.extend({
  components: {
    EditBar,
    NodeActionSelector,
    NodeProperties
  },

  data() {
    return {
      //TODO null problems?
      graph: null as dcbn.Graph | null,
      nodes: null as vis.DataSet<vis.Node, "id"> | null,
      nodeIndecies: [] as string[],
      edges: null as vis.DataSet<vis.Edge, "id"> | null,

      activeId: -1,
      editProperties: false,
      timeEdge: false,
      //TODO replace with $emit?
      selectedNode: null as dcbn.Node | null,
      hasError: false,
      errorMessage: ""
    };
  },

  methods: {
    addNode: function() {
      network.addNodeMode;
    },

    del: function() {
      network.deleteSelected();
    },

    addEdge: function() {
      this.timeEdge = false;
      var options = {
        edges: {
          label: " "
        }
      };
      network.setOptions(options);
      network.addEdgeMode();
    },
    addTEdge: function() {
      this.timeEdge = true;
      var optionsT = {
        edges: {
          label: "T",
          smooth: {
            enabled: true,
            type: "dynamic",
            roundness: 0.5
          }
        }
      };
      network.setOptions(optionsT);
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
          toNode.timeZeroDependency.parents.indexOf(fromName),
          1
        );
      }
    }
  },

  mounted() {
    this.axios
      .get(`/graphs/${this.$route.params.id}`)
      .then(res => {
        this.graph = res.data as dcbn.Graph;
        const self = this;
        const result = createEditGraph(
          document.getElementById("mynetwork")!,
          this.graph,
          {
            addNode(data: any, callback: Function) {
              self.graph!.nodes.push({
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
              });
              callback(data);
            },
            addEdge(data: any, callback: Function) {
              const fromId = data.from as number;
              const toId = data.to as number;

              const fromName = self.nodeIndecies[fromId];
              const toName = self.nodeIndecies[toId];

              const fromNode = self.graph!.nodes.find(
                node => node.name == fromName
              )!;
              const toNode = self.graph!.nodes.find(
                node => node.name === toName
              )!;

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
              callback(data);
            },
            deleteNode(data: any, callback: Function) {
              for (let edgeUuid of data.edges as string[]) {
                const edge = self.edges!.get(edgeUuid);
                if (!edge) {
                  break;
                }
                const toName = self.nodeIndecies[edge.to as number];
                const fromName = self.nodeIndecies[edge.from as number];

                const toNode = self.graph!.nodes.find(
                  node => node.name === toName
                )!;
                self.removeDependencies(toNode, fromName);
              }
              self.graph!.nodes.splice(
                self.graph!.nodes.findIndex(
                  node =>
                    node.name === self.nodeIndecies[data.nodes[0] as number]
                ),
                1
              );
              callback(data);
            },
            deleteEdge(data: any, callback: Function) {
              callback(data);
            }
          }
        );

        this.nodeIndecies = result.nodeIndices;
        this.nodes = result.nodes;
        this.edges = result.edges;
        network = result.network;

        (this.$refs.nodeActionSelector as any).register(network);

        network.on("click", graph => {
          if (graph.nodes[0]) {
            const nodeVis = this.nodes!.get(graph.nodes[0], {
              fields: ["label"]
            })!;
            this.selectedNode = this.graph!.nodes.find(
              node => node.name == nodeVis.label
            )!;
          } else {
            this.selectedNode = null;
          }
        });

        network.on("dragEnd", event => {
          if (!event.nodes.length) {
            return;
          }
          const nodeId = event.nodes[0];
          const nodeName = this.nodeIndecies[nodeId];

          const node = this.graph!.nodes.find(node => node.name === nodeName);
          if (!node) {
            return;
          }

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
