package lol.chimkin.notes.note;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Note {
  @Id
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
  @Column(length = 32, nullable = false, updatable = false)
  private UUID id;
  @NonNull
  private String title;
  @NonNull
  private String content;
  @NonNull
  private LocalDate createdAt;
  private LocalDate updatedAt;
  private LocalDate deletedAt;
  @NonNull
  private String tags;
  @Transient
  private List<String> parsedTags;

  public List<String> getParsedTags() {
    return Arrays.stream(tags.split(",")).toList();
  }


}
