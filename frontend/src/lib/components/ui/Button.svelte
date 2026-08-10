<script lang="ts">
  import type { Snippet } from "svelte";

  type ButtonVariant = "primaryDark" | "icon";

  interface Props {
    children: Snippet;
    onclick?: (event: MouseEvent) => void;
    variant?: ButtonVariant;
    class?: string;
    disabled?: boolean;
    "aria-label"?: string;
  }

  let {
    children,
    onclick,
    variant = "primaryDark",
    class: className = "",
    disabled = false,
    "aria-label": ariaLabel,
  }: Props = $props();

  const variantClass: Record<ButtonVariant, string> = {
    primaryDark:
      "px-4 py-2 bg-neutral-900 text-white text-sm rounded hover:bg-neutral-800 transition-colors disabled:opacity-50",
    icon: "p-2 hover:bg-neutral-100 rounded transition-colors",
  };
</script>

<button
  {onclick}
  {disabled}
  type="button"
  aria-label={ariaLabel}
  class="{variantClass[variant]} {className}"
>
  {@render children()}
</button>
