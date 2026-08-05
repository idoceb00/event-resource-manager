import { employees } from "$lib/data/mock-data";
import type { Employee } from "$lib/types/domain";
import type { StaffService } from "../types";

export const mockStaffService: StaffService = {
  async getStaff(): Promise<Employee[]> {
    return employees;
  },
};
