<template>
  <div>
    <!-- <h1>Test graph {{ $route.params.id }}</h1> -->
    <test-toolbar />
  </div>
</template>

<script lang="ts">
import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
export default Vue.extend({
  components: {
    TestToolbar
  }
});
</script>
