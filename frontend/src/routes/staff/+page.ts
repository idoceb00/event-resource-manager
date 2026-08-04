import { staffService } from '$lib/services';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	return { staff: await staffService.getStaff() };
};