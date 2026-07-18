const BASE_URL = "http://localhost:8080";

export async function apiRequest<T>(path: string, options?: RequestInit): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
        headers: {
            "Content-Type": "application/json",
            ...options?.headers,
        },
        ...options,
    });

    if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        throw new ApiError(response.status, errorBody?.message ?? response.statusText);
    }

    return response.json();
}

export class ApiError extends Error {
    status: number;

    constructor(status: number, message: string) {
        super(message);
        this.status = status;
        this.name = "ApiError";
    }
}