package org.example.shopping_mall_overview.domain.user.repository;

import org.example.shopping_mall_overview.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
