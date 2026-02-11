import { apiFetch } from "./api";

export const login = (login, password) =>
    apiFetch("/login", {
        method: "POST",
        body: JSON.stringify({ login, password })
    });

export const register = (name, login, password) =>
    apiFetch("/register", {
        method: "POST",
        body: JSON.stringify({ name, login, password })
    });

export const logout = () =>
    apiFetch("/logout", { method: "POST" });

export const me = () =>
    apiFetch("/me");

export const adminDashboard = () =>
    apiFetch("/dashboard");

export const userProfile = () =>
    apiFetch("/profile");
