import type {
	Employee,
	Equipment,
	Event,
	Reservation
} from '$lib/types/domain';

/**
 * Seed data for the UI. Values are preserved from the original design mockup so
 * the screens render exactly as designed. This file only holds data; the access
 * layer lives in `$lib/services` (mock implementations consume it here and will
 * be transparently swapped for real API calls later).
 */

export const events: Event[] = [
	{
		id: 'evt-001',
		name: 'Rock Festival Madrid',
		startDate: '2026-12-20',
		endDate: '2026-12-22',
		location: 'Wizink Center, Madrid',
		transportInfo: 'Camión #3, sale el 18/12 a las 08:00',
		performances: [
			{ id: 'perf-001', eventId: 'evt-001', name: 'Montaje escenario principal', date: '2026-12-20', time: '14:00' },
			{ id: 'perf-002', eventId: 'evt-001', name: 'Concierto de tarde', date: '2026-12-20', time: '20:00' },
			{ id: 'perf-003', eventId: 'evt-001', name: 'Concierto de tarde', date: '2026-12-21', time: '20:00' }
		]
	},
	{
		id: 'evt-002',
		name: 'Conferencia corporativa BCN',
		startDate: '2026-12-21',
		endDate: '2026-12-21',
		location: 'CCIB, Barcelona',
		transportInfo: 'Furgoneta #2, sale el 20/12 a las 06:00',
		performances: [
			{ id: 'perf-004', eventId: 'evt-002', name: 'Sesión matinal', date: '2026-12-21', time: '09:00' },
			{ id: 'perf-005', eventId: 'evt-002', name: 'Sesión de tarde', date: '2026-12-21', time: '15:00' }
		]
	},
	{
		id: 'evt-003',
		name: 'Noche de Jazz Valencia',
		startDate: '2026-12-23',
		endDate: '2026-12-23',
		location: 'Palau de la Música, Valencia',
		performances: [
			{ id: 'perf-006', eventId: 'evt-003', name: 'Actuación de noche', date: '2026-12-23', time: '21:00' }
		]
	},
	{
		id: 'evt-004',
		name: 'Gala de Fin de Año',
		startDate: '2026-12-31',
		endDate: '2027-01-01',
		location: 'Gran Teatro, Sevilla',
		transportInfo: 'Camión #1, sale el 29/12 a las 10:00',
		performances: [
			{ id: 'perf-007', eventId: 'evt-004', name: 'Concierto de Nochevieja', date: '2026-12-31', time: '22:00' }
		]
	}
];

export const products: Equipment[] = [
	{ id: 'prod-001', name: 'Meyer Sound LYON-M', category: 'Sistema PA', status: 'reserved', serialNumber: 'MSL-4521' },
	{ id: 'prod-002', name: 'Meyer Sound LYON-M', category: 'Sistema PA', status: 'available', serialNumber: 'MSL-4522' },
	{ id: 'prod-003', name: 'Robe Robin Pointe', category: 'Cabezal móvil', status: 'reserved', serialNumber: 'RBP-1134' },
	{ id: 'prod-004', name: 'Robe Robin Pointe', category: 'Cabezal móvil', status: 'available', serialNumber: 'RBP-1135' },
	{ id: 'prod-005', name: 'Martin MAC Aura XB', category: 'Cabezal móvil', status: 'available', serialNumber: 'MAC-7821' },
	{ id: 'prod-006', name: 'DiGiCo SD12', category: 'Mesa de mezclas', status: 'reserved', serialNumber: 'DGC-3301' },
	{ id: 'prod-007', name: 'DiGiCo SD10', category: 'Mesa de mezclas', status: 'available', serialNumber: 'DGC-2205' },
	{ id: 'prod-008', name: 'Shure Axient Digital', category: 'Micrófono inalámbrico', status: 'available', serialNumber: 'SHR-8844' },
	{ id: 'prod-009', name: 'Shure Axient Digital', category: 'Micrófono inalámbrico', status: 'inactive', serialNumber: 'SHR-8832' },
	{ id: 'prod-010', name: 'MA Lighting grandMA3', category: 'Consola de iluminación', status: 'reserved', serialNumber: 'MA3-1102' }
];

export const employees: Employee[] = [
	{ id: 'emp-001', name: 'Carlos Ramírez', role: 'Ingeniero de sonido', status: 'active', email: 'carlos.ramirez@eventory.com', phone: '+34 600 111 001' },
	{ id: 'emp-002', name: 'Laura Sánchez', role: 'Diseñadora de iluminación', status: 'active', email: 'laura.sanchez@eventory.com', phone: '+34 600 111 002' },
	{ id: 'emp-003', name: 'Miguel Torres', role: 'Jefe de escenario', status: 'active', email: 'miguel.torres@eventory.com', phone: '+34 600 111 003' },
	{ id: 'emp-004', name: 'Ana Morales', role: 'Técnica de sonido', status: 'active', email: 'ana.morales@eventory.com', phone: '+34 600 111 004' },
	{ id: 'emp-005', name: 'Javier López', role: 'Técnico de iluminación', status: 'active', email: 'javier.lopez@eventory.com', phone: '+34 600 111 005' },
	{ id: 'emp-006', name: 'Isabel Martín', role: 'Especialista en rigging', status: 'active', email: 'isabel.martin@eventory.com', phone: '+34 600 111 006' },
	{ id: 'emp-007', name: 'Pedro Ruiz', role: 'Conductor', status: 'inactive', email: 'pedro.ruiz@eventory.com', phone: '+34 600 111 007' }
];

export const reservations: Reservation[] = [
	// Rock Festival Madrid - múltiples reservas
	{ id: 'res-001', eventId: 'evt-001', productId: 'prod-001', startDate: '2026-12-19', endDate: '2026-12-23' },
	{ id: 'res-002', eventId: 'evt-001', productId: 'prod-003', startDate: '2026-12-19', endDate: '2026-12-23' },
	{ id: 'res-003', eventId: 'evt-001', productId: 'prod-006', startDate: '2026-12-19', endDate: '2026-12-23' },
	{ id: 'res-004', eventId: 'evt-001', productId: 'prod-010', startDate: '2026-12-19', endDate: '2026-12-23' },
	{ id: 'res-005', eventId: 'evt-001', employeeId: 'emp-001', startDate: '2026-12-20', endDate: '2026-12-22' },
	{ id: 'res-006', eventId: 'evt-001', employeeId: 'emp-002', startDate: '2026-12-20', endDate: '2026-12-22' },
	{ id: 'res-007', eventId: 'evt-001', employeeId: 'emp-003', startDate: '2026-12-20', endDate: '2026-12-22' },

	// Conferencia corporativa BCN - CONFLICTO: se solapa con el Rock Festival
	{ id: 'res-008', eventId: 'evt-002', productId: 'prod-006', startDate: '2026-12-20', endDate: '2026-12-22', hasConflict: true },
	{ id: 'res-009', eventId: 'evt-002', employeeId: 'emp-001', startDate: '2026-12-21', endDate: '2026-12-21', hasConflict: true },
	{ id: 'res-010', eventId: 'evt-002', employeeId: 'emp-004', startDate: '2026-12-21', endDate: '2026-12-21' },

	// Noche de Jazz Valencia
	{ id: 'res-011', eventId: 'evt-003', employeeId: 'emp-002', startDate: '2026-12-23', endDate: '2026-12-23' },
	{ id: 'res-012', eventId: 'evt-003', employeeId: 'emp-005', startDate: '2026-12-23', endDate: '2026-12-23' }
];