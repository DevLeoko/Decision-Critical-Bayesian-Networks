<template>
  <v-container>
    <v-row class="mb-3">
      <v-col>
        <h2 class="headline">
          {{ $t("cptContainer.conditionalProbabilityTable") }}
        </h2>
      </v-col>
      <v-col class="flex-grow-0">
        <v-btn
          @click="time0 = !time0"
          v-if="node.timeTDependency.parentsTm1.length !== 0"
        >
          {{ $t("cptContainer.currentTime") }} {{ time0 ? "0" : "T" }}
        </v-btn>
      </v-col>
    </v-row>
    <div style="overflow-x: auto">
      <prob-table
        :dependency="time0 ? node.timeZeroDependency : node.timeTDependency"
        :stateType="node.stateType"
        :style="
          `max-width: unset; width: ${Math.pow(2, tableNum) * 80 + 150}px`
        "
      />
    </div>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import ProbTable from "@/components/graph/properties/ProbTable.vue";
import { dcbn } from "@/utils/graph/graph";

export default Vue.extend({
  components: {
    ProbTable
  },
  props: { node: Object as () => dcbn.Node },

  data() {
    return {
      time0: true,
      dependency: this.node.timeZeroDependency as
        | dcbn.TimeZeroDependency
        | dcbn.TimeTDependency
    };
  },

  computed: {
    tableNum(): number {
      return this.time0
        ? this.node.timeZeroDependency.parents.length
        : this.node.timeTDependency.parents.length +
            this.node.timeTDependency.parentsTm1.length;
    }
  }
});
</script>
