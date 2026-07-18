import { useEffect, useState } from "react";
import type { Part } from "../types/Part";
import type { DispositionRequest, DispositionType } from "../types/DispositionRequest";
import type { CreateDispositionRequest } from "../types/CreateDispositionRequest";
import {
    getDispositionRequestsForPart,
    createDispositionRequest,
    submitDispositionRequest,
    approveDispositionRequest,
    rejectDispositionRequest,
} from "../api/dispositionRequests";

interface DispositionModalProps {
    part: Part;
    onClose: () => void;
    onChange: () => void;
}

export function DispositionModal({ part, onClose, onChange }: DispositionModalProps) {
    const [requests, setRequests] = useState<DispositionRequest[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [type, setType] = useState<DispositionType>("STOCK");
    const [quantity, setQuantity] = useState(0);
    const [justification, setJustification] = useState("");
    const [formError, setFormError] = useState<string | null>(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

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

    function handleCreate(e: React.FormEvent) {
        e.preventDefault();
        setFormError(null);
        setIsSubmittingForm(true);

        const newRequest: CreateDispositionRequest = { type, quantity, justification };

        createDispositionRequest(part.partNumber, newRequest)
            .then(() => {
                setType("STOCK");
                setQuantity(0);
                setJustification("");
                loadRequests();
                onChange();
            })
            .catch((err) => setFormError(err.message))
            .finally(() => setIsSubmittingForm(false));
    }

    function handleTransition(id: number, action: "submit" | "approve" | "reject") {
        const transitionFn = action === "submit" ? submitDispositionRequest :
                action === "approve" ? approveDispositionRequest : rejectDispositionRequest;

        transitionFn(id)
            .then(() => {
                loadRequests();
                onChange();
            })
            .catch((err) => setError(err.message));
    }

    const hasActiveRequest = requests.some(
        (r) => r.status === "DRAFT" || r.status === "SUBMITTED"
    );

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
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {requests.length === 0 && (
                            <tr>
                                <td colSpan={5}>No disposition requests yet.</td>
                            </tr>
                        )}
                        {requests.map((req) => (
                            <tr key={req.id}>
                                <td>{req.type}</td>
                                <td>{req.quantity}</td>
                                <td>{req.justification}</td>
                                <td>{req.status}</td>
                                <td>
                                    {req.status === "DRAFT" && (
                                        <button onClick={() => handleTransition(req.id, "submit")}>
                                            Submit
                                        </button>
                                    )}
                                    {req.status === "SUBMITTED" && (
                                        <>
                                            <button onClick={() => handleTransition(req.id, "approve")}>
                                                Approve
                                            </button>
                                            <button onClick={() => handleTransition(req.id, "reject")}>
                                                Reject
                                            </button>
                                        </>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                <h3>New Disposition Request</h3>

                {hasActiveRequest && (
                    <p>This part already has an active disposition request — creating a new one is disabled.</p>
                )}

                <form onSubmit={handleCreate}>
                    <div>
                        <label>
                            Type:
                            <select
                                value={type}
                                onChange={(e) => setType(e.target.value as DispositionType)}
                                disabled={hasActiveRequest}
                            >
                                <option value="STOCK">STOCK</option>
                                <option value="LAST_TIME_BUY">LAST_TIME_BUY</option>
                                <option value="DISCONTINUE">DISCONTINUE</option>
                            </select>
                        </label>
                    </div>

                    {type === "LAST_TIME_BUY" && (
                        <div>
                            <label>
                                Quantity:
                                <input
                                    type="number"
                                    value={quantity}
                                    onChange={(e) => setQuantity(Number(e.target.value))}
                                    min={1}
                                    disabled={hasActiveRequest}
                                    required
                                />
                            </label>
                        </div>
                    )}

                    <div>
                        <label>
                            Justification:
                            <textarea
                                value={justification}
                                onChange={(e) => setJustification(e.target.value)}
                                disabled={hasActiveRequest}
                                required
                            />
                        </label>
                    </div>

                    {formError && <p className="form-error">{formError}</p>}

                    <button type="submit" disabled={hasActiveRequest || isSubmittingForm}>
                        {isSubmittingForm ? "Creating..." : "Create Disposition Request"}
                    </button>
                </form>
            </div>
        </div>
    );
}