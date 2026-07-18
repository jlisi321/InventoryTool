import { useEffect, useState } from "react";
import type { Part } from "../types/Part";
import { getAllParts } from "../api/parts";

export function PartsList() {
    const [parts, setParts] = useState<Part[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getAllParts()
            .then((data) => setParts(data))
            .catch((err) => setError(err.message))
            .finally(() => setIsLoading(false));
    }, []);

    if (isLoading) {
        return <p>Loading parts...</p>;
    }

    if (error) {
        return <p>Failed to load parts: {error}</p>;
    }

    return (
        <table>
            <thead>
            <tr>
                <th>Part Number</th>
                <th>Description</th>
                <th>Monthly Demand</th>
                <th>Unit Cost</th>
                <th>Status</th>
                <th>Active Disposition</th>
            </tr>
            </thead>
            <tbody>
            {parts.map((part) => (
                <tr key={part.partNumber}>
                    <td>{part.partNumber}</td>
                    <td>{part.description}</td>
                    <td>{part.monthlyDemand}</td>
                    <td>${part.unitCost.toFixed(2)}</td>
                    <td>{part.status}</td>
                    <td>{part.activeDispositionStatus ?? "—"}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}