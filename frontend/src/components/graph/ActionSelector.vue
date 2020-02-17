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
  props: {
    isEdgeSelector: {
      type: Boolean,
      default: false
    }
  },
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
        const edgeId = param.edges[0];

        const nodeActive = nodeId !== undefined;
        const edgeActive = edgeId !== undefined && nodeId === undefined;

        if (
          (nodeActive && !this.isEdgeSelector) ||
          (edgeActive && this.isEdgeSelector)
        ) {
          if (!this.isEdgeSelector) {
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
            const position = param.pointer.DOM;
            const containerPos = container.getBoundingClientRect() as DOMRect;
            const offset = { x: 5, y: 5 };

            this.x = position.x + containerPos.x + offset.x;
            this.y = position.y + containerPos.y - offset.y;
          }

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
