<template>
  <div>
    <v-row>
      <v-card-title class="headline">
        <div v-if="time0">Conditional Probability Table Time 0</div>
        <div v-else>Conditional Probability Table Time T</div>
      </v-card-title>
      <v-btn v-if="time0" class="ma-3" @click="time0 = false">Time T</v-btn>
      <v-btn v-else class="ma-3" @click="time0 = true">Time 0</v-btn>
    </v-row>
    <div style="max-width: 100%; overflow-x: auto">
      <prob-table
        :node="node"
        :time0="time0"
        :style="`width: ${Math.pow(2, tableNum) * 150 + 150}px`"
      />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import ProbTable from "@/components/graph/ProbTable.vue";
import { dcbn } from "../../utils/graph";

export default Vue.extend({
  components: {
    ProbTable
  },
  props: { time0: Boolean, node: Object as () => dcbn.Node },

  computed: {
    tableNum(): number {
      return this.time0
        ? this.node.timeZeroDependency.parents.length
        : this.node.timeZeroDependency.parents.length +
            this.node.timeTDependency.parentsTm1.length;
    }
  }
});
</script>
