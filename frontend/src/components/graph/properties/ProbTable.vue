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
          v-model="dependency.probabilities[inputFields - 1][nodeState - 1]"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
.col {
  border: solid white 2px;
  // border-radius: 5px;
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
    // border-radius: 5px;
    outline: none !important;
  }
}

.leftName {
  flex-grow: 0;
  // border-top-left-radius: 25px;
  // border-bottom-left-radius: 25px;
  // border-right-width: 5px;
  font-weight: bold;
  // border-left: none;

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
  }
});
</script>
