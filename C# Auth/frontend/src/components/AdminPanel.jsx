import { adminDashboard } from "../api/authApi";
import { useState } from "react";
import "../panel.css";

export default function AdminPanel() {
    const [data, setData] = useState("");

    const loadDashboard = async () => {
        try {
            const res = await adminDashboard();
            setData(JSON.stringify(res, null, 2));
        } catch (e) {
            setData(e.message);
        }
    };

    return (
        <div className="panel">
            <h3>Admin API</h3>
            <button onClick={loadDashboard}>Load dashboard</button>
            <pre>{data}</pre>
        </div>
    );
}
