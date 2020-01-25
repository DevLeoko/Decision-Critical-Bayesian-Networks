<template>
  <v-layout row wrap fill-height align-content-center>
    <v-flex xs5>
      <v-layout row wrap fill-height align-content-center>
        <v-flex xs12>
          <img src="@/assets/LogoDark.svg" />
        </v-flex>
      </v-layout>
    </v-flex>

    <v-flex xs4 offset-xs1 pb-3>
      <slot></slot>
    </v-flex>
  </v-layout>
</template>

<style lang="scss" scoped>
img {
  height: 20vh;
  float: right;
}
</style>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({});
</script>
