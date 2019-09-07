package com.juli.models;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class Statistics {

    private final String sum;
    private final String avg;
    private final String max;
    private final String min;
    private final long count;
}
