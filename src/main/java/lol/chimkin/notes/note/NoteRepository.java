package lol.chimkin.notes.note;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

  List<Note> findByDeletedAtIsNull();

  List<Note> findByDeletedAtIsNotNull();

  @Transactional
  void deleteInBulkAllByDeletedAtIsNotNull();
}
