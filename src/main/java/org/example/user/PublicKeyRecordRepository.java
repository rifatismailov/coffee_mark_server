package org.example.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicKeyRecordRepository extends JpaRepository<PublicKeyRecord, Long> {

    // Знаходження запису за UUID
    Optional<PublicKeyRecord> findByUuid(String uuid);

    // Знаходження всіх ключів для певного користувача
    List<PublicKeyRecord> findByUser(User user);

    // Видалити ключ по UUID
    void deleteByUuid(String uuid);

    // Перевірити, чи вже існує UUID
    boolean existsByUuid(String uuid);

    // Видалити всі ключі користувача (наприклад, при видаленні акаунту)
    void deleteAllByUser(User user);
}

