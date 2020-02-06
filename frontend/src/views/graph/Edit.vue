<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar
      @nodeAdd="addNode()"
      @edgeAdd="addEdge()"
      @edgeTAdd="addTEdge()"
    />
    <div id="mynetwork" ref="network"></div>
    <v-menu
      v-model="showEditOptions"
      :position-x="x"
      :position-y="y"
      :close-on-click="false"
      absolute
    >
      <div class="white">
        <v-btn tile @click="editProperties = true">
          Properties
        </v-btn>
        <v-btn tile @click="del()">
          Delete
        </v-btn>
        <v-btn icon color="red">
          <v-icon>close</v-icon>
        </v-btn>
      </div>
    </v-menu>

    <v-dialog
      v-model="editProperties"
      fullscreen
      hide-overlay
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-toolbar dark color="primary">
          <v-btn icon dark @click="editProperties = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>Properties</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-items>
            <v-btn dark text @click="editProperties = false">Save</v-btn>
          </v-toolbar-items>
        </v-toolbar>
        <v-tabs>
          <v-tab>General</v-tab>

          <v-tab>Definition</v-tab>
          <v-tab>Format</v-tab>
        </v-tabs>
      </v-card>
    </v-dialog>
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
//Import test Graph

//Get the frontend Graph structure and the constructor
import { defaultColor, dcbn, createEditGraph } from "../../utils/graph";
import graphData from "@/../tests/resources/graph1.json";

let graph = graphData as dcbn.Graph;
let network = {} as vis.Network;

export default Vue.extend({
  components: {
    EditBar
  },

  data() {
    return {
      graph,
      nodes: null as vis.DataSet<vis.Node, "id"> | null,
      dcbnNodes: {} as { [uuid: string]: dcbn.Node },
      edges: null as vis.DataSet<vis.Edge, "id"> | null,
      showEditOptions: false,
      x: 0,
      y: 0,
      activeId: -1,
      editProperties: false,
      timeEdge: false
    };
  },

  methods: {
    addNode: function() {
      network.addNodeMode();
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
    },

    generateNewNodeName() {
      const defaultName = "newNode";
      for (let i = 0; ; i++) {
        let testName = `${defaultName}${i === 0 ? "" : i}`;
        if (!this.graph.nodes.filter(node => node.name == testName).length) {
          return testName;
        }
      }
    }
  },

  mounted() {
    graph = graphData as dcbn.Graph;
    const self = this;
    const { nodeData, edgeData, dcbnNodes, net } = createEditGraph(
      document.getElementById("mynetwork")!,
      this.graph,
      (nodeId, position) => {
        this.x = position.x + 10;
        this.y = position.y - 50;
        this.activeId = nodeId;
        this.showEditOptions = true;
      },
      {
        addNode(data: any, callback: Function) {
          data.label = self.generateNewNodeName();
          self.dcbnNodes[data.id] = {
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
          self.graph.nodes.push(self.dcbnNodes[data.id]);
          callback(data);
        },
        addEdge(data: any, callback: Function) {
          const fromId = data.from as number;
          const toId = data.to as number;

          const fromName = self.dcbnNodes[fromId].name;
          const toNode = self.dcbnNodes[toId];

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
            const toNode = self.dcbnNodes[edge.to as string];
            const fromName = self.dcbnNodes[edge.from as string].name;

            self.removeDependencies(toNode, fromName);
          }
          self.graph.nodes.splice(
            self.graph.nodes.findIndex(
              node => node.name === self.dcbnNodes[data.nodes[0] as string].name
            ),
            1
          );
          delete self.dcbnNodes[data.label];
          callback(data);
        },
        deleteEdge(data: any, callback: Function) {
          callback(data);
        }
      }
    );

    this.dcbnNodes = dcbnNodes;
    this.nodes = nodeData;
    this.edges = edgeData;
    network = net;

    net.on("selectNode", () => {
      this.showEditOptions = true;
    });

    net.on("deselectNode", () => {
      this.showEditOptions = false;
    });

    net.on("dragStart", () => {
      this.showEditOptions = false;
    });

    net.on("dragEnd", event => {
      if (!event.nodes.length) {
        return;
      }
      const nodeId = event.nodes[0];
      const node = this.dcbnNodes[nodeId];

      const newPosition = network.getPositions(nodeId)[0];
      node.position = newPosition;
    });
  }
});
</script>
