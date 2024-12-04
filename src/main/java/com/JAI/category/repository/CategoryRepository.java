package com.JAI.category.repository;

import com.JAI.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByGroup_GroupId(UUID groupId);

    List<Category> findByUser_UserId(UUID userId);

    @Query("SELECT c FROM Category c WHERE c.group.groupId IN " +
            "(SELECT gs.group.groupId FROM GroupSetting gs WHERE gs.user.userId = :userId)")
    List<Category> findGroupCategoriesByUserId(UUID userId);
}
