<template>
  <div style="max-height: 100%; width: 100% ">
    <edit-bar @nodeAdd="addNode()"/>
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
         @click="del()">
          Delete
        </v-btn>
        <v-btn icon color="red">
          <v-icon>close</v-icon>
        </v-btn>
      </div>
    </v-menu>

    <v-dialog v-model="editProperties" fullscreen hide-overlay transition="dialog-bottom-transition">
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
}
</style>

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import Vue from "vue";
import vis, {data} from "vis-network";
//Import test Graph
import graph from "@/../tests/resources/graph1.json";

//Get the frontend Graph structure and the constructor
import { dcbn, createEditGraph } from "../../utils/graph";

export default Vue.extend({
  components: {
    EditBar
  },

  data(){
    return{
      graph,
      nodes: null as vis.DataSet<vis.Node, "id"> | null,
      edges: vis.DataSet,
      showEditOptions: false,
      x: 0,
      y:0,
      activeId: -1,
      editProperties: false,
      network : {} as vis.Network
    };
  },

  methods:{
    addNode: function(){
      this.network.addNodeMode();
    },

    del: function(){
      this.network.deleteSelected();
    }
  },

  mounted() {
    const{nodeData, nodeIndecies, net} = createEditGraph(
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
    this.network = net;

    net.on("selectNode", () =>{
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
