<template>
  <v-toolbar bottom>
    <v-btn
      small
      color="primary"
      :loading="loading"
      :disabled="upToDate"
      class="ml-3"
      @click="$emit('save')"
    >
      <v-icon>save</v-icon>
    </v-btn>
    <v-btn small color="primary" class="ml-3" @click="$emit('nodeAdd')">
      <v-icon>add </v-icon>
    </v-btn>
    <v-btn small color="primary" class="ml-3" @click="$emit('edgeAdd')">
      <v-icon>arrow_right_alt</v-icon>
    </v-btn>
    <v-btn small color="primary" class="ml-3" @click="$emit('edgeTAdd')">
      <v-icon>arrow_right_alt</v-icon>Time
    </v-btn>
    <v-btn
      small
      color="primary"
      class="ml-3"
      @click="$emit('undo')"
      :disabled="undoDisabled"
    >
      <v-icon>undo</v-icon>
    </v-btn>
    <v-btn
      small
      color="primary"
      class="ml-3"
      @click="$emit('redo')"
      :disabled="redoDisabled"
    >
      <v-icon>redo</v-icon>
    </v-btn>
    <v-spacer></v-spacer>
    <v-text-field
      class="ml-3"
      outlined
      hide-details
      dense
      label="Timesteps"
      :value="timeSteps"
      @change="value => $emit('update:timeSteps', value)"
      style="max-width: 120px"
    ></v-text-field>
    <v-btn small color="primary" @click="$emit('formatNetwork')" class="ml-3">
      Format
    </v-btn>

    <v-spacer></v-spacer>
    <v-btn
      small
      color="primary lighten-2"
      @click="$router.push({ name: 'Test Graph' })"
      >Switch to Testenvironment</v-btn
    >
  </v-toolbar>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: {
    timeSteps: Number,
    loading: Boolean,
    upToDate: Boolean,
    undoDisabled: Boolean,
    redoDisabled: Boolean
  }
});
</script>
