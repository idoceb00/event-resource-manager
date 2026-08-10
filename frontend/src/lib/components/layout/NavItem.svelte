<script lang="ts">
  import type { Component } from "svelte";
  import { resolve } from "$app/paths";
  import { localizeHref } from "$lib/paraglide/runtime";

  interface Props {
    href: string;
    label: string;
    icon: Component<{ class?: string }>;
    active: boolean;
  }

  let { href, label, icon, active }: Props = $props();

  const Icon = $derived(icon);
</script>

<a
  href={resolve(localizeHref(href) as "/")}
  aria-current={active ? "page" : undefined}
  class="flex items-center gap-2.5 px-3 py-2 rounded text-sm transition-colors {active
    ? 'bg-neutral-900 text-white'
    : 'text-neutral-700 hover:bg-neutral-100'}"
>
  <Icon class="w-4 h-4" />
  {label}
</a>
