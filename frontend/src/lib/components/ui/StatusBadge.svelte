<script lang="ts">
	import type { Snippet } from 'svelte';
	import type { BadgeVariant } from '$lib/utils/status';

	interface Props {
		label: string;
		variant: BadgeVariant;
		icon?: Snippet;
	}

	let { label, variant, icon }: Props = $props();

	const variantClass: Record<BadgeVariant, string> = {
		green: 'bg-green-100 text-green-800',
		blue: 'bg-blue-100 text-blue-800',
		gray: 'bg-gray-100 text-gray-800',
		amber: 'bg-amber-100 text-amber-900',
		red: 'bg-red-50 text-red-600'
	};
</script>

<span class="inline-flex items-center gap-1 px-2 py-1 rounded text-xs font-medium {variantClass[variant]}">
	{#if icon}{@render icon()}{/if}
	{label}
</span>