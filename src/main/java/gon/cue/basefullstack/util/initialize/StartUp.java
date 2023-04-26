package gon.cue.basefullstack.util.initialize;

import gon.cue.basefullstack.entities.login.Authority;
import gon.cue.basefullstack.repository.login.AuthorityRepository;
import gon.cue.basefullstack.repository.login.UserRepository;
import gon.cue.basefullstack.service.login.UserService;
import gon.cue.basefullstack.service.login.dto.AdminUserDTO;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
@Slf4j
public class StartUp {
    private final AuthorityRepository authorityRepository;
    private final UserService userService;

    public StartUp(AuthorityRepository authorityRepository, UserRepository userRepository, UserService userService) {
        this.authorityRepository = authorityRepository;
        this.userService = userService;

        createAuthorities();
        userRepository.findOneByEmailIgnoreCase("admin@localhost").ifPresentOrElse(
                item -> log.info("Admin user already exists"), this::createAdminUser
        );
        userRepository.findOneByEmailIgnoreCase("user@localhost").ifPresentOrElse(
                item -> log.info("Normal user already exists"), this::createNormalUser
        );
    }

    private void createAuthorities() {
        String rolePrefix = "ROLE_";
        //Fill all authorities here
        String[] authorities = {"USER", "ADMIN"};

        Stream.of(authorities).forEach(item -> authorityRepository.findById(rolePrefix + item).ifPresentOrElse(
                authority -> log.info("Authority " + authority.getName() + " already exists"),
                () -> {
                    Authority authority = new Authority();
                    authority.setName(rolePrefix + item);
                    authorityRepository.save(authority);
                }
        ));
    }

    private void createNormalUser() {
        AtomicReference<AdminUserDTO> normalUserDTO = new AtomicReference<>(new AdminUserDTO());
        normalUserDTO.get().setLogin("user");
        normalUserDTO.get().setFirstName("User");
        normalUserDTO.get().setLastName("User");
        normalUserDTO.get().setEmail("user@localhost");
        normalUserDTO.get().setImageUrl("");
        normalUserDTO.get().setActivated(true);
        normalUserDTO.get().setLangKey("en");
        normalUserDTO.get().setCreatedBy("system");
        normalUserDTO.get().setLastModifiedBy("system");
        String user1 = this.userService.registerUser(normalUserDTO.get(), "user").getActivationKey();
        this.userService.activateRegistration(user1).ifPresent(item -> {
            normalUserDTO.set(new AdminUserDTO(item));
            Set<String> authorities = new HashSet<>();
            authorities.add("ROLE_USER");
            normalUserDTO.get().setAuthorities(authorities);
            this.userService.updateUser(normalUserDTO.get());
        });
    }

    private void createAdminUser() {
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setLogin("admin");
        adminUserDTO.setFirstName("Administator");
        adminUserDTO.setLastName("Administrator");
        adminUserDTO.setEmail("admin@localhost");
        adminUserDTO.setImageUrl("");
        adminUserDTO.setActivated(true);
        adminUserDTO.setLangKey("en");
        adminUserDTO.setCreatedBy("system");
        adminUserDTO.setLastModifiedBy("system");
        String admin1 = this.userService.registerUser(adminUserDTO, "admin").getActivationKey();
        this.userService.activateRegistration(admin1).ifPresent(item -> {
            AdminUserDTO adminUserDTO1 = new AdminUserDTO(item);
            Set<String> authorities = new HashSet<>();
            authorities.add("ROLE_ADMIN");
            authorities.add("ROLE_USER");
            adminUserDTO1.setAuthorities(authorities);
            this.userService.updateUser(adminUserDTO1);
        });
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        log.info("Destroying Bean " + this.getClass().getSimpleName());
    }
}
