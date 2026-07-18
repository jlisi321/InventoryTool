export type DispositionType = "STOCK" | "LAST_TIME_BUY" | "DISCONTINUE";
export type DispositionStatus = "DRAFT" | "SUBMITTED" | "APPROVED" | "REJECTED";

export interface DispositionRequest {
    id: number;
    type: DispositionType;
    quantity: number;
    justification: string;
    status: DispositionStatus;
    createdAt: string;
    updatedAt: string;
}