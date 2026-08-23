import type {
  Employee,
  Equipment,
  Event,
  Reservation,
} from "$lib/types/domain";

/**
 * Seed data for the UI. Values are preserved from the original design mockup so
 * the screens render exactly as designed. This file only holds data; the access
 * layer lives in `$lib/services` (mock implementations consume it here and will
 * be transparently swapped for real API calls later).
 */

export const events: Event[] = [
  {
    id: "evt-001",
    name: "Rock Festival Madrid",
    startDate: "2026-12-20T14:00:00",
    endDate: "2026-12-22T22:00:00",
    address: "Wizink Center, Madrid",
    transport: true,
    extraInfo: "Camion #3, sale el 18/12 a las 08:00",
    performances: [
      {
        id: "perf-001",
        name: "Montaje escenario principal",
        startTime: "14:00:00",
        duration: 180,
        rehearsalTime: "10:00:00",
      },
      {
        id: "perf-002",
        name: "Concierto de tarde",
        startTime: "20:00:00",
        duration: 120,
        rehearsalTime: "16:00:00",
      },
      {
        id: "perf-003",
        name: "Concierto de tarde",
        startTime: "20:00:00",
        duration: 120,
        rehearsalTime: "16:00:00",
      },
    ],
  },
  {
    id: "evt-002",
    name: "Conferencia corporativa BCN",
    startDate: "2026-12-21T09:00:00",
    endDate: "2026-12-21T18:00:00",
    address: "CCIB, Barcelona",
    transport: true,
    extraInfo: "Furgoneta #2, sale el 20/12 a las 06:00",
    performances: [
      {
        id: "perf-004",
        name: "Sesion matinal",
        startTime: "09:00:00",
        duration: 180,
        rehearsalTime: "07:00:00",
      },
      {
        id: "perf-005",
        name: "Sesion de tarde",
        startTime: "15:00:00",
        duration: 180,
        rehearsalTime: "12:00:00",
      },
    ],
  },
  {
    id: "evt-003",
    name: "Noche de Jazz Valencia",
    startDate: "2026-12-23T21:00:00",
    endDate: "2026-12-23T23:59:00",
    address: "Palau de la Musica, Valencia",
    transport: false,
    performances: [
      {
        id: "perf-006",
        name: "Actuacion de noche",
        startTime: "21:00:00",
        duration: 120,
        rehearsalTime: "18:00:00",
      },
    ],
  },
  {
    id: "evt-004",
    name: "Gala de Fin de Ano",
    startDate: "2026-12-31T22:00:00",
    endDate: "2027-01-01T02:00:00",
    address: "Gran Teatro, Sevilla",
    transport: true,
    extraInfo: "Camion #1, sale el 29/12 a las 10:00",
    performances: [
      {
        id: "perf-007",
        name: "Concierto de Nochevieja",
        startTime: "22:00:00",
        duration: 180,
        rehearsalTime: "18:00:00",
      },
    ],
  },
];

export const products: Equipment[] = [
  {
    id: "prod-001",
    name: "Meyer Sound LYON-M",
    category: "SOUND",
    status: "CATALOGUED",
    stock: 4,
  },
  {
    id: "prod-002",
    name: "Meyer Sound LYON-M",
    category: "SOUND",
    status: "CATALOGUED",
    stock: 2,
  },
  {
    id: "prod-003",
    name: "Robe Robin Pointe",
    category: "LIGHTING",
    status: "CATALOGUED",
    stock: 8,
  },
  {
    id: "prod-004",
    name: "Robe Robin Pointe",
    category: "LIGHTING",
    status: "CATALOGUED",
    stock: 6,
  },
  {
    id: "prod-005",
    name: "Martin MAC Aura XB",
    category: "LIGHTING",
    status: "CATALOGUED",
    stock: 12,
  },
  {
    id: "prod-006",
    name: "DiGiCo SD12",
    category: "SOUND",
    status: "CATALOGUED",
    stock: 2,
  },
  {
    id: "prod-007",
    name: "DiGiCo SD10",
    category: "SOUND",
    status: "CATALOGUED",
    stock: 3,
  },
  {
    id: "prod-008",
    name: "Shure Axient Digital",
    category: "SOUND",
    status: "CATALOGUED",
    stock: 10,
  },
  {
    id: "prod-009",
    name: "Shure Axient Digital",
    category: "SOUND",
    status: "DECATALOGUED",
    stock: 0,
  },
  {
    id: "prod-010",
    name: "MA Lighting grandMA3",
    category: "LIGHTING",
    status: "CATALOGUED",
    stock: 1,
  },
];

