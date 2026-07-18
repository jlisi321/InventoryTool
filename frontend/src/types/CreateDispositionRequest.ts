import type { DispositionType } from "./DispositionRequest";

export interface CreateDispositionRequest {
    type: DispositionType;
    quantity: number;
    justification: string;
}