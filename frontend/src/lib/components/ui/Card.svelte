<script lang="ts">
  import type { Snippet } from "svelte";

  interface Props {
    title: string;
    children: Snippet;
  }

  let { title, children }: Props = $props();
</script>

<section class="bg-white border border-neutral-200 rounded">
  <div class="border-b border-neutral-200 px-4 py-3">
    <h2 class="text-sm font-medium text-neutral-900">{title}</h2>
  </div>
  {@render children()}
</section>
