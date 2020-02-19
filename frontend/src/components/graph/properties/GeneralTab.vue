<template>
  <v-container>
    <v-row>
      <v-col cols="6" offset="3">
        <v-card-title>Name</v-card-title>
        <v-text-field
          outlined
          clearable
          color="primary"
          label="Name"
          v-model="node.name"
        />
        <v-card-title>Color</v-card-title>
        <v-color-picker
          class="ma-2"
          hide-inputs
          :value="node.color"
          @update:color="updateColor"
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
      mode: "hexa"
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
