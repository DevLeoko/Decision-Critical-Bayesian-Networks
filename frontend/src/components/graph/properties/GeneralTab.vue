<template>
  <v-container>
    <v-row>
      <v-col cols="6" offset="3">
        <v-card-title>{{
          $t("graph.edit.nodeProperties.generalTab.name")
        }}</v-card-title>
        <v-text-field
          outlined
          color="primary"
          label="Name"
          v-model="node.name"
        />
        <v-card-title>{{
          $t("graph.edit.nodeProperties.generalTab.color")
        }}</v-card-title>
        <v-color-picker
          class="ma-2"
          hide-inputs
          :value="node.color"
          @update:color="updateColor"
          show-swatches
          :swatches="swatches"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { dcbn } from "@/utils/graph/graph";

export default Vue.extend({
  data() {
    return {
      swatches: [
        ["#FFEB3B", "#E91E63", "#03A9F4", "#CDDC39"],
        ["#FFC107", "#9C27B0", "#00BCD4", "#9E9E9E"],
        ["#FF9800", "#673AB7", "#009688", "#607D8B"],
        ["#FF5722", "#3F51B5", "#4CAF50"],
        ["#F44336", "#2196F3", "#8BC34A"]
      ]
    };
  },
  props: { node: Object as () => dcbn.Node },
  methods: {
    updateColor(color: any) {
      this.node.color = color.hex;
    }
  }
});
</script>
