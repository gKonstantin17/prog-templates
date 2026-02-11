import { userProfile } from "../api/authApi";
import { useState } from "react";
import "../panel.css";

export default function UserPanel() {
    const [data, setData] = useState("");

    const loadProfile = async () => {
        try {
            const res = await userProfile();
            setData(JSON.stringify(res, null, 2));
        } catch (e) {
            setData(e.message);
        }
    };

    return (
        <div className="panel">
            <h3>User API</h3>
            <button onClick={loadProfile}>Load profile</button>
            <pre>{data}</pre>
        </div>
    );
}
