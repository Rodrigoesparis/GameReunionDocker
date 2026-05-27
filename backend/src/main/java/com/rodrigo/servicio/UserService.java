package com.rodrigo.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rodrigo.modelo.User;
import com.rodrigo.repositorio.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    //  Registro 

    public User registrarUsuario(String name, String username, String email,
                                  String password, Integer age) {

        // Validar que el username no esté en uso
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El username '" + username + "' ya está en uso.");
        }

        // Validar edad mínima
        if (age != null && age < 13) {
            throw new IllegalArgumentException("Debes tener al menos 13 años para registrarte.");
        }

        String code = String.valueOf((int)(Math.random() * 900000)+100000);

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);
        user.setVerified(false);
        user.setVerificationCode(code);


        emailService.sendVerificationCode(email, code);

        return userRepository.save(user);
    }

    //  Login básico (para consola, sin tokens)

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        if (!user.isVerified()) {
        throw new IllegalArgumentException("Cuenta no verificada. Revisa tu email.");
    }

        return user;
    }

 @Transactional
public void verificarCodigo(String email, String code) {
    System.out.println(">>> BUSCANDO EMAIL: " + email);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

    System.out.println(">>> USUARIO: " + user.getUsername() + " verified=" + user.isVerified());
    System.out.println(">>> CODE DB: " + user.getVerificationCode() + " CODE INPUT: " + code);

    if (user.isVerified()) {
        throw new IllegalStateException("La cuenta ya está verificada.");
    }

    if (!code.equals(user.getVerificationCode())) {
        throw new IllegalArgumentException("Código incorrecto.");
    }

    user.setVerified(true);
    user.setVerificationCode(null);
    userRepository.save(user);
    System.out.println(">>> GUARDADO verified=" + user.isVerified());
}

    //  Consultas

    public Optional<User> buscarPorId(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> buscarPorUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    //  Actualizar perfil 

    public User actualizarPerfil(Integer userId, String nuevoNombre, String nuevoEmail) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            user.setName(nuevoNombre);
        }
        if (nuevoEmail != null && !nuevoEmail.isBlank()) {
            user.setEmail(nuevoEmail);
        }

        return userRepository.save(user);
    }

    //  Eliminar usuario .

    public void eliminarUsuario(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        userRepository.deleteById(userId);
    }
}