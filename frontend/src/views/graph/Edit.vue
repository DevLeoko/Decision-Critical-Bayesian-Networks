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
          this.deleteNode;
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
import vis, {network, data} from "vis-network";

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
      showEditOptions: false,
      x: 0,
      y:0,
      activeId: -1,
      editProperties: false,
    };
  },

  methods:{

    addNode(){

    },
    //delete node with id
    deleteNode(id: number){
      

    },
    //edit Node Name and Value
    editNode(id: number){

    },
    //add a new Edge from Node to Node
    addEdge(from: Node,to: Node){

    },
    

  },

  mounted() {
    const{nodeData, nodeIndecies, network} = createEditGraph(
      document.getElementById("mynetwork")!,
      this.graph,
      (nodeId, position) => {
        this.x = position.x + 10;
        this.y = position.y - 50;
        this.activeId = nodeId;
        this.showEditOptions = true;
      }
    );
    
    network.on("selectNode", () =>{
      this.showEditOptions = true;
    });

    network.on("deselectNode", () => {
      this.showEditOptions = false;
    });

    network.on("dragStart", () => {
      this.showEditOptions = false;
    });

    network.on("dragging", () => {
      this.editProperties = false;
    });

    this.nodes = nodeData;
  }
});
</script>
