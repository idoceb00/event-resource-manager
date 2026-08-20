<script lang="ts">
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import Button from "$lib/components/ui/Button.svelte";
  import { m } from "$lib/paraglide/messages";
  import { authService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";

  let username = $state("");
  let password = $state("");
  let error = $state(false);
  let loading = $state(false);

  async function handleSubmit(e: Event) {
    e.preventDefault();
    error = false;
    loading = true;
    try {
      const user = await authService.login(username, password);
      session.setUser(user);
      goto(resolve("/"));
    } catch {
      error = true;
    } finally {
      loading = false;
    }
  }
</script>

<div class="min-h-screen bg-neutral-50 flex items-center justify-center p-4">
  <div class="w-full max-w-sm">
    <h1 class="text-2xl font-semibold text-neutral-900 text-center mb-8">
      {m.login_title()}
    </h1>

    <div class="bg-white border border-neutral-200 rounded p-6">
      <form onsubmit={handleSubmit} class="space-y-4">
        {#if error}
          <div
            class="text-sm text-red-600 bg-red-50 border border-red-200 rounded px-3 py-2"
          >
            {m.login_error()}
          </div>
        {/if}

        <div>
          <label for="username" class="block text-sm text-neutral-700 mb-1">
            {m.login_username()}
          </label>
          <input
            id="username"
            type="text"
            bind:value={username}
            required
            autocomplete="username"
            class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
          />
        </div>

        <div>
          <label for="password" class="block text-sm text-neutral-700 mb-1">
            {m.login_password()}
          </label>
          <input
            id="password"
            type="password"
            bind:value={password}
            required
            autocomplete="current-password"
            class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
          />
        </div>

        <Button type="submit" disabled={loading} class="w-full">
          {m.login_submit()}
        </Button>
      </form>
    </div>
  </div>
</div>
