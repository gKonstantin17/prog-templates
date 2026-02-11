import { useState } from "react";
import { login, register, me } from "../api/authApi";
import { useAuth } from "../auth/AuthContext";
import "./auth.css";

export default function AuthPage() {
    const { setUser } = useAuth();
    const [mode, setMode] = useState("login");
    const [form, setForm] = useState({ name: "", login: "", password: "" });
    const [error, setError] = useState("");

    const submit = async () => {
        try {
            if (mode === "login") {
                await login(form.login, form.password);
            } else {
                await register(form.name, form.login, form.password);
            }
            setUser(await me());
        } catch (e) {
            setError(e.message);
        }
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-card">
                <h2>Auth Demo</h2>

                <div className="tabs">
                    <button className={mode === "login" ? "active" : ""} onClick={() => setMode("login")}>Login</button>
                    <button className={mode === "register" ? "active" : ""} onClick={() => setMode("register")}>Register</button>
                </div>

                {mode === "register" && (
                    <input
                        placeholder="Name"
                        value={form.name}
                        onChange={e => setForm({ ...form, name: e.target.value })}
                    />
                )}

                <input
                    placeholder="Login"
                    value={form.login}
                    onChange={e => setForm({ ...form, login: e.target.value })}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={e => setForm({ ...form, password: e.target.value })}
                />

                <button className="submit" onClick={submit}>
                    {mode === "login" ? "Войти" : "Зарегистрироваться"}
                </button>

                {error && <p className="error">{error}</p>}
            </div>
        </div>
    );
}
