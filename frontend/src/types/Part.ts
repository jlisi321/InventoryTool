export type PartStatus = "ACTIVE" | "OBSOLETE";
export type DispositionStatus = "DRAFT" | "SUBMITTED" | "APPROVED" | "REJECTED";

export interface Part {
    partNumber: string;
    description: string;
    monthlyDemand: number;
    unitCost: number;
    status: PartStatus;
    activeDispositionStatus: DispositionStatus | null;
}