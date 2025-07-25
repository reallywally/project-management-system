package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"users"})
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "역할명은 필수입니다")
    @Size(max = 50, message = "역할명은 50자 이하여야 합니다")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    // 편의 생성자
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
} 