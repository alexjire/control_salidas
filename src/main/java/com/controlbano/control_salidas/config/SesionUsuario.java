package com.controlbano.control_salidas.config;

public class SesionUsuario {

    public static String username;
    public static String rol;
    public static String linea;

    public static void limpiarSesion(){
        username = null;
        rol = null;
        linea = null;
    }
}