export const employees: Employee[] = [
  {
    id: "emp-001",
    name: "Carlos Ramírez",
    role: "Ingeniero de sonido",
    status: "active",
    email: "carlos.ramirez@eventory.com",
    phone: "+34 600 111 001",
  },
  {
    id: "emp-002",
    name: "Laura Sánchez",
    role: "Diseñadora de iluminación",
    status: "active",
    email: "laura.sanchez@eventory.com",
    phone: "+34 600 111 002",
  },
  {
    id: "emp-003",
    name: "Miguel Torres",
    role: "Jefe de escenario",
    status: "active",
    email: "miguel.torres@eventory.com",
    phone: "+34 600 111 003",
  },
  {
    id: "emp-004",
    name: "Ana Morales",
    role: "Técnica de sonido",
    status: "active",
    email: "ana.morales@eventory.com",
    phone: "+34 600 111 004",
  },
  {
    id: "emp-005",
    name: "Javier López",
    role: "Técnico de iluminación",
    status: "active",
    email: "javier.lopez@eventory.com",
    phone: "+34 600 111 005",
  },
  {
    id: "emp-006",
    name: "Isabel Martín",
    role: "Especialista en rigging",
    status: "active",
    email: "isabel.martin@eventory.com",
    phone: "+34 600 111 006",
  },
  {
    id: "emp-007",
    name: "Pedro Ruiz",
    role: "Conductor",
    status: "inactive",
    email: "pedro.ruiz@eventory.com",
    phone: "+34 600 111 007",
  },
];

export const reservations: Reservation[] = [
  // Rock Festival Madrid - múltiples reservas
  {
    id: "res-001",
    eventId: "evt-001",
    productId: "prod-001",
    startDate: "2026-12-19",
    endDate: "2026-12-23",
  },
  {
    id: "res-002",
    eventId: "evt-001",
    productId: "prod-003",
    startDate: "2026-12-19",
    endDate: "2026-12-23",
  },
  {
    id: "res-003",
    eventId: "evt-001",
    productId: "prod-006",
    startDate: "2026-12-19",
    endDate: "2026-12-23",
  },
  {
    id: "res-004",
    eventId: "evt-001",
    productId: "prod-010",
    startDate: "2026-12-19",
    endDate: "2026-12-23",
  },
  {
    id: "res-005",
    eventId: "evt-001",
    employeeId: "emp-001",
    startDate: "2026-12-20",
    endDate: "2026-12-22",
  },
  {
    id: "res-006",
    eventId: "evt-001",
    employeeId: "emp-002",
    startDate: "2026-12-20",
    endDate: "2026-12-22",
  },
  {
    id: "res-007",
    eventId: "evt-001",
    employeeId: "emp-003",
    startDate: "2026-12-20",
    endDate: "2026-12-22",
  },

  // Conferencia corporativa BCN - CONFLICTO: se solapa con el Rock Festival
  {
    id: "res-008",
    eventId: "evt-002",
    productId: "prod-006",
    startDate: "2026-12-20",
    endDate: "2026-12-22",
    hasConflict: true,
  },
  {
    id: "res-009",
    eventId: "evt-002",
    employeeId: "emp-001",
    startDate: "2026-12-21",
    endDate: "2026-12-21",
    hasConflict: true,
  },
  {
    id: "res-010",
    eventId: "evt-002",
    employeeId: "emp-004",
    startDate: "2026-12-21",
    endDate: "2026-12-21",
  },

  // Noche de Jazz Valencia
  {
    id: "res-011",
    eventId: "evt-003",
    employeeId: "emp-002",
    startDate: "2026-12-23",
    endDate: "2026-12-23",
  },
  {
    id: "res-012",
    eventId: "evt-003",
    employeeId: "emp-005",
    startDate: "2026-12-23",
    endDate: "2026-12-23",
  },
];
