package br.com.estudo.anderson.quarkussocial.domain.repository;

import br.com.estudo.anderson.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // Como se fosse um singleton
public class UserRepository implements PanacheRepository<User> {

}
