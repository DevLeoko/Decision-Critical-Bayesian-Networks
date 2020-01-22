<template>
  <div>
    <div :key="formula.id" v-for="formula in formulas">
      <v-list-item>
        <v-list-item-content @click="$emit('open-formula', formula.id)">
          <v-list-item-title>{{ formula.name }}</v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn icon>
            <v-icon color="black">delete</v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["formulas"]
});
</script>
