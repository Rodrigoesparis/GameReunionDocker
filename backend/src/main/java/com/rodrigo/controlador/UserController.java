package com.rodrigo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rodrigo.modelo.User;
import com.rodrigo.servicio.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    //  Registro 
  @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    try {
        User user = userService.registrarUsuario(
            req.name, req.username, req.email, req.password, req.age
        );
        log.info("[USUARIO CREADO] idUser={} username='{}' email='{}'",
            user.getIdUser(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(user);
    } catch (IllegalArgumentException e) {
        log.error("[ERROR REGISTRO] username='{}' motivo='{}'", req.username, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    //  Login básico 

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    try {
        User user = userService.login(req.username, req.password);
        return ResponseEntity.ok(user);
    } catch (IllegalArgumentException e) {
        log.error("[ERROR LOGIN] username='{}' motivo='{}'", req.username, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    //  Listar todos (solo para pruebas) 

    @GetMapping
    public List<User> getAllUsers() {
        return userService.listarTodos();
    }

    //  Actualizar perfil 

@PutMapping("/{userId}")
public ResponseEntity<?> updateProfile(@PathVariable Integer userId,
                                        @RequestBody UpdateProfileRequest req) {
    try {
        User updated = userService.actualizarPerfil(userId, req.name, req.email);
        return ResponseEntity.ok(updated);
    } catch (IllegalArgumentException e) {
        log.error("[ERROR ACTUALIZAR USUARIO] userId={} motivo='{}'", userId, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/verify")
public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
    try {
        userService.verificarCodigo(req.email, req.code);
        return ResponseEntity.ok("Cuenta verificada correctamente.");
    } catch (IllegalArgumentException | IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}




    //  DTOs (clases internas para recibir JSON) 

    static class RegisterRequest {
        public String name;
        public String username;
        public String email;
        public String password;
        public Integer age;
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class UpdateProfileRequest {
        public String name;
        public String email;
    }

    static class VerifyRequest {
    public String email;
    public String code;
}
}