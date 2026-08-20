<script lang="ts">
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import { authService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";
  import { m } from "$lib/paraglide/messages";
  import LanguageSwitcher from "./LanguageSwitcher.svelte";

  async function handleLogout() {
    try {
      await authService.logout();
    } finally {
      session.clear();
      goto(resolve("/login"));
    }
  }
</script>

<header
  class="h-14 bg-white border-b border-neutral-200 flex items-center px-6 gap-4"
>
  <div class="flex-1"></div>
  <LanguageSwitcher />
  {#if session.user}
    <span class="text-sm text-neutral-600">
      {m.current_user({ name: session.user.name })}
    </span>
    <button
      onclick={handleLogout}
      class="text-sm text-neutral-500 hover:text-neutral-900 transition-colors"
    >
      {m.logout()}
    </button>
  {/if}
</header>
