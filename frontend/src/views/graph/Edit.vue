<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar />
    <div id="mynetwork" ref="network"></div>
    <v-menu
      v-model="showEditOptions"
      :position-x="x"
      :position-y="y"
      :close-on-click="false"
      absolute
    >
      <div class="white">
        <v-btn
          tile
          @click="
          editProperties = true;
          ">
          Properties
        </v-btn>
        <v-btn
          tile
          @click="
          deleteNode;
          ">
          Delete
        </v-btn>
        <v-btn icon color="red">
          <v-icon>close</v-icon>
        </v-btn>
      </div>
    </v-menu>

    <v-dialog v-model="editProperties" width="500" v-if="activeId !== -1">
      
    </v-dialog>

  </div>
</template>

<style lang="css" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import Vue from "vue";
import vis, {network, data} from "vis-network";

//Import test Graph
import graph from "@/../tests/resources/graph1.json";

//Get the Graph structure that will be use in the playground
import { createVisGraph, dcbn } from "../../utils/graph";

export default Vue.extend({
  components: {
    EditBar
  },

  data(){
    return{
      graph,
      nodes: null as vis.data.DataSet<vis.Node, "id"> | null,
      showEditOptions: false,
      x: 0,
      y:0,
      editProperties: false,
    };
  },

  methods:{
    addNode(id: number){
      
    },

    deleteNode(id: number){

    },

    editNode(id: number){

    },

    addEdge(from: Node,to: Node){

    },
    

  },

  mounted() {
    // create an array with nodes
    var nodes = new vis.DataSet([
      { id: 1, label: "Node 1" },
      { id: 2, label: "Node 2" },
      { id: 3, label: "Node 3" },
      { id: 4, label: "Node 4" },
      { id: 5, label: "Node 5" },
      { id: 6, label: "Node 6" }
    ]);

    // create an array with edges
    var edges = new vis.DataSet<any>([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 6 }
    ]);

    // create a network
    var container = document.getElementById("mynetwork");

    var data = {
      nodes: nodes,
      edges: edges
    };
    var options = {
      physics: {
        enabled: false
      },
      nodes: {
        shape: "square",
        title: undefined,
        value: undefined
      },
      edges: {
        arrows: {
          to: {
            enabled: true,
            imageHeight: undefined,
            imageWidth: undefined,
            scaleFactor: 1,
            src: undefined,
            type: "arrow"
          },
          smooth: {
            enabled: true
          }
        }
      },
      manipulation: {
        enabled: true,
        initiallyActive: true,
        addEdge: true,
        editNode: undefined,
        deleteNode: true,
        deleteEdge: true
      },
      layout: {}
    };

    var network = new vis.Network(container!, data, options);

    network.on("selectNode", () =>{
    })
  }
});
</script>
