<script lang="ts">
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import StatusBadge from "$lib/components/ui/StatusBadge.svelte";
  import { m } from "$lib/paraglide/messages";
  import { userService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";
  import { toast } from "$lib/stores/toast.svelte";
  import type { User } from "$lib/types/domain";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let users = $state<User[]>(data.users);
  let showDeactivated = $state(false);
  const isAdmin = $derived(session.user?.role === "admin");

  $effect(() => {
    if (session.status === "unknown") return;
    if (session.status === "authenticated" && !isAdmin) {
      goto(resolve("/"));
    }
  });

  const displayRows = $derived(
    showDeactivated
      ? users.filter((u) => !u.active)
      : users.filter((u) => u.active),
  );

  // --- Create form ---
  let showCreateForm = $state(false);
  let createUsername = $state("");
  let createName = $state("");
  let createPassword = $state("");
  let createRole = $state<"admin" | "employee">("employee");
  let createLoading = $state(false);

  async function handleCreate() {
    if (!createUsername.trim() || !createPassword.trim()) return;
    createLoading = true;
    try {
      const created = await userService.createUser({
        username: createUsername.trim(),
        name: createName.trim(),
        password: createPassword,
        role: createRole,
        active: true,
      });
      users = [...users, created];
      showCreateForm = false;
      createUsername = "";
      createName = "";
      createPassword = "";
      createRole = "employee";
      toast.show(m.user_create_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.user_create_duplicate());
      } else {
        toast.show(m.user_create_validation());
      }
    } finally {
      createLoading = false;
    }
  }

  // --- Edit form (with password) ---
  let editUser = $state<User | null>(null);
  let editName = $state("");
  let editRole = $state<"admin" | "employee">("employee");
  let editNewPassword = $state("");
  let editLoading = $state(false);

  function startEdit(user: User) {
    editUser = user;
    editName = user.name;
    editRole = user.role;
    editNewPassword = "";
  }

  async function handleEdit() {
    if (!editUser) return;
    editLoading = true;
    try {
      const updated = await userService.updateUser(editUser.id, {
        name: editName.trim(),
        role: editRole,
      });
      if (editNewPassword.trim()) {
        await userService.changePassword(editUser.id, editNewPassword);
      }
      users = users.map((u) => (u.id === updated.id ? updated : u));
      editUser = null;
      toast.show(m.user_edit_success());
    } catch {
      toast.show(m.user_edit_success());
    } finally {
      editLoading = false;
    }
  }

  // --- Activate / Deactivate ---
  let activateTarget = $state<User | null>(null);
  let activateLoading = $state(false);
  let activateAdminPassword = $state("");
  let activateError = $state("");

  function startActivation(user: User) {
    activateTarget = user;
    activateAdminPassword = "";
    activateError = "";
  }

  async function handleActivation() {
    if (!activateTarget) return;
    activateLoading = true;
    activateError = "";
    try {
      await userService.setActivation(
        activateTarget.id,
        !activateTarget.active,
        activateTarget.role === "admin" ? activateAdminPassword : undefined,
      );
      users = users.map((u) =>
        u.id === activateTarget!.id
          ? { ...u, active: !activateTarget!.active }
          : u,
      );
      const msg = activateTarget.active
        ? m.user_deactivate_success()
        : m.user_activate_success();
      activateTarget = null;
      toast.show(msg);
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      const errBody = (err as { message?: string }).message ?? "";
      if (errBody.includes("last active administrator")) {
        activateError = m.user_deactivate_last_admin();
      } else if (errBody.includes("Incorrect password")) {
        activateError = m.user_deactivate_wrong_password();
      } else if (errBody.includes("Re-authentication required")) {
        activateError = m.user_deactivate_wrong_password();
      } else if (status === 403 || status === 401) {
        activateError = m.user_deactivate_wrong_password();
      } else {
        toast.show(
          activateTarget?.active
            ? m.user_deactivate_error()
            : m.user_activate_error(),
        );
        activateTarget = null;
      }
    } finally {
      activateLoading = false;
    }
  }
</script>

<div class="p-6">
  <div class="mb-6">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-2xl">{m.staff()}</h1>
      {#if isAdmin}
        <div class="flex items-center gap-4">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              bind:checked={showDeactivated}
              class="rounded border-neutral-300"
            />
            <span class="text-sm">{m.user_show_deactivated()}</span>
          </label>
          <Button onclick={() => (showCreateForm = !showCreateForm)}>
            {m.user_create()}
          </Button>
        </div>
      {/if}
    </div>

    {#if showCreateForm}
      <div class="bg-white border border-neutral-200 rounded p-4 mb-4">
        <h2 class="text-sm font-medium text-neutral-900 mb-3">
          {m.user_create()}
        </h2>
        <form
          onsubmit={(e) => {
            e.preventDefault();
            handleCreate();
          }}
          class="flex items-end gap-3"
        >
          <div class="flex-1">
            <label
              for="create-username"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.login_username()}
            </label>
            <input
              id="create-username"
              type="text"
              bind:value={createUsername}
              required
              class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <div class="flex-1">
            <label
              for="create-name"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.user_edit_name()}
            </label>
            <input
              id="create-name"
              type="text"
              bind:value={createName}
              class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <div class="flex-1">
            <label
              for="create-password"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.user_password_new()}
            </label>
            <input
              id="create-password"
              type="password"
              bind:value={createPassword}
              required
              class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <div>
            <label
              for="create-role"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.user_edit_role()}
            </label>
            <select
              id="create-role"
              bind:value={createRole}
              class="border rounded px-3 py-1.5 text-sm"
            >
              <option value="employee">{m.user_role_employee()}</option>
              <option value="admin">{m.user_role_admin()}</option>
            </select>
          </div>
          <Button type="submit" disabled={createLoading}>
            {m.user_create()}
          </Button>
        </form>
      </div>
    {/if}
  </div>

  <DataTable
    rows={displayRows}
    variant="gray"
    rowKey={(row: User) => row.id}
    columns={[
      {
        header: m.login_username(),
        className: "text-sm text-gray-600",
        cell: usernameCell,
      },
      { header: m.user_edit_name(), cell: nameCell },
      {
        header: m.user_edit_role(),
        className: "text-sm text-gray-600",
        cell: roleCell,
      },
      { header: m.staff_status(), cell: statusCell },
      ...(isAdmin ? [{ header: "", cell: actionsCell }] : []),
    ]}
  />
