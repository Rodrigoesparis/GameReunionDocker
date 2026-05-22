package com.rodrigo.servicio;

import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private LenguageRepository lenguageRepository;

    @Autowired
    private PlatformRepository platformRepository;

    // ── GET perfil completo ───────────────────────────────────────────────────

    public Map<String, Object> getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Map<String, Object>> games = gamesRepository
                .findByUserIdUserOrderByDisplayOrder(userId)
                .stream()
                .map(g -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("gameName", g.getGameName());
                    m.put("displayOrder", g.getDisplayOrder());
                    return m;
                })
                .collect(Collectors.toList());

        List<Map<String, String>> languages = lenguageRepository
                .findByUserIdUser(userId)
                .stream()
                .map(l -> Map.of("language", l.getLanguage()))
                .collect(Collectors.toList());

        List<Map<String, String>> platforms = platformRepository
                .findByUserIdUser(userId)
                .stream()
                .map(p -> Map.of("platform", p.getPlatform()))
                .collect(Collectors.toList());

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("idUser", user.getIdUser());
        profile.put("name", user.getName());
        profile.put("username", user.getUsername());
        profile.put("age", user.getAge());
        profile.put("bio", user.getBio());
        profile.put("callStyle", user.getCallStyle());
        profile.put("country", user.getCountry());
        profile.put("timezone", user.getTimezone());
        profile.put("photoUrl", user.getPhotoUrl()); // ← aquí, junto a los demás campos
        profile.put("games", games);
        profile.put("languages", languages);
        profile.put("platforms", platforms);

        return profile;
    }

    // ── UPDATE perfil ─────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> updateProfile(Integer userId, Map<String, Object> body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (body.containsKey("username")) {
            String newUsername = body.get("username").toString().trim();
            if (newUsername.isEmpty()) throw new IllegalArgumentException("El username no puede estar vacío");
            user.setUsername(newUsername);
        }
        if (body.containsKey("bio")) {
            user.setBio(body.get("bio").toString().trim());
        }
        if (body.containsKey("callStyle")) {
            user.setCallStyle(body.get("callStyle").toString().trim());
        }
        if (body.containsKey("country")) {
            user.setCountry(body.get("country").toString().trim());
        }
        if (body.containsKey("timezone")) {
            user.setTimezone(body.get("timezone").toString().trim());
        }

        userRepository.save(user);

        if (body.containsKey("games")) {
            List<String> gameNames = castList(body.get("games"));
            if (gameNames.size() > 4) throw new IllegalArgumentException("Máximo 4 juegos favoritos");
            gamesRepository.deleteByUserIdUser(userId);
            for (int i = 0; i < gameNames.size(); i++) {
                Games g = new Games();
                g.setUser(user);
                g.setGameName(gameNames.get(i).trim());
                g.setDisplayOrder(i + 1);
                gamesRepository.save(g);
            }
        }

        if (body.containsKey("languages")) {
            List<String> langs = castList(body.get("languages"));
            lenguageRepository.deleteByUserIdUser(userId);
            for (String lang : langs) {
                Lenguage l = new Lenguage();
                l.setUser(user);
                l.setLanguage(lang.trim());
                lenguageRepository.save(l);
            }
        }

        if (body.containsKey("platforms")) {
            List<String> plats = castList(body.get("platforms"));
            platformRepository.deleteByUserIdUser(userId);
            for (String plat : plats) {
                Platform p = new Platform();
                p.setUser(user);
                p.setPlatform(plat.trim());
                platformRepository.save(p);
            }
        }

        return getProfile(userId);
    }

    @SuppressWarnings("unchecked")
    private List<String> castList(Object obj) {
        if (obj instanceof List<?>) return (List<String>) obj;
        return new ArrayList<>();
    }
}