const API_URL = "https://localhost:7093"; // важно HTTPS

export async function apiFetch(url, options = {}) {
    const response = await fetch(`${API_URL}${url}`, {
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `HTTP ${response.status}`);
    }

    if (response.status === 204) return null;

    return response.json();
}

