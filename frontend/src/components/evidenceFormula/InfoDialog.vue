<template>
  <v-dialog v-model="open" max-width="600">
    <v-card>
      <v-card-title class="headline">
        How to use the Evidence-Formula Editor
      </v-card-title>

      <v-card-text>
        Lorem ipsum dolor, sit amet consectetur adipisicing elit. Rem natus,
        distinctio officiis harum tempora delectus a dolorem, quas asperiores
        non, similique incidunt autem repudiandae qui possimus vero totam
        pariatur cumque?
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
export default Vue.extend({ props: ["open"] });
</script>
