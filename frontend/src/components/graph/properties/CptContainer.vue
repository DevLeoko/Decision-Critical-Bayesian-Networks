<template>
  <div>
    <v-row>
      <v-card-title class="headline">
        <div v-if="time0">
          {{ $t("cptContainer.conditionalProbabilityTime0") }}
        </div>
        <div v-else>{{ $t("cptContainer.conditionalProbabilityTimeT") }}</div>
      </v-card-title>
      <v-btn v-if="time0" class="ma-3" @click="time0 = false">{{
        $t("cptContainer.timeT")
      }}</v-btn>
      <v-btn v-else class="ma-3" @click="time0 = true">{{
        $t("cptContainer.time0")
      }}</v-btn>
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
import { dcbn } from "@/utils/graph/graph";

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
