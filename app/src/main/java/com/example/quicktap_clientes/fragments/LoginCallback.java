package com.example.quicktap_clientes.fragments;

/**
 * Controla el estado del login
 */
public interface LoginCallback {
    void onLoginSuccess(String mensaje, String usuario);
    void onLoginFailure(String mensaje);
}