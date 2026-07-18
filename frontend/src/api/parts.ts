import { apiRequest } from "./client";
import type { Part } from "../types/Part";

// GET a list of parts API call
export function getAllParts(): Promise<Part[]> {
    return apiRequest<Part[]>("/parts");
}