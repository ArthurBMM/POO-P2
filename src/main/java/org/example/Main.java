package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.http.*;
import java.net.URI;
import com.google.gson.Gson;

// Classe principal
public class Main {
    public static void main(String[] args) {
        SistemaCoworking sistema = new SistemaCoworking();
        sistema.iniciar();
    }
}
