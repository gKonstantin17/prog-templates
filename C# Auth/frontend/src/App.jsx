import { AuthProvider, useAuth } from "./auth/AuthContext";
import AuthPage from "./components/AuthPage";
import AdminPanel from "./components/AdminPanel";
import UserPanel from "./components/UserPanel";

export function AppContent() {
    const { user, loading, logout } = useAuth();

    if (loading) return <p style={{ padding: 30 }}>Загрузка...</p>;
    if (!user) return <AuthPage />;

    return (
        <div style={{ padding: 30, maxWidth: 800, margin: "auto" }}>
            <h2>
                {user.name} <small>({user.role})</small>
            </h2>

            <UserPanel />
            {user.role === "Admin" && <AdminPanel />}

            <button
                style={{
                    marginTop: 20,
                    padding: "10px 20px",
                    background: "#e53e3e",
                    border: "none",
                    color: "white",
                    borderRadius: 6,
                }}
                onClick={logout}
            >
                Logout
            </button>
        </div>
    );
}

export default function App() {
    return (
        <AuthProvider>
            <AppContent />
        </AuthProvider>
    );
}
