<template>
  <div>
    <!-- time 0 -->
    <v-container v-if="time0" fill-height class="mx-3">
      <v-row v-for="i in node.timeZeroDependency.parents.length" :key="i">
        <v-col
          cols="3"
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
        >
          {{ node.timeZeroDependency.parents[i - 1] }}
        </v-col>
        <v-col
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
          v-for="j in Math.pow(2, i)"
          :key="j"
        >
          <v-col>
            {{ node.stateType.states[(j + 1) % 2] }}
          </v-col>
        </v-col>
      </v-row>
      <v-row
        v-for="i in node.stateType.states.length"
        :key="i"
        style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
      >
        <v-col cols="3"> {{ node.stateType.states[i - 1] }}</v-col>
        <v-col
          v-for="i in Math.pow(2, node.timeZeroDependency.parents.length)"
          :key="i"
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
        >
          <v-text-field dense outlined solo></v-text-field
        ></v-col>
      </v-row>
    </v-container>
    <!-- time t -->
    <v-container v-else fill-height class="mx-3">
      <v-row
        v-for="k in node.timeTDependency.parents.length +
          node.timeTDependency.parentsTm1.length"
        :key="k"
      >
        <v-col
          cols="3"
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
        >
          <div v-if="k - 1 < node.timeTDependency.parents.length">
            {{ node.timeTDependency.parents[k - 1] }}
          </div>
          <div v-else>
            {{
              node.timeTDependency.parentsTm1[
                k - 1 - node.timeTDependency.parents.length
              ]
            }}
            (T-1)
          </div>
        </v-col>
        <v-col
          style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
          v-for="j in Math.pow(2, k)"
          :key="j"
        >
          <v-col>
            {{ node.stateType.states[(j + 1) % 2] }}
          </v-col>
        </v-col>
      </v-row>
      <v-row
        v-for="i in node.stateType.states.length"
        :key="i"
        style="outline-width: 2px;
  outline-style: solid;
  outline-color: black;"
      >
        <v-col cols="3"> {{ node.stateType.states[i - 1] }}</v-col>
        <v-col
          v-for="i in Math.pow(
            2,
            node.timeZeroDependency.parents.length +
              node.timeTDependency.parentsTm1.length
          )"
          :key="i"
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
import { dcbn } from "../../utils/graph";

export default Vue.extend({
  props: {
    node: Object as () => dcbn.Node,
    time0: Boolean
  },
  data() {
    return {};
  }
});
</script>
