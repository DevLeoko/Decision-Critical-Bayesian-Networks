<template>
  <div>
    <!-- useless div as both componentes are fixed -->
    <v-slide-y-transition>
      <v-btn
        v-if="!menu"
        fixed
        bottom
        icon
        large
        elevation="4"
        class="ml-3"
        @click="menu = true"
        ><v-icon>chevron_right</v-icon>
      </v-btn>
    </v-slide-y-transition>

    <v-navigation-drawer app fixed clipped width="20vw" v-model="menu">
      <template v-slot:prepend>
        <v-row align="center" no-gutters>
          <v-col class="flex-grow-0 ml-3">
            <v-btn icon elevation="4" large @click="menu = false">
              <v-icon>chevron_left</v-icon>
            </v-btn>
          </v-col>
          <v-col>
            <v-text-field
              v-model="search"
              label="Search for graph"
              solo
              class="ma-3"
              hide-details
              clearable
              clear-icon="clear"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-divider></v-divider>
      </template>
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
          <v-menu offset-y v-if="!item.children.length">
            <template v-slot:activator="{ on }">
              <v-btn v-on.stop="on" @click.stop icon>
                <v-icon>
                  more_vert
                </v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item @click="duplicateGraph(item.graph)">
                <v-list-item-title>
                  <v-icon class="mr-2">file_copy</v-icon> Duplicate
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.renameGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">text_fields</v-icon> Rename
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.moveGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">double_arrow</v-icon> Move
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.deleteGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">delete</v-icon> Delete
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </template>
      </v-treeview>
      <template v-slot:append>
        <v-divider></v-divider>
        <v-row justify="center">
          <v-col class="flex-grow-0 my-4">
            <v-btn
              color="primary"
              @click="graphs.push({ name: 'TestGraph' + i++, id: 10 + i })"
            >
              <v-icon>add</v-icon> Add new graph
            </v-btn>
          </v-col>
        </v-row>
      </template>
    </v-navigation-drawer>
    <folder-actions
      ref="actions"
      :folders="
        graphs
          .map(graph =>
            graph.name.substring(0, graph.name.lastIndexOf('/') + 1)
          )
          .filter((graph, index, self) => self.indexOf(graph) == index)
          .sort()
      "
      @rename="renameGraph"
      @delete="deleteGraph"
    ></folder-actions>
  </div>
</template>

<script lang="ts">
interface DenseGraph {
  name: string;
  id: number;
}

export interface TreeItem {
  name: string;
  id: string | number;
  graph?: DenseGraph;
  children: TreeItem[];
}

import Vue from "vue";
import FolderActions from "@/components/graph/FolderViewActions.vue";
export default Vue.extend({
  props: {
    graphs: Array as () => Array<DenseGraph>
  },

  components: { FolderActions },
  data() {
    return {
      search: null,
      menu: true,
      i: 100
    };
  },

  methods: {
    duplicateGraph(graph: DenseGraph) {
      let copy = JSON.parse(JSON.stringify(graph));

      copy.name += "_COPY";
      copy.id *= 21 + this.graphs.length;

      this.graphs.push(copy);
      // TDOD send /create request
    },

    renameGraph({ graph, name }: { graph: DenseGraph; name: string }) {
      graph.name = name;
      // TDOD send /rename request
    },

    deleteGraph({ graph }: { graph: DenseGraph }) {
      this.graphs.splice(this.graphs.indexOf(graph), 1);

      // TDOD send /delete request
    },

    // graphId is actually an int ._.
    selectGraph(id: string) {
      let targetRoute = this.$route.name;

      if (id) {
        if (targetRoute == "GraphBase") targetRoute = "Test Graph";
      } else {
        targetRoute = "GraphBase";
      }

      this.$router.push({
        name: targetRoute,
        params: { id }
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

      this.graphs
        .slice(0)
        .sort((a, b) => {
          const nameA = a.name.split("/");
          const nameB = b.name.split("/");

          const diff = nameA.length - nameB.length;

          if (diff != 0) return diff;

          return nameA[nameA.length - 1].localeCompare(nameB[nameA.length - 1]);
        })
        .forEach(entry => {
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
            graph: entry
          });
        });

      return result.children;
    }
  }
});
</script>
