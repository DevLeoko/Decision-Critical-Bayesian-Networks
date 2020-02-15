<template>
  <v-menu
    v-model="showNodeAction"
    :position-x="x"
    :position-y="y"
    :close-on-click="false"
    absolute
    top
  >
    <div class="white">
      <slot></slot>
      <v-btn icon color="red"><v-icon>close</v-icon></v-btn>
    </div>
  </v-menu>
</template>

<script lang="ts">
import Vue from "vue";
import vis from "vis-network";
export default Vue.extend({
  data() {
    return {
      showNodeAction: false,
      x: 0,
      y: 0
    };
  },

  methods: {
    register(network: vis.Network) {
      const container = document.getElementById("mynetwork")!;
      network.on("click", param => {
        const nodeId = param.nodes[0];

        if (nodeId !== undefined) {
          const boundingBox = network.getBoundingBox(nodeId);
          const position = network.canvasToDOM({
            x: boundingBox.left,
            y: boundingBox.top
          });
          const containerPos = container.getBoundingClientRect() as DOMRect;
          this.x = containerPos.x + position.x;
          this.y = containerPos.y + position.y;
          this.showNodeAction = true;
        } else {
          this.showNodeAction = false;
        }
      });

      network.on("dragStart", () => {
        this.showNodeAction = false;
      });
      network.on("zoom", () => {
        this.showNodeAction = false;
      });
    }
  }
});
</script>
