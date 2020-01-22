<template>
  <div>
    {{ formula.name }}
  </div>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["formula"]
});
</script>
