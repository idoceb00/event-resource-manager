import type { EmployeeStatus, EquipmentStatus } from '$lib/types/domain';

/**
 * Maps domain statuses to badge color variants. Labels are handled by the
 * i18n layer in the components; this only decides the visual treatment.
 */

export type BadgeVariant = 'green' | 'blue' | 'gray' | 'amber' | 'red';

export function equipmentStatusVariant(status: EquipmentStatus): BadgeVariant {
	switch (status) {
		case 'available':
			return 'green';
		case 'reserved':
			return 'blue';
		case 'inactive':
			return 'gray';
	}
}

export function employeeStatusVariant(status: EmployeeStatus): BadgeVariant {
	return status === 'active' ? 'green' : 'gray';
}