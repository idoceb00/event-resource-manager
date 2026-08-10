<script lang="ts">
  import { page } from "$app/state";
  import {
    Calendar,
    ClipboardList,
    FileText,
    LayoutDashboard,
    Package,
    Users,
  } from "@lucide/svelte";
  import { m } from "$lib/paraglide/messages";
  import NavItem from "./NavItem.svelte";

  const isActive = (routeId: string): boolean => {
    const current = page.route.id;
    if (routeId === "/") return current === "/";
    return current === routeId || current?.startsWith(`${routeId}/`) === true;
  };
</script>

<aside class="w-56 bg-white border-r border-neutral-200 flex flex-col">
  <div class="h-14 border-b border-neutral-200 flex items-center px-4">
    <h1 class="font-semibold text-neutral-900">Eventory</h1>
  </div>

  <nav class="flex-1 p-3 space-y-0.5">
    <NavItem
      href="/"
      label={m.dashboard()}
      icon={LayoutDashboard}
      active={isActive("/")}
    />
    <NavItem
      href="/calendar"
      label={m.calendar()}
      icon={Calendar}
      active={isActive("/calendar")}
    />
    <NavItem
      href="/events"
      label={m.events()}
      icon={FileText}
      active={isActive("/events")}
    />
    <NavItem
      href="/inventory"
      label={m.inventory()}
      icon={Package}
      active={isActive("/inventory")}
    />
    <NavItem
      href="/staff"
      label={m.staff()}
      icon={Users}
      active={isActive("/staff")}
    />
    <NavItem
      href="/reservations"
      label={m.reservations()}
      icon={ClipboardList}
      active={isActive("/reservations")}
    />
  </nav>
</aside>
