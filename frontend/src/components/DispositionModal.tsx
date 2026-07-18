import { useEffect, useState } from "react";
import type { Part } from "../types/Part";
import type { DispositionRequest } from "../types/DispositionRequest";
import { getDispositionRequestsForPart } from "../api/dispositionRequests";

interface DispositionModalProps {
    part: Part;
    onClose: () => void;
    onChange: () => void;
}

export function DispositionModal({ part, onClose, onChange }: DispositionModalProps) {
    const [requests, setRequests] = useState<DispositionRequest[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadRequests();
    }, [part.partNumber]);

    function loadRequests() {
        setIsLoading(true);
        getDispositionRequestsForPart(part.partNumber)
            .then((data) => setRequests(data))
            .catch((err) => setError(err.message))
            .finally(() => setIsLoading(false));
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <h2>{part.partNumber} — Disposition Requests</h2>
                <button onClick={onClose}>Close</button>

                {isLoading && <p>Loading history...</p>}
                {error && <p>Failed to load history: {error}</p>}

                {!isLoading && !error && (
                    <table>
                        <thead>
                        <tr>
                            <th>Type</th>
                            <th>Quantity</th>
                            <th>Justification</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requests.length === 0 && (
                            <tr>
                                <td colSpan={4}>No disposition requests yet.</td>
                            </tr>
                        )}
                        {requests.map((req) => (
                            <tr key={req.id}>
                                <td>{req.type}</td>
                                <td>{req.quantity}</td>
                                <td>{req.justification}</td>
                                <td>{req.status}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}