</div>

{#snippet usernameCell(row: User)}
  {row.username}
{/snippet}

{#snippet nameCell(row: User)}
  {row.name}
{/snippet}

{#snippet roleCell(row: User)}
  {row.role === "admin" ? m.user_role_admin() : m.user_role_employee()}
{/snippet}

{#snippet statusCell(row: User)}
  {#if row.active}
    <StatusBadge label={m.status_active()} variant="green" />
  {:else}
    <StatusBadge label={m.status_inactive()} variant="gray" />
  {/if}
{/snippet}

{#snippet actionsCell(row: User)}
  <div class="flex items-center gap-2 flex-wrap justify-end">
    <button
      onclick={() => startEdit(row)}
      class="text-sm text-neutral-500 hover:text-neutral-900 transition-colors"
    >
      {m.user_edit()}
    </button>
    {#if row.active}
      <button
        onclick={() => startActivation(row)}
        class="text-sm text-neutral-500 hover:text-red-700 transition-colors"
      >
        {m.user_deactivate()}
      </button>
    {:else}
      <button
        onclick={() => startActivation(row)}
        class="text-sm text-neutral-500 hover:text-green-700 transition-colors"
      >
        {m.user_activate()}
      </button>
    {/if}
  </div>
{/snippet}

{#if editUser}
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
      <h3 class="text-lg font-medium text-neutral-900 mb-2">
        {m.user_edit()}
      </h3>
      <div class="space-y-3 mb-4">
        <div>
          <label for="edit-name" class="block text-sm text-neutral-700 mb-1">
            {m.user_edit_name()}
          </label>
          <input
            id="edit-name"
            type="text"
            bind:value={editName}
            class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
          />
        </div>
        <div>
          <label for="edit-role" class="block text-sm text-neutral-700 mb-1">
            {m.user_edit_role()}
          </label>
          <select
            id="edit-role"
            bind:value={editRole}
            class="w-full border rounded px-3 py-1.5 text-sm"
          >
            <option value="employee">{m.user_role_employee()}</option>
            <option value="admin">{m.user_role_admin()}</option>
          </select>
        </div>
        <div>
          <label
            for="edit-password"
            class="block text-sm text-neutral-700 mb-1"
          >
            {m.user_password_new()}
            <span class="text-neutral-400">{m.user_password_optional()}</span>
          </label>
          <input
            id="edit-password"
            type="password"
            bind:value={editNewPassword}
            class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
          />
        </div>
      </div>
      <div class="flex gap-2 justify-end">
        <Button variant="icon" onclick={() => (editUser = null)}>&times;</Button
        >
        <Button onclick={handleEdit} disabled={editLoading}>
          {m.user_edit()}
        </Button>
      </div>
    </div>
  </div>
{/if}

{#if activateTarget}
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
      <h3 class="text-lg font-medium text-neutral-900 mb-2">
        {activateTarget.active ? m.user_deactivate() : m.user_activate()}
      </h3>

      {#if activateError}
        <div
          class="bg-red-50 border border-red-200 rounded p-3 mb-3 text-red-700 text-sm"
        >
          {activateError}
        </div>
      {/if}

      {#if activateTarget.active}
        {#if activateTarget.role === "admin"}
          <p class="text-sm text-neutral-600 mb-3">
            {m.user_deactivate_admin_confirm({ name: activateTarget.name })}
          </p>
          <label
            for="activate-password"
            class="block text-sm text-neutral-700 mb-1"
          >
            {m.user_deactivate_admin_password()}
          </label>
          <input
            id="activate-password"
            type="password"
            bind:value={activateAdminPassword}
            class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm mb-4 focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
          />
        {:else}
          <p class="text-sm text-neutral-600 mb-4">
            {m.user_deactivate_confirm({ name: activateTarget.name })}
          </p>
        {/if}
      {:else}
        <p class="text-sm text-neutral-600 mb-4">
          {m.user_activate_confirm({ name: activateTarget.name })}
        </p>
      {/if}

      <div class="flex gap-2 justify-end">
        <Button
          variant="icon"
          onclick={() => {
            activateTarget = null;
            activateError = "";
          }}
        >
          &times;
        </Button>
        <Button
          onclick={handleActivation}
          disabled={activateLoading ||
            (activateTarget.active &&
              activateTarget.role === "admin" &&
              !activateAdminPassword.trim())}
          variant={activateTarget.active ? "delete" : "primaryDark"}
        >
          {activateTarget.active ? m.user_deactivate() : m.user_activate()}
        </Button>
      </div>
    </div>
  </div>
{/if}
