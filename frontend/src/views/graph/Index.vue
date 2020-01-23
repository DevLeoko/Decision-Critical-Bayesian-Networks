<template>
  <!-- $route.params.id  -->
  <v-layout row wrap>
    <v-flex xs3>
      <v-navigation-drawer permanent width="100%">
        <v-text-field
          v-model="search"
          label="Search for graph"
          solo
          class="ma-3"
          hide-details
          clearable
          clear-icon="clear"
        ></v-text-field>
        <v-treeview
          :items="treeItems"
          :search="search"
          @update:active="arr => selectGraph(arr[0])"
          activatable
          open-on-click
        >
          <template v-slot:prepend="{ item, open }">
            <v-icon v-if="item.children.length > 0">
              {{ open ? "folder_open" : "folder" }}
            </v-icon>
            <v-icon v-else>device_hub</v-icon>
          </template>
          <template v-slot:append="{ item }">
            <v-icon v-if="!item.children.length">
              more_vert
            </v-icon>
          </template>
        </v-treeview>
      </v-navigation-drawer>
    </v-flex>
    <v-flex xs9>
      <router-view></router-view>
    </v-flex>
  </v-layout>
</template>

<script lang="ts">
interface TreeItem {
  name: string;
  id: string | number;
  active?: boolean;
  children: TreeItem[];
}

import Vue from "vue";
export default Vue.extend({
  data() {
    return {
      search: null,
      graphs: [
        { name: "TestGraph", id: 10 },
        { name: "testFolder/Graph1", id: 11 },
        { name: "testFolder/Graph2", id: 12 },
        { name: "testFolder2/Graph45", id: 13 },
        { name: "testFolder/1/grrr", id: 14 },
        { name: "testFolder/1/uff", id: 15 }
      ]
    };
  },

  methods: {
    // graphId is actually an int ._.
    selectGraph(grpahId: string) {
      let targetRoute = this.$route.name;

      if (grpahId) {
        if (targetRoute == "GraphBase") targetRoute = "Graph";
      } else {
        targetRoute = "GraphBase";
      }

      this.$router.push({
        name: targetRoute,
        params: { id: grpahId }
      });
    }
  },

  computed: {
    treeItems() {
      let result: TreeItem = {
        name: "Root",
        id: -1,
        children: []
      };

      this.graphs.forEach(entry => {
        const splitName = entry.name.split("/");
        let currentFolder = result.children;

        // resolve folders step by step
        for (let i = 0; i < splitName.length - 1; i++) {
          const element = splitName[i];

          // check whether the next subfolder alrady exists
          const targetFolder: TreeItem = currentFolder.find(
            subFolder => subFolder.name == element
          ) || { name: element, id: -1, children: [] };

          // if not, add it to the current folder
          if (targetFolder.id == -1) {
            currentFolder.push(targetFolder);
            targetFolder.id = `${entry.id} - ${element}`;
          }

          currentFolder = targetFolder.children;
        }

        const graphName = splitName[splitName.length - 1];
        // add actual graph to the "lowest" subfolder
        currentFolder.push({
          name: graphName,
          id: entry.id,
          children: [],
          active: entry.id == 13
        });
      });

      return result.children;
    }
  }
});
</script>
