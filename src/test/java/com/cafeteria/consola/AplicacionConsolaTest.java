package com.cafeteria.consola;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** La consola con entrada simulada: un flujo completo y entradas inválidas que no la cierran. */
class AplicacionConsolaTest {

    private static String ejecutar(String... lineas) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        String entrada = String.join("\n", lineas) + "\n";
        AplicacionConsola.ejecutar(new BufferedReader(new StringReader(entrada)), salida);
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void entradasInvalidasNoCierranLaConsola() {
        String salida = ejecutar("abc", "99", "", "2", "999", "0", "0");
        assertTrue(salida.contains("\"abc\" no es un número válido"));
        assertTrue(salida.contains("entre 0 y 7"));
        assertTrue(salida.contains("entre 0 y 14"));
        assertTrue(salida.contains("Hasta pronto."));
    }

    @Test
    void flujoCompletoDesdeElPedidoHastaElTicket() {
        String salida = ejecutar(
                "3", "s", "Ana",                 // nuevo pedido, cliente frecuente
                "1", "3", "s", "3", "4", "1", "0", "2", // latte grande, avena, shot, x2
                "1", "11", "1",                  // brownie x1
                "4", "3",                        // combo
                "5", "1", "100000",              // efectivo
                "6", "6", "6",                   // hasta entregado
                "8", "1",                        // ticket texto plano
                "0",                             // volver
                "0");                            // salir
        assertTrue(salida.contains("Pedido #1 creado para Ana (frecuente)"));
        assertTrue(salida.contains("Pago aprobado: Efectivo"));
        assertTrue(salida.contains("[COCINA] Pedido #1: Listo -> Entregado"));
        assertTrue(salida.contains("CAFETERIA - TICKET"));
        assertTrue(salida.contains("Hasta pronto."));
    }

    @Test
    void erroresDeNegocioSeMuestranYElMenuSigue() {
        String salida = ejecutar(
                "3", "n", "",                    // pedido anónimo
                "5", "1", "5000",                // pagar pedido vacío
                "4", "4", "FALSO",              // código inválido
                "6", "6", "6", "6",              // el cuarto avance falla
                "0", "0");
        assertTrue(salida.contains("No se puede pagar un pedido vacío"));
        assertTrue(salida.contains("no es válido"));
        assertTrue(salida.contains("no hay un estado siguiente"));
        assertTrue(salida.contains("Hasta pronto."));
    }

    @Test
    void montoInvalidoEnConsolaSeRechazaYSePuedeCorregir() {
        String salida = ejecutar(
                "3", "n", "",                    // pedido anónimo
                "1", "1", "n", "1",              // espresso x1, sin personalizar
                "5", "1", "20..000", "$100.000", // efectivo: primero mal, luego bien
                "0", "0");
        assertTrue(salida.contains("Monto inválido: \"20..000\""), salida);
        assertTrue(salida.contains("Pago aprobado: Efectivo (recibido $100.000"), salida);
        assertTrue(salida.contains("Hasta pronto."));
    }

    @Test
    void siSeAcabaLaEntradaTerminaSinExcepcion() {
        String salida = ejecutar("1");
        assertTrue(salida.contains("Fin de la entrada"));
    }
}
