package lol.chimkin.notes.note;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  public List<Note> getNotes() {
    return noteRepository.findByDeletedAtIsNull();
  }

  public List<Note> getSoftDeletedNotes() {
    return noteRepository.findByDeletedAtIsNotNull();
  }

  public Note createNote(String title, String content, String tags) {
    return noteRepository.save(new Note(title, content, LocalDate.now(), tags));
  }

  public void hardDelete() {
    noteRepository.deleteInBulkAllByDeletedAtIsNotNull();
  }

  @Transactional
  public void softDelete(UUID noteId) {
    Note note = noteRepository.findById(noteId)
        .orElseThrow(() -> new IllegalArgumentException("Cant soft delete note with id " + noteId + "does not exists"));
    note.setDeletedAt(LocalDate.now());
  }
}
