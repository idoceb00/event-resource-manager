<script lang="ts">
  import type { Component } from "svelte";

  interface Props {
    label: string;
    value: number;
    icon: Component<{ class?: string }>;
    variant?: "default" | "warning";
  }

  let { label, value, icon, variant = "default" }: Props = $props();

  const Icon = $derived(icon);
  const emphasized = $derived(variant === "warning" && value > 0);
</script>

<div class="bg-white border border-neutral-200 rounded p-4">
  <div class="flex items-start justify-between">
    <div>
      <div class="text-sm text-neutral-600">{label}</div>
      <div
        class="text-2xl font-semibold mt-1 {emphasized
          ? 'text-amber-600'
          : 'text-neutral-900'}"
      >
        {value}
      </div>
    </div>
    <Icon
      class={`w-5 h-5 ${emphasized ? "text-amber-600" : "text-neutral-400"}`}
    />
  </div>
</div>
