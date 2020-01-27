<template>
  <div v-if="currentGraph.name">
    <v-dialog v-model="deleteOpen" max-width="400">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">delete_sweep</v-icon> Confirm deletion
        </v-card-title>
        <v-card-text>
          You are about to delete the graph '{{ currentGraph.name }}'. Are you
          sure you want to do this? This action can not be undone, please
          confirm.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="gray" text @click="deleteOpen = false">
            Cancel
          </v-btn>

          <v-btn color="red" outlined @click="$emit('delete', currentGraph)">
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="renameOpen" max-width="400">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">text_fields</v-icon> Change name
        </v-card-title>
        <v-card-text class="pb-3">
          <!-- Enter a new name: -->
          <v-text-field v-model="newName" outlined hide-details></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="gray" text @click="renameOpen = false">
            Cancel
          </v-btn>

          <v-btn
            color="primary"
            outlined
            @click="
              $emit('rename', {
                name:
                  currentGraph.fullPath.substring(
                    0,
                    currentGraph.fullPath.lastIndexOf('/') + 1
                  ) + newName,
                graph: currentGraph
              })
            "
          >
            Rename
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="moveOpen" max-width="400">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">text_fields</v-icon> Move to
        </v-card-title>
        <v-card-text class="pb-3" style="max-height: 700px; overflow: auto">
          <v-list shaped>
            <v-list-item
              v-for="folder in folders"
              class="grey lighten-4 mb-2"
              @click="
                $emit('rename', {
                  name: folder + currentGraph.name,
                  graph: currentGraph
                })
              "
              :key="folder"
            >
              <v-list-item-title class="grey--text text--darken-1">
                {{ folder || "/" }}
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { TreeItem } from "./GraphFolderView.vue";
export default Vue.extend({
  props: ["folders"],
  data() {
    return {
      deleteOpen: false,
      moveOpen: false,
      renameOpen: false,
      duplicateOpen: false,
      newName: "",
      currentGraph: { name: "", id: -1, children: [] } as TreeItem
    };
  },

  methods: {
    closeAll() {
      this.deleteOpen = false;
      this.moveOpen = false;
      this.renameOpen = false;
      this.duplicateOpen = false;
    },

    deleteGraph(graph: TreeItem) {
      this.closeAll();
      this.currentGraph = graph;
      this.deleteOpen = true;
    },

    renameGraph(graph: TreeItem) {
      this.closeAll();
      this.currentGraph = graph;
      this.newName = graph.name;
      this.renameOpen = true;
    },

    moveGraph(graph: TreeItem) {
      this.closeAll();
      this.currentGraph = graph;
      this.moveOpen = true;
    }
  }
});
</script>
