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
    <v-btn
      small
      :color="addNodeModeActive ? `${activeColor}` : `${inactiveColor}`"
      class="ml-3"
      @click="$emit('nodeAdd')"
    >
      <v-icon>add</v-icon>
    </v-btn>
    <v-btn
      small
      :color="addEdgeModeActive ? `${activeColor}` : `${inactiveColor}`"
      class="ml-3"
      @click="$emit('edgeAdd')"
    >
      <v-icon>arrow_right_alt</v-icon>
    </v-btn>
    <v-btn
      small
      :color="addTimeEdgeModeActive ? `${activeColor}` : `${inactiveColor}`"
      class="ml-3"
      @click="$emit('edgeTAdd')"
    >
      <v-icon>arrow_right_alt</v-icon>{{ $t("graph.edit.toolbar.time") }}
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
      :label="this.$tc('graph.edit.toolbar.timeSteps', 2)"
      :value="timeSteps"
      @change="value => $emit('update:timeSteps', value)"
      style="max-width: 120px"
    ></v-text-field>
    <v-btn small color="primary" @click="$emit('formatNetwork')" class="ml-3">
      {{ $t("graph.edit.toolbar.format") }}
    </v-btn>

    <v-spacer></v-spacer>
    <v-btn
      small
      color="primary lighten-2"
      @click="$router.push({ name: 'Test Graph' })"
      >{{ $t("graph.edit.toolbar.switchToTest") }}</v-btn
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
    redoDisabled: Boolean,

    addNodeModeActive: Boolean,
    addEdgeModeActive: Boolean,
    addTimeEdgeModeActive: Boolean
  },

  data() {
    return {
      activeColor: "primary darken-3",
      inactiveColor: "primary"
    };
  }
});
</script>
