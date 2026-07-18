import { apiRequest } from "./client";
import type { DispositionRequest } from "../types/DispositionRequest";
import type { CreateDispositionRequest } from "../types/CreateDispositionRequest";

// GET request for all past and present dispositions requests for a given part
export function getDispositionRequestsForPart(partNumber: string): Promise<DispositionRequest[]> {
    return apiRequest<DispositionRequest[]>(`/dispositionRequests?partNumber=${partNumber}`);
}

// POST request to create a disposition request for a given part
export function createDispositionRequest(
    partNumber: string,
    request: CreateDispositionRequest
): Promise<DispositionRequest> {
    return apiRequest<DispositionRequest>(
        `/dispositionRequests?partNumber=${encodeURIComponent(partNumber)}`,
        {
            method: "POST",
            body: JSON.stringify(request),
        }
    );
}

// PATCH request to submit a disposition request that is inside the draft state
export function submitDispositionRequest(id: number): Promise<DispositionRequest> {
    return apiRequest<DispositionRequest>(`/dispositionRequests/${id}/submit`, {
        method: "PATCH",
    });
}

// PATCH request to approve a disposition request that is inside a submitted state
export function approveDispositionRequest(id: number): Promise<DispositionRequest> {
    return apiRequest<DispositionRequest>(`/dispositionRequests/${id}/approve`, {
        method: "PATCH",
    });
}

// PATCH request to reject a disposition request that is inside a submitted state
export function rejectDispositionRequest(id: number): Promise<DispositionRequest> {
    return apiRequest<DispositionRequest>(`/dispositionRequests/${id}/reject`, {
        method: "PATCH",
    });
}