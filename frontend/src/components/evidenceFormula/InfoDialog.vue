<template>
  <v-dialog v-model="open" max-width="600">
    <v-card>
      <v-card-title class="headline">
        How to use the Evidence-Formula Editor
      </v-card-title>

      <v-card-text>
        <p>
          The Evidence-Formula Editor currently supports boolean expressions
          including number comparisons, variables and function calls. More
          complex behaviour has to be done in pre-defined functions.
        </p>
        <p>
        Currently available functions are:
        <ul>
          <li>inArea(&lt;area name&gt;)</li>
          <li>distanceToNearest()</li>
        </ul>
        </p>
        <p>
          The available variables can be seen on the right.
          Keep in mind that currently only Boolean and Number type variables can be used directly inside the Evidence-Formula
          all others are instead used when evaluating functions.
        </p>
        <p>
          The formulas can be tested by pressing the "Test" button at the
          bottom. When testing an Evidence-Formula the variables in the gray box
          on the right are used.
        </p>
        <p>
          An expression could for example look like this: "speed > 10 &amp; inArea(WRECKAGE)".
        </p>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="open = false">
          Got it!
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["open"],
  watch: {
    open(value) {
      this.$emit("update:open", value);
    }
  }
});
</script>
