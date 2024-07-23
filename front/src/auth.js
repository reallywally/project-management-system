// src/auth.js
export function saveToken(token) {
    localStorage.setItem('jwt', token);
}

export function getToken() {
    return localStorage.getItem('jwt');
}

export function removeToken() {
    localStorage.removeItem('jwt');
}

export function isLoggedIn() {
    const token = getToken();
    return !!token;
}
