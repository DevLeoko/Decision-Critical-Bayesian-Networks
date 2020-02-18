<template>
  <div>
    <v-container fill-height class="mx-3">
      <v-row
        v-for="parents in dependency.parents.length +
          dependency.parentsTm1.length"
        :key="parents"
      >
        <v-col
          cols="3"
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
        >
          <div v-if="parents - 1 < dependency.parents.length">
            {{ dependency.parents[parents - 1] }}
          </div>
          <div v-else>
            {{ dependency.parentsTm1[parents - 1 - dependency.parents.length] }}
            (T-1)
          </div>
        </v-col>
        <v-col
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
          v-for="parentStates in Math.pow(2, parents)"
          :key="parentStates"
        >
          <v-col>
            {{ stateType.states[(parentStates + 1) % 2] }}
          </v-col>
        </v-col>
      </v-row>
      <v-row
        v-for="nodeStates in stateType.states.length"
        :key="nodeStates"
        style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
      >
        <v-col cols="3"> {{ stateType.states[nodeStates - 1] }}</v-col>
        <v-col
          v-for="inputFields in Math.pow(
            2,
            dependency.parents.length + dependency.parentsTm1.length
          )"
          :key="inputFields"
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
        >
          <v-text-field dense outlined solo></v-text-field
        ></v-col>
      </v-row>
    </v-container>
  </div>
</template>

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
