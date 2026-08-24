<script lang="ts">
  import { page } from "$app/state";
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import type { Snippet } from "svelte";
  import "../app.css";
  import Sidebar from "$lib/components/layout/Sidebar.svelte";
  import TopBar from "$lib/components/layout/TopBar.svelte";
  import Toast from "$lib/components/ui/Toast.svelte";
  import { setOnUnauthorized } from "$lib/api/client";
  import { authService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";
  import { toast } from "$lib/stores/toast.svelte";
  import { m } from "$lib/paraglide/messages";

  let { children }: { children: Snippet } = $props();

  const isLoginPage = $derived(page.route.id === "/login");

  setOnUnauthorized(() => {
    session.clear();
    toast.show(m.session_expired());
    goto(resolve("/login"));
  });

  $effect(() => {
    if (session.status !== "unknown") return;

    authService
      .me()
      .then((user) => {
        if (user) {
          session.setUser(user);
        } else {
          session.clear();
          if (!isLoginPage) {
            goto(resolve("/login"));
          }
        }
      })
      .catch(() => {
        session.clear();
        if (!isLoginPage) {
          goto(resolve("/login"));
        }
      });
  });

  $effect(() => {
    if (session.status === "authenticated" && isLoginPage) {
      goto(resolve("/"));
    }
  });
</script>

{#if session.status === "unknown"}
  <!-- Waiting for session recovery — render nothing to avoid shell flash -->
{:else if isLoginPage}
  {@render children()}
{:else}
  <div class="flex h-screen bg-neutral-50">
    <Sidebar />
    <div class="flex-1 flex flex-col overflow-hidden">
      <TopBar />
      <main class="flex-1 overflow-auto">
        {@render children()}
      </main>
    </div>
  </div>
{/if}

<Toast />
