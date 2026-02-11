import { useState } from "react";
import { login, register, me } from "../api/authApi";
import { useAuth } from "../auth/AuthContext";

export default function LoginForm() {
    const { setUser } = useAuth();
    const [loginData, setLogin] = useState({ login: "", password: "" });
    const [registerData, setRegister] = useState({ name: "", login: "", password: "" });
    const [error, setError] = useState("");

    const handleLogin = async () => {
        try {
            await login(loginData.login, loginData.password);
            setUser(await me());
        } catch (e) {
            setError(e.message);
        }
    };

    return (
        <div>
            <h3>Login</h3>
            <input placeholder="login" onChange={e => setLogin({ ...loginData, login: e.target.value })} />
            <input type="password" placeholder="password" onChange={e => setLogin({ ...loginData, password: e.target.value })} />
            <button onClick={handleLogin}>Login</button>

            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
}
