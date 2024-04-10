package com.example.quicktap_clientes.fragments;

public interface RegisterCallback {
    void onRegisterSuccess(String mensaje, String usuario);
    void onRegisterFailure(String mensaje);
}