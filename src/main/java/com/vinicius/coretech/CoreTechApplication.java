package com.vinicius.coretech;

import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.entity.Role;
import com.vinicius.coretech.repository.RoleRepository;
import com.vinicius.coretech.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class CoreTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreTechApplication.class, args);
	}

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(roleRepository.findByAuthority("ADMIN").isPresent()) return;

            Role adminRole = roleRepository.save(
                    Role.builder()
                            .authority("ADMIN")
                            .build()
            );

            Role userRole = roleRepository.save(
                    Role.builder()
                            .authority("USER")
                            .build()
            );

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);

            User adminUser = User.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .authorities(roles)
                    .build();

            userRepository.save(adminUser);
        };
    }
}
