package org.example.registration;

import org.example.cafe.Cafe;
import org.example.user.Role;
import org.example.user.User;
import org.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public RegisterResponse registerUser(RegisterRequest request) {
        System.out.println("Received request: " + request.getCafeList());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Користувач із email " + request.getEmail() + " вже зареєстрований!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(Role.valueOf(request.getRole()));
        user.setImage(request.getImage());
        if (Role.BARISTA.equals(user.getRole())) {
            System.out.println(request.getCafeList());
            List<Cafe> cafes = request.getCafeList().stream().map(c -> {
                Cafe cafe = new Cafe();
                cafe.setName(c.getName());
                cafe.setAddress(c.getAddress());
                cafe.setCafe_image(c.getCafe_image());
                cafe.setBarista(user);
                System.out.println(cafe.getName());

                return cafe;
            }).collect(Collectors.toList());

            user.setCafes(cafes);
        }
        userRepository.save(user);
        return new RegisterResponse(true, "Success");
    }
}



