package br.com.megaloja.filters;

import br.com.megaloja.models.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserFilter {

    private String name;

    private String email;

    private String role;

}