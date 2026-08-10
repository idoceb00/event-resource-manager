<script lang="ts">
  import { page } from "$app/state";
  import { resolve } from "$app/paths";
  import {
    deLocalizeHref,
    getLocale,
    localizeHref,
    locales,
  } from "$lib/paraglide/runtime";

  const current = getLocale();
  const path = deLocalizeHref(page.url.pathname);
</script>

<nav class="flex items-center gap-1 text-sm" aria-label="Language">
  {#each locales as locale (locale)}
    <a
      href={resolve(localizeHref(path, { locale }) as "/")}
      data-sveltekit-reload
      aria-current={locale === current ? "true" : undefined}
      class="px-2 py-1 rounded uppercase transition-colors {locale === current
        ? 'bg-neutral-900 text-white'
        : 'text-neutral-600 hover:bg-neutral-100'}"
    >
      {locale}
    </a>
  {/each}
</nav>
