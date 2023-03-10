package lol.chimkin.notes.note;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/note")
public class NoteController {
  private final NoteService noteService;

  @Autowired
  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @GetMapping()
  public List<Note> list(@RequestParam("soft_deleted") Optional<Boolean> softDeleted) {
    if (softDeleted.isPresent() && softDeleted.get()) {
      return noteService.getSoftDeletedNotes();
    }
    return noteService.getNotes();
  }

  @PutMapping(path = "{noteId}")
  public void update(@PathVariable("noteId") UUID noteId, @Valid @RequestBody NoteRequest note) {
    noteService.updateNote(noteId, note.title, note.content, note.tags);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public Note create(@Valid @RequestBody NoteRequest note) {
    return noteService.createNote(note.title, note.content, note.tags);
  }

  @DeleteMapping(path = "{noteId}")
  public void delete(@PathVariable("noteId") UUID noteId) {
    noteService.softDelete(noteId);
  }

  @DeleteMapping
  public void delete() {
    noteService.hardDelete();
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  public static class NoteRequest {
    @Nullable
    @NotEmpty(message = "Title is required")
    private String title;
    @Nullable
    @NotEmpty(message = "Content is required")
    private String content;
    @Nullable
    @NotEmpty(message = "Tags are required")
    private String tags;
  }

}
