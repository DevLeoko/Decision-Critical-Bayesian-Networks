<template>
  <div>
    <v-row>
      <v-card-title class="headline" @load="test()">
        <div v-if="time0">Conditional Probability Table Time 0</div>
        <div v-else>Conditional Probability Table Time T</div>
      </v-card-title>
      <v-btn v-if="time0" class="ma-3" @click="changeTimeInTable()"
        >Time T</v-btn
      >
      <v-btn v-else class="ma-3" @click="changeTimeInTable()">Time 0</v-btn>
    </v-row>
    <div style="max-width: 100%; overflow-x: auto">
      <prob-table
        :dependency="dependency"
        :stateType="node.stateType"
        :style="`width: ${Math.pow(2, tableNum) * 150 + 150}px`"
      />
    </div>
  </div>
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

  methods: {
    changeTimeInTable() {
      if (this.time0) {
        this.dependency = this.node.timeTDependency;
      } else {
        this.dependency = this.node.timeZeroDependency;
      }
      this.time0 = !this.time0;
    }
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
