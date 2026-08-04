<script lang="ts">
	import { page } from '$app/state';
	import { deLocalizeHref, getLocale, localizeHref, locales } from '$lib/paraglide/runtime';

	const current = getLocale();
	const path = deLocalizeHref(page.url.pathname);
</script>

<nav class="flex items-center gap-1 text-sm" aria-label="Language">
	{#each locales as locale}
		<a
			href={localizeHref(path, { locale })}
			data-sveltekit-reload
			aria-current={locale === current ? 'true' : undefined}
			class="px-2 py-1 rounded uppercase transition-colors {locale === current ? 'bg-neutral-900 text-white' : 'text-neutral-600 hover:bg-neutral-100'}"
		>
			{locale}
		</a>
	{/each}
</nav>