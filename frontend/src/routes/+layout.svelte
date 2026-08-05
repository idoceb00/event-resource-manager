<script lang="ts">
  import type { Snippet } from "svelte";
  import "../app.css";
  import Sidebar from "$lib/components/layout/Sidebar.svelte";
  import TopBar from "$lib/components/layout/TopBar.svelte";

  let { children }: { children: Snippet } = $props();
</script>

<div class="flex h-screen bg-neutral-50">
  <Sidebar />

  <div class="flex-1 flex flex-col overflow-hidden">
    <TopBar />
    <main class="flex-1 overflow-auto">
      {@render children()}
    </main>
  </div>
</div>
