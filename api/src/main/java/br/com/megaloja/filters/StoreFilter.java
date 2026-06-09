package br.com.megaloja.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class StoreFilter {

    private String name;

    private String city;

    private String state;

    private Boolean active;

}
