<script lang="ts">
  import { toast } from "$lib/stores/toast.svelte";
</script>

{#if toast.message}
  <div class="fixed bottom-4 right-4 z-50">
    <div
      class="bg-neutral-900 text-white text-sm px-4 py-3 rounded shadow-lg flex items-center gap-3"
    >
      <span>{toast.message}</span>
      <button
        onclick={() => toast.dismiss()}
        class="text-neutral-400 hover:text-white transition-colors"
        aria-label="Dismiss"
      >
        &times;
      </button>
    </div>
  </div>
{/if}
