<template>
  <v-container fill-height class="">
    <v-row
      v-for="parents in dependency.parents.length +
        dependency.parentsTm1.length"
      :key="`par${parents}`"
      no-gutters
    >
      <v-col class="leftName">
        <div v-if="parents - 1 < dependency.parents.length">
          {{ dependency.parents[parents - 1] }}
        </div>
        <div v-else>
          {{ dependency.parentsTm1[parents - 1 - dependency.parents.length] }}
          (T-1)
        </div>
      </v-col>
      <v-col v-for="parentStates in Math.pow(2, parents)" :key="parentStates">
        {{ stateType.states[(parentStates + 1) % 2] }}
      </v-col>
    </v-row>
    <v-row
      v-for="nodeState in stateType.states.length"
      :key="`nS${nodeState}`"
      no-gutters
    >
      <v-col class="leftName">
        <div>{{ stateType.states[nodeState - 1] }}</div>
      </v-col>
      <v-col
        v-for="inputFields in Math.pow(
          2,
          dependency.parents.length + dependency.parentsTm1.length
        )"
        :key="inputFields"
        class="inputEntry"
      >
        <input
          v-model.number="
            dependency.probabilities[inputFields - 1][nodeState - 1]
          "
          min="0"
          max="1"
          step="any"
          type="number"
          :class="
            dependency.probabilities[inputFields - 1].reduce(
              (a, b) => a + b,
              0
            ) == 1 || 'failed'
          "
          :ref="`propInput${inputFields}-${nodeState}`"
          @keydown.enter="
            fillProps(dependency.probabilities[inputFields - 1], nodeState - 1);
            $event.target.blur();
            reTarget(`propInput${inputFields + 1}-${nodeState}`);
          "
        />
      </v-col>
    </v-row>
    <span class="grey--text font-weight-light">
      ({{ $t("graph.edit.nodeProperties.definitionTab.autocomplete") }})
    </span>
  </v-container>
</template>

<style lang="scss" scoped>
.col {
  border: solid white 2px;
  background-color: #e9edf1;
  padding-top: 10px;
  padding-bottom: 10px;
  text-align: center;
  margin-top: -2px;
  margin-left: -2px;
}

.inputEntry {
  padding: 0px;
  background-color: white;

  input {
    height: 100%;
    width: 100%;
    padding: 5px;

    border: solid #e9edf1 3px;
    outline: none !important;
  }

  input.failed {
    border-color: #ff7b7b;
  }

  /* Chrome, Safari, Edge, Opera */
  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  /* Firefox */
  input[type="number"] {
    -moz-appearance: textfield;
  }
}

.leftName {
  flex-grow: 0;
  font-weight: bold;

  div {
    margin-left: 10px;
    text-align: left;
    width: 150px;
  }
}
</style>

<script lang="ts">
import Vue from "vue";
import { dcbn } from "@/utils/graph/graph";

export default Vue.extend({
  props: {
    dependency: Object as () => dcbn.TimeZeroDependency | dcbn.TimeTDependency,
    stateType: Object as () => dcbn.StateType
  },

  methods: {
    fillProps(prop: number[], index: number) {
      const fill =
        Math.round(((1 - prop[index]) / (prop.length - 1)) * 100) / 100;
      for (let i = 0; i < prop.length; i++) {
        if (i != index) prop[i] = fill;
      }

      prop[index] = Math.round(prop[index] * 100) / 100;
    },

    reTarget(id: string) {
      if (this.$refs[id] && (this.$refs[id] as any)[0]) {
        (this.$refs[id] as any)[0].focus();
        (this.$refs[id] as any)[0].select();
      }
    }
  }
});
</script>
