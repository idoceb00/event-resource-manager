<script lang="ts">
  import { currentUser } from "$lib/stores/currentUser.svelte";
  import { m } from "$lib/paraglide/messages";
  import LanguageSwitcher from "./LanguageSwitcher.svelte";
</script>

<header
  class="h-14 bg-white border-b border-neutral-200 flex items-center px-6 gap-4"
>
  <div class="flex-1"></div>
  <LanguageSwitcher />
  <div class="text-sm text-neutral-600">
    {m.current_user({ name: currentUser.user.name })}
  </div>
</header>
