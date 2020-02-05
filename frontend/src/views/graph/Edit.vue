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

    <v-layout row justify-center>
      <v-dialog v-model="editProperties" persistent max-width="500">
        <v-card>
          <v-card-title class="headline grey lighten-2" primary-title>
            Properties
          </v-card-title>
          <v-col cols="12" sm="6" md="8">
            <v-text-field label="Node Name" placeholder="new"></v-text-field>
          </v-col>
          <v-card-title primary-title>
            Values
          </v-card-title>
          <v-col cols="14" sm="6" md="8">
            <v-text-field label="True" placeholder="0.5"></v-text-field>
          </v-col>

          <v-col cols="18" sm="6" md="8">
            <v-text-field label="False" placeholder="0.5"></v-text-field>
          </v-col>
          <v-card-title primary-title>
            Format Node
          </v-card-title>
          <v-color-picker class="ma-2" hide-inputs></v-color-picker>

          <v-spacer></v-spacer>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" flat @click="editProperties = false"
              >Save</v-btn
            >
            <v-btn color="red" flat @click="editProperties = false"
              >Cancel</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-layout>
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
import graph from "@/../tests/resources/graph1.json";

//Get the frontend Graph structure and the constructor
import { dcbn, createEditGraph } from "../../utils/graph";

let network = {} as vis.Network;

export default Vue.extend({
  components: {
    EditBar
  },

  data() {
    return {
      graph,
      nodes: null as vis.DataSet<vis.Node, "id"> | null,
      edges: vis.DataSet,
      showEditOptions: false,
      x: 0,
      y: 0,
      activeId: -1,
      editProperties: false
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
      var options = {
        edges: {
          label: " "
        }
      };
      network.setOptions(options);
      network.addEdgeMode();
    },
    addTEdge: function() {
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
    }
  },

  mounted() {
    const { nodeData, nodeIndecies, net } = createEditGraph(
      document.getElementById("mynetwork")!,
      this.graph,
      (nodeId, position) => {
        this.x = position.x + 10;
        this.y = position.y - 50;
        this.activeId = nodeId;
        this.showEditOptions = true;
      }
    );
    this.nodes = nodeData;
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
  }
});
</script>
