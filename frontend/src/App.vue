<template>
  <v-app>
    <menu-bar />

    <v-content>
      <v-container fluid fill-height align-start pa-0>
        <router-view></router-view>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import MenuBar from "@/components/MenuBar.vue";
import Vue from "vue";

export default Vue.extend({
  name: "App",
  components: { MenuBar },
  data: () => ({})
});
</script>
