<template>
  <div>
    <edit-bar />
  </div>
</template>
<script lang="ts">
import EditBar from "@/components/graph/GraphMenuBar.vue";
import Vue from "vue";
export default Vue.extend({
  components: {
    EditBar
  }
});
</script>
